package com.example.invoiceapi.service;

import com.example.invoiceapi.exception.XmlProcessingException;
import com.example.invoiceapi.model.InvoiceEntity;
import com.example.invoiceapi.repository.InvoiceRepository;
import com.example.invoiceapi.util.Base64Helper;
import com.example.invoiceapi.xml.Faktura;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public void processInvoice(String base64xml) {
        try {
            log.info("Starting to process invoice XML");

            if (base64xml == null || base64xml.trim().isEmpty()) {
                throw new XmlProcessingException("Base64 XML string is empty or null");
            }

            // Base64 stringin ilk 20 karakterini loglayalım 
            log.debug("Base64 input starts with: {}...", 
                     base64xml.length() > 20 ? base64xml.substring(0, 20) : base64xml);

            // Bazı yaygın base64 sorunlarını düzeltelim
            String cleanBase64 = base64xml.trim();

            // Yeni satır ve boşlukları temizle
            cleanBase64 = cleanBase64.replaceAll("[\\s\\r\\n\\t]", "");

            // ... karakterini kontrol et ve temizle (genelde kırpılmış metinler için kullanılır)
            if (cleanBase64.contains("...")) {
                log.warn("Base64 input contains ellipsis (...), removing it for decoding");
                cleanBase64 = cleanBase64.replace("...", "");
            }

            // Tırnak işaretlerini kaldır (bazen JSON kopyala-yapıştır işlemlerinde kalabilir)
            if (cleanBase64.startsWith("\"") && cleanBase64.endsWith("\"")) {
                cleanBase64 = cleanBase64.substring(1, cleanBase64.length() - 1);
                log.debug("Removed surrounding quotes from base64 string");
            }

            // Örnek XML'i Base64'e kodlayıp test edelim
            String exampleXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Faktura xmlns=\"http://crd.gov.pl/wzor/2023/06/29/12648/\">\n  <Podmiot1>\n    <DaneIdentyfikacyjne>\n      <NIP>1234567890</NIP>\n    </DaneIdentyfikacyjne>\n  </Podmiot1>\n  <Fa>\n    <P_1>2023-08-31</P_1>\n    <P_2>FK2023/08/31</P_2>\n  </Fa>\n</Faktura>";
            String exampleBase64 = Base64.getEncoder().encodeToString(exampleXml.getBytes(StandardCharsets.UTF_8));
            log.debug("Example valid base64: {}...", exampleBase64.substring(0, Math.min(20, exampleBase64.length())));

            byte[] decoded;
            try {
                // Önce klasik Base64 decoder deneyelim
                decoded = Base64.getDecoder().decode(cleanBase64);
                log.debug("Standard Base64 decoder successfully decoded the input");
            } catch (IllegalArgumentException e1) {
                log.warn("Standard Base64 decode failed: {}", e1.getMessage());
                try {
                    // Fallback: URL-safe decoder deneyelim
                    decoded = Base64.getUrlDecoder().decode(cleanBase64);
                    log.debug("URL-safe Base64 decoder successfully decoded the input");
                } catch (IllegalArgumentException e2) {
                    log.error("Both Base64 decoders failed.");

                    // Örnek olarak doğru bir Base64 stringini gösterelim
                    throw new XmlProcessingException(
                        String.format("Base64 çözümleme hatası: %s. Doğru Base64 örneği: %s...", 
                        e1.getMessage(), exampleBase64.substring(0, Math.min(30, exampleBase64.length()))), e1);
                }
            }

            String xml = new String(decoded, StandardCharsets.UTF_8);
            log.debug("XML decoded successfully. Length: {}", xml.length());

            // Debug - XML içeriğini göster
            log.debug("Decoded XML content: \n{}\n", xml);

            // XML içeriği <specification> etiketiyle başlıyorsa uyarı ver
            if (xml.trim().startsWith("<specification")) {
                log.warn("XML starts with <specification> tag instead of <Faktura>. This may be a different format.");
                throw new XmlProcessingException("XML içeriği <Faktura> etiketi yerine <specification> ile başlıyor. Gönderdiğiniz XML, beklenen formatta değil.");
            }

            // XSD doğrulama
            try {
                log.info("Validating XML against XSD schema");
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

                // Şema dosyasının varlığını kontrol et
                var schemaResource = getClass().getClassLoader().getResource("schemat.xsd");
                if (schemaResource == null) {
                    throw new XmlProcessingException("Schema file 'schemat.xsd' not found in classpath");
                }

                log.debug("Schema location: {}", schemaResource.getPath());
                Schema schema = schemaFactory.newSchema(schemaResource);

                Validator validator = schema.newValidator();
                validator.validate(new StreamSource(new StringReader(xml)));
                log.info("XML validation successful");
            } catch (XmlProcessingException e) {
                throw e;
            } catch (Exception e) {
                log.error("XML validation failed", e);

                // Daha detaylı hata mesajı oluştur
                StringBuilder errorMsg = new StringBuilder("XML doğrulama hatası: ");
                errorMsg.append(e.getMessage()).append("\n");
                errorMsg.append("Olası nedenler:\n");
                errorMsg.append("1. XML root element ismi veya namespace hatalı (beklenen: <Faktura xmlns=\"http://crd.gov.pl/wzor/2023/06/29/12648/\">)\n");
                errorMsg.append("2. Zorunlu alanlar eksik (Podmiot1, DaneIdentyfikacyjne, NIP, Fa, P_1, P_2)\n");
                errorMsg.append("3. XML encoding formatı hatalı\n");

                throw new XmlProcessingException(errorMsg.toString(), e);
            }

            // XML to Java (unmarshal)
            Faktura faktura;
            try {
                log.info("Unmarshalling XML to Java objects");
                JAXBContext context = JAXBContext.newInstance(Faktura.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                faktura = (Faktura) unmarshaller.unmarshal(new StringReader(xml));
                log.debug("XML unmarshalled successfully");
            } catch (JAXBException e) {
                log.error("Failed to unmarshal XML", e);
                throw new XmlProcessingException("Failed to unmarshal XML: " + e.getMessage(), e);
            }

            // Veri çıkarımı ve kayıt
            log.info("Extracting required data from XML");
            String nip = faktura.getPodmiot1().getDaneIdentyfikacyjne().getNIP();
            String p1 = faktura.getFa().getP1();
            String p2 = faktura.getFa().getP2();

            log.info("Creating and saving invoice entity to database");
            InvoiceEntity entity = new InvoiceEntity();
            entity.setNip(nip);
            entity.setP1(p1);
            entity.setP2(p2);
            invoiceRepository.save(entity);

            log.info("Invoice processing completed successfully");

        } catch (XmlProcessingException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during invoice processing", e);
            throw new XmlProcessingException("Unexpected error during invoice processing: " + e.getMessage(), e);
        }
    }
}
