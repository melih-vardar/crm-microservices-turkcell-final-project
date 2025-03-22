# User Service Postman Collection

Bu repository, Turkcell CRM Microservices projesi kapsamında User Service API endpoint'lerini test etmek için bir Postman Collection içerir.

## İçerik

Collection, User Service'in tüm endpoint'lerini üç kategoriye ayrılmış şekilde içerir:

1. **Public Endpoints** - Kimlik doğrulama gerektirmeyen endpoint'ler
2. **User Specific Endpoints** - Kullanıcıların kendi bilgilerini yönetebileceği endpoint'ler
3. **Admin Endpoints** - Sadece admin rolüne sahip kullanıcıların erişebileceği endpoint'ler

## Postman Collection'ı İçe Aktarma

1. [Postman](https://www.postman.com/) uygulamasını açın
2. "Import" butonuna tıklayın (sağ üst köşede)
3. "File" sekmesini seçin ve `user-service-postman-collection.json` dosyasını seçin
4. "Import" butonuna tıklayarak collection'ı içe aktarın

## Çalışma Ortamı Oluşturma

Collection, environment variable'ları kullanır. Postman'de bir environment oluşturmanız gerekecek:

1. Sağ üst köşedeki dişli simgesine tıklayın
2. "Add" butonuna tıklayarak yeni bir environment oluşturun
3. Environment için bir isim verin (örn. "User Service Gateway")
4. Aşağıdaki değişkenleri ekleyin (ilk kullanımda boş bırakılabilir, otomatik olarak doldurulacaktır):
   - `base_url` (varsayılan değer: http://localhost:8080)
   - `customer_rep_token`
   - `admin_token`
   - `customer_rep_id`
   - `admin_id`
   - `user_id_to_delete`
   - `user_id_to_restore`
5. "Save" butonuna tıklayın

## Kullanım Kılavuzu

1. **Public Endpoints**:

   - "Register User" ile CUSTOMER_REPRESENTATIVE rolüne sahip kullanıcı oluşturun
   - "Register Admin User" ile ADMIN rolüne sahip kullanıcı oluşturun
   - "Login User" ile giriş yapın
   - "Test Service" ile servisin çalışıp çalışmadığını kontrol edin

2. **User Specific Endpoints**:

   - "Get Current User" ile mevcut kullanıcı bilgilerinizi görüntüleyin
   - "Get User By ID" ile belirli bir kullanıcının bilgilerini görüntüleyin
   - "Update User" ile kullanıcı bilgilerini güncelleyin
   - "Logout User" ile çıkış yapın
   - "Test Customer Representative Endpoint" ile Customer Representative rolüne özel endpoint'i test edin

3. **Admin Endpoints**:
   - "Get All Users" ile tüm aktif kullanıcıları listeleyin
   - "Get All Deleted Users" ile silinmiş kullanıcıları listeleyin
   - "Get All Users Including Deleted" ile silinmiş ve aktif tüm kullanıcıları listeleyin
   - "Delete User" ile kullanıcı silin
   - "Restore Deleted User" ile silinmiş kullanıcıyı geri yükleyin
   - "Get User Roles" ile kullanıcı rollerini görüntüleyin
   - "Test Admin Endpoint" ile Admin rolüne özel endpoint'i test edin

## Otomatik Token Yönetimi

Collection, başarılı bir giriş veya kayıt işlemi sonrasında token'ları otomatik olarak saklar:

1. CUSTOMER_REPRESENTATIVE kullanıcı için: `customer_rep_token` ve `customer_rep_id` değişkenleri otomatik olarak doldurulur
2. ADMIN kullanıcı için: `admin_token` ve `admin_id` değişkenleri otomatik olarak doldurulur

## API Gateway Kullanımı

Collection, API Gateway üzerinden servislere erişim için yapılandırılmıştır:

- Varsayılan olarak `http://localhost:8080` adresi kullanılmaktadır (API Gateway portu)
- Farklı bir adres kullanmak isterseniz, environment içindeki `base_url` değişkenini güncelleyebilirsiniz
- Gateway, gelen istekleri uygun servise yönlendirecektir (user-service için)

## Not

- İlk kullanımda `user_id_to_delete` ve `user_id_to_restore` değişkenlerini manuel olarak doldurmanız gerekebilir
