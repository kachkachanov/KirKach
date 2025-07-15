package ru.KirillKachanov.tgBot.repository;

import ru.KirillKachanov.tgBot.entity.ClientOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "client-orders", path = "client-orders")
public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {
}