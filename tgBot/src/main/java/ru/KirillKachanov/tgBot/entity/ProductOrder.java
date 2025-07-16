package ru.KirillKachanov.tgBot.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_orders")
public class ProductOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Long countProduct;

    @ManyToOne
    @JoinColumn(name = "client_order_id")
    private ClientOrder clientOrder;

    public ProductOrder() {
    }

    public ProductOrder(String customerName, LocalDateTime orderDate, Product product, Long countProduct, ClientOrder clientOrder) {
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.product = product;
        this.countProduct = countProduct;
        this.clientOrder = clientOrder;
    }

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