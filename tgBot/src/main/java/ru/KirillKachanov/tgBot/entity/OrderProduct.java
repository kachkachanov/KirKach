package ru.KirillKachanov.tgBot.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private ClientOrder clientOrder;

    @ManyToOne(optional = false)
    private Product product;

    @Column(nullable = false)
    private Integer countProduct;

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClientOrder getClientOrder() {
        return clientOrder;
    }

    public void setClientOrder(ClientOrder clientOrder) {
        this.clientOrder = clientOrder;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getCountProduct() {
        return countProduct;
    }

    public void setCountProduct(Integer countProduct) {
        this.countProduct = countProduct;
    }
}