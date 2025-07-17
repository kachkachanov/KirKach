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
        return productRepo.findAll().stream()
                .filter(p -> p.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientOrder> getClientOrders(Long clientId) {
        return orderRepo.findAll().stream()
                .filter(o -> o.getClient().getId().equals(clientId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getClientProducts(Long clientId) {
        List<ClientOrder> orders = getClientOrders(clientId);
        List<Long> orderIds = orders.stream().map(ClientOrder::getId).toList();
        return orderProductRepo.findAll().stream()
                .filter(op -> orderIds.contains(op.getClientOrder().getId()))
                .map(OrderProduct::getProduct)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getTopPopularProducts(Integer limit) {
        Map<Product, Integer> popularityMap = new HashMap<>();

        for (OrderProduct op : orderProductRepo.findAll()) {
            Product product = op.getProduct();
            popularityMap.put(product,
                    popularityMap.getOrDefault(product, 0) + op.getCountProduct());
        }

        return popularityMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<Client> searchClientsByName(String name) {
        return clientRepo.findAll().stream()
                .filter(client -> client.getFullName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        return productRepo.findAll().stream()
                .filter(product -> product.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
}

