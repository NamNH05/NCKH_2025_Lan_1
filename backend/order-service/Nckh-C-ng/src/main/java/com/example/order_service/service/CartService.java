package com.example.order_service.service;

import com.example.order_service.entity.Cart;
import com.example.order_service.entity.CartItem;
import com.example.order_service.repository.CartItemRepository;
import com.example.order_service.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepo;
    private final CartItemRepository itemRepo;

    public CartService(CartRepository cartRepo, CartItemRepository itemRepo) {
        this.cartRepo = cartRepo;
        this.itemRepo = itemRepo;
    }

    /**
     * Lấy hoặc tạo giỏ hàng mới
     */
    public Cart getCart(Long userId) {
        return cartRepo.findByUserIdAndStatus(userId, "CART")
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUserId(userId);
                    c.setStatus("CART");
                    c.setTotal(0.0);
                    return cartRepo.save(c);
                });
    }

    /**
     * Lấy danh sách sản phẩm trong giỏ
     */
    public List<CartItem> getItems(Long userId) {
        Cart cart = getCart(userId);
        return itemRepo.findByCartId(cart.getId());
    }

    /**
     * Thêm sản phẩm vào giỏ
     */
    public void addItem(Long userId, String name, Double price, Integer quantity) {
        Cart cart = getCart(userId);

        CartItem item = new CartItem();
        item.setCartId(cart.getId());
        item.setName(name);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepo.save(item);
        recalcTotal(cart.getId());
    }

    /**
     * Sửa thông tin sản phẩm trong giỏ
     */
    public void updateItem(Long itemId, String name, Double price, Integer quantity) {
        CartItem item = itemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        item.setName(name);
        item.setPrice(price);
        item.setQuantity(quantity);
        itemRepo.save(item);
        recalcTotal(item.getCartId());
    }

    /**
     * Xóa sản phẩm khỏi giỏ
     */
    public void deleteItem(Long itemId) {
        CartItem item = itemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        Long cartId = item.getCartId();
        itemRepo.delete(item);
        recalcTotal(cartId);
    }

    /**
     * Tính lại tổng tiền của giỏ hàng
     */
    private void recalcTotal(Long cartId) {
        List<CartItem> items = itemRepo.findByCartId(cartId);
        double total = items.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));
        cart.setTotal(total);
        cartRepo.save(cart);
    }

    /**
     * Xóa giỏ hàng
     */
    public void clearCart(Long userId) {
        Cart cart = getCart(userId);
        List<CartItem> items = itemRepo.findByCartId(cart.getId());
        itemRepo.deleteAll(items);
        cart.setTotal(0.0);
        cartRepo.save(cart);
    }
}








