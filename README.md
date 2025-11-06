## SocialApp API

Backend case kapsamında geliştirdiğim örnek bir sosyal medya RESTful API'sidir.<br>
Uygulamayı yalnızca API seviyesinde geliştirdim, arayüzü bulunmamaktadır.

⚠️ Proje aktif olarak geliştirilmektedir.
En güncel özellikler ve refactor çalışmaları için:
👉 improvement/refactor-and-features branch'ine göz atabilirsiniz.

## KULLANILAN TEKNOLOJİLER VE KÜTÜPHANELER(DEPENDENCIES)

-Spring Initializer üzerinden gerekli konfigürasyonlar yapıldı ve proje oluşturuldu.<br>
-Java 21<br>
-Spring Boot 3.5.6<br>
-Intellij IDE Community Version<br>
-Maven<br>
-PostgreSQL<br>
-JsonWebToken(api,impl,jackson)<br>
-Dotenv<br>
-Bcrypt<br>
-ModelMapper<br>
-Lombok<br>
-Spring Boot Devtools<br>
-Spring Boot Starter WEB<br>
-Spring Boot Starter JPA<br>
-POSTMAN (API testleri için)<br>


## KURULUM & ÇALIŞTIRMA ADIMLARI

1- git clone https://github.com/EkremL/socialapp-api.git adresi üzerinden proje klonlanır.<br>
2- Intellij IDE üzerinden açılır ve dependencyler otomatik yüklenecektir.<br>
3-.env dosyası oluşturulur ve aşağıdaki formatta değişken oluşturulur.<br>
SECRET_KEY=your_256_bit_secret_key_here<br>
4- Veritabanı yapılandırması (application.properties) için aşağıdaki şekilde yapılmalıdır.<br>
spring.application.name=SocialApp<br>
server.port=8000<br>
spring.datasource.url=jdbc:postgresql://localhost:5432/socialapp<br>
spring.datasource.username=postgres<br>
spring.datasource.password=yourPW<br>
spring.datasource.driver-class-name=org.postgresql.Driver<br>
spring.jpa.hibernate.ddl-auto=update<br>
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect<br>
5- Projeyi ya terminal üzerinden mvn spring-boot:run komutuyla ya da main class üzerinden run ederek çalıştırılır.<br>

NOT: Postman collection ve environment dosyaları "postman" klasöründedir. Test için login işlemi yaptıktan sonra kendi "accesToken"'inizi environment kısmında tanımlayabilirsiniz. (Bearer <alacağınız_token>)  

## HAZIR (VARSAYILAN) ADMIN KULLANICISI OLUŞTURMA BİLGİSİ

Proje ayağa kaldırıldığında AdminInitializer yardımcı sınıfı aracılığıyla bir admin oluşturulur.<br>
Admin bilgileri statik data olarak bu classın içinde mevcuttur fakat bilgilendirme açısından; şu bilgilerle oluşturuluyor:<br>

username: admin<br>
email:admin@admin.com<br>
password: Admin123*<br>
role: ADMIN<br>

## ENDPOINTLER (UÇ NOKTALAR)

#### AUTH

| HTTP | Endpoint | Açıklama |
|------|-----------|-----------|
| POST | /api/auth/signup | Yeni kullanıcı kaydedilir ve varsayılan olarak rolü USER olarak atanır. |
| POST | /api/auth/login | Giriş yapılır, JWT token üretilir ve bu token database'e kaydedilir. |
| POST | /api/auth/logout | Kullanıcı çıkış yapar ve token geçersiz kılınır. |
| GET  | /api/auth/me | Aktif kullanıcının bilgilerini döner. |

---

#### POSTS

| HTTP | Endpoint | Açıklama |
|------|-----------|-----------|
| POST | /api/posts | Yeni post oluşturulur. |
| GET  | /api/posts/{id} | Tekli post id'sine göre getirilir. |
| GET  | /api/posts | Tüm postlar döner. |
| PUT  | /api/posts/{id} | Post güncellenir. (post sahibi veya admin tarafından) |
| DELETE | /api/posts/{id} | İlgili post silinir. (post sahibi veya admin tarafından) |
| POST | /api/posts/{id}/view | Görüntülenme sayısı artar. |

---

#### COMMENTS

| HTTP | Endpoint | Açıklama |
|------|-----------|-----------|
| POST | /api/posts/{id}/comments | İlgili posta yorum ekleme işlemini yapar. |
| GET  | /api/posts/{id}/comments | İlgili postun içindeki tüm yorumları listeler. |
| DELETE | /api/comments/{id} | Yorumu siler. (Yorum sahibi, post sahibi veya admin tarafından. Not: Post silinirse yorumlar da silinir.) |

---

#### LIKES

| HTTP | Endpoint | Açıklama |
|------|-----------|-----------|
| POST | /api/posts/{id}/likes | Post beğenme işlemi. |
| DELETE | /api/posts/{id}/likes | Beğeniyi geri çekme işlemi. |



## POSTMAN COLLECTION
- Tüm endpointler Postman üzerinden test edilmiştir.<br>
- Koleksiyon içerisinde aşağıdaki akış takip edilmelidir:<br>

  signup -> login -> JWT Token ile role based işlemler -> logout

## Ortam Değişkenleri
{{url}} = http://localhost:8000/api   (port numarasını application.properties üzerinden 8000 verdim, ayrıca postmanda test ederken tekrar tekrar yazmamak adına /api yi de baseurl'e dahil ettim. Tercihe bağlı /api kısmı çıkartılabilir)<br>
{{accessToken}} = Bearer <jwt_token>  (Not. Test sürecinde token'i Postman Headers bölümünden Authorization'u aktif ederek elle (manual) şekilde Bearer <token> olarak ekledim. Tercihe bağlı olarak accesToken değişkeni de tanımlanabilir.)


## Varsayımlar & Kısıtlar
-Projede Spring Security kullanmadım. Dolayısıyla signature ve hashleme işlemlerini manual yaptım.<br>
-İşlevsel Gereksinimlerde belirtildiği üzere login ile token üretilir ve veritabanında aktif olarak kaydedilir, logout ile sonlandırılır.<br>
-Her korumalı (authorization) endpoint çağrısında token kontrolü yapılmaktadır.<br>
-Token geçerlilik süresini 30 gün yaptım çünkü test edilirken sorun yaşanmaması adına böyle bir tercihte bulundum. Ayrıca bu değeri Constants classında tanımladım.<br>
-256 bitlik Jwt secret keyi dotenv içerisinde tanımladım ve veri güvenliğini sağladım.<br>
-Hata yönetimi açısından global exception handler eklemek yerine hataları manual bir şekilde ele aldım. (Bu bilinçli bir tercih, dilenirse eklenebilir.)<br>
-Tüm endpointleri başarıyla test ettim ve başarılı sonuçlar aldım.<br>
-Nested mapper gereken yerlerde gerekli kısımları ModelMapper ile, diğer kısımları ise manual bir şekilde mapledim.

## Projeyi Geliştirme Adımları
-Proje boyunca katmanlı mimari (Model Repository Controller Service) tercih ederek temiz kod prensiplerine ve SoC prensibine sadık kalmaya çalıştım.<br>
-Sırasıyla User, Auth, Admin, Posts , Comments ve Like bölümlerini geliştirdim. İlişkilendirmeleri sonradan ekledim. (One to Many, Many to One gibi)<br>
-Bazı dto ları sonradan ekleyerek temiz kod prensibini benimsedim.
