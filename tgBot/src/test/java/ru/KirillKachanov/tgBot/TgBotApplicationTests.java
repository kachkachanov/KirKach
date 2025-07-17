package ru.KirillKachanov.tgBot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.KirillKachanov.tgBot.entity.*;
import ru.KirillKachanov.tgBot.repository.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FillingTests {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ProductRepository productRepository;

	@Test
	public void fillCategoriesAndProducts() {
		Category pizza = categoryRepository.save(createCategory("Пицца", null));
		Category rolls = categoryRepository.save(createCategory("Роллы", null));
		Category burgers = categoryRepository.save(createCategory("Бургеры", null));
		Category drinks = categoryRepository.save(createCategory("Напитки", null));

		Category classicRolls = categoryRepository.save(createCategory("Классические роллы", rolls));
		Category bakedRolls = categoryRepository.save(createCategory("Запеченные роллы", rolls));
		Category sweetRolls = categoryRepository.save(createCategory("Сладкие роллы", rolls));
		Category sets = categoryRepository.save(createCategory("Наборы", rolls));

		Category classicBurgers = categoryRepository.save(createCategory("Классические бургеры", burgers));
		Category spicyBurgers = categoryRepository.save(createCategory("Острые бургеры", burgers));

		Category soda = categoryRepository.save(createCategory("Газированные напитки", drinks));
		Category energy = categoryRepository.save(createCategory("Энергетические напитки", drinks));
		Category juices = categoryRepository.save(createCategory("Соки", drinks));
		Category other = categoryRepository.save(createCategory("Другие", drinks));

		addProductsToCategory(classicRolls, "Ролл");
		addProductsToCategory(bakedRolls, "Запеченный ролл");
		addProductsToCategory(sweetRolls, "Сладкий ролл");
		addProductsToCategory(sets, "Сет");

		addProductsToCategory(classicBurgers, "Бургер");
		addProductsToCategory(spicyBurgers, "Острый бургер");

		addProductsToCategory(soda, "Газировка");
		addProductsToCategory(energy, "Энергетик");
		addProductsToCategory(juices, "Сок");
		addProductsToCategory(other, "Напиток");
	}

	private Category createCategory(String name, Category parent) {
		Category category = new Category();
		category.setName(name);
		category.setParent(parent);
		return category;
	}

	private void addProductsToCategory(Category category, String prefix) {
		for (int i = 1; i <= 3; i++) {
			Product product = new Product();
			product.setCategory(category);
			product.setName(prefix + " " + i);
			product.setDescription("Описание для " + prefix + " " + i);
			product.setPrice(100.0 + i);
			productRepository.save(product);
		}
	}
}
