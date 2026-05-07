package fr.univcotedazur.skimaster.cashier.connectors.externaldto;

// External DTO (Data Transfert Object) to POST payment request to the external Bank system
public record PaymentRequestDTO (String creditCard, double amount)
{
}
