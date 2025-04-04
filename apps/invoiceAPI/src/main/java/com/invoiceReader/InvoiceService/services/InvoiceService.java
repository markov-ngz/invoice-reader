package com.invoiceReader.InvoiceService.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.invoiceReader.InvoiceService.repositories.InvoiceRepository;
import com.invoiceReader.InvoiceService.entities.Invoice;

@Service
public class InvoiceService {
    private final InvoiceRepository InvoiceRepository ; 

    public InvoiceService(InvoiceRepository InvoiceRepository) {
        this.InvoiceRepository = InvoiceRepository ; 
    }


    public List<Invoice> getAllInvoices(){
        return InvoiceRepository.findAll() ; 
    }

    
    public Invoice getInvoiceById(int id){
        return InvoiceRepository.findById(id).orElse(null) ; 
    }


    public Invoice createInvoice(Invoice Invoice){
        Invoice savedInvoice = InvoiceRepository.save(Invoice) ; 
        return savedInvoice ; 
    }

    public Invoice updateInvoice(Invoice Invoice){
        Invoice updatedInvoice = InvoiceRepository.save(Invoice); 
        return updatedInvoice ; 
    }

    public void deleteInvoice(Invoice Invoice){
        InvoiceRepository.delete(Invoice);
    }

    public void deleteInvoiceById(int id ){
        InvoiceRepository.deleteById(id);
    }
}
