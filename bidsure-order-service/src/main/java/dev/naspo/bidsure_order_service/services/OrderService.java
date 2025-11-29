package dev.naspo.bidsure_order_service.services;

import dev.naspo.bidsure_order_service.models.Order;
import dev.naspo.bidsure_order_service.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Responsible for order related business logic.
@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;

    @Transactional
    public Order createOrder(Order order) {

    }
}
