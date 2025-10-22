package com.example;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Warehouse {
    private static Warehouse warehouse = new Warehouse();
    private Warehouse() {}
    private static Map<String, Warehouse> prodCache = new ConcurrentHashMap<>();


    public static Warehouse getInstance(String name) {
        if (warehouse == null) {
            warehouse = new Warehouse();
        }
        return warehouse; //todo fix singleton instance
    }

    public Product addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }else {
            return warehouse.addProduct(product);
        }
    }

    public List<Product> getProducts() {
        return List.of(); //todo fix getproducts
    }

    public Optional<Product> getProductById(UUID id) {
        //return List.of(warehouse.getProducts()).stream().filter(f -> f.id.equals(id)).findFirst(); //todo id not working?
        var findProduct =  warehouse.getProducts().stream().anyMatch(f -> f.id.equals(id));
        if(!findProduct){
            return Optional.empty();
        }
        return Optional.of((Product) warehouse.getProducts().stream().filter(f -> f.id.equals(id)));
    }

    public void updateProductPrice(UUID id, BigDecimal price) {
        warehouse.getProductById(id).stream().findFirst().ifPresent(product -> product.price = price);
    }

    public List<Perishable> expiredProducts(){
        return warehouse.expiredProducts();
    }

    public List<Shippable> shippableProducts() {
        return warehouse.shippableProducts();
    }

    public void remove(UUID id) {
        var items = new java.util.ArrayList<>(getProductById(id).stream().toList());
        items.removeIf(product -> product.id == id);
    }


    public void clearProducts() {
        warehouse = new Warehouse();
    }

    public Map<Category, List<Product>> getProductsGroupedByCategories() {
        return warehouse.getProducts().stream().collect(Collectors.groupingBy(Product::category));
    }


    public boolean isEmpty() {
        warehouse = new Warehouse();
        return warehouse.isEmpty();
    }
}

class Category{
    private static final Map<String, Category> cache = new ConcurrentHashMap<>(); //Cache/FlyWeight implementation in category
    private String name;
    private Category(String name) {
            this.name = name.substring(0, 1).toUpperCase() + name.substring(1);
    }
    public String getName() {
        return name;
    }

    public static Category of(String name) {
        if(name == null) {
            throw new IllegalArgumentException("Category name can't be null");
        }else if (name.isEmpty()){
            throw new IllegalArgumentException("Category name can't be blank");
        } else if (name.isBlank()) {
            throw new IllegalArgumentException("Category name can't be blank");
        }else{
        String normName = name.substring(0, 1).toUpperCase() + name.substring(1);
        return cache.computeIfAbsent(normName,Category::new);
        }
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
    public BigDecimal calculateShippingCost() {
        if(weight.doubleValue() > 5.0){
            return BigDecimal.valueOf(weight.doubleValue() * (79+49));
        }
        else {
            return BigDecimal.valueOf(weight.doubleValue() * 79);
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
    public BigDecimal calculateShippingCost() {
        return BigDecimal.valueOf(weight.doubleValue() * 50);
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

