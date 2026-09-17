package com.bookstore.online_bookstore.service;

import com.bookstore.entity.*;
import com.bookstore.exception.BadRequestException;
import com.bookstore.exception.ResourceNotFoundException;
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
import java.util.Optional;

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
    void shouldCheckoutCartSuccessfully() {

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
                user,
                order.getUser()
        );

        assertEquals(
                OrderStatus.PLACED,
                order.getStatus()
        );

        assertNotNull(order.getCreatedAt());

        assertEquals(
                new BigDecimal("80.00"),
                order.getTotalAmount()
        );

        assertEquals(
                1,
                order.getItems().size()
        );

        OrderItem orderItem =
                order.getItems().get(0);

        assertEquals(
                book,
                orderItem.getBook()
        );

        assertEquals(
                "Godan",
                orderItem.getTitle()
        );

        assertEquals(
                new BigDecimal("40.00"),
                orderItem.getPrice()
        );

        assertEquals(
                2,
                orderItem.getQuantity()
        );

        assertEquals(
                new BigDecimal("80.00"),
                orderItem.getSubtotal()
        );

        verify(cartRepository)
                .findByUser(user);

        verify(orderRepository)
                .save(any(Order.class));

        verify(cartRepository)
                .deleteByUser(user);
    }

    @Test
    void shouldCheckoutMultipleCartItemsSuccessfully() {

        Book secondBook = new Book(
                "Nirmala",
                "Munshi Premchand",
                new BigDecimal("50.00")
        );

        secondBook.setId(5L);

        CartItem firstCartItem =
                new CartItem(user, book, 2);

        CartItem secondCartItem =
                new CartItem(user, secondBook, 3);

        when(cartRepository.findByUser(user))
                .thenReturn(
                        List.of(
                                firstCartItem,
                                secondCartItem
                        )
                );

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        Order order =
                orderService.checkout(user);

        assertEquals(
                new BigDecimal("230.00"),
                order.getTotalAmount()
        );

        assertEquals(
                2,
                order.getItems().size()
        );

        verify(orderRepository)
                .save(any(Order.class));

        verify(cartRepository)
                .deleteByUser(user);
    }

    @Test
    void shouldRejectCheckoutWhenCartIsEmpty() {

        when(cartRepository.findByUser(user))
                .thenReturn(List.of());

        BadRequestException exception =
                assertThrows(
                        BadRequestException.class,
                        () -> orderService.checkout(user)
                );

        assertEquals(
                "Cannot checkout an empty cart",
                exception.getMessage()
        );

        verify(cartRepository)
                .findByUser(user);

        verify(orderRepository, never())
                .save(any(Order.class));

        verify(cartRepository, never())
                .deleteByUser(user);
    }

    @Test
    void shouldNotDeleteCartWhenOrderSaveFails() {

        CartItem cartItem =
                new CartItem(user, book, 2);

        when(cartRepository.findByUser(user))
                .thenReturn(List.of(cartItem));

        when(orderRepository.save(any(Order.class)))
                .thenThrow(
                        new RuntimeException("Database error")
                );

        assertThrows(
                RuntimeException.class,
                () -> orderService.checkout(user)
        );

        verify(orderRepository)
                .save(any(Order.class));

        verify(cartRepository, never())
                .deleteByUser(user);
    }

    @Test
    void shouldGetOrdersForUser() {

        Order order1 = new Order();
        order1.setUser(user);
        order1.setStatus(OrderStatus.PLACED);
        order1.setTotalAmount(new BigDecimal("80.00"));

        Order order2 = new Order();
        order2.setUser(user);
        order2.setStatus(OrderStatus.PLACED);
        order2.setTotalAmount(new BigDecimal("120.00"));

        when(orderRepository
                .findByUserOrderByCreatedAtDesc(user))
                .thenReturn(
                        List.of(order1, order2)
                );

        List<Order> orders =
                orderService.getOrders(user);

        assertNotNull(orders);

        assertEquals(
                2,
                orders.size()
        );

        assertEquals(
                order1,
                orders.get(0)
        );

        assertEquals(
                order2,
                orders.get(1)
        );

        verify(orderRepository)
                .findByUserOrderByCreatedAtDesc(user);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoOrders() {

        when(orderRepository
                .findByUserOrderByCreatedAtDesc(user))
                .thenReturn(List.of());

        List<Order> orders =
                orderService.getOrders(user);

        assertNotNull(orders);

        assertTrue(orders.isEmpty());

        verify(orderRepository)
                .findByUserOrderByCreatedAtDesc(user);
    }

    @Test
    void shouldGetOrderForUser() {

        Order order = new Order();

        order.setUser(user);
        order.setStatus(OrderStatus.PLACED);
        order.setTotalAmount(
                new BigDecimal("80.00")
        );

        when(orderRepository
                .findByIdAndUser(10L, user))
                .thenReturn(
                        Optional.of(order)
                );

        Order result =
                orderService.getOrder(user, 10L);

        assertNotNull(result);

        assertEquals(
                order,
                result
        );

        assertEquals(
                user,
                result.getUser()
        );

        assertEquals(
                OrderStatus.PLACED,
                result.getStatus()
        );

        assertEquals(
                new BigDecimal("80.00"),
                result.getTotalAmount()
        );

        verify(orderRepository)
                .findByIdAndUser(10L, user);
    }

    @Test
    void shouldThrowExceptionWhenOrderDoesNotExist() {

        when(orderRepository
                .findByIdAndUser(999L, user))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> orderService.getOrder(user, 999L)
                );

        assertEquals(
                "Order not found: 999",
                exception.getMessage()
        );

        verify(orderRepository)
                .findByIdAndUser(999L, user);
    }

    @Test
    void shouldNotReturnOrderBelongingToAnotherUser() {

        when(orderRepository
                .findByIdAndUser(10L, user))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.getOrder(user, 10L)
        );

        verify(orderRepository)
                .findByIdAndUser(10L, user);
    }
}

