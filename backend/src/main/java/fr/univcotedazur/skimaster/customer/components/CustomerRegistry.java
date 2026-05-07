package fr.univcotedazur.skimaster.customer.components;

import fr.univcotedazur.skimaster.customer.entities.Category;
import fr.univcotedazur.skimaster.customer.entities.Customer;
import fr.univcotedazur.skimaster.customer.exceptions.AlreadyExistingCustomerException;
import fr.univcotedazur.skimaster.customer.exceptions.CustomerIdNotFoundException;
import fr.univcotedazur.skimaster.customer.interfaces.CustomerFinder;
import fr.univcotedazur.skimaster.customer.interfaces.CustomerRegistration;
import fr.univcotedazur.skimaster.customer.repositories.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerRegistry implements CustomerRegistration, CustomerFinder {

    private final CustomerRepository customerRepository;

    public CustomerRegistry(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public Customer register(String name, String creditCard, Category category)
            throws AlreadyExistingCustomerException {
        if (findByName(name).isPresent())
            throw new AlreadyExistingCustomerException(name);
        Customer newcustomer = new Customer(name, creditCard, category);
        return customerRepository.save(newcustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findByName(String name) {
        return customerRepository.findCustomerByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer retrieveCustomer(Long customerId) throws CustomerIdNotFoundException {
        return findById(customerId).orElseThrow(() -> new CustomerIdNotFoundException(customerId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

}
