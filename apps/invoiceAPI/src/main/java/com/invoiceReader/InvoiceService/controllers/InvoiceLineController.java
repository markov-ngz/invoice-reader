package com.invoiceReader.InvoiceService.controllers;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

import com.invoiceReader.InvoiceService.dtos.InvoiceCreateDTO;
import com.invoiceReader.InvoiceService.entities.Invoice;
import com.invoiceReader.InvoiceService.entities.InvoiceLine;
import com.invoiceReader.InvoiceService.services.InvoiceLineService;
import com.invoiceReader.InvoiceService.services.InvoiceService;
import com.invoiceReader.InvoiceService.dtos.InvoiceLineCreateDTO;
import com.invoiceReader.InvoiceService.dtos.InvoiceDTO;
import com.invoiceReader.InvoiceService.dtos.InvoiceLineDTO;

@RestController
@RequestMapping("/invoices")
public class InvoiceLineController {

    private final InvoiceLineService invoiceLineService ; 
    private final InvoiceService invoiceService ; 

    public InvoiceLineController( InvoiceService invoiceService,InvoiceLineService invoiceLineService){
        this.invoiceLineService = invoiceLineService ;
        this.invoiceService = invoiceService ; 

    }

    @GetMapping("/lines")
    public ResponseEntity<List<InvoiceLineDTO>> getAllInvoiceLines() {
        List<InvoiceLineDTO> invoiceLines = invoiceLineService.findAllInvoiceLines();
        return ResponseEntity.ok(invoiceLines);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<InvoiceLineDTO> getInvoiceLineById(@PathVariable int id) {
        InvoiceLineDTO invoiceLine = invoiceLineService.findInvoiceLineById(id);
        return ResponseEntity.ok(invoiceLine);
    }

    @GetMapping("/{invoiceId}/lines")
    public ResponseEntity<List<InvoiceLineDTO>> getInvoiceLineByInvoiceId(@PathVariable int invoiceId) {
        InvoiceDTO invoiceDTO = invoiceService.findInvoiceById(invoiceId) ; 
        List<InvoiceLineDTO> invoiceLines = invoiceLineService.findInvoiceLinesByInvoice(invoiceService.convertToEntity(invoiceDTO));
        return ResponseEntity.ok(invoiceLines);
    }

    @PostMapping("/{invoiceId}/lines")
    public ResponseEntity<List<InvoiceLineDTO>> createInvoiceLines(@PathVariable int invoiceId, @RequestBody List<InvoiceLineCreateDTO> invoiceLineCreateDTO) {
        InvoiceDTO invoiceDTO = invoiceService.findInvoiceById(invoiceId) ; 
        Invoice invoice = invoiceService.convertToEntity(invoiceDTO) ;

        List<InvoiceLineDTO> createdInvoiceLines = invoiceLineService.createInvoiceLines(invoiceLineCreateDTO, invoice) ; 

        return new ResponseEntity<List<InvoiceLineDTO>>(createdInvoiceLines, HttpStatus.CREATED);
    }
    

    @PutMapping("/{invoiceId}/lines/{id}")
    public ResponseEntity<InvoiceLineDTO> updateInvoice(@PathVariable int invoiceId, @PathVariable int id, @RequestBody InvoiceLineDTO invoiceLineDTO) {
        invoiceLineDTO.setInvoiceId(invoiceId); 
        InvoiceLineDTO updatedInvoice = invoiceLineService.updateInvoiceLine(invoiceLineDTO);
        return ResponseEntity.ok(updatedInvoice);
    }

    @DeleteMapping("/{invoiceId}/lines/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable int invoiceId,@PathVariable int id) {
        invoiceLineService.deleteInvoiceLine(id);
        return ResponseEntity.noContent().build();
    }
}
