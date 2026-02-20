package com.example.order_service.controller;

import com.example.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // TẠO ĐƠN TỪ GIỎ
    @GetMapping("/create")
    public String createOrder() {
        Long userId = 1L;
        orderService.checkout(userId);
        return "redirect:/orders";
    }

    // XEM DANH SÁCH ĐƠN
    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", orderService.getAll());
        return "orders";
    }

    // THANH TOÁN
    @GetMapping("/pay/{id}")
    public String pay(@PathVariable Long id) {
        orderService.pay(id);
        return "redirect:/orders";
    }

    // XÓA ĐƠN
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        orderService.delete(id);
        return "redirect:/orders";
    }
}










