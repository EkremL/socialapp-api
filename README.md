## SocialApp API

Backend case kapsamında geliştirdiğim örnek bir sosyal medya RESTful API'sidir.
Uygulamayı yalnızca API seviyesinde geliştirdim, arayüzü bulunmamaktadır. 

## KULLANILAN TEKNOLOJİLER VE KÜTÜPHANELER(DEPENDENCIES)

-Spring Initializer üzerinden gerekli konfigürasyonlar yapıldı ve proje oluşturuldu.
-Java 21
-Spring Boot 3.5.6
-Intellij IDE Community Version
-Maven
-PostgreSQL
-JsonWebToken(api,impl,jackson)
-Dotenv
-Bcrypt
-ModelMapper
-Lombok
-Spring Boot Devtools
-Spring Boot Starter WEB
-Spring Boot Starter JPA
-POSTMAN (API testleri için)


## KURULUM & ÇALIŞTIRMA ADIMLARI

1- git clone https://github.com/EkremL/socialapp-api.git adresi üzerinden proje klonlanır.
2- Intellij IDE üzerinden açılır ve dependencyler otomatik yüklenecektir.
3-.env dosyası oluşturulur ve aşağıdaki formatta değişken oluşturulur.
SECRET_KEY=your_256_bit_secret_key_here
4- Veritabanı yapılandırması (application.properties) için aşağıdaki şekilde yapılmalıdır.
spring.application.name=SocialApp
server.port=8000
spring.datasource.url=jdbc:postgresql://localhost:5432/socialapp
spring.datasource.username=postgres
spring.datasource.password=yourPW
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
5- Projeyi ya terminal üzerinden mvn spring-boot:run komutuyla ya da main class üzerinden run ederek çalıştırılır.

##HAZIR (VARSAYILAN) ADMIN KULLANICISI OLUŞTURMA BİLGİSİ

Proje ayağa kaldırıldığında AdminInitializer yardımcı sınıfı aracılığıyla bir admin oluşturulur.
Admin bilgileri statik data olarak bu classın içinde mevcuttur fakat bilgilendirme açısından; şu bilgilerle oluşturuluyor:

username: admin
email:admin@admin.com
password: Admin123*
role: ADMIN

ENDPOINTLER (UÇ NOKTALAR)

####AUTH
| HTTP     | ENDPOINT                  | AÇIKLAMA |
|---------|--------------------------|
| POST     | /api/auth/signup          | Yeni kullanıcı kaydedilir ve varsayılan olarak rolü USER olarak atanır. |
| POST     | /api/auth/login           | Giriş yapılır, JWT token üretilir ve bu token databaseye kaydedilir. |
| POST     | /api/auth/logout          | Kullanıcı çıkış yapar ve token geçersiz kılınır. |
| POST     | /api/auth/me              | Aktif kullanıcının bilgilerini döner. |

####POSTS
| HTTP     | ENDPOINT                  | AÇIKLAMA|
|---------|--------------------------|
| POST     | /api/posts                | Yeni post oluşturulur. |
| GET     | /api/posts/{id}            | Tekli postu idsine göre getirir. |
| GET     | /api/posts                 | Tüm postları döner. |
| PUT     | /api/posts/{id}            | Post güncellenir. (post sahibi veya admin tarafından) |
| DELETE    | /api/posts/{id}          | İlgili post, idsine göre silinir.  (post sahibi veya admin tarafından) |
| POST     | /api/posts/{id}/view      | Görüntülenme sayısı artar. |

####COMMENTS
| HTTP     | ENDPOINT                   |AÇIKLAMA |
|---------|--------------------------|
| POST     | /api/posts/{id}/comments     | İlgili posta yorum ekleme işlemini yapar. |
| GET      | /api/posts/{id}/comments     | İlgili postun içindeki tüm yorumlar listelenir. |
| DELETE   | /api/comments/{id}           | Yorumu siler. (Yorum sahibi, post sahibi veya admin tarafından. Not: Post silinirse yorumlar da silinir!) |

####LIKES
| HTTP     | ENDPOINT                  | AÇIKLAMA |
|---------|--------------------------|
| POST     | /api/posts/{id}/likes        | Post beğenme işlemi. |
| DELETE   | /api/posts/{id}/likes        | Beğeniyi geri çekme işlemi. |


##POSTMAN COLLECTION
- Tüm endpointler Postman üzerinden test edilmiştir.
- Koleksiyon içerisinde aşağıdaki akış takip edilmelidir:

  signup -> login -> JWT Token ile role based işlemler -> logout

#Ortam Değişkenleri
{{url}} = http://localhost:8000/api   (port numarasını application.properties üzerinden 8000 verdim, ayrıca postmanda test ederken tekrar tekrar yazmamak adına /api yi de baseurl'e dahil ettim. Tercihe bağlı /api kısmı çıkartılabilir)
{{accessToken}} = Bearer <jwt_token>  (Not. Test sürecinde token'i Postman Headers bölümünden Authorization'u aktif ederek elle (manual) şekilde Bearer <token> olarak ekledim. Tercihe bağlı olarak accesToken değişkeni de tanımlanabilir.)


##Varsayımlar & Kısıtlar
-Projede Spring Security kullanmadım. Dolayısıyla signature ve hashleme işlemlerini manual yaptım.
-İşlevsel Gereksinimlerde belirtildiği üzere login ile token üretilir ve veritabanında aktif olarak kaydedilir, logout ile sonlandırılır.
-Her korumalı (authorization) endpoint çağrısında token kontrolü yapılmaktadır.
-Token geçerlilik süresini 30 gün yaptım çünkü test edilirken sorun yaşanmaması adına böyle bir tercihte bulundum. Ayrıca bu değeri Constants classında tanımladım.
-256 bitlik Jwt secret keyi dotenv içerisinde tanımladım ve veri güvenliğini sağladım.
-Hata yönetimi açısından global exception handler eklemek yerine hataları manual bir şekilde ele aldım. (Bu bilinçli bir tercih, dilenirse eklenebilir.)
-Tüm endpointleri başarıyla test ettim ve başarılı sonuçlar aldım.
-Nested mapper gereken yerlerde gerekli kısımları ModelMapper ile, diğer kısımları ise manual bir şekilde mapledim.

##Projeyi Geliştirme Adımları
-Proje boyunca katmanlı mimari (Model Repository Controller Service) tercih ederek temiz kod prensiplerine ve SoC prensibine sadık kalmaya çalıştım.
-Sırasıyla User, Auth, Admin, Posts , Comments ve Like bölümlerini geliştirdim. İlişkilendirmeleri sonradan ekledim. (One to Many, Many to One gibi)
-Bazı dto ları sonradan ekleyerek temiz kod prensibini benimsedim.
