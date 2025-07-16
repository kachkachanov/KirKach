package ru.KirillKachanov.tgBot.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ClientOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToMany(mappedBy = "clientOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("clientOrder")
    private List<OrderProduct> productOrders = new ArrayList<>();

    @Column(nullable = false)
    private Integer status;

    @Column(nullable = false)
    private Double total;

    public ClientOrder() {}

    public ClientOrder(Client client, Integer status, Double total) {
        this.client = client;
        this.status = status;
        this.total = total;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public List<OrderProduct> getProductOrders() { return productOrders; }
    public void setProductOrders(List<OrderProduct> productOrders) { this.productOrders = productOrders; }

    public void addProductOrder(OrderProduct productOrder) {
        productOrders.add(productOrder);
        productOrder.setClientOrder(this);
    }
}