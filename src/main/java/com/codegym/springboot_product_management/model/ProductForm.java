package com.codegym.springboot_product_management.model;

import lombok.Data;

import jakarta.persistence.*;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductForm {
    private Long id;
    private String name;
    private Double price;
    private Integer quantity;
    private MultipartFile imageUrl;
    private String oldImage;

    public ProductForm() {
    }
}
