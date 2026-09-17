package com.bookstore.service;

import com.bookstore.dto.AddCartItemRequest;
import com.bookstore.dto.UpdateCartItemRequest;
import com.bookstore.entity.AppUser;
import com.bookstore.entity.Book;
import com.bookstore.entity.CartItem;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartRepository;
    private final BookRepository bookRepository;

    public CartService(
            CartItemRepository cartRepository,
            BookRepository bookRepository
    ) {
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
    }

    public List<CartItem> getCart(AppUser user) {
        return cartRepository.findByUser(user);
    }

    @Transactional
    public CartItem addItem(
            AppUser user,
            AddCartItemRequest request
    ) {

        Book book = bookRepository
                .findById(request.bookId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book not found: " + request.bookId()
                        )
                );

        return cartRepository
                .findByUserAndBookId(user, book.getId())
                .map(existing -> {

                    existing.setQuantity(
                            existing.getQuantity()
                                    + request.quantity()
                    );

                    return cartRepository.save(existing);
                })
                .orElseGet(() ->
                        cartRepository.save(
                                new CartItem(
                                        user,
                                        book,
                                        request.quantity()
                                )
                        )
                );
    }

    @Transactional
    public CartItem updateItem(
            AppUser user,
            Long bookId,
            UpdateCartItemRequest request
    ) {

        CartItem item = cartRepository
                .findByUserAndBookId(user, bookId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book is not in cart"
                        )
                );

        item.setQuantity(request.quantity());

        return cartRepository.save(item);
    }

    @Transactional
    public void removeItem(
            AppUser user,
            Long bookId
    ) {

        CartItem item = cartRepository
                .findByUserAndBookId(user, bookId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book is not in cart"
                        )
                );

        cartRepository.delete(item);
    }

    public BigDecimal calculateTotal(AppUser user) {

        return getCart(user)
                .stream()
                .map(item ->
                        item.getBook()
                                .getPrice()
                                .multiply(
                                        BigDecimal.valueOf(
                                                item.getQuantity()
                                        )
                                )
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }
}
