# Gateway Server

Gateway Server, mikroservis mimarisinin ön kapısı olarak hizmet veren, Spring Cloud Gateway tabanlı bir API Gateway'dir.

## Temel Özellikler

- Tüm mikroservislere tek bir noktadan erişim
- Güvenlik ve kimlik doğrulama isteklerini yönlendirme
- Circuit breaker desteği ile dayanıklılık
- CORS desteği
- Fallback mekanizması
- CSRF koruması devre dışı bırakılmış (API istekleri için)

## API Yönlendirmeleri

Gateway aşağıdaki yönlendirmeleri sağlar:

### Güvenlik Endpointleri

- `/api/auth/**` -> `user-service`: Tüm kimlik doğrulama istekleri (login, register, logout)

### Mikroservis Endpointleri

- `/api/users/**` -> `user-service`: Kullanıcı yönetimi
- `/api/customers/**` -> `customer-service`: Müşteri yönetimi
- `/api/contracts/**` -> `contract-service`: Sözleşme yönetimi
- `/api/plans/**` -> `plan-service`: Plan yönetimi
- `/api/billings/**` -> `billing-service`: Fatura yönetimi
- `/api/support/**` -> `support-service`: Destek yönetimi
- `/api/analytics/**` -> `analytics-service`: Analitik
- `/api/notifications/**` -> `notification-service`: Bildirim yönetimi

## Güvenlik

Gateway, tüm kimlik doğrulama (authentication) isteklerini user-service'e yönlendirir. Diğer servisler doğrudan authentication işlemleri yapmaz, bunun yerine user-service'e yönlendirme yapar.

### Security Konfigürasyonu

- Gateway seviyesinde CSRF koruması devre dışı bırakılmıştır (API istekleri için gerekli)
- Kimlik doğrulama kontrolü her mikroservisin kendi içinde JWT ile yapılır
- Gateway'den gelen isteklere herhangi bir kısıtlama uygulanmaz, güvenlik kontrolü mikroservisler tarafından gerçekleştirilir

## Dayanıklılık

Her servis için Circuit Breaker tanımlanmıştır. Bir servis başarısız olduğunda, istekler uygun bir fallback yanıtına yönlendirilir.

## CORS Desteği

Gateway, CORS (Cross-Origin Resource Sharing) desteği sağlayarak farklı originlerden gelen API çağrılarına izin verir.

## Fallback Mekanizması

Her servis için `/fallback/{service-name}` yolunda bir fallback endpoint'i tanımlanmıştır. Bir servis kullanılamadığında, istekler bu endpoint'e yönlendirilir.
