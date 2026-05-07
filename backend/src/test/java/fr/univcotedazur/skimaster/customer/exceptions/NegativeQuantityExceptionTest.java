package fr.univcotedazur.skimaster.customer.exceptions;

import fr.univcotedazur.skimaster.customer.entities.Plan;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NegativeQuantityExceptionTest {

    @Test
    void testFullConstructorAndGetters() {
        String expectedName = "TestItem";
        Plan expectedPlan = Plan.BASIC_PLAN;
        int expectedQty = -5;

        NegativeQuantityException exception = new NegativeQuantityException(expectedName, expectedPlan, expectedQty);

        assertEquals(expectedName, exception.getName());
        assertEquals(expectedPlan, exception.getPlan());
        assertEquals(expectedQty, exception.getPotentialQuantity());
    }

    @Test
    void testSetters() {
        NegativeQuantityException exception = new NegativeQuantityException();
        
        exception.setName("NewName");
        exception.setPlan(Plan.BASIC_PLAN);
        exception.setPotentialQuantity(-10);

        assertEquals("NewName", exception.getName());
        assertEquals(Plan.BASIC_PLAN, exception.getPlan());
        assertEquals(-10, exception.getPotentialQuantity());
    }

    @Test
    void testDefaultConstructor() {
        NegativeQuantityException exception = new NegativeQuantityException();
        
        assertNull(exception.getName());
        assertNull(exception.getPlan());
        assertEquals(0, exception.getPotentialQuantity());
    }
}