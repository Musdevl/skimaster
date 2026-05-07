package fr.univcotedazur.skimaster.customer.controllers;

import fr.univcotedazur.skimaster.customer.dto.CustomerDTO;
import fr.univcotedazur.skimaster.customer.entities.Customer;
import fr.univcotedazur.skimaster.customer.exceptions.AlreadyExistingCustomerException;
import fr.univcotedazur.skimaster.customer.exceptions.CustomerIdNotFoundException;
import fr.univcotedazur.skimaster.customer.interfaces.CustomerFinder;
import fr.univcotedazur.skimaster.customer.interfaces.CustomerRegistration;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = CustomerCareController.BASE_URI, produces = APPLICATION_JSON_VALUE)
public class CustomerCareController {

    public static final String BASE_URI = "/customers";

    private final CustomerRegistration registry;

    private final CustomerFinder finder;

    public CustomerCareController(CustomerRegistration registry, CustomerFinder finder) {
        this.registry = registry;
        this.finder = finder;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerDTO> register(@RequestBody @Valid CustomerDTO cusdto)
            throws AlreadyExistingCustomerException {
        // Note that there is no validation at all on the CustomerDto mapped
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertCustomerToDto(
                        registry.register(cusdto.name(), cusdto.creditCard(), cusdto.category())));
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getCustomers() {
        return ResponseEntity.ok(finder.findAll().stream().map(CustomerCareController::convertCustomerToDto).toList());
    }

    @GetMapping(path = "/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable("customerId") Long customerId)
            throws CustomerIdNotFoundException {
        return ResponseEntity.ok(convertCustomerToDto(finder.retrieveCustomer(customerId)));
    }

    private static CustomerDTO convertCustomerToDto(Customer customer) { // In more complex cases, we could use a
                                                                         // ModelMapper such as MapStruct
        return new CustomerDTO(customer.getId(), customer.getName(), customer.getCreditCard(), customer.getCategory());
    }

}