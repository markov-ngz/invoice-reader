package com.invoiceReader.InvoiceService.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.invoiceReader.InvoiceService.entities.Invoice;
import com.invoiceReader.InvoiceService.repositories.InvoiceLineRepository;

import com.invoiceReader.InvoiceService.dtos.InvoiceDTO;

@Service
public class InvoiceLineService {
    private final InvoiceLineRepository invoiceLineRepository ; 

    public InvoiceLineService(InvoiceLineRepository invoiceLineRepository) {
        this.invoiceLineRepository = invoiceLineRepository ; 
    }
}
