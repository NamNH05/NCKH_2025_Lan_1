package com.BackEnd_Tien.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.BackEnd_Tien.Entity.Products;

@Repository
public interface ProductRepository extends JpaRepository<Products, Long> {
	// Tìm kiếm sản phẩm theo tên
    List<Products> findByNameContaining(String name);
    
 // Tìm chính xác theo loại (hoặc chứa từ khóa trong loại)
    List<Products> findByCategoryContaining(String category);
    
}