package com.example;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.util.UUID;

public class Warehouse {
    private static Warehouse warehouse = new Warehouse();
    private Warehouse() {}

    public static Warehouse getInstance(String warehouseName) {
        if (warehouse == null) {
            warehouse = new Warehouse();
        }
        return warehouse = new Warehouse(warehouseName);
    }
}

class Category{
    private String name;

    private Category(String name) {
        if(name == null) {
            throw new IllegalArgumentException("Category name can't be null");
        }else if (name == ""){
            throw new IllegalArgumentException("Category name can't be empty");
        } else if (name == " ") {
            throw new IllegalArgumentException("Category name can't be blank");
        }else{
            this.name = name.substring(0, 1).toUpperCase() + name.substring(1);
        }
    }

    public String getName() {
        return name;
    }

    public static Category of(String name) {
        return new Category(name);
    }
}

class ElectronicsProduct extends Product implements Shippable{
    private int warrantyMonths;
    private BigDecimal weight;

    public ElectronicsProduct(UUID uuid, String name, Category category, BigDecimal price, int warrantyMonths, BigDecimal weight) {
        super(uuid, name, category, price);
        if (warrantyMonths < 0) {
            throw new IllegalArgumentException("Warranty months cannot be negative.");
        }
        else {
            this.warrantyMonths = warrantyMonths;
            this.weight = weight;
        }
    }

    @Override
    public String productDetails() {
        return "Electronics: "+ name + ", Warranty: "+warrantyMonths + " months";
    }


    @Override
    public double calculateShippingCost() {
        if(weight.doubleValue() > 5.0){
            return weight.doubleValue() * (79+49);
        }
        else {
            return weight.doubleValue() * 79;
        }
    }

    @Override
    public double weight() {
        return weight.doubleValue();
    }
}

class FoodProduct extends Product implements Perishable, Shippable{
    private final LocalDate expiryDate;
    private final BigDecimal weight;

    public FoodProduct(UUID uuid, String name, Category category, BigDecimal price, LocalDate expiryDate, BigDecimal weight) {
        super(uuid, name, category, price);
        if (weight.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Weight cannot be negative.");
        } else if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }else {
        this.expiryDate = expiryDate;
        this.weight = weight;
        }
    }

    @Override
    public double calculateShippingCost() {
        return weight.doubleValue() * 50;
    }

    @Override
    public double weight() {
        return weight.doubleValue();
    }

    @Override
    public LocalDate expirationDate() {
        return isExpired();
    }

    @Override
    public LocalDate isExpired() {
        return Perishable.super.isExpired();
    }

    @Override
    public String productDetails(){
        return "Food: " + name +", Expires: " + expiryDate;
    }
}

abstract class Product {
    protected UUID id;
    protected String name;
    protected Category category;
    protected BigDecimal price;

    public Product(UUID uuid, String name, Category category, BigDecimal price) {
        this.id = uuid;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public UUID uuid() {
        return id;
    }
    public String name() {
        return name;
    }
    public Category category() {
        return category;
    }
    public BigDecimal price() {
        return price;
    }

    public void price(BigDecimal price) {
        this.price = price;
    }

    public String productDetails() {
        return "Product details: " + name;
    }
}

