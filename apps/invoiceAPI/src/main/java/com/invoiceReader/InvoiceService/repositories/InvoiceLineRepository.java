package com.invoiceReader.InvoiceService.repositories ;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoiceReader.InvoiceService.entities.InvoiceLine;


@Repository
public interface InvoiceLineRepository extends JpaRepository<InvoiceLine,Integer>{
    public Optional<InvoiceLine> findById(int id);
}