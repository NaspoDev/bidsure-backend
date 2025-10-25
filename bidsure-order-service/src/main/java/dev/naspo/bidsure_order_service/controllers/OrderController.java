package dev.naspo.bidsure_order_service.controllers;

import dev.naspo.bidsure_order_service.HibernateManager;
import dev.naspo.bidsure_order_service.dto.OrderDTO;
import dev.naspo.bidsure_order_service.models.*;
import jakarta.validation.Valid;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    HibernateManager hibernateManager;

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Query to get the entities that Order needs.
            User user = session.find(User.class, orderDTO.getUserId());
            PaymentMethod paymentMethod = session.find(PaymentMethod.class, orderDTO.getPaymentId());
            Address address = session.find(Address.class, orderDTO.getAddressId());
            Auction auction = session.find(Auction.class, orderDTO.getAuctionId());

            // If any of the above are null, return NOT FOUND response.
            if (user == null || paymentMethod == null || address == null || auction == null) {
                return ResponseEntity.notFound().build();
            }

            // Create an order and transfer it the data from the DTO.
            Order order = new Order();
            order.setTotalCost(orderDTO.getTotalCost());
            order.setUser(user);
            order.setPaymentMethod(paymentMethod);
            order.setAddress(address);
            order.setAuction(auction);

            // Persist the order.
            session.persist(order);

            session.getTransaction().commit();

            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get a single order by id.
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable int id) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Query for the order.
            Order order = session.find(Order.class, id);
            session.getTransaction().commit();

            if (order != null) {
                return ResponseEntity.ok(order);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get all a user's orders.
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable int userId) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Query for the orders.
            List<Order> orders = session.createQuery("from Order o where o.user.id = :userId", Order.class)
                    .setParameter("userId", userId)
                    .getResultList();

            session.getTransaction().commit();

            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // No PUT mapping needed. Orders can't be changed.

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable int id) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // First find the order.
            Order order = session.find(Order.class, id);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }

            // Delete
            session.remove(order);
            session.getTransaction().commit();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
