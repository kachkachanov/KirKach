package ru.KirillKachanov.tgBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.KirillKachanov.tgBot.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
