package fr.univcotedazur.skimaster.monitoring.dto;

import fr.univcotedazur.skimaster.monitoring.dto.nfc.SuperCardInvoiceDTO;

import java.util.List;

public record InvoicingResultDTO(
        int totalCardsInvoiced,
        double totalAmount,
        List<SuperCardInvoiceDTO> invoices
) {}
