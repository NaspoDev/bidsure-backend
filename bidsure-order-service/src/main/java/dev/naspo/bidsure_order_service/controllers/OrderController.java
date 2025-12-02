package dev.naspo.bidsure_order_service.controllers;

import dev.naspo.bidsure_order_service.dto.OrderDTO;
import dev.naspo.bidsure_order_service.models.*;
import dev.naspo.bidsure_order_service.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Endpoint to create an order.
    // This is only expected to be called by the Auction Service. Orders are never created
    // directly from the client.
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        Optional<Order> order = orderService.createOrder(orderDTO);
        if (order.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(order.get());
        }
    }

    // Get a single order by id.
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable int id) {
        Optional<Order> order = orderService.getOrderById(id);
        if (order.isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok(order.get());
        }
    }

    // Get all a user's orders.
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable int userId) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    // No PUT mapping needed. Orders can't be changed.

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable int id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }
}
