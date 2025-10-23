package com.example;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Warehouse {
    private static Warehouse warehouse = new Warehouse();
    private final List<Product> products = new ArrayList<>();
    private Warehouse() {}

    public static Warehouse getInstance(String name) {
        if (warehouse == null) {
            warehouse = new Warehouse();
        }
        return warehouse;
    }

    public void addProduct(Product product) {
            if (product == null) {
                throw new IllegalArgumentException("Product cannot be null.");
            }
            if (products.stream().anyMatch(p -> p.id.equals(product.id))) {
                throw new IllegalArgumentException("Product with that id already exists, use updateProduct for updates.");
            }
            products.add(product);
    }

    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }

    public Optional<Product> getProductById(UUID id) {
        return products.stream().filter(f -> f.id == id).findFirst();
    }

    public void updateProductPrice(UUID id, BigDecimal price) {
        boolean found = products.stream().anyMatch(f -> f.id == id);
        if (found) {
            products.stream()
                    .filter(d -> d.id.equals(id))
                    .findFirst()
                    .ifPresent(product -> product.price(price));
        }
        else {
            throw new NoSuchElementException("Product not found with id:" + id);
        }
    }

    public List<Perishable> expiredProducts(){
        return products.stream()
                .filter(p -> p instanceof Perishable)
                .filter(p -> ((Perishable) p).isExpired())
                .map(product -> (Perishable) product)
                .toList();

    }

    public List<Shippable> shippableProducts() {
        return products.stream().filter( p -> p instanceof Shippable)
                .map(p -> (Shippable) p)
                .collect(Collectors.toList());
    }

    public void remove(UUID id) {
        products.removeIf(p -> p.id.equals(id));
    }

    public void clearProducts() {
        warehouse = new Warehouse();
    }

    public Map<Category, List<Product>> getProductsGroupedByCategories() {
        return products.stream().collect(Collectors.groupingBy(p -> p.category));
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }
}

class Category{
    private static final Map<String, Category> cache = new ConcurrentHashMap<>(); //Cache/FlyWeight implementation in category
    private final String name;

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
    private final int warrantyMonths;
    private final BigDecimal weight;

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
            return BigDecimal.valueOf((79+49));
        }
        else {
            return BigDecimal.valueOf(49);
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
        LocalDate now = LocalDate.now();
        return now.isAfter(expiryDate) ? expiryDate : now;
    }

    @Override
    public boolean isExpired() {
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

