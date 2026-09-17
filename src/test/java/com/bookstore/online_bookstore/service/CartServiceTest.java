package com.bookstore.online_bookstore.service;

import com.bookstore.dto.AddCartItemRequest;
import com.bookstore.dto.UpdateCartItemRequest;
import com.bookstore.entity.AppUser;
import com.bookstore.entity.Book;
import com.bookstore.entity.CartItem;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
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
    private final Long VALID_BOOK_ID = 4L;
    private final Long INVALID_BOOK_ID = 999L;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@gmail.com");

        book = new Book("Godan", "Munshi Premchand", new BigDecimal("40.00"));
        book.setId(VALID_BOOK_ID);
    }

    @Nested
    @DisplayName("Tests for adding items to cart")
    class AddItemTests {

        @Test
        @DisplayName("Should successfully add a new book to the cart when it doesn't exist yet")
        void shouldAddNewBookToCart() {
            AddCartItemRequest request = new AddCartItemRequest(VALID_BOOK_ID, 1);
            CartItem savedItem = new CartItem(user, book, 1);

            when(bookRepository.findById(VALID_BOOK_ID)).thenReturn(Optional.of(book));
            when(cartRepository.findByUserAndBookId(user, VALID_BOOK_ID)).thenReturn(Optional.empty());
            when(cartRepository.save(any(CartItem.class))).thenReturn(savedItem);

            CartItem result = cartService.addItem(user, request);

            assertNotNull(result);
            assertEquals(1, result.getQuantity());
            assertEquals(book, result.getBook());
            verify(cartRepository).save(any(CartItem.class));
        }

        @Test
        @DisplayName("Should increment quantity of an existing item when the same book is added again")
        void shouldIncrementQuantityForExistingBook() {
            CartItem existingItem = new CartItem(user, book, 1);
            AddCartItemRequest request = new AddCartItemRequest(VALID_BOOK_ID, 2);

            when(bookRepository.findById(VALID_BOOK_ID)).thenReturn(Optional.of(book));
            when(cartRepository.findByUserAndBookId(user, VALID_BOOK_ID)).thenReturn(Optional.of(existingItem));
            when(cartRepository.save(existingItem)).thenReturn(existingItem);

            CartItem result = cartService.addItem(user, request);

            assertEquals(3, result.getQuantity());
            verify(cartRepository).save(existingItem);
        }

        @Test
        @DisplayName("Negative Case: Should throw ResourceNotFoundException when trying to add a non-existent book")
        void shouldThrowExceptionWhenBookDoesNotExist() {
            AddCartItemRequest request = new AddCartItemRequest(INVALID_BOOK_ID, 1);

            when(bookRepository.findById(INVALID_BOOK_ID)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> cartService.addItem(user, request)
            );

            assertEquals("Book not found: " + INVALID_BOOK_ID, exception.getMessage());
            verify(cartRepository, never()).findByUserAndBookId(any(), any());
            verify(cartRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Tests for updating cart items")
    class UpdateItemTests {

        @Test
        @DisplayName("Should successfully update the quantity of a book currently in the cart")
        void shouldUpdateCartQuantity() {
            CartItem item = new CartItem(user, book, 1);
            UpdateCartItemRequest request = new UpdateCartItemRequest(5);

            when(cartRepository.findByUserAndBookId(user, VALID_BOOK_ID)).thenReturn(Optional.of(item));
            when(cartRepository.save(item)).thenReturn(item);

            CartItem result = cartService.updateItem(user, VALID_BOOK_ID, request);

            assertEquals(5, result.getQuantity());
            verify(cartRepository).save(item);
        }

        @Test
        @DisplayName("Negative Case: Should throw ResourceNotFoundException when updating a book not in the user's cart")
        void shouldThrowExceptionWhenUpdatingItemNotInCart() {
            UpdateCartItemRequest request = new UpdateCartItemRequest(5);

            when(cartRepository.findByUserAndBookId(user, VALID_BOOK_ID)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> cartService.updateItem(user, VALID_BOOK_ID, request)
            );

            assertEquals("Book is not in cart", exception.getMessage());
            verify(cartRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Tests for removing cart items")
    class RemoveItemTests {

        @Test
        @DisplayName("Should successfully remove a book from the cart when it exists")
        void shouldRemoveBookFromCart() {
            CartItem item = new CartItem(user, book, 1);

            when(cartRepository.findByUserAndBookId(user, VALID_BOOK_ID)).thenReturn(Optional.of(item));

            cartService.removeItem(user, VALID_BOOK_ID);

            verify(cartRepository).delete(item);
        }

        @Test
        @DisplayName("Negative Case: Should throw ResourceNotFoundException when trying to remove a book not in the cart")
        void shouldThrowExceptionWhenRemovingItemNotInCart() {
            when(cartRepository.findByUserAndBookId(user, VALID_BOOK_ID)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> cartService.removeItem(user, VALID_BOOK_ID)
            );

            assertEquals("Book is not in cart", exception.getMessage());
            verify(cartRepository, never()).delete(any(CartItem.class));
        }
    }

    @Nested
    @DisplayName("Tests for calculating cart totals")
    class TotalCalculationTests {

        @Test
        @DisplayName("Should correctly calculate total price for multiple quantities of books in the cart")
        void shouldCalculateCartTotalForItems() {
            CartItem item = new CartItem(user, book, 2);

            when(cartRepository.findByUser(user)).thenReturn(List.of(item));

            BigDecimal total = cartService.calculateTotal(user);

            assertEquals(new BigDecimal("80.00"), total);
        }

        @Test
        @DisplayName("Edge Case: Should return zero total when the user's cart is empty")
        void shouldReturnZeroTotalWhenCartIsEmpty() {
            when(cartRepository.findByUser(user)).thenReturn(Collections.emptyList());

            BigDecimal total = cartService.calculateTotal(user);

            assertEquals(BigDecimal.ZERO, total);
        }
    }
}
