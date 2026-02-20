package com.example.order_service.controller;

import com.example.order_service.entity.Order;
import com.example.order_service.dto.OrderDTO;
import com.example.order_service.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrderRestController {

    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Convert Order to OrderDTO
     */
    private OrderDTO convertToDTO(Order order) {
        return new OrderDTO(
            order.getId(),
            order.getCustomerId(),
            order.getTotal(),
            order.getStatus(),
            order.getAddress(),
            order.getItemDetails(),
            order.getShippingFee(),
            order.getCreatedAt()
        );
    }

    /**
     * GET /api/orders - Get all orders or filter by customerId
     */
    @GetMapping
    public ResponseEntity<?> getOrders(@RequestParam(required = false) String customerId) {
        try {
            System.out.println("[GET-ORDERS] Received request with customerId: " + customerId);
            List<Order> orders;
            
            if (customerId != null && !customerId.isEmpty()) {
                try {
                    Long userId = Long.parseLong(customerId);
                    System.out.println("[GET-ORDERS] Parsed userId: " + userId);
                    orders = orderService.getOrdersByCustomerId(userId);
                    System.out.println("[GET-ORDERS] Found " + orders.size() + " orders for userId: " + userId);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest()
                            .body(createErrorResponse(400, "Invalid customerId format"));
                }
            } else {
                orders = orderService.getAll();
            }
            
            // Convert to DTOs
            List<OrderDTO> orderDTOs = orders.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            System.out.println("[GET-ORDERS] Returning " + orderDTOs.size() + " orders");
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Success");
            response.put("data", orderDTOs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("[GET-ORDERS-ERROR] Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(createErrorResponse(500, "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * GET /api/orders/{id} - Get order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            System.out.println("[GET-ORDER-BY-ID] Fetching order with ID: " + id);
            Order order = orderService.getOrderById(id);
            if (order != null) {
                OrderDTO orderDTO = convertToDTO(order);
                System.out.println("[GET-ORDER-BY-ID] Order found: " + orderDTO);
                
                Map<String, Object> response = new HashMap<>();
                response.put("code", 200);
                response.put("message", "Success");
                response.put("data", orderDTO);
                return ResponseEntity.ok(response);
            } else {
                System.out.println("[GET-ORDER-BY-ID] Order not found with ID: " + id);
                return ResponseEntity.status(404)
                        .body(createErrorResponse(404, "Order not found"));
            }
        } catch (Exception e) {
            System.out.println("[GET-ORDER-BY-ID-ERROR] Exception: " + e.getMessage());
            return ResponseEntity.status(500)
                    .body(createErrorResponse(500, "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * POST /api/orders - Create order from cart
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestParam Long customerId) {
        try {
            orderService.checkout(customerId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 201);
            response.put("message", "Order created successfully");
            return ResponseEntity.status(201).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                    .body(createErrorResponse(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(createErrorResponse(500, "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * POST /api/orders/checkout - Create order from cart (checkout)
     */
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody Map<String, Object> payload) {
        try {
            System.out.println("[CHECKOUT] Received payload: " + payload);
            Object userIdObj = payload.get("userId");
            System.out.println("[CHECKOUT] userId from payload: " + userIdObj);
            
            if (userIdObj == null) {
                System.out.println("[CHECKOUT-ERROR] userId is missing in payload");
                return ResponseEntity.badRequest()
                        .body(createErrorResponse(400, "userId is required"));
            }
            
            Long userId;
            if (userIdObj instanceof Number) {
                userId = ((Number) userIdObj).longValue();
            } else {
                userId = Long.parseLong(userIdObj.toString());
            }
            
            // Get checkout data
            Object cartItemsObj = payload.get("cartItems");
            Object shippingFeeObj = payload.get("shippingFee");
            Object totalAmountObj = payload.get("totalAmount");
            Object addressObj = payload.get("address");
            
            System.out.println("[CHECKOUT] Processing checkout for userId: " + userId);
            System.out.println("[CHECKOUT] Cart Items: " + cartItemsObj);
            System.out.println("[CHECKOUT] Shipping Fee: " + shippingFeeObj);
            System.out.println("[CHECKOUT] Total Amount: " + totalAmountObj);
            System.out.println("[CHECKOUT] Address: " + addressObj);
            
            // Convert cartItems to JSON string
            String itemsJson = "";
            if (cartItemsObj != null) {
                try {
                    itemsJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(cartItemsObj);
                    System.out.println("[CHECKOUT] Items JSON: " + itemsJson);
                } catch (Exception e) {
                    System.out.println("[CHECKOUT] Error converting items to JSON: " + e.getMessage());
                }
            }
            
            String address = addressObj != null ? addressObj.toString() : "";
            
            // Pass checkout data to service and get created order
            Order createdOrder = orderService.checkoutAndReturn(userId, itemsJson, shippingFeeObj, totalAmountObj, address);
            OrderDTO orderDTO = convertToDTO(createdOrder);
            
            System.out.println("[CHECKOUT] Order created successfully for userId: " + userId + " with orderId: " + createdOrder.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 201);
            response.put("message", "Order created successfully");
            response.put("data", orderDTO);
            return ResponseEntity.status(201).body(response);
        } catch (NumberFormatException e) {
            System.out.println("[CHECKOUT-ERROR] NumberFormatException: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(createErrorResponse(400, "Invalid userId format"));
        } catch (RuntimeException e) {
            System.out.println("[CHECKOUT-ERROR] RuntimeException: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(400)
                    .body(createErrorResponse(400, e.getMessage()));
        } catch (Exception e) {
            System.out.println("[CHECKOUT-ERROR] Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(createErrorResponse(500, "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/orders/{id}/pay - Mark order as paid
     */
    @PutMapping("/{id}/pay")
    public ResponseEntity<?> payOrder(@PathVariable Long id) {
        try {
            orderService.pay(id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Payment successful");
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
     * PUT /api/orders/{id}/status - Update order status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            System.out.println("[UPDATE-ORDER-STATUS] Updating order " + id + " with payload: " + payload);
            String status = payload.get("status");
            
            if (status == null || status.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse(400, "Status is required"));
            }
            
            orderService.updateStatus(id, status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Order status updated successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.out.println("[UPDATE-ORDER-STATUS-ERROR] RuntimeException: " + e.getMessage());
            return ResponseEntity.status(400)
                    .body(createErrorResponse(400, e.getMessage()));
        } catch (Exception e) {
            System.out.println("[UPDATE-ORDER-STATUS-ERROR] Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(createErrorResponse(500, "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/orders/{id} - Delete order
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        try {
            orderService.delete(id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Order deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                    .body(createErrorResponse(400, e.getMessage()));
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
