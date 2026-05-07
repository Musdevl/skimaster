package fr.univcotedazur.skimaster.cucumber;

import fr.univcotedazur.skimaster.cashier.exceptions.PaymentException;
import fr.univcotedazur.skimaster.cashier.interfaces.Payment;
import fr.univcotedazur.skimaster.customer.components.CartHandler;
import fr.univcotedazur.skimaster.customer.components.Catalog;
import fr.univcotedazur.skimaster.customer.components.CustomerRegistry;
import fr.univcotedazur.skimaster.customer.entities.*;
import fr.univcotedazur.skimaster.customer.exceptions.*;
import fr.univcotedazur.skimaster.customer.interfaces.CustomerFinder;
import fr.univcotedazur.skimaster.customer.repositories.CustomerRepository;
import fr.univcotedazur.skimaster.order.entities.Order;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerSteps {

    private Payment payment;
    private CustomerFinder customerFinder;
    private CustomerRepository customerRepository;

    private CartHandler cartHandler;
    private Catalog catalog;
    private CustomerRegistry customerRegistry;

    private Customer customer;
    private Item lastUpdatedItem;
    private Set<Plan> catalogResult;
    private Order resultOrder;
    private Exception thrownException;

    @Before
    public void setUp() {
        payment            = mock(Payment.class);
        customerFinder     = mock(CustomerFinder.class);
        customerRepository = mock(CustomerRepository.class);

        cartHandler      = new CartHandler(payment, customerFinder);
        catalog          = new Catalog();
        customerRegistry = new CustomerRegistry(customerRepository);

        customer         = null;
        lastUpdatedItem  = null;
        catalogResult    = null;
        resultOrder      = null;
        thrownException  = null;
    }

    @Given("an ADULT customer named {string} with credit card {string} and id {long}")
    public void anAdultCustomerWithId(String name, String creditCard, long id) throws CustomerIdNotFoundException {
        customer = spy(new Customer(name, creditCard, Category.ADULT));
        // On force l'id retourné par getId() car JPA ne l'assignera pas hors contexte
        doReturn(id).when(customer).getId();
        when(customerFinder.retrieveCustomer(id)).thenReturn(customer);
    }

    @Given("the payment succeeds for Alice")
    public void thePaymentSucceeds() throws PaymentException {
        Order fakeOrder = new Order(customer, customer.getCart(), 10.0, "receipt-ok");
        when(payment.payOrderFromCart(eq(customer), anyDouble())).thenReturn(fakeOrder);
    }

    @Given("the payment is refused for Alice")
    public void thePaymentIsRefused() throws PaymentException {
        String customerName = customer.getName();
        when(payment.payOrderFromCart(eq(customer), anyDouble()))
                .thenThrow(new PaymentException(customerName, 0));
    }

    @Given("no customer named {string} exists")
    public void noCustomerNamed(String name) {
        when(customerRepository.findCustomerByName(name)).thenReturn(Optional.empty());
    }

    @Given("a customer named {string} already exists")
    public void customerAlreadyExists(String name) {
        Customer existing = new Customer(name, "1111111111", Category.ADULT);
        when(customerRepository.findCustomerByName(name)).thenReturn(Optional.of(existing));
    }

    @Given("no customer exists with id {long}")
    public void noCustomerWithId(long id) throws CustomerIdNotFoundException {
        when(customerRepository.findById(id)).thenReturn(Optional.empty());
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // When — CartHandler
    // ─────────────────────────────────────────────────────────────────────────────

    @When("Alice adds {int} {string} to her cart")
    public void aliceAddsToCart(int quantity, String planName) {
        try {
            lastUpdatedItem = cartHandler.update(customer.getId(), new Item(Plan.valueOf(planName), quantity));
        } catch (NegativeQuantityException | CustomerIdNotFoundException e) {
            thrownException = e;
        }
    }

    @When("Alice validates her empty cart")
    public void aliceValidatesEmptyCart() {
        try {
            resultOrder = cartHandler.validate(customer.getId());
        } catch (PaymentException | EmptyCartException | CustomerIdNotFoundException e) {
            thrownException = e;
        }
    }

    @When("Alice validates her cart")
    public void aliceValidatesCart() {
        try {
            resultOrder = cartHandler.validate(customer.getId());
        } catch (PaymentException | EmptyCartException | CustomerIdNotFoundException e) {
            thrownException = e;
        }
    }

    @When("the catalog lists all plans")
    public void catalogListsAllPlans() {
        catalogResult = catalog.listPlans();
    }

    @When("the catalog is explored with regexp {string}")
    public void catalogExploredWithRegexp(String regexp) {
        catalogResult = catalog.exploreCatalogue(regexp);
    }

    @When("{string} registers with credit card {string} as ADULT")
    public void customerRegisters(String name, String creditCard) {
        try {
            Customer saved = new Customer(name, creditCard, Category.ADULT);
            when(customerRepository.save(any(Customer.class))).thenReturn(saved);
            customerRegistry.register(name, creditCard, Category.ADULT);
        } catch (AlreadyExistingCustomerException e) {
            thrownException = e;
        }
    }

    @When("retrieving customer with id {long}")
    public void retrievingCustomerWithId(long id) {
        try {
            customerRegistry.retrieveCustomer(id);
        } catch (CustomerIdNotFoundException e) {
            thrownException = e;
        }
    }

    @Then("Alice's cart contains {int} {string}")
    public void aliceCartContains(int expectedQty, String planName) {
        Plan plan = Plan.valueOf(planName);
        Optional<Item> item = customer.getCart().stream()
                .filter(i -> i.getPlan() == plan)
                .findFirst();
        assertThat(item).isPresent();
        assertThat(item.get().getQuantity()).isEqualTo(expectedQty);
    }

    @Then("Alice's cart is empty")
    public void aliceCartIsEmpty() {
        assertThat(customer.getCart()).isEmpty();
    }

    @Then("Alice's cart price is {double}")
    public void aliceCartPrice(double expectedPrice) throws CustomerIdNotFoundException {
        double actualPrice = cartHandler.cartPrice(customer.getId());
        assertThat(actualPrice).isEqualTo(expectedPrice);
    }

    @Then("the order is created")
    public void theOrderIsCreated() {
        assertThat(resultOrder).isNotNull();
    }

    @Then("a NegativeQuantityException is thrown")
    public void negativeQuantityExceptionThrown() {
        assertThat(thrownException).isInstanceOf(NegativeQuantityException.class);
    }

    @Then("an EmptyCartException is thrown")
    public void emptyCartExceptionThrown() {
        assertThat(thrownException).isInstanceOf(EmptyCartException.class);
    }

    @Then("a PaymentException is thrown for the customer")
    public void paymentExceptionThrown() {
        assertThat(thrownException).isInstanceOf(PaymentException.class);
    }
    @Then("the result contains {string}")
    public void resultContains(String planName) {
        assertThat(catalogResult)
                .extracting(Plan::getName)
                .contains(planName);
    }

    @Then("the result does not contain {string}")
    public void resultDoesNotContain(String planName) {
        assertThat(catalogResult)
                .extracting(Plan::getName)
                .doesNotContain(planName);
    }

    @Then("the result is empty")
    public void resultIsEmpty() {
        assertThat(catalogResult).isEmpty();
    }

    @Then("the customer {string} is saved")
    public void customerIsSaved(String name) {
        verify(customerRepository).save(argThat(c -> c.getName().equals(name)));
    }

    @Then("an AlreadyExistingCustomerException is thrown")
    public void alreadyExistingCustomerExceptionThrown() {
        assertThat(thrownException).isInstanceOf(AlreadyExistingCustomerException.class);
    }

    @Then("a CustomerIdNotFoundException is thrown")
    public void customerIdNotFoundExceptionThrown() {
        assertThat(thrownException).isInstanceOf(CustomerIdNotFoundException.class);
    }
}