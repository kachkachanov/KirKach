package ru.KirillKachanov.tgBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.KirillKachanov.tgBot.entity.Client;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT c FROM Client c WHERE LOWER(c.fullName) LIKE LOWER(CONCAT('%', :namePart, '%'))")
    List<Client> searchByName(@Param("namePart") String namePart);
}
