package fr.purse.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document
public class Payment {
    @Id
    private String id;
    private int amount;
    private String currency;
    private PaymentMode paymentMode;
    private PaymentStatus paymentStatus = PaymentStatus.IN_PROGRESS;
    private List<PaymentOrderLine> paymentOrderLines = new ArrayList<>();

}