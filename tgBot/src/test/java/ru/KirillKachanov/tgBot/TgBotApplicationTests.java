package ru.KirillKachanov.tgBot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.KirillKachanov.tgBot.entity.*;
import ru.KirillKachanov.tgBot.repository.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class FillingTests {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private ClientOrderRepository clientOrderRepository;

	@Autowired
	private OrderProductRepository orderProductRepository;

	@BeforeEach
	@Transactional
	public void clearDatabase() {
		// Очистка базы данных перед каждым тестом
		orderProductRepository.deleteAll();
		clientOrderRepository.deleteAll();
		productRepository.deleteAll();
		clientRepository.deleteAll();
		categoryRepository.deleteAll();
	}

	@Test
	@Transactional
	public void testCreateCategory() {
		Category category = new Category();
		category.setName("Electronics");
		category = categoryRepository.save(category);

		assertNotNull(category.getId(), "Категория должна быть сохранена с непустым ID");
	}

	@Test
	@Transactional
	public void testCreateProduct() {
		Category category = new Category();
		category.setName("Electronics");
		category = categoryRepository.save(category);

		Product product = new Product();
		product.setName("Smartphone");
		product.setDescription("High-end smartphone");
		product.setPrice(999.99);
		product.setCategory(category);
		product = productRepository.save(product);

		assertNotNull(product.getId(), "Продукт должен быть сохранен с непустым ID");
	}

	@Test
	@Transactional
	public void testCreateClient() {
		Client client = new Client();
		client.setExternalId(System.currentTimeMillis());
		client.setFullName("John Doe");
		client.setPhoneNumber("9876543210");
		client.setAddress("123 Main St");
		client = clientRepository.save(client);

		assertNotNull(client.getId(), "Клиент должен быть сохранен с непустым ID");
	}

	@Test
	@Transactional
	public void testCreateClientOrder() {
		Client client = new Client();
		client.setExternalId(System.currentTimeMillis());
		client.setFullName("John Doe");
		client.setPhoneNumber("9876543210");
		client.setAddress("123 Main St");
		client = clientRepository.save(client);

		ClientOrder clientOrder = new ClientOrder(client, 1, 0.0);
		clientOrder = clientOrderRepository.save(clientOrder);

		assertNotNull(clientOrder.getId(), "Заказ клиента должен быть сохранен с непустым ID");
	}

	@Test
	@Transactional
	public void testCreateOrderProduct() {
		// Создание категории
		Category category = new Category();
		category.setName("Electronics");
		category = categoryRepository.save(category);

		// Создание продукта
		Product product = new Product();
		product.setName("Smartphone");
		product.setDescription("High-end smartphone");
		product.setPrice(999.99);
		product.setCategory(category);
		product = productRepository.save(product);

		// Создание клиента
		Client client = new Client();
		client.setExternalId(System.currentTimeMillis());
		client.setFullName("John Doe");
		client.setPhoneNumber("9876543210");
		client.setAddress("123 Main St");
		client = clientRepository.save(client);

		// Создание заказа клиента
		ClientOrder clientOrder = new ClientOrder(client, 1, 0.0);
		clientOrder = clientOrderRepository.save(clientOrder);

		// Создание OrderProduct
		OrderProduct orderProduct = new OrderProduct();
		orderProduct.setCustomerName("Customer_" + System.currentTimeMillis());
		orderProduct.setProduct(product);
		orderProduct.setCountProduct(2L);
		orderProduct.setClientOrder(clientOrder);
		orderProduct.setOrderDate(LocalDateTime.now());
		orderProduct = orderProductRepository.save(orderProduct);

		assertNotNull(orderProduct.getId(), "OrderProduct должен быть сохранен с непустым ID");
	}
}