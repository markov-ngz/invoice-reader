package com.invoiceReader.InvoiceService.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.invoiceReader.InvoiceService.services.S3Service;
import com.invoiceReader.InvoiceService.services.InvoiceService;
import com.invoiceReader.InvoiceService.dtos.InvoiceDTO;
import com.invoiceReader.InvoiceService.dtos.InvoiceCreateDTO;
import com.invoiceReader.InvoiceService.entities.Invoice;


@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService ; 
    private final S3Service fileService ; 

    public InvoiceController(InvoiceService invoiceService, S3Service fileService){
        this.invoiceService = invoiceService ;
        this.fileService = fileService ;  
    }

    @GetMapping
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices() {
        List<InvoiceDTO> invoices = invoiceService.findAllInvoices();
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable int id) {
        InvoiceDTO invoice = invoiceService.findInvoiceById(id);
        return ResponseEntity.ok(invoice);
    }

    @PostMapping
    public ResponseEntity<InvoiceDTO> createInvoice(@RequestBody InvoiceCreateDTO invoiceCreateDTO) {
        InvoiceDTO createdInvoice = invoiceService.createInvoice(invoiceCreateDTO);
        return new ResponseEntity<>(createdInvoice, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDTO> updateInvoice(@PathVariable int id, @RequestBody InvoiceDTO invoiceDTO) {
        invoiceDTO.setId(id); // Ensure ID in path matches the one in DTO
        InvoiceDTO updatedInvoice = invoiceService.updateInvoice(invoiceDTO);
        return ResponseEntity.ok(updatedInvoice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable int id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

    // @PostMapping(path="/create_from_file", consumes={MediaType.MULTIPART_FORM_DATA_VALUE})
    // public ResponseEntity<String> createInvoiceFromFile(@RequestParam("file") MultipartFile file){

    //     Map<String,String> metadata = new HashMap<String,String>() ; 

    //     metadata.put("x-amz-meta-invoiceid","142") ; 
        
    //     try {
            
    //         InvoiceDTO invoiceCreated = this.invoiceService.createInvoice(new InvoiceCreateDTO()) ; 
            
    //         metadata.put("x-amz-meta-invoiceid", String.valueOf(invoiceCreated.getId())) ; 

    //         fileService.uploadFile(file.getBytes(), file.getOriginalFilename(), file.getContentType(), metadata);
            
    //         return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(file.getOriginalFilename()) ;
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(file.getOriginalFilename()) ;
    //     }
    // }


    // @GetMapping("/search")
    // public ResponseEntity<List<InvoiceDTO>> searchInvoices(
    //         @RequestParam(required = false) String invoiceNumber,
    //         @RequestParam(required = false) String supplier) {
    //     List<InvoiceDTO> invoices = invoiceService.searchInvoices(invoiceNumber, supplier);
    //     return ResponseEntity.ok(invoices);
    // }


}
