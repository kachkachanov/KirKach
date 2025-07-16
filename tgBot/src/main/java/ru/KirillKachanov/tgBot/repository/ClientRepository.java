package ru.KirillKachanov.tgBot.repository;

import ru.KirillKachanov.tgBot.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "clients", collectionResourceRel = "clients")
public interface ClientRepository extends JpaRepository<Client, Long> {}