package ru.KirillKachanov.tgBot;

import org.springframework.beans.factory.annotation.Autowired;
import ru.KirillKachanov.tgBot.entity.Category;
import ru.KirillKachanov.tgBot.entity.Product;
import ru.KirillKachanov.tgBot.repository.CategoryRepository;
import ru.KirillKachanov.tgBot.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest
class FillingCategoryProductTests {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ProductRepository productRepository;

	@Test
	void createCategoriesAndProducts() {
		// Корневые категории
		Category pizza = saveCategory("Пицца", null);
		Category rolls = saveCategory("Роллы", null);
		Category burgers = saveCategory("Бургеры", null);
		Category drinks = saveCategory("Напитки", null);

		// Подкатегории для Роллы
		Category classicRolls = saveCategory("Классические роллы", rolls);
		Category bakedRolls = saveCategory("Запеченные роллы", rolls);
		Category sweetRolls = saveCategory("Сладкие роллы", rolls);
		Category sets = saveCategory("Наборы", rolls);

		// Подкатегории для Бургеры
		Category classicBurgers = saveCategory("Классические бургеры", burgers);
		Category spicyBurgers = saveCategory("Острые бургеры", burgers);

		// Подкатегории для Напитки
		Category soda = saveCategory("Газированные напитки", drinks);
		Category energy = saveCategory("Энергетические напитки", drinks);
		Category juice = saveCategory("Соки", drinks);
		Category otherDrinks = saveCategory("Другие", drinks);

		// Продукты (по 3 для каждой подкатегории)
		addProductsToCategory(classicRolls, "Классический ролл");
		addProductsToCategory(bakedRolls, "Запеченный ролл");
		addProductsToCategory(sweetRolls, "Сладкий ролл");
		addProductsToCategory(sets, "Набор роллов");

		addProductsToCategory(classicBurgers, "Классический бургер");
		addProductsToCategory(spicyBurgers, "Острый бургер");

		addProductsToCategory(soda, "Газировка");
		addProductsToCategory(energy, "Энергетик");
		addProductsToCategory(juice, "Сок");
		addProductsToCategory(otherDrinks, "Напиток");
	}

	private Category saveCategory(String name, Category parent) {
		Category category = new Category();
		category.setName(name);
		category.setParent(parent);
		return categoryRepository.save(category);
	}

	private void addProductsToCategory(Category category, String baseName) {
		for (int i = 1; i <= 3; i++) {
			Product product = new Product();
			product.setName(baseName + " " + i);
			product.setDescription("Описание для " + baseName + " " + i);
			product.setPrice(100 + i * 10);
			product.setCategory(category);
			productRepository.save(product);
		}
	}
}
@SpringBootTest
class TgBotApplicationTests {

	@Test
	void contextLoads() {
	}

}
