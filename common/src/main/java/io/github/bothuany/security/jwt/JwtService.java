package io.github.bothuany.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private Key key;

    // Token blacklist'i tutmak için ConcurrentHashMap ve Set kullanıyoruz
    private final Map<String, Date> blacklistedTokens = new ConcurrentHashMap<>();
    private final Set<String> invalidatedTokens = new HashSet<>();

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        initKey();
    }

    @PostConstruct
    public void initKey() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationMs()))
                .setIssuer(jwtProperties.getIssuer())
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Generates a JWT token with additional claims like user ID
     *
     * @param authentication the authentication object
     * @param userId         the ID of the user to include in the token
     * @return the JWT token
     */
    public String generateToken(Authentication authentication, UUID userId) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Map<String, Object> claims = new HashMap<>();
        claims.put("auth", authorities);
        claims.put("userId", userId.toString());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(authentication.getName())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationMs()))
                .setIssuer(jwtProperties.getIssuer())
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .filter(auth -> !auth.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            // Önce blacklist'te olup olmadığını kontrol et
            if (isTokenBlacklisted(token)) {
                return false;
            }

            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Token'ı geçersiz kılmak için blacklist'e ekler.
     * 
     * @param token Geçersiz kılınacak JWT token
     * @return İşlem başarılı ise true, token zaten geçersiz veya hatalıysa false
     */
    public boolean invalidateToken(String token) {
        try {
            // Token'ı doğrula, eğer geçerliyse blacklist'e ekle
            if (!validateToken(token)) {
                return false;
            }

            // Token'ın son kullanma tarihini al
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();

            // Token'ı blacklist'e ekle
            blacklistedTokens.put(token, expiration);
            invalidatedTokens.add(token);

            // Eski süresi dolmuş tokenları temizle
            cleanupExpiredTokens();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verilen token'ın blacklist'te olup olmadığını kontrol eder.
     * 
     * @param token Kontrol edilecek token
     * @return Token blacklist'te ise true, değilse false
     */
    private boolean isTokenBlacklisted(String token) {
        return invalidatedTokens.contains(token);
    }

    /**
     * Süresi dolmuş tokenları blacklist'ten temizler.
     */
    private void cleanupExpiredTokens() {
        Date now = new Date();
        // Süresi dolmuş tokenları bul ve sil
        blacklistedTokens.entrySet().removeIf(entry -> now.after(entry.getValue()));
        // invalidatedTokens'dan da sil
        blacklistedTokens.keySet().forEach(invalidatedTokens::remove);
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Extracts all claims from the token.
     * 
     * @param token JWT token
     * @return Claims
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extracts the user ID from the token
     * 
     * @param token JWT token
     * @return user ID
     */
    public UUID extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        if (claims.containsKey("userId")) {
            return UUID.fromString(claims.get("userId", String.class));
        }
        return null;
    }
}