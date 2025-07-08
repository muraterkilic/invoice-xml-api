# 📄 XML Fatura İşleme API

Bu proje, Base64 formatında gönderilen XML fatura verilerini alan, XSD şemasıyla doğrulayan ve PostgreSQL veritabanına kaydeden bir **Spring Boot** uygulamasıdır.

---

## 🚀 Özellikler

- 📥 Base64 formatında XML alma
- ✅ XSD şemasıyla XML doğrulama
- 🔁 JAXB ile XML → Java nesnesi dönüşümü
- 🛢️ H2 (geliştirme) ve PostgreSQL (üretim) desteği
- 📊 Swagger/OpenAPI dokümantasyonu
- ⚠️ Gelişmiş hata yönetimi ve loglama

---

## ⚙️ Gereksinimler

- Java 17+
- Maven
- PostgreSQL (manuel kurulum için)
- Docker (opsiyonel)

---

## 📦 Kurulum

### Docker ile Hızlı Başlangıç

```bash
git clone https://github.com/yourusername/invoice-xml-api.git
cd invoice-xml-api
docker-compose up --build
```

Uygulama şu adreste çalışacaktır: [http://localhost:8080](http://localhost:8080)

### Manuel Kurulum

1. PostgreSQL'de `invoicedb` adında bir veritabanı oluşturun.
2. `src/main/resources/application.properties` dosyasındaki bağlantı ayarlarını düzenleyin.
3. Maven ile projeyi çalıştırın:

```bash
mvn clean install
mvn spring-boot:run
```

---

## 📮 API Kullanımı

### Fatura Gönderme

**Endpoint:**

```
POST /api/invoices
Content-Type: application/json
```

**Request Örneği:**

```json
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

### Hatalı Yanıt

```json
{
  "error": "base64xml alanı gerekli ve boş olamaz",
  "örnek_xml": "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Faktura xmlns=\"http://crd.gov.pl/wzor/2023/06/29/12648/\">\n  <Podmiot1>\n    <DaneIdentyfikacyjne>\n      <NIP>1234567890</NIP>\n    </DaneIdentyfikacyjne>\n  </Podmiot1>\n  <Fa>\n    <P_1>2023-08-31</P_1>\n    <P_2>FK2023/08/31</P_2>\n  </Fa>\n</Faktura>",
  "örnek_base64": "UEQ5NGJXZ2d2V..."
}
```

---

## 🧪 H2 Veritabanı Konsolu

Geliştirme sırasında H2 veritabanı konsoluna erişmek için:

```
http://localhost:8080/h2-console
```

**Bağlantı bilgileri:**

- JDBC URL: `jdbc:h2:mem:invoicedb`
- Kullanıcı adı: `sa`
- Şifre: (boş bırakın)

---

## 🧰 JAXB & XSD

XML şeması `src/main/resources/schemat.xsd` altında yer almaktadır. JAXB sınıfları bu şemaya göre oluşturulmuştur (`Faktura`, `Podmiot1`, vb.).

Yeni bir XSD şemasından Java sınıfları üretmek için:

```bash
xjc -d src/main/java -p com.example.invoiceapi.xml path/to/your.xsd
```

---

## 📑 Swagger/OpenAPI

Swagger arayüzüne erişmek için:

```
http://localhost:8080/swagger-ui/index.html
```

