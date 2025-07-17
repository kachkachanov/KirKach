package ru.KirillKachanov.tgBot.controller;

import org.springframework.web.bind.annotation.*;
import ru.KirillKachanov.tgBot.entity.Product;
import ru.KirillKachanov.tgBot.entity.Client;
import ru.KirillKachanov.tgBot.entity.ClientOrder;
import ru.KirillKachanov.tgBot.service.EntitiesService;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class EntitiesRestController {

    private final EntitiesService service;

    public EntitiesRestController(EntitiesService service) {
        this.service = service;
    }

    @GetMapping(value = "/products/search", params = "categoryId")
    public List<Product> getProductsByCategory(@RequestParam Long categoryId) {
        return service.getProductsByCategoryId(categoryId);
    }

    @GetMapping("/clients/{id}/orders")
    public List<ClientOrder> getClientOrders(@PathVariable Long id) {
        return service.getClientOrders(id);
    }

    @GetMapping("/clients/{id}/products")
    public List<Product> getClientProducts(@PathVariable Long id) {
        return service.getClientProducts(id);
    }

    @GetMapping("/products/popular")
    public List<Product> getPopularProducts(@RequestParam Integer limit) {
        return service.getTopPopularProducts(limit);
    }

    // Дополнительное задание:

    @GetMapping(value = "/clients/search", params = "name")
    public List<Client> searchClientsByName(@RequestParam String name) {
        return service.searchClientsByName(name);
    }

    @GetMapping(value = "/products/search", params = "name")
    public List<Product> searchProductsByName(@RequestParam String name) {
        return service.searchProductsByName(name);
    }
}

