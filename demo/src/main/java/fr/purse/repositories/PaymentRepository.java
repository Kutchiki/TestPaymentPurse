package fr.purse.repositories;

import fr.purse.domain.Payment;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends ReactiveMongoRepository<Payment, String> {
}