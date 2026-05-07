package fr.univcotedazur.skimaster.common.controller;

import fr.univcotedazur.skimaster.cashier.exceptions.PaymentException;
import fr.univcotedazur.skimaster.common.dto.ErrorDTO;
import fr.univcotedazur.skimaster.customer.exceptions.AlreadyExistingCustomerException;
import fr.univcotedazur.skimaster.customer.exceptions.CustomerIdNotFoundException;
import fr.univcotedazur.skimaster.customer.exceptions.EmptyCartException;
import fr.univcotedazur.skimaster.customer.exceptions.NegativeQuantityException;
import fr.univcotedazur.skimaster.monitoring.exceptions.FailedToGetGateStatusException;
import fr.univcotedazur.skimaster.monitoring.exceptions.GateNotFoundException;
import fr.univcotedazur.skimaster.monitoring.exceptions.InvalidThresholdsException;
import fr.univcotedazur.skimaster.monitoring.exceptions.PanelNotFoundException;
import fr.univcotedazur.skimaster.nfc.exceptions.AlreadyExistingNFCCardException;
import fr.univcotedazur.skimaster.order.exceptions.OrderIdNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler({ CustomerIdNotFoundException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ErrorDTO handleExceptions(CustomerIdNotFoundException e) {
        return new ErrorDTO("Customer not found", e.getId() + " is not a valid customer Id");
    }

    @ExceptionHandler({ OrderIdNotFoundException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ErrorDTO handleExceptions(OrderIdNotFoundException e) {
        return new ErrorDTO("Order not found", e.getId() + " is not a valid order Id");
    }

    @ExceptionHandler({ EmptyCartException.class })
    @ResponseStatus(HttpStatus.FORBIDDEN) // 402
    public ErrorDTO handleExceptions(EmptyCartException e) {
        return new ErrorDTO("Cart is empty", "from Customer " + e.getName());
    }

    @ExceptionHandler({ PaymentException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorDTO handleExceptions(PaymentException e) {
        return new ErrorDTO("Payment was rejected", "from Customer " + e.getName() + " for amount " + e.getAmount());
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class })
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // 422
    public ErrorDTO handleValidationExceptions(MethodArgumentNotValidException e) {
        return new ErrorDTO("Cannot process data", e.getMessage());
    }

    @ExceptionHandler({ NegativeQuantityException.class })
    @ResponseStatus(HttpStatus.FORBIDDEN) // 403
    public ErrorDTO handleEmtyCartError(NegativeQuantityException e) {
        return new ErrorDTO("Cannot remove from empty cart", e.getPotentialQuantity() + " is not a valide quantity");
    }

    @ExceptionHandler({ AlreadyExistingCustomerException.class })
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    public ErrorDTO handleEmtyCartError(AlreadyExistingCustomerException e) {
        return new ErrorDTO("User already exist", e.getConflictingName() + " was duplicated");
    }

    @ExceptionHandler({ FailedToGetGateStatusException.class })
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE) // 503
    public ErrorDTO handleEmtyCartError(FailedToGetGateStatusException e) {
        return new ErrorDTO("Could not access gate status", e.getMessage());
    }

    @ExceptionHandler({ GateNotFoundException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ErrorDTO handleExceptions(GateNotFoundException e) {
        return new ErrorDTO("Could not access gate ", e.getMessage());
    }

    @ExceptionHandler({ InvalidThresholdsException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorDTO handleExceptions(InvalidThresholdsException e) {
        return new ErrorDTO("Invalide value for threshold", e.getMessage());
    }

    @ExceptionHandler({ PanelNotFoundException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ErrorDTO handleExceptions(PanelNotFoundException e) {
        return new ErrorDTO("Could not access pannel ", e.getMessage());
    }

    @ExceptionHandler({ AlreadyExistingNFCCardException.class })
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    public ErrorDTO handleEmtyCartError(AlreadyExistingNFCCardException e) {
        return new ErrorDTO("NFC card already exist", e.getMessage());
    }
}