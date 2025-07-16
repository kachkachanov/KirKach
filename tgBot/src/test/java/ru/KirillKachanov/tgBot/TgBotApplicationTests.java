package ru.KirillKachanov.tgBot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.KirillKachanov.tgBot.entity.*;
import ru.KirillKachanov.tgBot.repository.*;

import java.util.List;

@SpringBootTest
class FillingCategoryProductTests {

	@Autowired private CategoryRepository categoryRepository;
	@Autowired private ProductRepository productRepository;
	@Autowired private ClientRepository clientRepository;
	@Autowired private ClientOrderRepository clientOrderRepository;
	@Autowired private OrderProductRepository orderProductRepository;

	@BeforeEach
	void clearDatabase() {
		orderProductRepository.deleteAll();
		clientOrderRepository.deleteAll();
		clientRepository.deleteAll();
		productRepository.deleteAll();
		categoryRepository.deleteAll();
	}

	private Category createCategory(String name, Category parent) {
		Category category = new Category();
		category.setName(name);
		category.setParent(parent);
		return categoryRepository.save(category);
	}

	private Product createProduct(Category category, String baseName, int index) {
		Product product = new Product();
		product.setName(baseName + " " + index);
		product.setDescription("Описание для " + baseName + " " + index);
		product.setPrice(100.0 + index * 10.0);
		product.setCategory(category);
		return productRepository.save(product);
	}

	private Client createClient() {
		Client client = new Client();
		client.setFullName("Иван Иванов");
		client.setExternalId(System.nanoTime()); // Гарантированно уникальное значение
		client.setPhoneNumber("+71234567890");
		client.setAddress("ул. Тестовая, 1");
		return clientRepository.save(client);
	}

	@Test
	void createCategoriesAndOrders() {
		// Создаем категории
		Category rolls = createCategory("Роллы", null);
		Category classicRolls = createCategory("Классические роллы", rolls);
		Category bakedRolls = createCategory("Запеченные роллы", rolls);

		// Создаем продукты
		Product classicRoll1 = createProduct(classicRolls, "Классический ролл", 1);
		Product classicRoll2 = createProduct(classicRolls, "Классический ролл", 2);
		Product bakedRoll1 = createProduct(bakedRolls, "Запеченный ролл", 1);

		// Создаем клиента
		Client client = createClient();

		// Создаем заказ
		ClientOrder order = new ClientOrder(client, 1, 0.0);
		order = clientOrderRepository.save(order);

		// Добавляем продукты в заказ
		OrderProduct item1 = new OrderProduct(
				client.getFullName(),
				classicRoll1,
				2L,
				order
		);

		OrderProduct item2 = new OrderProduct(
				client.getFullName(),
				bakedRoll1,
				1L,
				order
		);

		// Сохраняем элементы заказа
		orderProductRepository.save(item1);
		orderProductRepository.save(item2);
	}
}