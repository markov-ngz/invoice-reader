package com.invoiceReader.InvoiceService.repositories ;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoiceReader.InvoiceService.entities.Invoice;
import com.invoiceReader.InvoiceService.entities.InvoiceLine;


@Repository
public interface InvoiceLineRepository extends JpaRepository<InvoiceLine,Integer>{
    public Optional<InvoiceLine> findById(int id);
    public Optional<List<InvoiceLine>> findByInvoice(Invoice invoice) ;
}