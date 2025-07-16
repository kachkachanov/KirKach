package ru.KirillKachanov.tgBot.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "product_orders")
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;

    private LocalDateTime orderDate;

    private Double totalPrice;

    @OneToMany(mappedBy = "productOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    public ProductOrder() {
    }

    public ProductOrder(String customerName, LocalDateTime orderDate, Double totalPrice, List<OrderProduct> orderProducts) {
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.orderProducts = orderProducts;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public List<OrderProduct> getOrderProducts() { return orderProducts; }
    public void setOrderProducts(List<OrderProduct> orderProducts) { this.orderProducts = orderProducts; }

    // Если тебе нужно просто получить список продуктов без количества
    @Transient
    public List<Product> getProducts() {
        return orderProducts.stream()
                .map(OrderProduct::getProduct)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "ProductOrder{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", orderDate=" + orderDate +
                ", totalPrice=" + totalPrice +
                ", products=" + getProducts() +
                '}';
    }
}
