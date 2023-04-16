package com.adatb.bookaround.controllers;

import com.adatb.bookaround.models.CustomerDetails;
import com.adatb.bookaround.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class InvoiceRestController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/invoices/download/{id}")
    public ResponseEntity<ByteArrayResource> downloadInvoiceById(@PathVariable Long id,
                                                                 @AuthenticationPrincipal CustomerDetails customerDetails) throws IOException {

        if (customerDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!customerService.doesInvoiceBelongToCustomer(id, customerDetails.getCustomerId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }


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
