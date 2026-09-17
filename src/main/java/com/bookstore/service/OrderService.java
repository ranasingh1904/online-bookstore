package com.bookstore.service;

import com.bookstore.entity.*;
import com.bookstore.exception.BadRequestException;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final CartItemRepository cartRepository;
    private final OrderRepository orderRepository;

    public OrderService(
            CartItemRepository cartRepository,
            OrderRepository orderRepository
    ) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order checkout(AppUser user) {

        List<CartItem> cartItems =
                cartRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new BadRequestException(
                    "Cannot checkout an empty cart"
            );
        }

        Order order = new Order();

        order.setUser(user);
        order.setStatus(OrderStatus.PLACED);
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {

            Book book = cartItem.getBook();

            BigDecimal subtotal =
                    book.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            );

            OrderItem orderItem = new OrderItem();

            orderItem.setBook(book);
            orderItem.setTitle(book.getTitle());
            orderItem.setPrice(book.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(subtotal);

            order.addItem(orderItem);

            total = total.add(subtotal);
        }

        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);

        cartRepository.deleteByUser(user);

        return savedOrder;
    }

    public List<Order> getOrders(AppUser user) {

        return orderRepository
                .findByUserOrderByCreatedAtDesc(user);
    }

    public Order getOrder(
            AppUser user,
            Long orderId
    ) {

        return orderRepository
                .findByIdAndUser(orderId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found: " + orderId
                        )
                );
    }
}
