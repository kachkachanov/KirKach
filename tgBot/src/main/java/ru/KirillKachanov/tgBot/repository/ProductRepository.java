package ru.KirillKachanov.tgBot.repository;
import ru.KirillKachanov.tgBot.entity.Product;
import ru.KirillKachanov.tgBot.entity.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "products", collectionResourceRel = "products")
public interface ProductRepository extends JpaRepository<Product, Long> {}