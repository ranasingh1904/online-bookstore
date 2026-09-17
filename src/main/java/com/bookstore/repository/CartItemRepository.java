package com.bookstore.repository;

import com.bookstore.entity.AppUser;
import com.bookstore.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(AppUser user);

    Optional<CartItem> findByUserAndBookId(AppUser user, Long bookId);

    void deleteByUser(AppUser user);
}
