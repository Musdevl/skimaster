package fr.univcotedazur.skimaster.monitoring.components;

import fr.univcotedazur.skimaster.cashier.components.Cashier;
import fr.univcotedazur.skimaster.cashier.exceptions.PaymentException;
import fr.univcotedazur.skimaster.customer.entities.Customer;
import fr.univcotedazur.skimaster.customer.entities.Item;
import fr.univcotedazur.skimaster.customer.exceptions.CustomerIdNotFoundException;
import fr.univcotedazur.skimaster.customer.repositories.CustomerRepository;
import fr.univcotedazur.skimaster.monitoring.connectors.interfaces.Gate;
import fr.univcotedazur.skimaster.monitoring.dto.InvoicingResultDTO;
import fr.univcotedazur.skimaster.monitoring.dto.nfc.SuperCardInvoiceDTO;
import fr.univcotedazur.skimaster.monitoring.entities.GateConnection;
import fr.univcotedazur.skimaster.monitoring.repositories.GateConnectionRepository;
import fr.univcotedazur.skimaster.nfc.dto.NFCCardDTO;
import fr.univcotedazur.skimaster.customer.entities.Plan;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InvoicingHandler {

    private final GateConnectionRepository gateConnectionRepository;
    private final CustomerRepository customerRepository;
    private final Cashier cashier;
    private final Gate gateProxy;

    public InvoicingHandler(Cashier cashier, GateConnectionRepository gateConnectionRepository, CustomerRepository customerRepository, Gate gateProxy) {

        this.gateConnectionRepository = gateConnectionRepository;
        this.customerRepository = customerRepository;
        this.gateProxy = gateProxy;
        this.cashier = cashier;
    }

    private List<NFCCardDTO> getSuperCardsToInvoice(GateConnection gateConnection){
        Optional<List<NFCCardDTO>> superCards = this.gateProxy.requestSuperCards(gateConnection.getURI());
        return superCards.orElseGet(ArrayList::new);
    }

    @Transactional
    public InvoicingResultDTO invoiceSuperCardsForDay(LocalDate day) throws CustomerIdNotFoundException, PaymentException {

        List<GateConnection> gateConnections = gateConnectionRepository.findAll();
        List<SuperCardInvoiceDTO> invoices = new ArrayList<>();
        double totalAmount = 0;
        int totalCustomersInvoiced = 0;

        Map<Long, Integer> superCardCounts = new HashMap<>();

        for(GateConnection gateConnection : gateConnections) {
            List<NFCCardDTO> superCards = getSuperCardsToInvoice(gateConnection);

            superCards.stream()
                    .filter(card -> card.plan() == Plan.SUPER_CARD)
                    .collect(Collectors.toMap(
                            NFCCardDTO::customerId,
                            card -> 1,
                            Integer::sum
                    ))
                    .forEach((customerId, count) ->
                            superCardCounts.merge(customerId, count, Integer::sum)
                    );
        }

        for (Map.Entry<Long, Integer> entry : superCardCounts.entrySet()) {
            Long customerId = entry.getKey();
            int quantity = entry.getValue();

            Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
            if(optionalCustomer.isEmpty()){
                throw new CustomerIdNotFoundException(customerId);
            }

            Customer customer = optionalCustomer.get();
            Set<Item> cart = new HashSet<>();
            Item item = new Item(Plan.SUPER_CARD_PASSAGE, quantity);
            cart.add(item);
            customer.setCart(cart);

            cashier.payOrderFromCart(customer, cartPriceFromCustomer(customer));

            double unitPrice = Plan.SUPER_CARD_PASSAGE.getPrice();
            double totalInvoicePrice = unitPrice * quantity;

            invoices.add(new SuperCardInvoiceDTO(
                    customerId,
                    Plan.SUPER_CARD_PASSAGE.getName(),
                    unitPrice,
                    quantity,
                    totalInvoicePrice
            ));

            totalAmount += totalInvoicePrice;
            totalCustomersInvoiced++;
        }

        return new InvoicingResultDTO(totalCustomersInvoiced, totalAmount, invoices);
    }

    private double cartPriceFromCustomer(Customer customer) {
        return customer.getCart().stream()
                .mapToDouble(item -> item.getPlan().getPrice() * item.getQuantity())
                .sum();
    }

}
