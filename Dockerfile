FROM eclipse-temurin:17-jdk-jammy as build
WORKDIR /app

# Maven bağımlılıklarını kopyala
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Maven bağımlılıklarını önce yükle (hızlı yeniden yapılandırma için)
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Kaynak kodunu kopyala
COPY src src

# Uygulamayı derle
RUN ./mvnw package -DskipTests

# Çalışma zamanı imajı
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Derlenmiş jar dosyasını kopyala
COPY --from=build /app/target/*.jar app.jar

# PostgreSQL veritabanı bağlantısı için ortam değişkenleri
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/invoicedb
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=postgres
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Uygulamayı çalıştır
ENTRYPOINT ["java", "-jar", "app.jar"]

# Port yönlendirme
EXPOSE 8080
