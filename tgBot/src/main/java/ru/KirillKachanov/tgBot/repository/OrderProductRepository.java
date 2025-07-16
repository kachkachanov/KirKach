package ru.KirillKachanov.tgBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.KirillKachanov.tgBot.entity.OrderProduct;

@RepositoryRestResource(path = "order-products", collectionResourceRel = "order-products")
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {}
