package com.bookstore.controller;

import com.bookstore.entity.AppUser;
import com.bookstore.entity.Order;
import com.bookstore.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    private AppUser user(Authentication authentication) {
        return (AppUser) authentication.getPrincipal();
    }

    @PostMapping("/checkout")
    public Order checkout(
            Authentication authentication
    ) {

        return orderService.checkout(
                user(authentication)
        );
    }

    @GetMapping
    public List<Order> getOrders(
            Authentication authentication
    ) {

        return orderService.getOrders(
                user(authentication)
        );
    }

    @GetMapping("/{id}")
    public Order getOrder(
            Authentication authentication,
            @PathVariable Long id
    ) {

        return orderService.getOrder(
                user(authentication),
                id
        );
    }
}
