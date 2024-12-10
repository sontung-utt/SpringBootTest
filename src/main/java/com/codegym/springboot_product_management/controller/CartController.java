package com.codegym.springboot_product_management.controller;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import com.codegym.springboot_product_management.model.Items;
import com.codegym.springboot_product_management.model.Product;
import com.codegym.springboot_product_management.service.IProductService;
import com.codegym.springboot_product_management.service.implement.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/cart")
public class CartController {
    @Autowired
    private IProductService pm;

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/order/{id}", method = RequestMethod.GET)
    public String orderNow(@PathVariable(value = "id") Long id, HttpSession session) {
        if (session.getAttribute("cart") == null) {
            List<Items> cart = new ArrayList<Items>();
            Optional<Product> product = pm.findById(id);
            cart.add(new Items(product.get(), 1));
            session.setAttribute("cart",cart);
        } else {
            List<Items> cart = (List<Items>) session.getAttribute("cart");
            // using method isExisting here
            int index = isExisting(id, session);
            if (index == -1)
                cart.add(new Items(this.pm.findById(id).get(), 1));
            else {
                int quantity = cart.get(index).getQuantity() + 1;
                cart.get(index).setQuantity(quantity);
            }
            session.setAttribute("cart", cart);
        }
        return "/product/cart";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(HttpServletRequest request, HttpSession session) {
        List<Items> cart = (List<Items>) session.getAttribute("cart");
        String[] quantities = request.getParameterValues("qty");
        for (int i = 0; i < cart.size(); i++) {
            int quantity = Integer.parseInt(quantities[i]);
            cart.get(i).setQuantity(quantity);
        }
        session.setAttribute("cart", cart);
        return "/product/cart";
    }

    @SuppressWarnings("unchecked")
    private int isExisting(Long id, HttpSession session) {
        List<Items> cart = (List<Items>) session.getAttribute("cart");
        for (int i = 0; i < cart.size(); i++) {
            Product product = cart.get(i).getProduct();
            if (product.getId() == id) {
                return i;
            }
        }
        return -1;
    }
}
