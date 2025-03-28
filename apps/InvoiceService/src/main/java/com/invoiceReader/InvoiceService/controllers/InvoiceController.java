package com.invoiceReader.InvoiceService.controllers;

import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.invoiceReader.InvoiceService.services.InvoiceService;
import com.invoiceReader.InvoiceService.entities.Invoice;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService ; 
    public InvoiceController(InvoiceService invoiceService){
        this.invoiceService = invoiceService ; 
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices(){
        List<Invoice> invoices = this.invoiceService.getAllInvoices() ; 
        return ResponseEntity.ok(invoices) ; 
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable int id){
        Invoice invoice = this.invoiceService.getInvoiceById(id) ; 
        if(invoice == null){
            return  ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(invoice) ; 
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice){
        Invoice invoiceCreated = this.invoiceService.createInvoice(invoice) ; 
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(invoiceCreated) ; 
    }

    @PostMapping(path="/file", consumes={MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadInvoice(@RequestParam("file") MultipartFile file){
        
        String filename = file.getOriginalFilename() ; 

        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(filename) ; 
    }

    @PutMapping
    public ResponseEntity<Invoice> updateInvoice(@RequestBody Invoice invoice){
        Invoice invoiceUpdated = this.invoiceService.updateInvoice(invoice) ;
        return ResponseEntity.ok(invoiceUpdated) ; 
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> deleteInvoice(@PathVariable int id ){
        this.invoiceService.deleteInvoiceById(id);
        return new ResponseEntity<>(null, HttpStatus.OK) ; 
    } 
}
