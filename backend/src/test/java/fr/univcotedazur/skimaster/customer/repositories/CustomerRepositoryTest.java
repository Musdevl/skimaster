package fr.univcotedazur.skimaster.customer.repositories;

import fr.univcotedazur.skimaster.customer.entities.Category;
import fr.univcotedazur.skimaster.customer.entities.Customer;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest // Only run a test container with the JPA layer (only repositories are up)
// @DataJpaTest is "transactional rollback by default
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testIdGenerationAndUnicity() {
        Customer john = new Customer("john", "1234567890", Category.ADULT);
        Assertions.assertNull(john.getId());
        customerRepository.saveAndFlush(john); // save in the persistent context and force saving in the DB (thus ensuring validation by Hibernate)
        Assertions.assertNotNull(john.getId());
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> customerRepository.saveAndFlush(new Customer("john", "1234567890", Category.ADULT)));
    }

    @Test
    void testFindCustomerByName() {
        Customer john = new Customer("john", "1234567890", Category.ADULT);
        customerRepository.saveAndFlush(john);
        Assertions.assertEquals(customerRepository.findCustomerByName("john").get(),john);
    }

    @Test
    void testEmptyName() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> customerRepository.saveAndFlush(new Customer("", "1234567890", Category.ADULT)));;
    }

    @Test
    void testBlankName() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> customerRepository.saveAndFlush(new Customer("    ", "1234567890", Category.ADULT)));
    }

    @Test
    void testEmptyCreditCard() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> customerRepository.saveAndFlush(new Customer("badguy", "", Category.ADULT)));
    }

    @Test
    void testCreditCardPatternOnNumerals() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> customerRepository.saveAndFlush(new Customer("badguy", "creditCard", Category.ADULT)));
    }

    @Test
    void testCreditCardPatternLength() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> customerRepository.saveAndFlush(new Customer("badguy", "123456789", Category.ADULT)));
    }
}
