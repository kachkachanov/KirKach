package ru.KirillKachanov.tgBot.repository;

import ru.KirillKachanov.tgBot.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "order-products", path = "order-products")
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
}