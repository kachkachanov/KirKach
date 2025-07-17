package ru.KirillKachanov.tgBot.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.KirillKachanov.tgBot.entity.Client;
import ru.KirillKachanov.tgBot.entity.ClientOrder;
import ru.KirillKachanov.tgBot.entity.OrderProduct;
import ru.KirillKachanov.tgBot.entity.Product;
import ru.KirillKachanov.tgBot.repository.ClientOrderRepository;
import ru.KirillKachanov.tgBot.repository.ClientRepository;
import ru.KirillKachanov.tgBot.repository.OrderProductRepository;
import ru.KirillKachanov.tgBot.repository.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EntitiesServiceImpl implements EntitiesService {

    private final ProductRepository productRepo;
    private final ClientRepository clientRepo;
    private final ClientOrderRepository orderRepo;
    private final OrderProductRepository orderProductRepo;

    public EntitiesServiceImpl(ProductRepository productRepo,
                               ClientRepository clientRepo,
                               ClientOrderRepository orderRepo,
                               OrderProductRepository orderProductRepo) {
        this.productRepo = productRepo;
        this.clientRepo = clientRepo;
        this.orderRepo = orderRepo;
        this.orderProductRepo = orderProductRepo;
    }

    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepo.findByCategoryId(categoryId);
    }

    @Override
    public List<ClientOrder> getClientOrders(Long clientId) {
        return orderRepo.findByClientId(clientId);
    }

    @Override
    public List<Product> getClientProducts(Long clientId) {
        return orderProductRepo.findAllProductsByClientId(clientId);
    }

    @Override
    public List<Product> getTopPopularProducts(Integer limit) {
        return productRepo.findTopPopularProducts(PageRequest.of(0, limit));
    }

    @Override
    public List<Client> searchClientsByName(String name) {
        return clientRepo.searchByName(name);
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        return productRepo.searchByName(name);
    }
}

