package com.invoiceReader.InvoiceService.repositories ;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoiceReader.InvoiceService.entities.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice,Integer>{
    public Optional<Invoice> findById(int id);
}