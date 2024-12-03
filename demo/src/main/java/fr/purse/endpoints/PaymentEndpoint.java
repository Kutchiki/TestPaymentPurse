package fr.purse.endpoints;

import fr.purse.domain.Payment;
import fr.purse.services.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/payment/v0")
public class PaymentEndpoint {

    private final PaymentService paymentService;

    public PaymentEndpoint(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{paymentId}")
    private Mono<Payment> getPaymentById(@PathVariable String paymentId) {
        return paymentService.getPaymentById(paymentId);
    }

    @GetMapping
    private Flux<Payment> getAll() {
        return paymentService.getPayments(); //We can imagine a pagination system and/or a filter to this request.
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Payment> createPayment(@RequestBody Payment payment) {
        return paymentService.createPayment(payment);
    }

    @PatchMapping("/{paymentId}")
    public Mono<Payment> updatePayment(@PathVariable String paymentId, @RequestBody Payment payment) {
        return paymentService.updatePayment(paymentId, payment);
    }
}
