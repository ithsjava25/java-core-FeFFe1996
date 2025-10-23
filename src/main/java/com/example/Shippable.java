package com.example;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Shippable {
    BigDecimal calculateShippingCost();
    double weight();
}
