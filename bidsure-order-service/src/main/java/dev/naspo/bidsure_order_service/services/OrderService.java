package dev.naspo.bidsure_order_service.services;

import dev.naspo.bidsure_order_service.dto.OrderDTO;
import dev.naspo.bidsure_order_service.models.*;
import dev.naspo.bidsure_order_service.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Responsible for order related business logic.
@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderClient orderClient;


    // Builds the Order from the OrderDTO, and calls to have it saved to the database.
    public Optional<Order> createOrder(OrderDTO orderDTO) {
        // Get all the entities needed to build the Order entity.
        User user = orderClient.getUserById(orderDTO.getUserId());
        PaymentMethod paymentMethod = orderClient.getPaymentMethodById(orderDTO.getPaymentId());
        Address address = orderClient.getAddressById(orderDTO.getAddressId());
        Auction auction = orderClient.getAuctionById(orderDTO.getAuctionId());

        // Check if any of the above are null.
        if (user == null || paymentMethod == null || address == null || auction == null) {
            return Optional.empty();
        }

        // Build the Order.
        Order order = OrderDTO.toEntity(orderDTO);
        order.setUser(user);
        order.setPaymentMethod(paymentMethod);
        order.setAddress(address);
        order.setAuction(auction);

        // Call to save the order to the database.
        return Optional.of(saveOrder(order));
    }

    // Saves the order to the database.
    @Transactional
    private Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public Optional<Order> getOrderById(int id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public List<Order> getUserOrders(int userId) {
        return orderRepository.findAllUserOrders(userId);
    }

    @Transactional
    public void deleteOrder(int orderId) {
        orderRepository.deleteById(orderId);
    }
}
