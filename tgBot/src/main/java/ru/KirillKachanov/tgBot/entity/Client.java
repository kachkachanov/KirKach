package ru.KirillKachanov.tgBot.entity;

import jakarta.persistence.*;

@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private Long externalId;

    @Column(nullable = false, length = 255)
    private String fullName;

    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @Column(nullable = false, length = 400)
    private String address;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getExternalId() { return externalId; }
    public void setExternalId(Long externalId) { this.externalId = externalId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}