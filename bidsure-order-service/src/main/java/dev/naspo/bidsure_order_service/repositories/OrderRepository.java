package dev.naspo.bidsure_order_service.repositories;

import dev.naspo.bidsure_order_service.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("select o from Order o where o.user.id = ?1")
    List<Order> findAllUserOrders(int userId);
}
