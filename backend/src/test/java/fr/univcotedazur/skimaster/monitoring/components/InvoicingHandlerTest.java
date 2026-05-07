package fr.univcotedazur.skimaster.monitoring.components;

import fr.univcotedazur.skimaster.cashier.components.Cashier;
import fr.univcotedazur.skimaster.cashier.exceptions.PaymentException;
import fr.univcotedazur.skimaster.customer.entities.Category;
import fr.univcotedazur.skimaster.customer.entities.Customer;
import fr.univcotedazur.skimaster.customer.entities.Plan;
import fr.univcotedazur.skimaster.customer.exceptions.CustomerIdNotFoundException;
import fr.univcotedazur.skimaster.customer.repositories.CustomerRepository;
import fr.univcotedazur.skimaster.monitoring.connectors.interfaces.Gate;
import fr.univcotedazur.skimaster.monitoring.dto.InvoicingResultDTO;
import fr.univcotedazur.skimaster.monitoring.entities.GateConnection;
import fr.univcotedazur.skimaster.monitoring.repositories.GateConnectionRepository;
import fr.univcotedazur.skimaster.nfc.dto.NFCCardDTO;
import fr.univcotedazur.skimaster.customer.entities.Sound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoicingHandlerTest {

    @Mock GateConnectionRepository gateConnectionRepository;
    @Mock CustomerRepository customerRepository;
    @Mock Cashier cashier;
    @Mock Gate gateProxy;

    @InjectMocks InvoicingHandler invoicingHandler;

    private GateConnection gate1;
    private GateConnection gate2;
    private Customer customer1;
    private Customer customer2;

    private static final NFCCardDTO SUPER_CARD_C1 = new NFCCardDTO((long) 1, (long) 1, Sound.LOW_SOUND, Plan.SUPER_CARD);
    private static final NFCCardDTO SUPER_CARD_C2 = new NFCCardDTO((long) 2, (long) 2, Sound.LOW_SOUND, Plan.SUPER_CARD);
    private static final NFCCardDTO BASIC_CARD_C1 = new NFCCardDTO((long) 3, (long) 1, Sound.LOW_SOUND, Plan.BASIC_PLAN);

    @BeforeEach
    void setUp() {
        gate1 = mock(GateConnection.class);
        gate2 = mock(GateConnection.class);

        customer1 = new Customer("alice", "1111", Category.ADULT);
        customer2 = new Customer("bob", "2222", Category.ADULT);
    }

    // No gates
    @Test
    void invoiceSuperCardsForDay_noGates_returnsEmptyResult() throws Exception {
        when(gateConnectionRepository.findAll()).thenReturn(List.of());

        InvoicingResultDTO result = invoicingHandler.invoiceSuperCardsForDay(LocalDate.now());

        assertEquals(0, result.totalCardsInvoiced());
        assertEquals(0.0, result.totalAmount());
        assertTrue(result.invoices().isEmpty());
        verifyNoInteractions(cashier);
    }

    // Gate returns empty list
    @Test
    void invoiceSuperCardsForDay_gateReturnsEmpty_returnsEmptyResult() throws Exception {
        when(gate1.getURI()).thenReturn("http://gate1:8080");
        when(gateConnectionRepository.findAll()).thenReturn(List.of(gate1));
        when(gateProxy.requestSuperCards("http://gate1:8080")).thenReturn(Optional.empty());

        InvoicingResultDTO result = invoicingHandler.invoiceSuperCardsForDay(LocalDate.now());

        assertEquals(0, result.totalCardsInvoiced());
        verifyNoInteractions(cashier);
    }

    // Non-SUPER_CARD cards
    @Test
    void invoiceSuperCardsForDay_onlyNonSuperCards_returnsEmptyResult() throws Exception {
        when(gate1.getURI()).thenReturn("http://gate1:8080");
        when(gateConnectionRepository.findAll()).thenReturn(List.of(gate1));
        when(gateProxy.requestSuperCards("http://gate1:8080")).thenReturn(Optional.of(List.of(BASIC_CARD_C1)));

        InvoicingResultDTO result = invoicingHandler.invoiceSuperCardsForDay(LocalDate.now());

        assertEquals(0, result.totalCardsInvoiced());
        verifyNoInteractions(cashier);
    }

    // One gate, one customer with one super card
    @Test
    void invoiceSuperCardsForDay_oneCustomerOneSuperCard_invoicesCorrectly() throws Exception {
        when(gate1.getURI()).thenReturn("http://gate1:8080");
        when(gateConnectionRepository.findAll()).thenReturn(List.of(gate1));
        when(gateProxy.requestSuperCards("http://gate1:8080")).thenReturn(Optional.of(List.of(SUPER_CARD_C1)));
        when(customerRepository.findById((long) 1)).thenReturn(Optional.of(customer1));

        InvoicingResultDTO result = invoicingHandler.invoiceSuperCardsForDay(LocalDate.now());

        assertEquals(1, result.totalCardsInvoiced());
        assertEquals(Plan.SUPER_CARD_PASSAGE.getPrice(), result.totalAmount());
        assertEquals(1, result.invoices().size());
        verify(cashier).payOrderFromCart(eq(customer1), anyDouble());
    }

    // Two gates, same customer, merged
    @Test
    void invoiceSuperCardsForDay_twoGatesSameCustomer_countsMerged() throws Exception {
        when(gate1.getURI()).thenReturn("http://gate1:8080");
        when(gate2.getURI()).thenReturn("http://gate2:8080");
        when(gateConnectionRepository.findAll()).thenReturn(List.of(gate1, gate2));
        when(gateProxy.requestSuperCards("http://gate1:8080")).thenReturn(Optional.of(List.of(SUPER_CARD_C1)));
        when(gateProxy.requestSuperCards("http://gate2:8080")).thenReturn(Optional.of(List.of(SUPER_CARD_C1)));
        when(customerRepository.findById((long) 1)).thenReturn(Optional.of(customer1));

        InvoicingResultDTO result = invoicingHandler.invoiceSuperCardsForDay(LocalDate.now());

        assertEquals(1, result.totalCardsInvoiced());
        assertEquals(Plan.SUPER_CARD_PASSAGE.getPrice() * 2, result.totalAmount());
        assertEquals(2, result.invoices().get(0).quantity());
    }

    // Two customers with two gates
    @Test
    void invoiceSuperCardsForDay_twoCustomersTwoGates_bothInvoiced() throws Exception {
        when(gate1.getURI()).thenReturn("http://gate1:8080");
        when(gate2.getURI()).thenReturn("http://gate2:8080");
        when(gateConnectionRepository.findAll()).thenReturn(List.of(gate1, gate2));
        when(gateProxy.requestSuperCards("http://gate1:8080")).thenReturn(Optional.of(List.of(SUPER_CARD_C1)));
        when(gateProxy.requestSuperCards("http://gate2:8080")).thenReturn(Optional.of(List.of(SUPER_CARD_C2)));
        when(customerRepository.findById((long) 1)).thenReturn(Optional.of(customer1));
        when(customerRepository.findById((long) 2)).thenReturn(Optional.of(customer2));

        InvoicingResultDTO result = invoicingHandler.invoiceSuperCardsForDay(LocalDate.now());

        assertEquals(2, result.totalCardsInvoiced());
        verify(cashier, times(2)).payOrderFromCart(any(Customer.class), anyDouble());
    }

    // Unknown customer
    @Test
    void invoiceSuperCardsForDay_unknownCustomer_throwsCustomerIdNotFoundException() {
        when(gate1.getURI()).thenReturn("http://gate1:8080");
        when(gateConnectionRepository.findAll()).thenReturn(List.of(gate1));
        when(gateProxy.requestSuperCards("http://gate1:8080")).thenReturn(Optional.of(List.of(SUPER_CARD_C1)));
        when(customerRepository.findById((long) 1)).thenReturn(Optional.empty());

        assertThrows(CustomerIdNotFoundException.class,
                () -> invoicingHandler.invoiceSuperCardsForDay(LocalDate.now()));
    }

    // Payment failure
    @Test
    void invoiceSuperCardsForDay_paymentFails_throwsPaymentException() throws Exception {
        when(gate1.getURI()).thenReturn("http://gate1:8080");
        when(gateConnectionRepository.findAll()).thenReturn(List.of(gate1));
        when(gateProxy.requestSuperCards("http://gate1:8080")).thenReturn(Optional.of(List.of(SUPER_CARD_C1)));
        when(customerRepository.findById((long) 1)).thenReturn(Optional.of(customer1));
        doThrow(new PaymentException()).when(cashier).payOrderFromCart(any(), anyDouble());

        assertThrows(PaymentException.class,
                () -> invoicingHandler.invoiceSuperCardsForDay(LocalDate.now()));
    }
}