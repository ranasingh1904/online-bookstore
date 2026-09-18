package com.bookstore.online_bookstore.controller;

import com.bookstore.controller.OrderController;
import com.bookstore.entity.AppUser;
import com.bookstore.entity.Order;
import com.bookstore.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private Authentication authentication;

    @Mock
    private AppUser appUser;

    @Mock
    private Order order;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        when(authentication.getPrincipal())
                .thenReturn(appUser);
    }

    @Test
    void checkout_shouldReturnCreatedOrder() {
        when(orderService.checkout(appUser))
                .thenReturn(order);

        Order result = orderController.checkout(authentication);

        assertNotNull(result);
        assertSame(order, result);

        verify(authentication, times(1))
                .getPrincipal();

        verify(orderService, times(1))
                .checkout(appUser);

        verifyNoMoreInteractions(authentication, orderService);
    }

    @Test
    void getOrders_shouldReturnUserOrders() {
        List<Order> orders = List.of(order);

        when(orderService.getOrders(appUser))
                .thenReturn(orders);

        List<Order> result =
                orderController.getOrders(authentication);

        assertNotNull(result);
        assertSame(orders, result);
        assertEquals(1, result.size());

        verify(authentication, times(1))
                .getPrincipal();

        verify(orderService, times(1))
                .getOrders(appUser);

        verifyNoMoreInteractions(authentication, orderService);
    }

    @Test
    void getOrder_shouldReturnOrderById() {
        Long orderId = 1L;

        when(orderService.getOrder(appUser, orderId))
                .thenReturn(order);

        Order result =
                orderController.getOrder(
                        authentication,
                        orderId
                );

        assertNotNull(result);
        assertSame(order, result);

        verify(authentication, times(1))
                .getPrincipal();

        verify(orderService, times(1))
                .getOrder(appUser, orderId);

        verifyNoMoreInteractions(authentication, orderService);
    }
}
