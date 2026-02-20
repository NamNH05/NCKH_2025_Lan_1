package com.example.order_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class OrderDTO {
    
    private Long id;
    
    @JsonProperty("customerId")
    private Long customerId;
    
    private Double total;
    
    private String status;
    
    private String address;
    
    @JsonProperty("itemDetails")
    private String itemDetails;
    
    @JsonProperty("shippingFee")
    private Double shippingFee;
    
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
    
    // ===== CONSTRUCTOR =====
    
    public OrderDTO() {}
    
    public OrderDTO(Long id, Long customerId, Double total, String status, 
                    String address, String itemDetails, Double shippingFee, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.total = total;
        this.status = status;
        this.address = address;
        this.itemDetails = itemDetails;
        this.shippingFee = shippingFee;
        this.createdAt = createdAt;
    }
    
    // ===== GETTERS / SETTERS =====
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public Double getTotal() {
        return total;
    }
    
    public void setTotal(Double total) {
        this.total = total;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getItemDetails() {
        return itemDetails;
    }
    
    public void setItemDetails(String itemDetails) {
        this.itemDetails = itemDetails;
    }
    
    public Double getShippingFee() {
        return shippingFee;
    }
    
    public void setShippingFee(Double shippingFee) {
        this.shippingFee = shippingFee;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", total=" + total +
                ", status='" + status + '\'' +
                ", address='" + address + '\'' +
                ", itemDetails='" + itemDetails + '\'' +
                ", shippingFee=" + shippingFee +
                ", createdAt=" + createdAt +
                '}';
    }
}
