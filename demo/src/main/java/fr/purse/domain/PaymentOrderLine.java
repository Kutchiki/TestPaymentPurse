package fr.purse.domain;

import lombok.Data;

@Data
public class PaymentOrderLine {
    private String productName;
    private int quantity;
    private String reference;
    private Double price; // Maybe use a custom type for price (amount + currency)
}
