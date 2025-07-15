package ru.KirillKachanov.tgBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.KirillKachanov.tgBot.entity.ProductOrder;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {
}