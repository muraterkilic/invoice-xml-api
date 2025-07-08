package com.example.invoiceapi.util;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Base64Helper {

    /**
     * Safely decodes a Base64 string that might contain invalid characters.
     * 
     * @param input The Base64 string to decode
     * @return The decoded string
     * @throws IllegalArgumentException if the string cannot be decoded
     */
    public static String safeBase64Decode(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Base64 input is null or empty");
        }

        // Temizleme işlemleri
        String cleanInput = input.trim();

        // Yeni satır, tab ve boşlukları kaldır
        cleanInput = cleanInput.replaceAll("[\\s\\r\\n\\t]", "");

        // Elipsis işaretini kaldır
        cleanInput = cleanInput.replace("...", "");

        // Tırnak işaretlerini kaldır
        if (cleanInput.startsWith("\"") && cleanInput.endsWith("\"")) {
            cleanInput = cleanInput.substring(1, cleanInput.length() - 1);
        }

        // Üç nokta işaretini kaldır
        if (cleanInput.endsWith("…")) {
            cleanInput = cleanInput.substring(0, cleanInput.length() - 1);
        }

        // Eğer base64 uzunluğu 4'ün katı değilse, = ekleyerek düzelt
        int remainder = cleanInput.length() % 4;
        if (remainder > 0) {
            cleanInput = cleanInput + "=".repeat(4 - remainder);
            log.debug("Added {} padding characters to make Base64 length a multiple of 4", 4 - remainder);
        }

        // İlk 20 karakteri ve son 20 karakteri loglayalım
        if (cleanInput.length() > 40) {
            log.debug("Base64 after cleaning: {}...{} (length: {})", 
                    cleanInput.substring(0, 20), 
                    cleanInput.substring(cleanInput.length() - 20), 
                    cleanInput.length());
        } else {
            log.debug("Base64 after cleaning: {} (length: {})", cleanInput, cleanInput.length());
        }

        // Base64 alfabesinde olmayan karakterleri kontrol et
        StringBuilder invalidChars = new StringBuilder();
        for (int i = 0; i < cleanInput.length(); i++) {
            char c = cleanInput.charAt(i);
            if (c != '=' && c != '+' && c != '/' && c != '-' && c != '_' && !Character.isLetterOrDigit(c)) {
                invalidChars.append("Pos ").append(i).append(": '").append(c)
                           .append("' (hex: ").append(Integer.toHexString((int)c))
                           .append("), ");
            }
        }

        if (invalidChars.length() > 0) {
            log.warn("Found invalid Base64 characters: {}", invalidChars);
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(cleanInput);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            try {
                byte[] decoded = Base64.getUrlDecoder().decode(cleanInput);
                return new String(decoded, StandardCharsets.UTF_8);
            } catch (IllegalArgumentException e2) {
                throw new IllegalArgumentException("Base64 input could not be decoded with either standard or URL-safe decoder", e);
            }
        }
    }

    /**
     * Encodes a string to Base64.
     * 
     * @param input The string to encode
     * @return The Base64 encoded string
     */
    public static String encode(String input) {
        if (input == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate a valid example XML and its Base64 encoded form.
     * 
     * @return A map containing the XML and its Base64 encoded form
     */
    public static Map<String, String> generateExample() {
        String exampleXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<Faktura xmlns=\"http://crd.gov.pl/wzor/2023/06/29/12648/\">\n"
                + "  <Podmiot1>\n"
                + "    <DaneIdentyfikacyjne>\n"
                + "      <NIP>1234567890</NIP>\n"
                + "    </DaneIdentyfikacyjne>\n"
                + "  </Podmiot1>\n"
                + "  <Fa>\n"
                + "    <P_1>2023-08-31</P_1>\n"
                + "    <P_2>FK2023/08/31</P_2>\n"
                + "  </Fa>\n"
                + "</Faktura>";

        String base64Encoded = encode(exampleXml);

        Map<String, String> result = new java.util.HashMap<>();
        result.put("xml", exampleXml);
        result.put("base64", base64Encoded);

        return result;
    }
}
