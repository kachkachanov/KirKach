package ru.KirillKachanov.tgBot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import ru.KirillKachanov.tgBot.entity.*;
import ru.KirillKachanov.tgBot.repository.*;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FillingTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

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
		System.out.println("После очистки: OrderProducts = " + orderProductRepository.findAll().size());
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
	public void testCreateProduct() throws InterruptedException {
		// Даём серверу время на запуск
		TimeUnit.SECONDS.sleep(5);

		Category category = new Category();
		category.setName("Electronics");
		category = categoryRepository.save(category);

		Product product = new Product();
		product.setName("Smartphone");
		product.setDescription("High-end smartphone");
		product.setPrice(999.99);
		product.setCategory(category);
		product = productRepository.save(product);

		assertNotNull(product.getId(), "Продукт должен быть сохранён с непустым ID");

		// Проверка через HTTP-запрос
		String json = String.format("""
            {
                "name": "Smartphone",
                "description": "High-end smartphone",
                "price": 999.99,
                "category": "/categories/%d"
            }
            """, category.getId());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(json, headers);

		ResponseEntity<Product> response = restTemplate.postForEntity(
				"http://localhost:" + port + "/products",
				request,
				Product.class
		);

		if (response.getStatusCode() != HttpStatus.CREATED) {
			System.out.println("Ошибка при создании продукта: " + response.getStatusCode());
			System.out.println("Тело ответа: " + response.getBody());
			System.out.println("Заголовки: " + response.getHeaders());
			System.out.println("Отправленный JSON: " + json);
		}

		assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Ожидается код 201 для создания продукта");
	}

	@Test
	@Transactional
	public void testCreateClient() throws InterruptedException {
		// Даём серверу время на запуск
		TimeUnit.SECONDS.sleep(5);

		Client client = new Client();
		client.setExternalId(System.currentTimeMillis());
		client.setFullName("John Doe");
		client.setPhoneNumber("9876543210");
		client.setAddress("123 Main St");
		client = clientRepository.save(client);

		assertNotNull(client.getId(), "Клиент должен быть сохранён с непустым ID");

		// Проверка через HTTP-запрос
		String json = String.format("""
            {
                "externalId": %d,
                "fullName": "John Doe",
                "phoneNumber": "9876543210",
                "address": "123 Main St"
            }
            """, System.currentTimeMillis());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(json, headers);

		ResponseEntity<Client> response = restTemplate.postForEntity(
				"http://localhost:" + port + "/clients",
				request,
				Client.class
		);

		if (response.getStatusCode() != HttpStatus.CREATED) {
			System.out.println("Ошибка при создании клиента: " + response.getStatusCode());
			System.out.println("Тело ответа: " + response.getBody());
			System.out.println("Заголовки: " + response.getHeaders());
			System.out.println("Отправленный JSON: " + json);
		}

		assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Ожидается код 201 для создания клиента");
	}

	@Test
	@Transactional
	public void testCreateClientOrder() throws InterruptedException {
		// Даём серверу время на запуск
		TimeUnit.SECONDS.sleep(5);

		Client client = new Client();
		client.setExternalId(System.currentTimeMillis());
		client.setFullName("John Doe");
		client.setPhoneNumber("9876543210");
		client.setAddress("123 Main St");
		client = clientRepository.save(client);

		ClientOrder clientOrder = new ClientOrder(client, 1, 0.0);
		clientOrder = clientOrderRepository.save(clientOrder);

		assertNotNull(clientOrder.getId(), "Заказ клиента должен быть сохранён с непустым ID");

		// Проверка через HTTP-запрос
		String json = String.format("""
            {
                "client": "/clients/%d",
                "orderNumber": 1,
                "totalAmount": 0.0
            }
            """, client.getId());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(json, headers);

		ResponseEntity<ClientOrder> response = restTemplate.postForEntity(
				"http://localhost:" + port + "/client-orders",
				request,
				ClientOrder.class
		);

		if (response.getStatusCode() != HttpStatus.CREATED) {
			System.out.println("Ошибка при создании заказа клиента: " + response.getStatusCode());
			System.out.println("Тело ответа: " + response.getBody());
			System.out.println("Заголовки: " + response.getHeaders());
			System.out.println("Отправленный JSON: " + json);
		}

		assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Ожидается код 201 для создания заказа клиента");
	}

	@Test
	@Transactional
	public void testCreateOrderProduct() throws InterruptedException {
		// Даём серверу время на запуск
		TimeUnit.SECONDS.sleep(10);

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

		// Создание OrderProduct через репозиторий
		OrderProduct orderProduct = new OrderProduct();
		orderProduct.setCustomerName("Customer_" + System.currentTimeMillis());
		orderProduct.setProduct(product);
		orderProduct.setCountProduct(2L);
		orderProduct.setClientOrder(clientOrder);
		orderProduct.setOrderDate(LocalDateTime.now().plusNanos(System.nanoTime()));
		System.out.println("Создаём OrderProduct через репозиторий: " + orderProduct);
		orderProduct = orderProductRepository.save(orderProduct);

		assertNotNull(orderProduct.getId(), "OrderProduct должен быть сохранён с непустым ID");

		// POST-запрос к /order-products
		String json = String.format("""
            {
                "customerName": "Customer_%d",
                "orderDate": "%s",
                "product": "/products/%d",
                "countProduct": 2,
                "clientOrder": "/client-orders/%d"
            }
            """, System.currentTimeMillis(), LocalDateTime.now().plusNanos(System.nanoTime()), product.getId(), clientOrder.getId());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(json, headers);

		System.out.println("Отправляемый JSON: " + json);
		ResponseEntity<OrderProduct> response = restTemplate.postForEntity(
				"http://localhost:" + port + "/order-products",
				request,
				OrderProduct.class
		);

		if (response.getStatusCode() != HttpStatus.CREATED) {
			System.out.println("Ошибка при создании OrderProduct: " + response.getStatusCode());
			System.out.println("Тело ответа: " + response.getBody());
			System.out.println("Заголовки: " + response.getHeaders());
		}

		assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Ожидается код 201 для создания OrderProduct, но получен: " + response.getStatusCode());
	}
}