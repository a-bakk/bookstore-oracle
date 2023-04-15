package com.adatb.bookaround.controllers;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class InvoiceRestController {
    @GetMapping("/invoices/download/{id}")
    public ResponseEntity<ByteArrayResource> downloadInvoiceById(@PathVariable Long id) throws IOException {

        // TODO Check if user is authenticated & invoice belongs to them (db side function)

        ClassPathResource file = new ClassPathResource("static/invoices/invoice_" + id + ".pdf");

        var inBytes = StreamUtils.copyToByteArray(file.getInputStream());
        ByteArrayResource resource = new ByteArrayResource(inBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getFilename())
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(inBytes.length)
                .body(resource);
    }
}
