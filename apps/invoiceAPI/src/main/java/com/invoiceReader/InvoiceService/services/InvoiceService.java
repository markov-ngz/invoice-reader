package com.invoiceReader.InvoiceService.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invoiceReader.InvoiceService.repositories.InvoiceRepository;

import com.invoiceReader.InvoiceService.services.InvoiceLineService;

import com.invoiceReader.InvoiceService.dtos.InvoiceDTO;
import com.invoiceReader.InvoiceService.dtos.InvoiceLineCreateDTO;
import com.invoiceReader.InvoiceService.dtos.InvoiceLineDTO;

import com.invoiceReader.InvoiceService.dtos.InvoiceCreateDTO;
import com.invoiceReader.InvoiceService.entities.Invoice;
import com.invoiceReader.InvoiceService.entities.InvoiceLine;
import com.invoiceReader.InvoiceService.exceptions.ResourceNotFoundException;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineService invoiceLineService ; 

    public InvoiceService(InvoiceRepository invoiceRepository, InvoiceLineService invoiceLineService) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceLineService = invoiceLineService;
    }

    public List<InvoiceDTO> findAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoices.stream()
                .map(invoice -> convertToDTO(invoice))
                .collect(Collectors.toList());
    }

    public InvoiceDTO findInvoiceById(int id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        return convertToDTO(invoice);
    }

    @Transactional
    public InvoiceDTO createInvoice(InvoiceCreateDTO invoiceCreateDTO) {

        Invoice invoice = convertToEntity(invoiceCreateDTO);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        List<InvoiceLineDTO> savedInvoiceLinesDTO = invoiceLineService.createInvoiceLines(invoiceCreateDTO.getInvoiceLines(), savedInvoice) ; 
        InvoiceDTO invoiceDTO = convertToDTO(savedInvoice) ; 
        invoiceDTO.setInvoiceLines(savedInvoiceLinesDTO);
        return invoiceDTO ; 
    }

    @Transactional
    public InvoiceDTO updateInvoice(InvoiceDTO invoiceDTO) {
        // Check if invoice exists
        invoiceRepository.findById(invoiceDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceDTO.getId()));
        
        Invoice invoice = convertToEntity(invoiceDTO);
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return convertToDTO(updatedInvoice);
    }

    @Transactional
    public void deleteInvoice(int id) {
        // Check if invoice exists
        invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        
        invoiceRepository.deleteById(id);
    }


    // DTO <-> Entity conversion methods
    private InvoiceDTO convertToDTO(Invoice invoice) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setInvoiceDate(invoice.getInvoiceDate());
        dto.setSupplier(invoice.getSupplier());
        dto.setSupplierAdress(invoice.getSupplierAdress());
        dto.setCustomerName(invoice.getCustomerName());
        dto.setCustomerAdress(invoice.getCustomerAdress());
        dto.setTotalAmount(invoice.getTotalAmount());
        
        // // Convert invoice lines
        // if (invoice.getInvoiceLines() != null) {
        //     List<InvoiceLineDTO> linesDTOs = invoice.getInvoiceLines().stream()
        //             .map( l -> invoiceLineService.convertToDTO(l))
        //             .collect(Collectors.toList());
        //     dto.setInvoiceLines(linesDTOs);
        // } else {
        //     dto.setInvoiceLines(new ArrayList<>());
        // }
        
        return dto;
    }

    private Invoice convertToEntity(InvoiceDTO dto) {
        Invoice entity = new Invoice();
        entity.setId(dto.getId());
        entity.setInvoiceNumber(dto.getInvoiceNumber());
        entity.setInvoiceDate(dto.getInvoiceDate());
        entity.setSupplier(dto.getSupplier());
        entity.setSupplierAdress(dto.getSupplierAdress());
        entity.setCustomerName(dto.getCustomerName());
        entity.setCustomerAdress(dto.getCustomerAdress());
        entity.setTotalAmount(dto.getTotalAmount());
        
        // Convert invoice lines
        if (dto.getInvoiceLines() != null) {
            Set<InvoiceLine> lines = new HashSet<>();
            for (InvoiceLineDTO lineDTO : dto.getInvoiceLines()) {
                InvoiceLine line = invoiceLineService.convertToEntity(lineDTO); 
                line.setInvoice(entity); // Establish bidirectional relationship
                lines.add(line);
            }
            entity.setInvoiceLines(lines);
        } else {
            entity.setInvoiceLines(new HashSet<>());
        }
        
        return entity;
    }
    // polymorphism
    private Invoice convertToEntity(InvoiceCreateDTO dto) {
        Invoice entity = new Invoice();
        entity.setInvoiceNumber(dto.getInvoiceNumber());
        entity.setInvoiceDate(dto.getInvoiceDate());
        entity.setSupplier(dto.getSupplier());
        entity.setSupplierAdress(dto.getSupplierAdress());
        entity.setCustomerName(dto.getCustomerName());
        entity.setCustomerAdress(dto.getCustomerAdress());
        entity.setTotalAmount(dto.getTotalAmount());
        
        // // Convert invoice lines
        // if (dto.getInvoiceLines() != null) {
        //     for (InvoiceLineCreateDTO lineDTO : dto.getInvoiceLines()) {
        //         InvoiceLine line = invoiceLineService.convertToEntity(lineDTO);
        //         entity.addInvoiceLine(line);
        //     }
        // } else {
        //     entity.setInvoiceLines(new HashSet<>());
        // }
        
        return entity;
    }

    // public List<InvoiceDTO> searchInvoices(String invoiceNumber, String supplier) {
    //     List<Invoice> invoices;
        
    //     // Simple search implementation - in a real application, you might want to use
    //     // Spring Data JPA Specifications or QueryDSL for more complex queries
    //     if (invoiceNumber != null && supplier != null) {
    //         invoices = invoiceRepository.findByInvoiceNumberContainingAndSupplierContaining(
    //                 invoiceNumber, supplier);
    //     } else if (invoiceNumber != null) {
    //         invoices = invoiceRepository.findByInvoiceNumberContaining(invoiceNumber);
    //     } else if (supplier != null) {
    //         invoices = invoiceRepository.findBySupplierContaining(supplier);
    //     } else {
    //         invoices = invoiceRepository.findAll();
    //     }
        
    //     return invoices.stream()
    //             .map(invoice -> convertToDTO(invoice))
    //             .collect(Collectors.toList());
    // }
}
