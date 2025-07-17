package ru.KirillKachanov.tgBot.service;

import ru.KirillKachanov.tgBot.entity.Product;
import ru.KirillKachanov.tgBot.entity.ClientOrder;
import ru.KirillKachanov.tgBot.entity.Client;

import java.util.List;

public interface EntitiesService {


    List<Product> getProductsByCategoryId(Long categoryId);


    List<ClientOrder> getClientOrders(Long clientId);


    List<Product> getClientProducts(Long clientId);


    List<Product> getTopPopularProducts(Integer limit);


    List<Client> searchClientsByName(String name);


    List<Product> searchProductsByName(String name);
}

