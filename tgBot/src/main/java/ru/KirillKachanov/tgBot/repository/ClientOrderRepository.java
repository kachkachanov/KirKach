package ru.KirillKachanov.tgBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.KirillKachanov.tgBot.entity.ClientOrder;

import java.util.List;

public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {

    @Query("SELECT o FROM ClientOrder o WHERE o.client.id = :clientId")
    List<ClientOrder> findByClientId(@Param("clientId") Long clientId);
}
