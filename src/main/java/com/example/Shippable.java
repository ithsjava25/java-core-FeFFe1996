package com.example;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Shippable {
    double calculateShippingCost();
    double weight();
}
