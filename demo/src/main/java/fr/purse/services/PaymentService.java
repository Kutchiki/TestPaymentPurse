package fr.purse.services;

import fr.purse.domain.Payment;
import fr.purse.domain.PaymentStatus;
import fr.purse.exceptions.InvalidPaymentException;
import fr.purse.exceptions.PaymentNotFoundException;
import fr.purse.repositories.PaymentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Mono<Payment> getPaymentById(String id) {
        return paymentRepository.findById(id)
                .switchIfEmpty(Mono.error(new PaymentNotFoundException(String.format("Payment with id %s was not found", id))));
    }

    public Mono<Payment> createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Mono<Payment> updatePayment(String paymentId, Payment payment) {
        return this.getPaymentById(paymentId)
                .flatMap(p -> isPaymentCompliantForUpdate(payment, p))
                .flatMap(p -> {
                            payment.setId(paymentId);
                            payment.setPaymentOrderLines(p.getPaymentOrderLines());
                            return paymentRepository.save(payment);
                        });
    }

    public Flux<Payment> getPayments() {
        return paymentRepository.findAll();
    }

    private Mono<Payment> isPaymentCompliantForUpdate(Payment request, Payment storedPayment) {
        var requestStatus = request.getPaymentStatus();
        if (PaymentStatus.CAPTURED.equals(storedPayment.getPaymentStatus()) && !PaymentStatus.CAPTURED.equals(requestStatus)) {
            return Mono.error(new InvalidPaymentException("Captured payment cannot have a different payment status"));
        }
        if (PaymentStatus.CAPTURED.equals(requestStatus) && !PaymentStatus.AUTHORIZED.equals(storedPayment.getPaymentStatus())) {
            return Mono.error(new InvalidPaymentException("Payment cannot accept CAPTURED, must be AUTHORIZED before"));

        }
        return Mono.just(storedPayment);
    }
}