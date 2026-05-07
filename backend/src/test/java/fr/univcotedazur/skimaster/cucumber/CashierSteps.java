package fr.univcotedazur.skimaster.cucumber;

import fr.univcotedazur.skimaster.cashier.components.Cashier;
import fr.univcotedazur.skimaster.cashier.connectors.interfaces.Bank;
import fr.univcotedazur.skimaster.cashier.exceptions.PaymentException;
import fr.univcotedazur.skimaster.customer.entities.*;
import fr.univcotedazur.skimaster.monitoring.components.DomainHandler;
import fr.univcotedazur.skimaster.nfc.components.NFCCardRegistry;
import fr.univcotedazur.skimaster.nfc.dto.NFCCardDTO;
import fr.univcotedazur.skimaster.nfc.entities.NFCCard;
import fr.univcotedazur.skimaster.order.entities.Order;
import fr.univcotedazur.skimaster.order.interfaces.OrderCreator;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CashierSteps {

    private Bank bank;
    private OrderCreator orderCreator;
    private NFCCardRegistry nfcCardRegistry;
    private DomainHandler domainHandler;

    private Cashier cashier;

    private Customer customer;
    private Set<Item> cartItems;
    private Order resultOrder;
    private Exception thrownException;

    private List<NFCCard> registeredCards;

    @Before
    public void setUp() {
        bank            = mock(Bank.class);
        orderCreator    = mock(OrderCreator.class);
        nfcCardRegistry = mock(NFCCardRegistry.class);
        domainHandler   = mock(DomainHandler.class);

        cashier = new Cashier(bank, orderCreator, nfcCardRegistry, domainHandler);

        cartItems        = new HashSet<>();
        registeredCards  = new ArrayList<>();
        resultOrder      = null;
        thrownException  = null;
    }

    @Given("the bank is available")
    public void theBankIsAvailable() {
        when(bank.pay(any(Customer.class), anyDouble()))
                .thenReturn(Optional.of("receipt-001"));
    }

    @Given("an ADULT customer named {string} with credit card {string}")
    public void anAdultCustomer(String name, String creditCard) {
        customer = new Customer(name, creditCard, Category.ADULT);
    }

    @Given("a CHILD customer named {string} with credit card {string}")
    public void aChildCustomer(String name, String creditCard) {
        customer = new Customer(name, creditCard, Category.CHILD);
    }

    @Given("the customer's cart contains {int} {string}")
    public void theCartContains(int quantity, String planName) {
        Plan plan = Plan.valueOf(planName);
        cartItems.add(new Item(plan, quantity));
    }

    @Given("the bank refuses the payment")
    public void theBankRefusesThePayment() {
        when(bank.pay(any(Customer.class), anyDouble()))
                .thenReturn(Optional.empty());
    }

    @When("the customer pays {double}")
    public void theCustomerPays(double price) {
        stubOrderCreatorAndNfcRegistry(price);
        try {
            resultOrder = cashier.payOrderFromCart(customer, price);
        } catch (PaymentException e) {
            thrownException = e;
        }
    }

    @When("the customer tries to pay {double}")
    public void theCustomerTriesToPay(double price) {
        theCustomerPays(price);
    }


    @Then("the order is created with receipt id {string}")
    public void theOrderIsCreatedWithReceiptId(String receiptId) {
        assertThat(resultOrder).isNotNull();
        assertThat(resultOrder.getPayReceiptId()).isEqualTo(receiptId);
    }

    @Then("no PaymentException is thrown")
    public void noPaymentExceptionIsThrown() {
        assertThat(thrownException).isNull();
    }

    @Then("a PaymentException is thrown")
    public void aPaymentExceptionIsThrown() {
        assertThat(thrownException).isInstanceOf(PaymentException.class);
    }

    @Then("no order is created")
    public void noOrderIsCreated() {
        assertThat(resultOrder).isNull();
    }

    @Then("no NFC card is registered")
    public void noNfcCardIsRegistered() {
        assertThat(registeredCards).isEmpty();
    }

    @Then("{int} NFC card is registered")
    public void oneNfcCardIsRegistered(int expectedCount) {
        assertThat(registeredCards).hasSize(expectedCount);
    }

    @Then("{int} NFC cards are registered")
    public void nNfcCardsAreRegistered(int expectedCount) {
        assertThat(registeredCards).hasSize(expectedCount);
    }

    @Then("the NFC card has sound {string}")
    public void theNfcCardHasSound(String soundName) {
        Sound expectedSound = Sound.valueOf(soundName);
        assertThat(registeredCards)
                .hasSize(1)
                .first()
                .extracting(NFCCard::getSound)
                .isEqualTo(expectedSound);
    }

    @Then("{int} NFC cards have sound {string}")
    public void nNfcCardsHaveSound(int expectedCount, String soundName) {
        Sound expectedSound = Sound.valueOf(soundName);
        long actualCount = registeredCards.stream()
                .filter(c -> c.getSound() == expectedSound)
                .count();
        assertThat(actualCount)
                .as("Expected %d cards with sound %s but found %d", expectedCount, soundName, actualCount)
                .isEqualTo(expectedCount);
    }

    @Then("the gates are notified {int} time")
    public void theGatesAreNotified(int times) {
        verify(domainHandler, times(times)).addCardToGates(any(NFCCardDTO.class));
    }

    @Then("the gates are notified {int} times")
    public void theGatesAreNotifiedTimes(int times) {
        verify(domainHandler, times(times)).addCardToGates(any(NFCCardDTO.class));
    }


    private void stubOrderCreatorAndNfcRegistry(double price) {
        Order fakeOrder = new Order(customer, cartItems, price, "receipt-001");
        when(orderCreator.createOrder(any(Customer.class), anyDouble(), anyString()))
                .thenReturn(fakeOrder);

        when(nfcCardRegistry.register(any(Customer.class), any(Plan.class), any(Sound.class)))
                .thenAnswer(invocation -> {
                    Customer c    = invocation.getArgument(0);
                    Plan     plan = invocation.getArgument(1);
                    Sound    sound = invocation.getArgument(2);
                    NFCCard card  = new NFCCard(c, plan, sound);
                    registeredCards.add(card);
                    return card;
                });
    }
}