package ru.KirillKachanov.tgBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.KirillKachanov.tgBot.entity.OrderProduct;
import ru.KirillKachanov.tgBot.entity.ClientOrder; // Добавьте этот импорт

import java.time.LocalDateTime;
import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> findByCustomerNameAndOrderDate(String customerName, LocalDateTime orderDate);
    List<OrderProduct> findAllByClientOrder(ClientOrder clientOrder);
}