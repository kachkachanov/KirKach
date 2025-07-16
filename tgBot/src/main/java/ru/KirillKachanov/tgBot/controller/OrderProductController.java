package ru.KirillKachanov.tgBot.controller;

import org.springframework.web.bind.annotation.*;
import ru.KirillKachanov.tgBot.entity.OrderProduct;
import ru.KirillKachanov.tgBot.repository.OrderProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/orders/order-products")
public class OrderProductController {

    private final OrderProductRepository repository;

    public OrderProductController(OrderProductRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<OrderProduct> createOrderProduct(@RequestBody OrderProduct orderProduct) {
        if (orderProduct == null || orderProduct.getProduct() == null || orderProduct.getClientOrder() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 вместо 404
        }
        try {
            OrderProduct savedOrder = repository.save(orderProduct);
            return new ResponseEntity<>(savedOrder, HttpStatus.CREATED); // 201
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    @GetMapping
    public List<OrderProduct> getAllOrderProducts() {
        return repository.findAll();
    }
}