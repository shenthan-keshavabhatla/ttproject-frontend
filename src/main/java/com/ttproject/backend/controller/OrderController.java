package com.ttproject.backend.controller;
import java.util.List;
import java.util.Map;

import com.ttproject.backend.entity.Order;
import com.ttproject.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/create")
    public Order createOrder(@RequestParam String phone,
                             @RequestParam int timer) {

        return orderService.createOrder(phone, timer);
    }
    @GetMapping("/login")
public Order login(@RequestParam String phone,
                   @RequestParam String password) {

    return orderService.login(phone, password);
}
    @GetMapping("/mark-ready")
public Order markReady(@RequestParam String orderId){
    return orderService.markReady(orderId);
}
@GetMapping("/all")
public List<Order> getAllOrders(){
    return orderService.getAllOrders();
}
@GetMapping("/get")
public Order getOrder(@RequestParam String orderId) {
    return orderService.getOrderById(orderId);
}

@GetMapping("/add-time")
public Order addTime(@RequestParam String orderId, @RequestParam(defaultValue = "5") int minutes) {
    return orderService.addExtraTime(orderId, minutes);
}

@GetMapping("/mark-completed")
public Order markCompleted(@RequestParam String orderId) {
    return orderService.markCompleted(orderId);
}

@GetMapping("/completed")
public List<Order> getCompletedOrders() {
    return orderService.getCompletedOrders();
}

@GetMapping("/analytics")
public Map<String, Object> getAnalytics() {
    return orderService.getAnalytics();
}

@GetMapping("/delete")
public String deleteOrder(@RequestParam String orderId) {
    orderService.deleteOrder(orderId);
    return "Order marked as completed successfully";
}

@GetMapping("/delete-all-completed")
public String deleteAllCompleted() {
    int deletedCount = orderService.deleteAllCompleted();
    return deletedCount + " completed order(s) deleted successfully";
}
}