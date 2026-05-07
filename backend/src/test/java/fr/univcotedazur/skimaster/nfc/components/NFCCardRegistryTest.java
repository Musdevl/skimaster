package fr.univcotedazur.skimaster.nfc.components;

import fr.univcotedazur.skimaster.cashier.connectors.interfaces.Bank;
import fr.univcotedazur.skimaster.cashier.exceptions.PaymentException;
import fr.univcotedazur.skimaster.cashier.interfaces.Payment;
import fr.univcotedazur.skimaster.customer.entities.Category;
import fr.univcotedazur.skimaster.customer.entities.Customer;
import fr.univcotedazur.skimaster.customer.entities.Item;
import fr.univcotedazur.skimaster.customer.entities.Plan;
import fr.univcotedazur.skimaster.customer.repositories.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class NFCCardRegistryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private Payment cashier;


    @Autowired
    private NFCCardRegistry nfcCardRegistry;

    @MockitoBean
    private Bank bankMock;

    private Long nfcCardId;

    private Set<Item> items;
    private Customer john;
    private Customer pat;

    @BeforeEach
    void setUpContext() {
        items = new HashSet<>();
        items.add(new Item(Plan.BASIC_PLAN, 3));
        // Customers
        john = new Customer("john", "1234896983", Category.ADULT);  // ends with the secret YES Card number
        john.setCart(items);
        customerRepository.save(john);
        pat  = new Customer("pat", "1234567890", Category.ADULT);   // should be rejected by the payment service
        pat.setCart(items);
        customerRepository.save(pat);
        // Mocking the bank proxy
        when(bankMock.pay(eq(john),  anyDouble())).thenReturn(Optional.of("playReceiptOKId"));
        when(bankMock.pay(eq(pat),  anyDouble())).thenReturn(Optional.empty());
    }

    @AfterEach
    void cleanUpContext() {
        customerRepository.deleteAll();
    }

    @Test
    void unknownNFCCard() {
        assertFalse(nfcCardRegistry.findById(324L).isPresent());
    }

    @Test
    void findAllByPlan() throws PaymentException {
        assertEquals(0, nfcCardRegistry.findAllByPlan(Plan.BASIC_PLAN).size());
        cashier.payOrderFromCart(john, 30.0);
        assertEquals(3, nfcCardRegistry.findAllByPlan(Plan.BASIC_PLAN).size());
    }

    @Test
    void findById() throws PaymentException {
        cashier.payOrderFromCart(john, 30.0);
        nfcCardId = john.getNfcCards().iterator().next().getId();
        assertTrue(nfcCardRegistry.findById(nfcCardId).isPresent());
    }

    @Test
    void findByCustomer() throws PaymentException{
        cashier.payOrderFromCart(john, 30.0);
        assertEquals(3, nfcCardRegistry.findAllByCustomer(john).size());
    }

    @Test
    void findAll() throws PaymentException {
        assertEquals(0, nfcCardRegistry.findAll().size());
        cashier.payOrderFromCart(john, 30.0);
        assertEquals(3, nfcCardRegistry.findAll().size());
    }

    @Test
    void register() {
        assert(true);
    }

}