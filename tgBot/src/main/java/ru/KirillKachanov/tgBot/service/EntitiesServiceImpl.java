package ru.KirillKachanov.tgBot.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.KirillKachanov.tgBot.entity.*;
import ru.KirillKachanov.tgBot.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EntitiesServiceImpl implements EntitiesService {

    private final ProductRepository productRepo;
    private final ClientRepository clientRepo;
    private final ClientOrderRepository orderRepo;
    private final OrderProductRepository orderProductRepo;
    private final CategoryRepository categoryRepo;

    public EntitiesServiceImpl(ProductRepository productRepo,
                               ClientRepository clientRepo,
                               ClientOrderRepository orderRepo,
                               OrderProductRepository orderProductRepo,
                               CategoryRepository categoryRepo) {
        this.productRepo = productRepo;
        this.clientRepo = clientRepo;
        this.orderRepo = orderRepo;
        this.orderProductRepo = orderProductRepo;
        this.categoryRepo = categoryRepo;
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

    @Override
    public Client getOrCreateClient(Long externalId, String fullName, String phoneNumber, String address) {
        return clientRepo.findByExternalId(externalId)
                .orElseGet(() -> {
                    Client newClient = new Client();
                    newClient.setExternalId(externalId);
                    newClient.setFullName(fullName);
                    newClient.setPhoneNumber(phoneNumber != null ? phoneNumber : "N/A");
                    newClient.setAddress(address != null ? address : "N/A");
                    return clientRepo.save(newClient);
                });
    }

    @Override
    public ClientOrder getOrCreateActiveOrder(Client client) {
        Optional<ClientOrder> activeOrder = orderRepo.findByClientAndStatus(client, 1);
        if (activeOrder.isPresent()) {
            return activeOrder.get();
        } else {
            ClientOrder newOrder = new ClientOrder();
            newOrder.setClient(client);
            newOrder.setStatus(1);
            newOrder.setTotal(0.0);
            return orderRepo.save(newOrder);
        }
    }

    @Override
    public void addProductToOrder(ClientOrder order, Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        Optional<OrderProduct> existingOrderProduct = orderProductRepo.findByClientOrderAndProduct(order, product);

        if (existingOrderProduct.isPresent()) {
            OrderProduct op = existingOrderProduct.get();
            op.setCountProduct(op.getCountProduct() + 1);
            orderProductRepo.save(op);
        } else {
            OrderProduct newOrderProduct = new OrderProduct();
            newOrderProduct.setClientOrder(order);
            newOrderProduct.setProduct(product);
            newOrderProduct.setCountProduct(1);
            orderProductRepo.save(newOrderProduct);
        }

        order.setTotal(calculateOrderTotal(order));
        orderRepo.save(order);
    }

    @Override
    public List<Category> getCategoriesByParentId(Long parentId) {
        if (parentId == null) {
            return categoryRepo.findByParentIsNull();
        } else {
            return categoryRepo.findByParentId(parentId);
        }
    }

    @Override
    public List<Product> getProductsByCategoryIdForDisplay(Long categoryId) {
        return productRepo.findByCategoryId(categoryId);
    }

    @Override
    public Double calculateOrderTotal(ClientOrder order) {
        return orderProductRepo.findByClientOrder(order).stream()
                .mapToDouble(op -> op.getProduct().getPrice() * op.getCountProduct())
                .sum();
    }

    @Override
    public void closeOrder(ClientOrder order) {
        if (orderProductRepo.findByClientOrder(order).isEmpty()) {
            throw new IllegalStateException("Невозможно закрыть пустой заказ.");
        }
        order.setStatus(2);
        orderRepo.save(order);
    }

    @Override
    public List<OrderProduct> getOrderProducts(ClientOrder order) {
        return orderProductRepo.findByClientOrder(order);
    }

    @Override
    public ClientOrder getActiveOrderByClientId(Long clientId) {
        Client client = clientRepo.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + clientId));
        return orderRepo.findByClientAndStatus(client, 1)
                .orElse(null);
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepo.findById(productId).orElse(null);
    }

    @Override
    public Client getClientById(Long clientId) {
        return clientRepo.findById(clientId).orElse(null);
    }
}