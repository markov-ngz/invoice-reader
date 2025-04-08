package com.invoiceReader.InvoiceService.services;

import java.util.List;
import java.util.stream.Collectors;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.invoiceReader.InvoiceService.entities.Invoice;
import com.invoiceReader.InvoiceService.entities.InvoiceLine;

import com.invoiceReader.InvoiceService.repositories.InvoiceLineRepository;

import com.invoiceReader.InvoiceService.exceptions.ResourceNotFoundException;

import com.invoiceReader.InvoiceService.dtos.InvoiceCreateDTO;
import com.invoiceReader.InvoiceService.dtos.InvoiceLineDTO;
import com.invoiceReader.InvoiceService.dtos.InvoiceLineCreateDTO;
import com.invoiceReader.InvoiceService.dtos.InvoiceLineDTO;

@Service
public class InvoiceLineService {
    private final InvoiceLineRepository invoiceLineRepository ; 

    public InvoiceLineService(InvoiceLineRepository invoiceLineRepository) {
        this.invoiceLineRepository = invoiceLineRepository ; 
    }

    
    public List<InvoiceLineDTO> findAllInvoiceLines() {
        List<InvoiceLine> invoiceLines = invoiceLineRepository.findAll();
        return invoiceLines.stream()
                .map(line -> convertToDTO(line))
                .collect(Collectors.toList());
    }

    public InvoiceLineDTO findInvoiceLineById(int id) {
        InvoiceLine invoiceLine = invoiceLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice Line not found with id: " + id));
        return convertToDTO(invoiceLine);
    }

    @Transactional
    public InvoiceLineDTO createInvoiceLine(InvoiceLineCreateDTO invoiceLineCreateDTO, Invoice invoice) {

        InvoiceLine invoiceLine = convertToEntity(invoiceLineCreateDTO);
        invoiceLine.setInvoice(invoice);
        InvoiceLine savedInvoiceLine = invoiceLineRepository.save(invoiceLine);
        return convertToDTO(savedInvoiceLine);
    }

    @Transactional
    public List<InvoiceLineDTO> createInvoiceLines(List<InvoiceLineCreateDTO> invoiceLinesCreateDTO, Invoice invoice) {

        return invoiceLinesCreateDTO.stream().map(i -> createInvoiceLine(i, invoice)).collect(Collectors.toList());
    }

    @Transactional
    public InvoiceLineDTO updateInvoiceLine(InvoiceLineDTO invoiceLineDTO) {
        // Check if invoiceLine exists
        invoiceLineRepository.findById(invoiceLineDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice Line not found with id: " + invoiceLineDTO.getId()));
        
        InvoiceLine invoiceLine = convertToEntity(invoiceLineDTO);
        InvoiceLine updatedInvoiceLine = invoiceLineRepository.save(invoiceLine);
        return convertToDTO(updatedInvoiceLine);
    }

    @Transactional
    public void deleteInvoiceLine(int id) {
        // Check if invoiceLine exists
        invoiceLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice Line not found with id: " + id));
        
        invoiceLineRepository.deleteById(id);
    }

    public InvoiceLineDTO convertToDTO(InvoiceLine line){
        InvoiceLineDTO lineDTO = new InvoiceLineDTO() ; 
        lineDTO.setId(line.getId());
        lineDTO.setDescription(line.getDescription());
        lineDTO.setQuantity(line.getQuantity());
        lineDTO.setUnitPrice(line.getUnitPrice());
        lineDTO.setTax(line.getTax());
        lineDTO.setAmount(line.getAmount());
        lineDTO.setInvoiceId(line.getInvoice().getId());
        return lineDTO ; 
    }

    public InvoiceLine convertToEntity(InvoiceLineCreateDTO lineDTO){

        InvoiceLine line = new InvoiceLine();
        line.setDescription(lineDTO.getDescription());
        line.setQuantity(lineDTO.getQuantity());
        line.setUnitPrice(lineDTO.getUnitPrice());
        line.setTax(lineDTO.getTax());
        line.setAmount(lineDTO.getAmount());

        return line ; 
    }
    public InvoiceLine convertToEntity(InvoiceLineDTO lineDTO){
        InvoiceLine line = new InvoiceLine();
        line.setId(lineDTO.getId());
        line.setDescription(lineDTO.getDescription());
        line.setQuantity(lineDTO.getQuantity());
        line.setUnitPrice(lineDTO.getUnitPrice());
        line.setTax(lineDTO.getTax());
        line.setAmount(lineDTO.getAmount());

        return line ; 
    }
}
