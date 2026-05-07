package fr.univcotedazur.skimaster.customer.components;

import fr.univcotedazur.skimaster.customer.entities.Category;
import fr.univcotedazur.skimaster.customer.entities.Customer;
import fr.univcotedazur.skimaster.customer.exceptions.AlreadyExistingCustomerException;
import fr.univcotedazur.skimaster.customer.interfaces.CustomerFinder;
import fr.univcotedazur.skimaster.customer.interfaces.CustomerRegistration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // default behavior : rollback DB operations after each test (even if it fails)
class CustomerRegistryTest {

    @Autowired
    private CustomerRegistration customerRegistration;

    @Autowired
    private CustomerFinder customerFinder;

    private final String name = "John";
    private final String creditCard = "1234567890";
    private final Category category = Category.ADULT;

    @Test
    void unknownCustomer() {
        assertFalse(customerFinder.findByName(name).isPresent());
    }

    @Test
    void registerCustomer() throws Exception {
        Customer returned = customerRegistration.register(name, creditCard, category);
        Optional<Customer> customer = customerFinder.findByName(name);
        assertTrue(customer.isPresent());
        Customer john = customer.get();
        assertEquals(john, returned);
        assertEquals(john, customerFinder.findById(returned.getId()).get());
        assertEquals(name, john.getName());
        assertEquals(creditCard, john.getCreditCard());
    }

    @Test
    void cannotRegisterTwice() throws Exception {
        customerRegistration.register(name, creditCard, Category.ADULT);
        Assertions.assertThrows(AlreadyExistingCustomerException.class, () -> customerRegistration.register(name, creditCard, Category.ADULT));
    }

}
