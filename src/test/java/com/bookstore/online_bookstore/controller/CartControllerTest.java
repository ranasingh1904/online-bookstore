package com.bookstore.online_bookstore.controller;

import com.bookstore.controller.CartController;
import com.bookstore.dto.AddCartItemRequest;
import com.bookstore.dto.UpdateCartItemRequest;
import com.bookstore.entity.AppUser;
import com.bookstore.entity.CartItem;
import com.bookstore.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private Authentication authentication;

    @Mock
    private AppUser appUser;

    @Mock
    private CartItem cartItem;

    @Mock
    private AddCartItemRequest addCartItemRequest;

    @Mock
    private UpdateCartItemRequest updateCartItemRequest;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    void setUp() {
        when(authentication.getPrincipal()).thenReturn(appUser);
    }

    @Test
    void getCart_shouldReturnCartItemsAndTotal() {
        List<CartItem> items = List.of(cartItem);
        BigDecimal total = new BigDecimal("49.99");

        when(cartService.getCart(appUser))
                .thenReturn(items);

        when(cartService.calculateTotal(appUser))
                .thenReturn(total);

        Map<String, Object> result =
                cartController.getCart(authentication);

        assertNotNull(result);
        assertSame(items, result.get("items"));
        assertSame(total, result.get("total"));

        assertEquals(2, result.size());

        verify(authentication, times(1))
                .getPrincipal();

        verify(cartService, times(1))
                .getCart(appUser);

        verify(cartService, times(1))
                .calculateTotal(appUser);

        verifyNoMoreInteractions(authentication, cartService);
    }

    @Test
    void addItem_shouldAddItemToCart() {
        when(cartService.addItem(
                appUser,
                addCartItemRequest
        )).thenReturn(cartItem);

        CartItem result =
                cartController.addItem(
                        authentication,
                        addCartItemRequest
                );

        assertSame(cartItem, result);

        verify(authentication, times(1))
                .getPrincipal();

        verify(cartService, times(1))
                .addItem(
                        appUser,
                        addCartItemRequest
                );

        verifyNoMoreInteractions(authentication, cartService);
    }

    @Test
    void updateItem_shouldUpdateCartItem() {
        Long bookId = 10L;

        when(cartService.updateItem(
                appUser,
                bookId,
                updateCartItemRequest
        )).thenReturn(cartItem);

        CartItem result =
                cartController.updateItem(
                        authentication,
                        bookId,
                        updateCartItemRequest
                );

        assertSame(cartItem, result);

        verify(authentication, times(1))
                .getPrincipal();

        verify(cartService, times(1))
                .updateItem(
                        appUser,
                        bookId,
                        updateCartItemRequest
                );

        verifyNoMoreInteractions(authentication, cartService);
    }

    @Test
    void removeItem_shouldRemoveItemFromCart() {
        Long bookId = 10L;

        doNothing().when(cartService)
                .removeItem(appUser, bookId);

        cartController.removeItem(
                authentication,
                bookId
        );

        verify(authentication, times(1))
                .getPrincipal();

        verify(cartService, times(1))
                .removeItem(
                        appUser,
                        bookId
                );

        verifyNoMoreInteractions(authentication, cartService);
    }
}

