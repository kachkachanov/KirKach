package ru.KirillKachanov.tgBot.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_products", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"customerName", "orderDate", "product_id", "client_order_id"})
})
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;

    @Column(nullable = false)
    private Long countProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_order_id", nullable = false)
    @JsonIgnoreProperties({"orderProducts", "hibernateLazyInitializer", "handler"})
    private ClientOrder clientOrder;

    // Конструкторы
    public OrderProduct() {}

    public OrderProduct(String customerName, Product product, Long countProduct, ClientOrder clientOrder) {
        this.customerName = customerName;
        this.product = product;
        this.countProduct = countProduct;
        this.clientOrder = clientOrder;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Long getCountProduct() { return countProduct; }
    public void setCountProduct(Long countProduct) { this.countProduct = countProduct; }
    public ClientOrder getClientOrder() { return clientOrder; }
    public void setClientOrder(ClientOrder clientOrder) { this.clientOrder = clientOrder; }
}