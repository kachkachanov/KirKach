package ru.KirillKachanov.tgBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.KirillKachanov.tgBot.entity.OrderProduct;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductOrderRepository extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> findByCustomerNameAndOrderDate(String customerName, LocalDateTime orderDate);
}