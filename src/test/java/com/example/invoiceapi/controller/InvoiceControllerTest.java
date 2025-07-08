package com.example.invoiceapi.controller;

import com.example.invoiceapi.dto.InvoiceRequest;
import com.example.invoiceapi.exception.XmlProcessingException;
import com.example.invoiceapi.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class InvoiceControllerTest {

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private InvoiceController invoiceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadInvoice_WithValidRequest_ReturnsCreated() {
        // Düzenle
        InvoiceRequest request = new InvoiceRequest();
        String base64xml = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPEZha3R1cmEgeG1sbnM9Imh0dHA6Ly9jcmQuZ292LnBsL3d6b3IvMjAyMy8wNi8yOS8xMjY0OC8iPgogIDxQb2RtaW90MT4KICAgIDxEYW5lSWRlbnR5ZmlrYWN5am5lPgogICAgICA8TklQPjEyMzQ1Njc4OTA8L05JUD4KICAgIDwvRGFuZUlkZW50eWZpa2FjeWpuZT4KICA8L1BvZG1pb3QxPgogIDxGYT4KICAgIDxQXzE+MjAyMy0wOC0zMTwvUF8xPgogICAgPFBfMj5GSzIwMjMvMDgvMzE8L1BfMj4KICA8L0ZhPgo8L0Zha3R1cmE+";
        request.setBase64xml(base64xml);

        // Test et
        ResponseEntity<?> response = invoiceController.uploadInvoice(request);

        // Doğrula
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(invoiceService, times(1)).processInvoice(base64xml);
    }

    @Test
    void uploadInvoice_WithEmptyRequest_ReturnsBadRequest() {
        // Düzenle
        InvoiceRequest request = new InvoiceRequest();

        // Test et
        ResponseEntity<?> response = invoiceController.uploadInvoice(request);

        // Doğrula
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(invoiceService, never()).processInvoice(anyString());
    }

    @Test
    void uploadInvoice_WithProcessingException_ReturnsBadRequest() {
        // Düzenle
        InvoiceRequest request = new InvoiceRequest();
        request.setBase64xml("invalid-base64");

        doThrow(new XmlProcessingException("Invalid XML")).when(invoiceService).processInvoice(anyString());

        // Test et
        ResponseEntity<?> response = invoiceController.uploadInvoice(request);

        // Doğrula
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void uploadInvoice_WithGenericException_ReturnsInternalServerError() {
        // Düzenle
        InvoiceRequest request = new InvoiceRequest();
        request.setBase64xml("valid-base64");

        doThrow(new RuntimeException("Database error")).when(invoiceService).processInvoice(anyString());

        // Test et
        ResponseEntity<?> response = invoiceController.uploadInvoice(request);

        // Doğrula
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
