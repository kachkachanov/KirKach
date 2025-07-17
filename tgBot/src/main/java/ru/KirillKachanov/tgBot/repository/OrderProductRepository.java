package ru.KirillKachanov.tgBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.KirillKachanov.tgBot.entity.OrderProduct;
import ru.KirillKachanov.tgBot.entity.Product;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

    @Query("SELECT op.product FROM OrderProduct op WHERE op.clientOrder.client.id = :clientId")
    List<Product> findAllProductsByClientId(@Param("clientId") Long clientId);
}
