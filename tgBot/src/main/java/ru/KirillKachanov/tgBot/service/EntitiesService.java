package ru.KirillKachanov.tgBot.service;

import ru.KirillKachanov.tgBot.entity.*;
import ru.KirillKachanov.tgBot.repository.ClientOrderRepository; // Убедитесь, что этот импорт есть, если используется в реализации
import java.util.List;
import java.util.Optional;

public interface EntitiesService {
    List<Product> getProductsByCategoryId(Long categoryId);
    List<ClientOrder> getClientOrders(Long clientId);
    List<Product> getClientProducts(Long clientId);
    List<Product> getTopPopularProducts(Integer limit);
    List<Client> searchClientsByName(String name);
    List<Product> searchProductsByName(String name);

    Client getOrCreateClient(Long externalId, String fullName, String phoneNumber, String address);
    ClientOrder getOrCreateActiveOrder(Client client);
    void addProductToOrder(ClientOrder order, Long productId);
    List<Category> getCategoriesByParentId(Long parentId);
    List<Product> getProductsByCategoryIdForDisplay(Long categoryId);
    Double calculateOrderTotal(ClientOrder order);
    void closeOrder(ClientOrder order);
    List<OrderProduct> getOrderProducts(ClientOrder order);
    ClientOrder getActiveOrderByClientId(Long clientId);

    // Новый метод для получения продукта по ID
    Product getProductById(Long productId);
    // Новый метод для получения клиента по ID
    Client getClientById(Long clientId);

    // *** ДОБАВЬТЕ ЭТОТ МЕТОД ***
    ClientOrder getOrderById(Long orderId);
}