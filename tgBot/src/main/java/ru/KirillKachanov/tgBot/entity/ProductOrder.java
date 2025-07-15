package ru.KirillKachanov.tgBot.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product_orders")
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;

    private LocalDateTime orderDate;

    private Double totalPrice;

    @ManyToMany
    @JoinTable(
            name = "product_order_products",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;


    public ProductOrder() {
    }

    public ProductOrder(String customerName, LocalDateTime orderDate, Double totalPrice, List<Product> products) {
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.products = products;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }

    @Override
    public String toString() {
        return "ProductOrder{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", orderDate=" + orderDate +
                ", totalPrice=" + totalPrice +
                ", products=" + products +
                '}';
    }
}