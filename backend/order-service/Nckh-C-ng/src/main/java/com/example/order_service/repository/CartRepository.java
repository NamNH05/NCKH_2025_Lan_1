package com.example.order_service.repository;

import com.example.order_service.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserIdAndStatus(Long userId, String status);

    // Find most recent cart for a user (ordered by ID descending)
    Optional<Cart> findFirstByUserIdOrderByIdDesc(Long userId);

    // Find all carts for a user
    List<Cart> findAllByUserId(Long userId);
}
