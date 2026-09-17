package com.bookstore.online_bookstore.service;

import com.bookstore.dto.AddCartItemRequest;
import com.bookstore.dto.UpdateCartItemRequest;
import com.bookstore.entity.AppUser;
import com.bookstore.entity.Book;
import com.bookstore.entity.CartItem;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.service.CartService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CartService cartService;

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
    void shouldAddBookToCart() {

        AddCartItemRequest request =
                new AddCartItemRequest(4L, 1);

        when(bookRepository.findById(4L))
                .thenReturn(Optional.of(book));

        when(cartRepository.findByUserAndBookId(user, 4L))
                .thenReturn(Optional.empty());

        CartItem savedItem =
                new CartItem(user, book, 1);

        when(cartRepository.save(any(CartItem.class)))
                .thenReturn(savedItem);

        CartItem result =
                cartService.addItem(user, request);

        assertEquals(1, result.getQuantity());
        assertEquals(book, result.getBook());

        verify(cartRepository).save(any(CartItem.class));
    }

    @Test
    void shouldIncreaseQuantityWhenBookAlreadyExists() {

        CartItem existingItem =
                new CartItem(user, book, 1);

        AddCartItemRequest request =
                new AddCartItemRequest(4L, 2);

        when(bookRepository.findById(4L))
                .thenReturn(Optional.of(book));

        when(cartRepository.findByUserAndBookId(user, 4L))
                .thenReturn(Optional.of(existingItem));

        when(cartRepository.save(existingItem))
                .thenReturn(existingItem);

        CartItem result =
                cartService.addItem(user, request);

        assertEquals(3, result.getQuantity());

        verify(cartRepository).save(existingItem);
    }

    @Test
    void shouldUpdateCartQuantity() {

        CartItem item =
                new CartItem(user, book, 1);

        when(cartRepository.findByUserAndBookId(user, 4L))
                .thenReturn(Optional.of(item));

        when(cartRepository.save(item))
                .thenReturn(item);

        UpdateCartItemRequest request =
                new UpdateCartItemRequest(5);

        CartItem result =
                cartService.updateItem(user, 4L, request);

        assertEquals(5, result.getQuantity());
    }

    @Test
    void shouldRemoveBookFromCart() {

        CartItem item =
                new CartItem(user, book, 1);

        when(cartRepository.findByUserAndBookId(user, 4L))
                .thenReturn(Optional.of(item));

        cartService.removeItem(user, 4L);

        verify(cartRepository).delete(item);
    }

    @Test
    void shouldCalculateCartTotal() {

        CartItem item =
                new CartItem(user, book, 2);

        when(cartRepository.findByUser(user))
                .thenReturn(List.of(item));

        BigDecimal total =
                cartService.calculateTotal(user);

        assertEquals(
                new BigDecimal("80.00"),
                total
        );
    }

    @Test
    void shouldRejectBookThatDoesNotExist() {

        when(bookRepository.findById(999L))
                .thenReturn(Optional.empty());

        AddCartItemRequest request =
                new AddCartItemRequest(999L, 1);

        assertThrows(
                RuntimeException.class,
                () -> cartService.addItem(user, request)
        );
    }
}