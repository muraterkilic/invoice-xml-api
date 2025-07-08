package com.example.invoiceapi.controller;

import com.example.invoiceapi.dto.InvoiceRequest;
import com.example.invoiceapi.exception.XmlProcessingException;
import com.example.invoiceapi.service.InvoiceService;
import com.example.invoiceapi.util.Base64Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Slf4j
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<?> uploadInvoice(@RequestBody InvoiceRequest request) {
        try {
            if (request == null || request.getBase64xml() == null || request.getBase64xml().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "base64xml alanı gerekli ve boş olamaz");

                // Örnek XML ve Base64 ekleyelim
                Map<String, String> example = Base64Helper.generateExample();
                response.put("örnek_xml", example.get("xml"));
                response.put("örnek_base64", example.get("base64"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            invoiceService.processInvoice(request.getBase64xml());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Fatura başarıyla kaydedildi"));
        } catch (XmlProcessingException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            response.put("type", "XML_PROCESSING_ERROR");

            // Gönderdikleri XML'i decode edip gösterelim
            try {
                // İlk olarak, gelen veriyi temizleyelim
                String cleanBase64 = request.getBase64xml().trim();

                // Yeni satır ve boşlukları temizle
                cleanBase64 = cleanBase64.replaceAll("[\\s\\r\\n\\t]", "");

                // Elipsis (...) karakterlerini temizle
                cleanBase64 = cleanBase64.replace("...", "");

                // Tırnak işaretlerini kaldır
                if (cleanBase64.startsWith("\"") && cleanBase64.endsWith("\"")) {
                    cleanBase64 = cleanBase64.substring(1, cleanBase64.length() - 1);
                }

                // Base64 ile çözmeyi dene
                byte[] decodedBytes;
                try {
                    decodedBytes = Base64.getDecoder().decode(cleanBase64);
                } catch (Exception decodeE) {
                    // URL-safe Base64 ile çözmeyi dene
                    decodedBytes = Base64.getUrlDecoder().decode(cleanBase64);
                }

                String xmlContent = new String(decodedBytes, StandardCharsets.UTF_8);
                response.put("gönderilen_xml", xmlContent);

                // Ayrıca gönderilen veriyi de ekleyelim
                response.put("gönderilen_veri", request.getBase64xml());
            } catch (Exception decodeEx) {
                response.put("decode_hatası", decodeEx.getMessage());
                response.put("gönderilen_veri", request.getBase64xml());

                // İlk 10 karakteri loglayalım
                String firstChars = request.getBase64xml().length() > 10 ? 
                    request.getBase64xml().substring(0, 10) : request.getBase64xml();
                response.put("gönderilen_veri_ilk_10", firstChars);

                // Karakter analizi için /api/diagnostic/analyze-base64 endpointini önerin
                response.put("ipucu", "Base64 verilerinizi daha detaylı analiz etmek için /api/diagnostic/analyze-base64 endpoint'ini kullanabilirsiniz.");
            }

            // Doğru örnek XML ekleyelim
            response.put("örnek_xml", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Faktura xmlns=\"http://crd.gov.pl/wzor/2023/06/29/12648/\">\n  <Podmiot1>\n    <DaneIdentyfikacyjne>\n      <NIP>1234567890</NIP>\n    </DaneIdentyfikacyjne>\n  </Podmiot1>\n  <Fa>\n    <P_1>2023-08-31</P_1>\n    <P_2>FK2023/08/31</P_2>\n  </Fa>\n</Faktura>");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Beklenmedik bir hata oluştu: " + e.getMessage());
            response.put("type", "INTERNAL_SERVER_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
