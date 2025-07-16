package ru.KirillKachanov.tgBot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.KirillKachanov.tgBot.entity.Category;
import ru.KirillKachanov.tgBot.entity.Client;
import ru.KirillKachanov.tgBot.entity.ClientOrder;
import ru.KirillKachanov.tgBot.entity.OrderProduct;
import ru.KirillKachanov.tgBot.entity.Product;
import ru.KirillKachanov.tgBot.repository.CategoryRepository;
import ru.KirillKachanov.tgBot.repository.ClientRepository;
import ru.KirillKachanov.tgBot.repository.ClientOrderRepository;
import ru.KirillKachanov.tgBot.repository.OrderProductRepository;
import ru.KirillKachanov.tgBot.repository.ProductRepository;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FillingCategoryProductTests {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private ClientOrderRepository clientOrderRepository;

	@Autowired
	private OrderProductRepository orderRepository;

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

		// Клиент с уникальным externalId
		Client client = new Client();
		client.setFullName("Иван Иванов");
		client.setExternalId(generateUniqueExternalId());
		client.setPhoneNumber("+71234567890");
		client.setAddress("ул. Тестовая, 1");
		client = clientRepository.save(client);

		// Заказ клиента
		ClientOrder clientOrder = new ClientOrder(client, 1, 0.0);
		clientOrder = clientOrderRepository.save(clientOrder);

		// Продукты в заказе
		List<Product> classicRollsProducts = productRepository.findAllByCategory(classicRolls);
		if (classicRollsProducts.isEmpty()) {
			throw new IllegalStateException("No products found for category: " + classicRolls.getName());
		}
		Product classicRoll1 = classicRollsProducts.get(0);

		List<Product> bakedRollsProducts = productRepository.findAllByCategory(bakedRolls);
		if (bakedRollsProducts.isEmpty()) {
			throw new IllegalStateException("No products found for category: " + bakedRolls.getName());
		}
		Product bakedRoll1 = bakedRollsProducts.get(0);

		// Создание через конструктор
		OrderProduct order1 = new OrderProduct("Иван Иванов", classicRoll1, 2L, clientOrder);
		OrderProduct order2 = new OrderProduct("Иван Иванов", bakedRoll1, 1L, clientOrder);

		// Синхронизация двусторонней связи
		clientOrder.addProductOrder(order1);
		clientOrder.addProductOrder(order2);
		clientOrderRepository.save(clientOrder);
	}

	private Long generateUniqueExternalId() {
		return System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(1000);
	}
}