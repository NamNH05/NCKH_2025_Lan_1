package com.example.order_service.repository;

import com.example.order_service.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // KHỚP VỚI FIELD cartId
    List<CartItem> findByCartId(Long cartId);
}
