package com.codegym.springboot_product_management.controller;

import com.codegym.springboot_product_management.model.Product;
import com.codegym.springboot_product_management.model.ProductForm;
import com.codegym.springboot_product_management.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Value("${file.upload}")
    private String uploadFile;

    @Autowired
    private IProductService productService;

    @GetMapping("/create")
    public ModelAndView showCreateForm(){
        ModelAndView modelAndView = new ModelAndView("/product/create");
        modelAndView.addObject("productForm",new ProductForm());
        return modelAndView;
    }

    @PostMapping("/create")
    public String saveProduct(@ModelAttribute("productForm") ProductForm productForm){
        MultipartFile multipartFile = productForm.getImageUrl();
        String fileName = multipartFile.getOriginalFilename();
        try {
            FileCopyUtils.copy(productForm.getImageUrl().getBytes(),new File(uploadFile + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Product product = new Product(productForm.getId(), productForm.getName(), productForm.getPrice(), productForm.getQuantity(), fileName);
        productService.save(product);
        return "redirect:/products/list";
    }

    @GetMapping("/list")
    public ModelAndView showAllProduct(){
        ModelAndView modelAndView = new ModelAndView("product/list");
        modelAndView.addObject("products",productService.findAll());
        return modelAndView;
    }

    @GetMapping("/update")
    public ModelAndView showUpdateForm(@RequestParam Long id){
        Optional<Product> productOptional = productService.findById(id);
        ModelAndView modelAndView = new ModelAndView("product/update");
        if (!productOptional.isPresent()) {
            throw new IllegalArgumentException("Invalid product Id:" + id);
        }
        Product product = productOptional.get();
        ProductForm productForm = new ProductForm();
        productForm.setId(product.getId());
        productForm.setName(product.getName());
        productForm.setPrice(product.getPrice());
        productForm.setQuantity(product.getQuantity());
        productForm.setOldImage(product.getImageUrl());
        modelAndView.addObject("productForm", productForm);
        return modelAndView;
    }
    @PostMapping("/update")
    public String updateProduct(@ModelAttribute ProductForm productForm){
        MultipartFile multipartFile = productForm.getImageUrl();
        String fileName = multipartFile.getOriginalFilename();
        if (!multipartFile.isEmpty()){
            try{
                File oldFile = new File(uploadFile + productForm.getOldImage());
                if (oldFile.exists()){
                    oldFile.delete();
                }
                FileCopyUtils.copy(productForm.getImageUrl().getBytes(),new File(uploadFile + fileName));
            } catch (IOException e){

            }
        } else {
            fileName = productForm.getOldImage();
        }

        Product product = new Product(productForm.getId(),productForm.getName(), productForm.getPrice(), productForm.getQuantity(), fileName);
        productService.save(product);
        return "redirect:/products/list";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam Long id){
        productService.remove(id);
        return "redirect:/products/list";
    }
}
