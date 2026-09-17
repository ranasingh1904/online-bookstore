package com.bookstore.controller;

import com.bookstore.dto.AddCartItemRequest;
import com.bookstore.dto.UpdateCartItemRequest;
import com.bookstore.entity.AppUser;
import com.bookstore.entity.CartItem;
import com.bookstore.service.CartService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    private AppUser user(Authentication authentication) {
        return (AppUser) authentication.getPrincipal();
    }

    @GetMapping
    public Map<String, Object> getCart(
            Authentication authentication
    ) {

        AppUser user = user(authentication);

        List<CartItem> items =
                cartService.getCart(user);

        BigDecimal total =
                cartService.calculateTotal(user);

        return Map.of(
                "items", items,
                "total", total
        );
    }

    @PostMapping("/items")
    public CartItem addItem(
            Authentication authentication,
            @Valid @RequestBody AddCartItemRequest request
    ) {

        return cartService.addItem(
                user(authentication),
                request
        );
    }

    @PatchMapping("/items/{bookId}")
    public CartItem updateItem(
            Authentication authentication,
            @PathVariable Long bookId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {

        return cartService.updateItem(
                user(authentication),
                bookId,
                request
        );
    }

    @DeleteMapping("/items/{bookId}")
    public void removeItem(
            Authentication authentication,
            @PathVariable Long bookId
    ) {

        cartService.removeItem(
                user(authentication),
                bookId
        );
    }
}
