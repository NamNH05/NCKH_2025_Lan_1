package com.BackEnd_Tien.Controller.API;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.BackEnd_Tien.Entity.ProductDTO;
import com.BackEnd_Tien.Entity.Products;
import com.BackEnd_Tien.Service.ProductService;
import com.BackEnd_Tien.util.InputValidator;

@RestController
@RequestMapping("/api/products")
@CrossOrigin("*") 
public class ProductController {

    @Autowired
    private ProductService productService;
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

   //Thêm sản phẩm
    @PostMapping("")
    public Products addProduct(@RequestBody ProductDTO productDTO) {
        return productService.saveProduct(productDTO);
    }
    
    
    //Api cập nhật lại sản phẩm
    @PutMapping(value = "/{id}")
    public Products updateProduct(@PathVariable Long id,@RequestBody ProductDTO productDTO) {
    	return productService.updateProducts(id,productDTO);
    }
    
    
    // xoá sản phẩm 
    @DeleteMapping(value = "/{id}")
    public Products deleteProducts(@PathVariable Long id) {
    	return productService.deleteProducts(id);
    }
    
 // API lấy danh sách tất cả sản phẩm
    @GetMapping("")
    public List<Products> getAll() {
        return productService.getAllProducts();
    }    
    // API lấy chi tiết sản phẩm theo ID
    @GetMapping(value = "/{id}")
    public Products getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }
        //Tìm kiếm thông tin sản phẩm hoặc lọc theo category
     @GetMapping(value = "/search")
     public ResponseEntity<?> searchProducts(@RequestParam(name = "keyword", required = false) String keyword) {
         if (keyword == null || keyword.trim().isEmpty()) {
             return ResponseEntity.ok(productService.getAllProducts());
         }
         
         // Validate keyword to prevent SQL injection
         if (!InputValidator.isValidKeyword(keyword)) {
             logger.warn("Invalid keyword attempted: {}", keyword);
             return ResponseEntity.badRequest()
                 .body("Invalid keyword. Maximum length is 255 characters. Special SQL keywords are not allowed.");
         }
         
         return ResponseEntity.ok(productService.getGroupProducts(keyword));
     }
     
     //Phân loại sản phẩm theo category
     @GetMapping(value = "/category/{type}")
     public ResponseEntity<?> getGroupProduct(@PathVariable("type") String type){
         // Validate category format
         if (!InputValidator.isValidCategory(type)) {
             logger.warn("Invalid category attempted: {}", type);
             return ResponseEntity.badRequest()
                 .body("Invalid category format");
         }
    	 return ResponseEntity.ok(productService.getGroupProducts(type));
     }
     
//     //Cập nhật theo số lượng sản phẩm
//     @PutMapping(value = "/quantum/{num}")
//     public Products updateProducts 
     
     //Kiểm tra xem số lượng sản phẩm còn kho
     @PostMapping("/purchase/{id}")
     public ResponseEntity<?> purchaseProduct(@PathVariable Long id, 
                                     @RequestParam("quantity") int quantity) {
         // Validate quantity
         if (!InputValidator.isValidQuantity(quantity)) {
             logger.warn("Invalid quantity attempted: {}", quantity);
             return ResponseEntity.badRequest()
                 .body("Invalid quantity. Must be between 1 and 10000");
         }
         
         return ResponseEntity.ok(productService.purchaseProduct(id, quantity));
     }
}