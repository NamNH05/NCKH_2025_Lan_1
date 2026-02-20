package com.example.order_service.service;

import com.example.order_service.entity.Cart;
import com.example.order_service.entity.Order;
import com.example.order_service.repository.CartRepository;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.client.AuditClient;
import com.example.order_service.client.AuditLogRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepo;
    private final CartRepository cartRepo;
    private final AuditClient auditClient;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepo, CartRepository cartRepo,
            AuditClient auditClient, ObjectMapper objectMapper) {
        this.orderRepo = orderRepo;
        this.cartRepo = cartRepo;
        this.auditClient = auditClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Lấy tất cả đơn hàng
     */
    public List<Order> getAll() {
        return orderRepo.findAll();
    }

    /**
     * Lấy đơn hàng theo ID
     */
    public Order getOrderById(Long id) {
        return orderRepo.findById(id).orElse(null);
    }

    /**
     * Lấy đơn hàng theo ID khách hàng
     */
    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepo.findByCustomerId(customerId);
    }

    /**
     * Tạo đơn hàng từ giỏ hàng
     */
    @Transactional
    public void checkout(Long userId, String itemsJson, Object shippingFeeObj, Object totalAmountObj, String address) {
        System.out.println("[OrderService.checkout] Starting checkout for userId: " + userId);
        System.out.println("[OrderService.checkout] Items JSON: " + itemsJson);
        System.out.println("[OrderService.checkout] Shipping Fee: " + shippingFeeObj);
        System.out.println("[OrderService.checkout] Total Amount: " + totalAmountObj);
        System.out.println("[OrderService.checkout] Address: " + address);

        try {
            // Parse total amount first (frontend sends complete total including shipping)
            Double totalAmount = 0.0;
            if (totalAmountObj != null) {
                try {
                    totalAmount = totalAmountObj instanceof Number
                            ? ((Number) totalAmountObj).doubleValue()
                            : Double.parseDouble(totalAmountObj.toString());
                    System.out.println("[OrderService.checkout] Using totalAmount from frontend: " + totalAmount);
                } catch (Exception e) {
                    System.out.println("[OrderService.checkout] Error parsing total amount: " + e.getMessage());
                    totalAmount = 0.0;
                }
            }

            // Try to find CART to update its status, but don't fail if not found
            // (Frontend may not sync cart to database before checkout)
            Cart cart = null;
            var optCart = cartRepo.findByUserIdAndStatus(userId, "CART");
            System.out.println("[OrderService.checkout] Found CART status: " + optCart.isPresent());

            if (optCart.isPresent()) {
                cart = optCart.get();
                System.out.println("[OrderService.checkout] Using existing CART with ID: " + cart.getId() + ", total: "
                        + cart.getTotal());
            } else {
                // If no CART, try to find the most recent cart for this user
                System.out.println(
                        "[OrderService.checkout-WARNING] No CART found, trying to find most recent cart for user");
                var anyCart = cartRepo.findFirstByUserIdOrderByIdDesc(userId);

                if (anyCart.isPresent()) {
                    cart = anyCart.get();
                    System.out.println("[OrderService.checkout] Found existing cart: " + cart.getId() + " with status: "
                            + cart.getStatus() + ", total: " + cart.getTotal());
                } else {
                    System.out.println(
                            "[OrderService.checkout-WARNING] No cart found in database - using frontend data only");
                    // Don't throw exception - frontend will have sent the cart items
                }
            }

            System.out.println("[OrderService.checkout] Creating order with total: " + totalAmount);
            Order order = new Order();
            order.setCustomerId(userId);
            order.setTotal(totalAmount); // Use total from frontend
            order.setStatus("PENDING");
            order.setAddress(address); // Set address from payment
            order.setItemDetails(itemsJson); // Store item details from frontend

            // Set shipping fee
            if (shippingFeeObj != null) {
                try {
                    double shippingFee = shippingFeeObj instanceof Number
                            ? ((Number) shippingFeeObj).doubleValue()
                            : Double.parseDouble(shippingFeeObj.toString());
                    order.setShippingFee(shippingFee);
                    System.out.println("[OrderService.checkout] Setting shipping fee: " + shippingFee);
                } catch (Exception e) {
                    System.out.println("[OrderService.checkout] Error parsing shipping fee: " + e.getMessage());
                }
            }

            order.setCreatedAt(java.time.LocalDateTime.now());
            orderRepo.save(order);

            System.out.println("[OrderService.checkout] Order created with ID: " + order.getId());

            // Log to Audit Service
            try {
                auditClient.logAudit(AuditLogRequest.builder()
                        .sourceService("order-service")
                        .actionType("CREATE")
                        .entityName("ORDER")
                        .entityId(order.getId().toString())
                        .userId(userId.toString())
                        .newValue(objectMapper.writeValueAsString(order))
                        .ipAddress("0.0.0.0")
                        .userAgent("order-service")
                        .build());
            } catch (Exception e) {
                logger.warn("Failed to audit order creation: {}", e.getMessage());
            }

            // Cập nhật trạng thái giỏ hàng nếu tìm thấy
            if (cart != null) {
                cart.setStatus("ORDERED");
                cartRepo.save(cart);
                System.out.println("[OrderService.checkout] Cart status updated to ORDERED");
            }

            System.out.println("[OrderService.checkout] SUCCESS - Checkout completed for userId: " + userId);
        } catch (Exception e) {
            System.out.println("[OrderService.checkout-ERROR] Exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Create order and return the created Order object
     */
    @Transactional
    public Order checkoutAndReturn(Long userId, String itemsJson, Object shippingFeeObj, Object totalAmountObj,
            String address) {
        System.out.println("[OrderService.checkoutAndReturn] Starting checkout for userId: " + userId);
        System.out.println("[OrderService.checkoutAndReturn] Items JSON: " + itemsJson);
        System.out.println("[OrderService.checkoutAndReturn] Shipping Fee: " + shippingFeeObj);
        System.out.println("[OrderService.checkoutAndReturn] Total Amount: " + totalAmountObj);
        System.out.println("[OrderService.checkoutAndReturn] Address: " + address);

        try {
            // Parse total amount first (frontend sends complete total including shipping)
            Double totalAmount = 0.0;
            if (totalAmountObj != null) {
                try {
                    totalAmount = totalAmountObj instanceof Number
                            ? ((Number) totalAmountObj).doubleValue()
                            : Double.parseDouble(totalAmountObj.toString());
                    System.out.println(
                            "[OrderService.checkoutAndReturn] Using totalAmount from frontend: " + totalAmount);
                } catch (Exception e) {
                    System.out
                            .println("[OrderService.checkoutAndReturn] Error parsing total amount: " + e.getMessage());
                    totalAmount = 0.0;
                }
            }

            // Try to find CART to update its status, but don't fail if not found
            // (Frontend may not sync cart to database before checkout)
            Cart cart = null;
            var optCart = cartRepo.findByUserIdAndStatus(userId, "CART");
            System.out.println("[OrderService.checkoutAndReturn] Found CART status: " + optCart.isPresent());

            if (optCart.isPresent()) {
                cart = optCart.get();
                System.out.println("[OrderService.checkoutAndReturn] Using existing CART with ID: " + cart.getId()
                        + ", total: " + cart.getTotal());
            } else {
                // If no CART, try to find the most recent cart for this user
                System.out.println(
                        "[OrderService.checkoutAndReturn-WARNING] No CART found, trying to find most recent cart for user");
                var anyCart = cartRepo.findFirstByUserIdOrderByIdDesc(userId);

                if (anyCart.isPresent()) {
                    cart = anyCart.get();
                    System.out.println("[OrderService.checkoutAndReturn] Found existing cart: " + cart.getId()
                            + " with status: " + cart.getStatus() + ", total: " + cart.getTotal());
                } else {
                    System.out.println(
                            "[OrderService.checkoutAndReturn-WARNING] No cart found in database - using frontend data only");
                    // Don't throw exception - frontend will have sent the cart items
                }
            }

            System.out.println("[OrderService.checkoutAndReturn] Creating order with total: " + totalAmount);
            Order order = new Order();
            order.setCustomerId(userId);
            order.setTotal(totalAmount); // Use total from frontend
            order.setStatus("PENDING");
            order.setAddress(address); // Set address from payment
            order.setItemDetails(itemsJson); // Store item details from frontend

            // Set shipping fee
            if (shippingFeeObj != null) {
                try {
                    double shippingFee = shippingFeeObj instanceof Number
                            ? ((Number) shippingFeeObj).doubleValue()
                            : Double.parseDouble(shippingFeeObj.toString());
                    order.setShippingFee(shippingFee);
                    System.out.println("[OrderService.checkoutAndReturn] Setting shipping fee: " + shippingFee);
                } catch (Exception e) {
                    System.out
                            .println("[OrderService.checkoutAndReturn] Error parsing shipping fee: " + e.getMessage());
                }
            }

            order.setCreatedAt(java.time.LocalDateTime.now());
            Order savedOrder = orderRepo.save(order);

            System.out.println("[OrderService.checkoutAndReturn] Order created with ID: " + savedOrder.getId());

            // Log to Audit Service
            try {
                auditClient.logAudit(AuditLogRequest.builder()
                        .sourceService("order-service")
                        .actionType("CREATE")
                        .entityName("ORDER")
                        .entityId(savedOrder.getId().toString())
                        .userId(userId.toString())
                        .newValue(objectMapper.writeValueAsString(savedOrder))
                        .ipAddress("0.0.0.0")
                        .userAgent("order-service")
                        .build());
            } catch (Exception e) {
                logger.warn("Failed to audit order creation: {}", e.getMessage());
            }

            // Cập nhật trạng thái giỏ hàng nếu tìm thấy
            if (cart != null) {
                cart.setStatus("ORDERED");
                cartRepo.save(cart);
                System.out.println("[OrderService.checkoutAndReturn] Cart status updated to ORDERED");
            }

            System.out.println("[OrderService.checkoutAndReturn] SUCCESS - Checkout completed for userId: " + userId
                    + ", orderId: " + savedOrder.getId());

            return savedOrder;
        } catch (Exception e) {
            System.out.println("[OrderService.checkoutAndReturn-ERROR] Exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Overload method for backward compatibility
     */
    @Transactional
    public void checkout(Long userId) {
        checkout(userId, "", null, null, "");
    }

    /**
     * Thanh toán đơn hàng
     */
    public void pay(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        String oldValue = null;
        try {
            oldValue = objectMapper.writeValueAsString(order);
        } catch (Exception e) {
            logger.warn("Failed to serialize old order state: {}", e.getMessage());
        }

        order.setStatus("PAID");
        orderRepo.save(order);

        // Log to Audit Service
        try {
            auditClient.logAudit(AuditLogRequest.builder()
                    .sourceService("order-service")
                    .actionType("UPDATE")
                    .entityName("ORDER")
                    .entityId(orderId.toString())
                    .userId(order.getCustomerId().toString())
                    .oldValue(oldValue)
                    .newValue(objectMapper.writeValueAsString(order))
                    .ipAddress("0.0.0.0")
                    .userAgent("order-service")
                    .build());
        } catch (Exception e) {
            logger.warn("Failed to audit order payment: {}", e.getMessage());
        }
    }

    /**
     * Cập nhật trạng thái đơn hàng
     */
    public void updateStatus(Long orderId, String newStatus) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        String oldValue = null;
        try {
            oldValue = objectMapper.writeValueAsString(order);
        } catch (Exception e) {
            logger.warn("Failed to serialize old order state: {}", e.getMessage());
        }

        String oldStatus = order.getStatus();
        order.setStatus(newStatus);
        orderRepo.save(order);

        logger.info("Order {} status updated from {} to {}", orderId, oldStatus, newStatus);

        // Log to Audit Service
        try {
            auditClient.logAudit(AuditLogRequest.builder()
                    .sourceService("order-service")
                    .actionType("UPDATE")
                    .entityName("ORDER")
                    .entityId(orderId.toString())
                    .userId(order.getCustomerId().toString())
                    .oldValue(oldValue)
                    .newValue(objectMapper.writeValueAsString(order))
                    .ipAddress("0.0.0.0")
                    .userAgent("order-service")
                    .build());
        } catch (Exception e) {
            logger.warn("Failed to audit order status update: {}", e.getMessage());
        }
    }

    /**
     * Xóa đơn hàng
     */
    public void delete(Long id) {
        if (!orderRepo.existsById(id)) {
            throw new RuntimeException("Đơn hàng không tồn tại");
        }

        Order order = orderRepo.findById(id).orElse(null);

        orderRepo.deleteById(id);

        // Log to Audit Service
        try {
            if (order != null) {
                auditClient.logAudit(AuditLogRequest.builder()
                        .sourceService("order-service")
                        .actionType("DELETE")
                        .entityName("ORDER")
                        .entityId(id.toString())
                        .userId(order.getCustomerId().toString())
                        .oldValue(objectMapper.writeValueAsString(order))
                        .ipAddress("0.0.0.0")
                        .userAgent("order-service")
                        .build());
            }
        } catch (Exception e) {
            logger.warn("Failed to audit order deletion: {}", e.getMessage());
        }
    }
}
