package ru.KirillKachanov.tgBot.controller;

import org.springframework.web.bind.annotation.*;
import ru.KirillKachanov.tgBot.entity.OrderProduct;
import ru.KirillKachanov.tgBot.repository.OrderProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/order-products")
public class OrderProductController {

    private final OrderProductRepository repository;

    public OrderProductController(OrderProductRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<OrderProduct> createOrderProduct(@RequestBody OrderProduct orderProduct) {
        OrderProduct savedOrder = repository.save(orderProduct);
        return new ResponseEntity<>(savedOrder, HttpStatus.CREATED); // Возвращает код 201
    }

    @GetMapping
    public List<OrderProduct> getAllOrderProducts() {
        return repository.findAll();
    }
}
