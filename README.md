# XML Fatura İşleme API
# Fatura XML API

Bu proje, fatura XML dosyalarının Base64 formatında alınıp işlenmesini sağlayan bir Spring Boot uygulamasıdır.

## Özellikler

- XML fatura dosyalarının Base64 formatında alınması ve işlenmesi
- Base64 içeriğini çözümlemek için gelişmiş algoritmalar
- XML şema doğrulaması
- PostgreSQL veritabanına fatura verilerinin kaydedilmesi
- Teşhis ve test amaçlı API endpoint'leri

## Kurulum

### Gereksinimler

- Java 17
- Maven
- PostgreSQL (Docker kullanıyorsanız isteğe bağlı)

### Docker ile Kurulum

1. Projeyi klonlayın
2. Terminal/komut istemcisini açın ve proje dizinine gidin
3. Docker Compose ile uygulamayı başlatın:

```bash
docker-compose up --build
```

Uygulama http://localhost:8080 adresinde çalışmaya başlayacaktır.

### Manuel Kurulum

1. PostgreSQL veritabanını kurun ve `invoicedb` adında bir veritabanı oluşturun
2. `src/main/resources/application.properties` dosyasını veritabanı bağlantı bilgilerinize göre düzenleyin
3. Maven ile uygulamayı derleyin ve çalıştırın:

```bash
./mvnw spring-boot:run
```

## API Kullanımı

POST /api/diagnostic/analyze-base64
Content-Type: application/json

{
  "data": "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz..."
}
```

## Özellikler

- XML fatura verilerinin Base64 kodlamasını çözme
- XSD şemasına göre XML doğrulama
- JAXB ile XML'den Java nesnelerine dönüştürme
- Spring Data JPA ile veritabanı entegrasyonu
- Hata yönetimi ve doğrulama
- OpenAPI (Swagger) dokümantasyonu

## Teknolojiler

- Java 17
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- H2 Database (Geliştirme için)
- PostgreSQL (Üretim için)
- JAXB (XML işleme)
- Lombok
- Swagger (OpenAPI)

## Kurulum ve Çalıştırma

### Gereksinimleri

- Java 17 veya üzeri
- Maven

### Projeyi Oluşturma ve Çalıştırma

```bash
# Projeyi klonlayın
git clone https://github.com/yourusername/invoice-xml-api.git
cd invoice-xml-api

# Maven ile derleyin
mvn clean install

# Uygulamayı çalıştırın
mvn spring-boot:run
```

Uygulama varsayılan olarak 8080 portunda çalışacak ve bellek içi H2 veritabanını kullanacaktır.

## API Kullanımı

### Fatura Yükleme

```
POST /api/invoices
Content-Type: application/json

{
  "base64xml": "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPEZha3R1cmEgeG1sbnM9Imh0dHA6Ly9jcmQuZ292LnBsL3d6b3IvMjAyMy8wNi8yOS8xMjY0OC8iPgogIDxQb2RtaW90MT4KICAgIDxEYW5lSWRlbnR5ZmlrYWN5am5lPgogICAgICA8TklQPjEyMzQ1Njc4OTA8L05JUD4KICAgIDwvRGFuZUlkZW50eWZpa2FjeWpuZT4KICA8L1BvZG1pb3QxPgogIDxGYT4KICAgIDxQXzE+MjAyMy0wOC0zMTwvUF8xPgogICAgPFBfMj5GSzIwMjMvMDgvMzE8L1BfMj4KICA8L0ZhPgo8L0Zha3R1cmE+"
}

```

### Başarılı Yanıt

```json
{
  "message": "Fatura başarıyla kaydedildi"
}
```

### Hata Yanıtı

```json
Request
{
  "data": "PHNwZWNpZmljYXRpb24geG1sbnM9Imh0dHA6Ly9..."
}

Response
{
  "örnek_xml": "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Faktura xmlns=\"http://crd.gov.pl/wzor/2023/06/29/12648/\">\n  <Podmiot1>\n    <DaneIdentyfikacyjne>\n      <NIP>1234567890</NIP>\n    </DaneIdentyfikacyjne>\n  </Podmiot1>\n  <Fa>\n    <P_1>2023-08-31</P_1>\n    <P_2>FK2023/08/31</P_2>\n  </Fa>\n</Faktura>",
  "örnek_base64": "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPEZha3R1cmEgeG1sbnM9Imh0dHA6Ly9jcmQuZ292LnBsL3d6b3IvMjAyMy8wNi8yOS8xMjY0OC8iPgogIDxQb2RtaW90MT4KICAgIDxEYW5lSWRlbnR5ZmlrYWN5am5lPgogICAgICA8TklQPjEyMzQ1Njc4OTA8L05JUD4KICAgIDwvRGFuZUlkZW50eWZpa2FjeWpuZT4KICA8L1BvZG1pb3QxPgogIDxGYT4KICAgIDxQXzE+MjAyMy0wOC0zMTwvUF8xPgogICAgPFBfMj5GSzIwMjMvMDgvMzE8L1BfMj4KICA8L0ZhPgo8L0Zha3R1cmE+",
  "error": "base64xml alanı gerekli ve boş olamaz"
}
```

## H2 Veritabanı Konsolu

Geliştirme sırasında H2 veritabanına erişmek için:

```
http://localhost:8080/h2-console
```

Bağlantı bilgileri:
- JDBC URL: `jdbc:h2:mem:invoicedb`
- Kullanıcı Adı: `sa`
- Şifre: [boş bırakın]

## PostgreSQL'e Geçiş

Üretim ortamında PostgreSQL kullanmak için `application.yml` dosyasındaki PostgreSQL yapılandırmasını etkinleştirin ve H2 yapılandırmasını devre dışı bırakın.

## XSD ve JAXB Kullanımı

Projede XML şeması `src/main/resources/schemat.xsd` dosyasında tanımlanmıştır. JAXB sınıfları (`Faktura`, `Podmiot1`, vb.) bu şemaya göre oluşturulmuştur.

Yeni bir XSD şemasından Java sınıfları oluşturmak için:

```bash
xjc -d src/main/java -p com.example.invoiceapi.xml path/to/your.xsd
```

## Swagger Dokümantasyonu

API dokümantasyonuna erişmek için:

```
http://localhost:8080/swagger-ui/index.html
```
