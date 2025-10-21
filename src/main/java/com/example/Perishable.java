package com.example;

import java.time.LocalDate;

public interface Perishable{
    LocalDate expirationDate();
    
    default LocalDate isExpired(){
        LocalDate now = LocalDate.now();
        return now.isBefore(expirationDate()) ? now : expirationDate();
    }
}
