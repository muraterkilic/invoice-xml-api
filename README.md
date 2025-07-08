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

### Fatura Yükleme

```http
POST /api/invoices
Content-Type: application/json

{
  "base64xml": "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz..."
}
```

### Teşhis Araçları

```http
POST /api/diagnostic/analyze-base64
Content-Type: application/json

{
  "data": "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz..."
}
```

```http
GET /api/diagnostic/example-base64
```

## Lisans

Bu proje özel lisanslıdır. Tüm hakları saklıdır.
Bu proje, XML formatındaki faturaları işlemek için bir REST API sunar. Base64 ile kodlanmış XML fatura verilerini alır, XSD şemasına göre doğrular, gerekli verileri çıkarır ve veritabanına kaydeder.

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
  "base64xml": "PHNwZWNpZmljYXRpb24geG1sbnM9Imh0dHA6Ly9..."
}
```

### Başarılı Yanıt

```json
{
  "message": "Invoice saved successfully"
}
```

### Hata Yanıtı

```json
{
  "error": "XML validation failed: [Error message]"
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
