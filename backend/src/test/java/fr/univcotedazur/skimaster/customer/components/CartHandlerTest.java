package fr.univcotedazur.skimaster.customer.components;

import fr.univcotedazur.skimaster.cashier.exceptions.PaymentException;
import fr.univcotedazur.skimaster.cashier.interfaces.Payment;
import fr.univcotedazur.skimaster.customer.entities.Category;
import fr.univcotedazur.skimaster.customer.entities.Customer;
import fr.univcotedazur.skimaster.customer.entities.Item;
import fr.univcotedazur.skimaster.customer.entities.Plan;
import fr.univcotedazur.skimaster.customer.exceptions.EmptyCartException;
import fr.univcotedazur.skimaster.customer.exceptions.NegativeQuantityException;
import fr.univcotedazur.skimaster.customer.interfaces.CustomerFinder;
import fr.univcotedazur.skimaster.order.entities.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartHandlerTest {

    @Mock
    private Payment payment;

    @Mock
    private CustomerFinder customerFinder;

    private CartHandler cartHandler;
    private Customer alice;
    private final Long ALICE_ID = (long) 1;

    @BeforeEach
    void setUp() {
        cartHandler = new CartHandler(payment, customerFinder);
        alice = new Customer("Alice", "1234", Category.ADULT);
    }

    // update

    @Test
    void update_addNewItem_Success() throws Exception {
        when(customerFinder.retrieveCustomer(ALICE_ID)).thenReturn(alice);
        Item newItem = new Item(Plan.BASIC_PLAN, 2);

        Item result = cartHandler.update(ALICE_ID, newItem);

        assertEquals(2, result.getQuantity());
        assertTrue(alice.getCart().contains(result));
    }

    @Test
    void update_existingItem_IncrementsQuantity() throws Exception {
        alice.getCart().add(new Item(Plan.BASIC_PLAN, 2));
        when(customerFinder.retrieveCustomer(ALICE_ID)).thenReturn(alice);

        Item update = new Item(Plan.BASIC_PLAN, 3);
        Item result = cartHandler.update(ALICE_ID, update);

        assertEquals(5, result.getQuantity());
        assertEquals(1, alice.getCart().size());
    }

    @Test
    void update_toZeroQuantity_RemovesItem() throws Exception {
        alice.getCart().add(new Item(Plan.BASIC_PLAN, 2));
        when(customerFinder.retrieveCustomer(ALICE_ID)).thenReturn(alice);

        Item update = new Item(Plan.BASIC_PLAN, -2);
        Item result = cartHandler.update(ALICE_ID, update);

        assertEquals(0, result.getQuantity());
        assertTrue(alice.getCart().isEmpty());
    }

    @Test
    void update_NegativeQuantity_ThrowsException() throws Exception {
        alice.getCart().add(new Item(Plan.BASIC_PLAN, 2));
        when(customerFinder.retrieveCustomer(ALICE_ID)).thenReturn(alice);

        Item update = new Item(Plan.BASIC_PLAN, -5);

        assertThrows(NegativeQuantityException.class, () -> 
            cartHandler.update(ALICE_ID, update)
        );
    }

    // cartContent & cartPrice

    @Test
    void cartContent_returnsItems() throws Exception {
        alice.getCart().add(new Item(Plan.BASIC_PLAN, 1));
        when(customerFinder.retrieveCustomer(ALICE_ID)).thenReturn(alice);

        assertEquals(1, cartHandler.cartContent(ALICE_ID).size());
    }

    @Test
    void cartPrice_CalculatesCorrectly() throws Exception {
        alice.getCart().add(new Item(Plan.BASIC_PLAN, 2));
        alice.getCart().add(new Item(Plan.BEGINNER_PASS, 1));
        when(customerFinder.retrieveCustomer(ALICE_ID)).thenReturn(alice);

        double expectedPrice = (2 * Plan.BASIC_PLAN.getPrice()) + (1 * Plan.BEGINNER_PASS.getPrice());
        assertEquals(expectedPrice, cartHandler.cartPrice(ALICE_ID));
    }

    // validate

    @Test
    void validate_Success_ClearsCart() throws Exception {
        alice.getCart().add(new Item(Plan.BASIC_PLAN, 1));
        when(customerFinder.retrieveCustomer(ALICE_ID)).thenReturn(alice);
        Order mockOrder = mock(Order.class);
        when(payment.payOrderFromCart(eq(alice), anyDouble())).thenReturn(mockOrder);

        Order result = cartHandler.validate(ALICE_ID);

        assertNotNull(result);
        assertTrue(alice.getCart().isEmpty());
        verify(payment).payOrderFromCart(eq(alice), anyDouble());
    }

    @Test
    void validate_EmptyCart_ThrowsException() throws Exception {
        when(customerFinder.retrieveCustomer(ALICE_ID)).thenReturn(alice);

        assertThrows(EmptyCartException.class, () -> cartHandler.validate(ALICE_ID));
    }

    @Test
    void validate_PaymentFails_PropagatesException() throws Exception {
        alice.getCart().add(new Item(Plan.BASIC_PLAN, 1));
        when(customerFinder.retrieveCustomer(ALICE_ID)).thenReturn(alice);
        when(payment.payOrderFromCart(any(), anyDouble())).thenThrow(new PaymentException("Alice", 10.0));

        assertThrows(PaymentException.class, () -> cartHandler.validate(ALICE_ID));
        // le panier ne doit pas être vidé si le paiement échoue
        assertFalse(alice.getCart().isEmpty());
    }
}