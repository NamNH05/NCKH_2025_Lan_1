package com.example.order_service.service;

import com.example.order_service.entity.CartItem;
import com.example.order_service.repository.CartItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartItemService {

    private final CartItemRepository cartItemRepo;

    public CartItemService(CartItemRepository cartItemRepo) {
        this.cartItemRepo = cartItemRepo;
    }

    public List<CartItem> getByCartId(Long cartId) {
        return cartItemRepo.findByCartId(cartId);
    }

    public void deleteItem(Long itemId) {
        cartItemRepo.deleteById(itemId);
    }
}
