package com.bookstore.online_bookstore.service;

import com.bookstore.entity.*;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.OrderRepository;
import com.bookstore.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CartItemRepository cartRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private AppUser user;
    private Book book;

    @BeforeEach
    void setUp() {

        user = new AppUser();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@gmail.com");

        book = new Book(
                "Godan",
                "Munshi Premchand",
                new BigDecimal("40.00")
        );

        book.setId(4L);
    }

    @Test
    void shouldCheckoutCart() {

        CartItem cartItem =
                new CartItem(user, book, 2);

        when(cartRepository.findByUser(user))
                .thenReturn(List.of(cartItem));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        Order order =
                orderService.checkout(user);

        assertNotNull(order);

        assertEquals(
                new BigDecimal("80.00"),
                order.getTotalAmount()
        );

        assertEquals(
                OrderStatus.PLACED,
                order.getStatus()
        );

        assertEquals(
                1,
                order.getItems().size()
        );

        verify(orderRepository)
                .save(any(Order.class));

        verify(cartRepository)
                .deleteByUser(user);
    }

    @Test
    void shouldRejectEmptyCart() {

        when(cartRepository.findByUser(user))
                .thenReturn(List.of());

        assertThrows(
                RuntimeException.class,
                () -> orderService.checkout(user)
        );

        verify(orderRepository, never())
                .save(any(Order.class));

        verify(cartRepository, never())
                .deleteByUser(user);
    }
}
