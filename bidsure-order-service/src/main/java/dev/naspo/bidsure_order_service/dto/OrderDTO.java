package dev.naspo.bidsure_order_service.dto;

import dev.naspo.bidsure_order_service.models.Order;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderDTO {

    @NotNull
    private BigDecimal totalCost;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer paymentId;

    @NotNull
    private Integer addressId;

    @NotNull
    private Integer auctionId;

    /**
     * Maps the core properties of an Order entity to an OrderDTO.
     * <p>
     * Only copies fields directly present in the Order entity.
     * @param order the Order entity to map.
     * @return a new OrderDTO containing the mapped fields.
     */
    public static OrderDTO from(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setTotalCost(order.getTotalCost());
        orderDTO.setUserId(order.getUser().getId());
        orderDTO.setPaymentId(order.getPaymentMethod().getId());
        orderDTO.setAddressId(order.getAddress().getId());
        orderDTO.setAuctionId(order.getAuction().getId());
        return orderDTO;
    }

    /**
     * Maps the core properties of an OrderDTO to an Order entity.
     * <p>
     * Only copies fields directly present in the OrderDTO entity.
     * @param orderDTO the OrderDTO entity to map.
     * @return a new Order containing the mapped fields.
     */
    public static Order toEntity(OrderDTO orderDTO) {
        Order order = new Order();
        order.setTotalCost(orderDTO.getTotalCost());
        return order;
    }
}
