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

		// Клиент
		Client client = new Client();
		client.setName("Иван Иванов");
		clientRepository.save(client);

		// Заказ клиента
		ClientOrder clientOrder = new ClientOrder(client, 1, 0.0); // status = 1 (например, "в процессе")
		clientOrderRepository.save(clientOrder);

		// Продукты в заказе
		LocalDateTime orderDate = LocalDateTime.now();
		Product classicRoll1 = productRepository.findAllByCategory(classicRolls).get(0);
		Product bakedRoll1 = productRepository.findAllByCategory(bakedRolls).get(0);

		ProductOrder order1 = new ProductOrder("Иван Иванов", orderDate, classicRoll1, 2L, clientOrder);
		ProductOrder order2 = new ProductOrder("Иван Иванов", orderDate, bakedRoll1, 1L, clientOrder);

		orderRepository.save(order1);
		orderRepository.save(order2);

		// Динамическое вычисление общей стоимости
		Double total = orderRepository.findAllByClientOrder(clientOrder)
				.stream()
				.mapToDouble(order -> order.getProduct().getPrice() * order.getCountProduct())
				.sum();
		clientOrder.setTotal(total);
		clientOrderRepository.save(clientOrder);

		System.out.println("Общая стоимость заказа для " + client.getName() + ": " + total);
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
