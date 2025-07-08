package com.example.invoiceapi.service;

import com.example.invoiceapi.exception.XmlProcessingException;
import com.example.invoiceapi.model.InvoiceEntity;
import com.example.invoiceapi.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    private String validBase64Xml;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        validBase64Xml = loadBase64XmlFromResource("/sample-invoice.xml");
    }

    @Test
    void processInvoice_WithValidXml_ShouldSaveInvoice() {
        // Test et
        invoiceService.processInvoice(validBase64Xml);

        // Doğrula
        verify(invoiceRepository, times(1)).save(any(InvoiceEntity.class));
    }

    @Test
    void processInvoice_WithEmptyInput_ShouldThrowException() {
        // Test et & Doğrula
        assertThrows(XmlProcessingException.class, () -> {
            invoiceService.processInvoice("");
        });

        verify(invoiceRepository, never()).save(any(InvoiceEntity.class));
    }

    @Test
    void processInvoice_WithInvalidBase64_ShouldThrowException() {
        // Test et & Doğrula
        assertThrows(XmlProcessingException.class, () -> {
            invoiceService.processInvoice("invalid-base64-string");
        });

        verify(invoiceRepository, never()).save(any(InvoiceEntity.class));
    }

    private String loadBase64XmlFromResource(String resourcePath) throws Exception {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            byte[] bytes = is.readAllBytes();
            String xmlContent = new String(bytes, StandardCharsets.UTF_8);
            return Base64.getEncoder().encodeToString(xmlContent.getBytes(StandardCharsets.UTF_8));
        }
    }
}
