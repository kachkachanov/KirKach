package ru.KirillKachanov.tgBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.KirillKachanov.tgBot.entity.ProductOrder;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {
    List<ProductOrder> findByCustomerNameAndOrderDate(String customerName, LocalDateTime orderDate);
}