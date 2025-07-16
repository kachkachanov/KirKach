package ru.KirillKachanov.tgBot.entity;

import jakarta.persistence.*;

@Entity
public class OrderProduct {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_order_id")
    private ClientOrder clientOrder;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "product_order_id")
    private ProductOrder productOrder;
    @Column(nullable = false)
    private Long countProduct;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ClientOrder getClientOrder() { return clientOrder; }
    public void setClientOrder(ClientOrder clientOrder) { this.clientOrder = clientOrder; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Long getCountProduct() { return countProduct; }
    public void setCountProduct(Long countProduct) { this.countProduct = countProduct; }
}
