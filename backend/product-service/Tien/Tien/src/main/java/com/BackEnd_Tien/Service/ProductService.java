package com.BackEnd_Tien.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.BackEnd_Tien.Entity.ProductDTO;
import com.BackEnd_Tien.Entity.Products;
import com.BackEnd_Tien.Repository.ProductRepository;
import com.BackEnd_Tien.client.AuditClient;
import com.BackEnd_Tien.client.AuditLogRequest;

import jakarta.transaction.Transactional;

@Service
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private AuditClient auditClient;
    
    @Autowired
    private ObjectMapper objectMapper;

    public List<Products> getAllProducts() {
        return productRepository.findAll();
    }

    // Lấy sản phẩm theo ID
    public Products getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm có ID: " + id));
    }

   
    //Them sản phẩm 
    public Products saveProduct(ProductDTO productDTO) {	
    	Products newProduct = new Products();
    	newProduct.setName(productDTO.getName());
    	newProduct.setCategory(productDTO.getCategory());
    	newProduct.setDescription(productDTO.getDescription());
    	newProduct.setImageUrl(productDTO.getImageUrl());
    	newProduct.setPrice(productDTO.getPrice());
    	newProduct.setQuantum(productDTO.getQuantum());
    	
    	Products savedProduct = productRepository.save(newProduct);
    	
    	// Log to Audit Service
        try {
            auditClient.logAudit(AuditLogRequest.builder()
                .sourceService("product-service")
                .actionType("CREATE")
                .entityName("PRODUCT")
                .entityId(savedProduct.getId().toString())
                .userId("admin")
                .newValue(objectMapper.writeValueAsString(savedProduct))
                .ipAddress("0.0.0.0")
                .userAgent("product-service")
                .build());
        } catch (Exception e) {
            logger.warn("Failed to audit product creation: {}", e.getMessage());
        }
    	
    	return savedProduct;
    }
    
//    public List<Products> getGroupProducts(String typeOfProduct) {
//       
//        return productRepository.searchByKeyword(typeOfProduct);
//    }
    /// CẬP NHẬT SẢN PHẨM
    public Products updateProducts(Long id, ProductDTO productDTO) {
		Products exProduct = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm có ID: " + id));
		
		String oldValue = null;
        try {
            oldValue = objectMapper.writeValueAsString(exProduct);
        } catch (Exception e) {
            logger.warn("Failed to serialize old product state: {}", e.getMessage());
        }
    	
    	exProduct.setName(productDTO.getName());
    	exProduct.setPrice(productDTO.getPrice());
    	exProduct.setDescription(productDTO.getDescription());
    	exProduct.setCategory(productDTO.getCategory());		
    	exProduct.setImageUrl(productDTO.getImageUrl());
    	exProduct.setQuantum(productDTO.getQuantum());
    	
    	Products updatedProduct = productRepository.save(exProduct);
    	
    	// Log to Audit Service
        try {
            auditClient.logAudit(AuditLogRequest.builder()
                .sourceService("product-service")
                .actionType("UPDATE")
                .entityName("PRODUCT")
                .entityId(id.toString())
                .userId("admin")
                .oldValue(oldValue)
                .newValue(objectMapper.writeValueAsString(updatedProduct))
                .ipAddress("0.0.0.0")
                .userAgent("product-service")
                .build());
        } catch (Exception e) {
            logger.warn("Failed to audit product update: {}", e.getMessage());
        }
    	
    	return updatedProduct;
    }
    //Tìm kiếm theo danh mục sản phẩm
    public List<Products> getGroupProducts(String typeOfProduct) {
        // Tìm trong field Category
        return productRepository.findByCategoryContaining(typeOfProduct);
    }
	public Products deleteProducts(Long id) {
		// TODO Auto-generated method stub
		if (!productRepository.existsById(id)) {
	        throw new RuntimeException("Không tìm thấy sản phẩm để xóa!");
	    }
	    
	    Products product = productRepository.findById(id).orElse(null);
	    productRepository.deleteById(id);
	    
	    // Log to Audit Service
        try {
            if (product != null) {
                auditClient.logAudit(AuditLogRequest.builder()
                    .sourceService("product-service")
                    .actionType("DELETE")
                    .entityName("PRODUCT")
                    .entityId(id.toString())
                    .userId("admin")
                    .oldValue(objectMapper.writeValueAsString(product))
                    .ipAddress("0.0.0.0")
                    .userAgent("product-service")
                    .build());
            }
        } catch (Exception e) {
            logger.warn("Failed to audit product deletion: {}", e.getMessage());
        }
		
		return null;
	}
	
	@Transactional
	public Products purchaseProduct(Long id, int quantityToBuy) {
		//tìm sản phẩm
	    Products product = productRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm có ID: " + id));

	    // Lấy số lượng hiện tại, nếu null thì gán bằng 0
	    Integer currentQuantum = product.getQuantum();
	    if (currentQuantum == null) {
	        currentQuantum = 0;
	    }

	    // Kiểm tra đủ hàng không
	    if (currentQuantum < quantityToBuy) {
	        throw new RuntimeException("Sản phẩm này chỉ còn " + currentQuantum + " cái (hoặc chưa nhập số lượng), không đủ để bán!");
	    }

	    // Trừ kho
	    int newQuantum = currentQuantum - quantityToBuy;
	    product.setQuantum(newQuantum);

	    return productRepository.save(product);
	}
}
