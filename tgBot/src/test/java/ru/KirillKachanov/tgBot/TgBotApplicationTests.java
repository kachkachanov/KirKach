package ru.KirillKachanov.tgBot;

import ru.KirillKachanov.tgBot.entity.Category;
import ru.KirillKachanov.tgBot.entity.Client;
import ru.KirillKachanov.tgBot.entity.ClientOrder;
import ru.KirillKachanov.tgBot.entity.Product;
import ru.KirillKachanov.tgBot.entity.ProductOrder;
import ru.KirillKachanov.tgBot.repository.CategoryRepository;
import ru.KirillKachanov.tgBot.repository.ClientOrderRepository;
import ru.KirillKachanov.tgBot.repository.ClientRepository;
import ru.KirillKachanov.tgBot.repository.ProductOrderRepository;
import ru.KirillKachanov.tgBot.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class FillingCategoryProductTests {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductOrderRepository orderRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private ClientOrderRepository clientOrderRepository;

	@Test
	void createCategoriesAndOrders() {
		// Корневые категории
		Category pizza = saveCategory("Пицца", null);
		Category rolls = saveCategory("Роллы", null);
		Category burgers = saveCategory("Бургеры", null);
		Category drinks = saveCategory("Напитки", null);

		// Подкатегории для Роллы
		Category classicRolls = saveCategory("Классические роллы", rolls);
		Category bakedRolls = saveCategory("Запеченные роллы", rolls);

		// Продукты
		addProductsToCategory(classicRolls, "Классический ролл");
		addProductsToCategory(bakedRolls, "Запеченный ролл");

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
			product.setPrice(100.0 + i * 10.0);
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
