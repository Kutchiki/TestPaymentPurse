package fr.purse.endpoints;

import fr.purse.exceptions.InvalidPaymentException;
import fr.purse.exceptions.PaymentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidPaymentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidPaymentException(InvalidPaymentException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleInvalidPaymentException(PaymentNotFoundException exception) {
        return exception.getMessage();
    }
}
