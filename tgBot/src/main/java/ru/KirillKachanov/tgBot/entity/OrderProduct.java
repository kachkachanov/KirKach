package ru.KirillKachanov.tgBot.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_products")
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private LocalDateTime orderDate;

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
    public OrderProduct() {
        this.orderDate = LocalDateTime.now();
    }

    public OrderProduct(String customerName, Product product, Long countProduct, ClientOrder clientOrder) {
        this.customerName = customerName;
        this.product = product;
        this.countProduct = countProduct;
        this.clientOrder = clientOrder;
        this.orderDate = LocalDateTime.now();
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

    @Override
    public String toString() {
        return "OrderProduct{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", orderDate=" + orderDate +
                ", productId=" + (product != null ? product.getId() : "null") +
                ", countProduct=" + countProduct +
                ", clientOrderId=" + (clientOrder != null ? clientOrder.getId() : "null") +
                '}';
    }
}