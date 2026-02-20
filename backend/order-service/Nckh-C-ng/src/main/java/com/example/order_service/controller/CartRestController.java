package com.example.order_service.controller;

import com.example.order_service.entity.Cart;
import com.example.order_service.entity.CartItem;
import com.example.order_service.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carts")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CartRestController {

    private final CartService cartService;

    public CartRestController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * GET /api/carts/{userId} - Get cart for user
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getCart(@PathVariable Long userId) {
        try {
            System.out.println("[GET-CART] Received request for userId: " + userId);
            Cart cart = cartService.getCart(userId);
            List<CartItem> items = cartService.getItems(userId);
            
            Map<String, Object> cartData = new HashMap<>();
            cartData.put("id", cart.getId());
            cartData.put("userId", cart.getUserId());
            cartData.put("status", cart.getStatus());
            cartData.put("total", cart.getTotal());
            cartData.put("items", items);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Success");
            response.put("data", cartData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(createErrorResponse(500, "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * GET /api/carts/{userId}/items - Get all items in cart
     */
    @GetMapping("/{userId}/items")
    public ResponseEntity<?> getCartItems(@PathVariable Long userId) {
        try {
            List<CartItem> items = cartService.getItems(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Success");
            response.put("data", items);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(createErrorResponse(500, "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * POST /api/carts/{userId}/items - Add item to cart
     * Accepts both query params and request body
     */
    @PostMapping("/{userId}/items")
    public ResponseEntity<?> addItem(
            @PathVariable Long userId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) Integer quantity,
            @RequestBody(required = false) Map<String, Object> body) {
        try {
            System.out.println("[ADD-ITEM] userId: " + userId + ", name: " + name + ", price: " + price + ", quantity: " + quantity);
            System.out.println("[ADD-ITEM] body: " + body);
            
            // Use body if provided, otherwise use query params
            String itemName = name;
            Double itemPrice = price;
            Integer itemQuantity = quantity;
            
            if (body != null && !body.isEmpty()) {
                itemName = body.getOrDefault("name", name) != null ? body.get("name").toString() : name;
                itemPrice = body.get("price") != null ? Double.parseDouble(body.get("price").toString()) : price;
                itemQuantity = body.get("quantity") != null ? Integer.parseInt(body.get("quantity").toString()) : quantity;
                System.out.println("[ADD-ITEM] Using body - name: " + itemName + ", price: " + itemPrice + ", quantity: " + itemQuantity);
            }
            
            if (itemName == null || itemPrice == null || itemQuantity == null) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse(400, "Missing required fields: name, price, quantity"));
            }
            
            cartService.addItem(userId, itemName, itemPrice, itemQuantity);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 201);
            response.put("message", "Item added successfully");
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            System.out.println("[ADD-ITEM-ERROR] " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(createErrorResponse(500, "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/carts/items/{itemId} - Update cart item
     * Accepts both query params and request body
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<?> updateItem(
            @PathVariable Long itemId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) Integer quantity,
            @RequestBody(required = false) Map<String, Object> body) {
        try {
            System.out.println("[UPDATE-ITEM] itemId: " + itemId + ", name: " + name + ", price: " + price + ", quantity: " + quantity);
            System.out.println("[UPDATE-ITEM] body: " + body);
            
            String itemName = name;
            Double itemPrice = price;
            Integer itemQuantity = quantity;
            
            if (body != null && !body.isEmpty()) {
                itemName = body.getOrDefault("name", name) != null ? body.get("name").toString() : name;
                itemPrice = body.get("price") != null ? Double.parseDouble(body.get("price").toString()) : price;
                itemQuantity = body.get("quantity") != null ? Integer.parseInt(body.get("quantity").toString()) : quantity;
            }
            
            if (itemName == null || itemPrice == null || itemQuantity == null) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse(400, "Missing required fields: name, price, quantity"));
            }
            
            cartService.updateItem(itemId, itemName, itemPrice, itemQuantity);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Item updated successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.out.println("[UPDATE-ITEM-ERROR] " + e.getMessage());
            return ResponseEntity.status(400)
                    .body(createErrorResponse(400, e.getMessage()));
        } catch (Exception e) {
            System.out.println("[UPDATE-ITEM-ERROR] " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(createErrorResponse(500, "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/carts/items/{itemId} - Delete item from cart
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId) {
        try {
            cartService.deleteItem(itemId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Item deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                    .body(createErrorResponse(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(createErrorResponse(500, "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * POST /api/carts/{userId}/checkout - Checkout cart (convert to order)
     */
    @PostMapping("/{userId}/checkout")
    public ResponseEntity<?> checkout(@PathVariable Long userId) {
        try {
            // This would need to be added to CartService or call OrderService
            // For now, return a placeholder response
            Map<String, Object> response = new HashMap<>();
            response.put("code", 201);
            response.put("message", "Checkout successful");
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(createErrorResponse(500, "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/carts/{userId} - Clear cart
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {
        try {
            cartService.clearCart(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Cart cleared successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(createErrorResponse(500, "Internal server error: " + e.getMessage()));
        }
    }

    private Map<String, Object> createErrorResponse(int code, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("code", code);
        error.put("message", message);
        return error;
    }
}
