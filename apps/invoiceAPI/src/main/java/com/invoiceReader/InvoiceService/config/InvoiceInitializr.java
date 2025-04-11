package com.invoiceReader.InvoiceService.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.invoiceReader.InvoiceService.dtos.InvoiceCreateDTO;
import com.invoiceReader.InvoiceService.dtos.InvoiceDTO;
import com.invoiceReader.InvoiceService.dtos.InvoiceLineCreateDTO;
import com.invoiceReader.InvoiceService.services.InvoiceLineService;
import com.invoiceReader.InvoiceService.services.InvoiceService;

@Configuration
public class InvoiceInitializr {
    @Bean
    CommandLineRunner initializeInvoice(InvoiceService invoiceService , InvoiceLineService invoiceLineService){
        InvoiceCreateDTO invoice = new InvoiceCreateDTO() ; 
        invoice.setSupplier("CONTURA");

        InvoiceCreateDTO invoice2 = new InvoiceCreateDTO() ; 
        invoice.setSupplier("TERMATECH");

        InvoiceLineCreateDTO invoiceLine = new InvoiceLineCreateDTO() ;
        invoiceLine.setDescription("SONO RAO 300 ");
        InvoiceLineCreateDTO invoiceLine2 = new InvoiceLineCreateDTO() ;
        invoiceLine2.setDescription("BLAO POTO ");
        return args -> {
            InvoiceDTO invoiceDTO = invoiceService.createInvoice(invoice) ; 
            InvoiceDTO invoiceDTO2 = invoiceService.createInvoice(invoice2) ; 
            invoiceLineService.createInvoiceLine(invoiceLine, invoiceService.convertToEntity(invoiceDTO)) ; 
            invoiceLineService.createInvoiceLine(invoiceLine2, invoiceService.convertToEntity(invoiceDTO)) ; 
        } ;
    }
}
