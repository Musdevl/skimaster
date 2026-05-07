package fr.univcotedazur.skimaster.cashier.components;

import fr.univcotedazur.skimaster.customer.entities.*;
import fr.univcotedazur.skimaster.monitoring.components.DomainHandler;
import fr.univcotedazur.skimaster.nfc.components.NFCCardRegistry;
import fr.univcotedazur.skimaster.nfc.dto.NFCCardDTO;
import fr.univcotedazur.skimaster.nfc.entities.NFCCard;
import fr.univcotedazur.skimaster.order.interfaces.OrderCreator;
import fr.univcotedazur.skimaster.cashier.connectors.interfaces.Bank;
import fr.univcotedazur.skimaster.cashier.exceptions.PaymentException;
import fr.univcotedazur.skimaster.cashier.interfaces.Payment;
import fr.univcotedazur.skimaster.order.entities.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
public class Cashier implements Payment {

    private final Bank bank;

    private final OrderCreator orderer;

    private final NFCCardRegistry nfcCardRegistry;

    private final DomainHandler domainHandler;
    public Cashier(Bank bank, OrderCreator orderer, NFCCardRegistry nfcCardRegistry, DomainHandler domainHandler) {
        this.bank = bank;
        this.orderer = orderer;
        this.nfcCardRegistry = nfcCardRegistry;
        this.domainHandler = domainHandler;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Order payOrderFromCart(Customer customer, double price) throws PaymentException {
        String paymentReceiptId  = bank.pay(customer, price).orElseThrow(() -> new PaymentException(customer.getName(), price));
        Order order = orderer.createOrder(customer, price, paymentReceiptId);
        this.addNFCCardToCustomer(order);
        return order;
    }


    private void addNFCCardToCustomer(Order order){
        Set<Item> items = order.getItems();
        for (Item item : items) {
            if (item.getPlan().isSubscription()) {
                for (int i = 0; i < item.getQuantity(); i++) {

                    if (item.getPlan() == Plan.FAMILY_PLAN) {
                        notifyGates(nfcCardRegistry.register(order.getCustomer(), item.getPlan(), Sound.HIGH_SOUND));
                        notifyGates(nfcCardRegistry.register(order.getCustomer(), item.getPlan(), Sound.HIGH_SOUND));
                        notifyGates(nfcCardRegistry.register(order.getCustomer(), item.getPlan(), Sound.LOW_SOUND));
                        notifyGates(nfcCardRegistry.register(order.getCustomer(), item.getPlan(), Sound.LOW_SOUND));
                    } else {
                        Sound sound = order.getCustomer().getCategory() == Category.ADULT ? Sound.HIGH_SOUND : Sound.LOW_SOUND;
                        notifyGates(nfcCardRegistry.register(order.getCustomer(), item.getPlan(), sound));
                    }
                }
            }
        }
    }

    private void notifyGates(NFCCard card) {
        NFCCardDTO dto = new NFCCardDTO(card.getId(), card.getCustomer().getId(), card.getSound(), card.getPlan());
        domainHandler.addCardToGates(dto);
    }

}