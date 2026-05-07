package fr.univcotedazur.skimaster.order.components;

import fr.univcotedazur.skimaster.customer.entities.Category;
import fr.univcotedazur.skimaster.customer.entities.Customer;
import fr.univcotedazur.skimaster.customer.entities.Item;
import fr.univcotedazur.skimaster.customer.entities.Plan;
import fr.univcotedazur.skimaster.customer.interfaces.CustomerRegistration;
import fr.univcotedazur.skimaster.order.entities.Order;
import fr.univcotedazur.skimaster.order.entities.OrderStatus;
import fr.univcotedazur.skimaster.order.exceptions.OrderIdNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class OrdererTest {

    @Autowired
    private CustomerRegistration registry;

    @Autowired
    private Orderer orderer;

    private Long orderId;

    @BeforeEach
    void setUpContext() throws Exception {
        Set<Item> items = new HashSet<>();
        items.add(new Item(Plan.BASIC_PLAN, 3));
        Customer john = registry.register("john", "1234896983", Category.ADULT);
        john.setCart(items);
        orderId = orderer.createOrder(john, (3 * Plan.BASIC_PLAN.getPrice()), "payReceiptIdOK").getId();
    }

    @Test
    void orderFinding() {
        assertTrue(orderer.findById(orderId).isPresent());
        assertTrue(orderer.findById(324L).isEmpty());
        Assertions.assertThrows(OrderIdNotFoundException.class, () -> orderer.retrieveOrder(324L));
    }

    @Test
    void orderCreation() throws Exception {
        Order order = orderer.retrieveOrder(orderId);
        Customer john = order.getCustomer();
        assertEquals(orderId, order.getId());
        assertEquals(OrderStatus.VALIDATED, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals(3, order.getItems().stream().filter(item -> item.getPlan().equals(Plan.BASIC_PLAN)).findFirst().get().getQuantity());
        assertEquals("payReceiptIdOK", order.getPayReceiptId());
        assertEquals("john", john.getName());
        // John's cart is not empty, we can reuse it to create another order
        orderer.createOrder(john, 30.0, "payReceiptIdOK");
        assertEquals(2, orderer.findAll().size());
    }

}