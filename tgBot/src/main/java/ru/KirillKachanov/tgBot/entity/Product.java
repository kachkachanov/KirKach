package ru.KirillKachanov.tgBot.entity;

import jakarta.persistence.*;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Category category;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, length = 400)
    private String description;

    @Column(nullable = false)
    private Double price;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}