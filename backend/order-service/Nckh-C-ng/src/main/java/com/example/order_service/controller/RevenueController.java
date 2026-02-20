package com.example.order_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.order_service.service.OrderService;
import com.example.order_service.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/revenue")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class RevenueController {

    @Autowired
    private OrderService orderService;
    
    private static final Logger logger = LoggerFactory.getLogger(RevenueController.class);
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getRevenueSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        try {
            List<Order> allOrders = orderService.getAll();
            
            // Tính toán tổng doanh thu
            BigDecimal totalRevenue = BigDecimal.ZERO;
            int totalOrders = 0;
            
            if (allOrders != null && !allOrders.isEmpty()) {
                totalOrders = allOrders.size();
                
                // Tính tổng doanh thu từ các đơn hàng
                for (Order order : allOrders) {
                    if (order.getTotal() != null) {
                        totalRevenue = totalRevenue.add(BigDecimal.valueOf(order.getTotal()));
                    }
                }
            }
            
            BigDecimal averageOrderValue = totalOrders > 0 
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
            
            summary.put("totalRevenue", totalRevenue.setScale(2, RoundingMode.HALF_UP));
            summary.put("totalOrders", totalOrders);
            summary.put("averageOrderValue", averageOrderValue);
            summary.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to load revenue summary: " + e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/daily")
    public ResponseEntity<List<Map<String, Object>>> getDailyRevenue(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<Map<String, Object>> dailyData = new ArrayList<>();
        
        try {
            // Validate dates if provided
            LocalDate start = null;
            LocalDate end = null;
            
            if (startDate != null && !startDate.trim().isEmpty()) {
                try {
                    // Accept both formats: yyyy-MM-dd and yyyy-MM-dd HH:mm:ss
                    if (startDate.contains(" ")) {
                        start = LocalDateTime.parse(startDate.trim(), DATE_FORMATTER).toLocalDate();
                    } else {
                        start = LocalDate.parse(startDate.trim());
                    }
                } catch (DateTimeParseException e) {
                    logger.warn("Invalid startDate format: {}", startDate);
                    return ResponseEntity.badRequest().build();
                }
            }
            
            if (endDate != null && !endDate.trim().isEmpty()) {
                try {
                    // Accept both formats: yyyy-MM-dd and yyyy-MM-dd HH:mm:ss
                    if (endDate.contains(" ")) {
                        end = LocalDateTime.parse(endDate.trim(), DATE_FORMATTER).toLocalDate();
                    } else {
                        end = LocalDate.parse(endDate.trim());
                    }
                } catch (DateTimeParseException e) {
                    logger.warn("Invalid endDate format: {}", endDate);
                    return ResponseEntity.badRequest().build();
                }
            }
            
            // Validate date range if both provided
            if (start != null && end != null && start.isAfter(end)) {
                logger.warn("Invalid date range: start date is after end date");
                return ResponseEntity.badRequest().build();
            }
            
            List<Order> allOrders = orderService.getAll();
            Map<String, BigDecimal> dailyRevenue = new LinkedHashMap<>();
            Map<String, Integer> dailyOrderCount = new LinkedHashMap<>();
            
            if (allOrders != null && !allOrders.isEmpty()) {
                for (Order order : allOrders) {
                    if (order.getCreatedAt() != null && order.getTotal() != null) {
                        LocalDate date = order.getCreatedAt().toLocalDate();
                        
                        // Filter by date range if specified
                        if (start != null && date.isBefore(start)) continue;
                        if (end != null && date.isAfter(end)) continue;
                        
                        String dateKey = date.toString();
                        
                        dailyRevenue.put(dateKey,
                            dailyRevenue.getOrDefault(dateKey, BigDecimal.ZERO).add(BigDecimal.valueOf(order.getTotal())));
                        dailyOrderCount.put(dateKey,
                            dailyOrderCount.getOrDefault(dateKey, 0) + 1);
                    }
                }
            }
            
            dailyRevenue.forEach((date, revenue) -> {
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", date);
                dayData.put("amount", revenue.setScale(2, RoundingMode.HALF_UP));
                dayData.put("orders", dailyOrderCount.get(date));
                dailyData.add(dayData);
            });
            
            return ResponseEntity.ok(dailyData);
        } catch (Exception e) {
            logger.error("Error in getDailyRevenue: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
