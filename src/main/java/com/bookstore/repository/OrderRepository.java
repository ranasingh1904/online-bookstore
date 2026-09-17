package com.bookstore.repository;

import com.bookstore.entity.AppUser;
import com.bookstore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByCreatedAtDesc(AppUser user);

    Optional<Order> findByIdAndUser(Long id, AppUser user);
}
