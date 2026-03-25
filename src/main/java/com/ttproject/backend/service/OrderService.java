package com.ttproject.backend.service;
import java.util.List;
import com.ttproject.backend.entity.Customer;
import com.ttproject.backend.entity.Order;
import com.ttproject.backend.repository.CustomerRepository;
import com.ttproject.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // CREATE ORDER
    public Order createOrder(String phone, int timer) {

        String orderId = generateOrderId();

        Customer customer = customerRepository.findById(phone).orElse(null);

        if (customer == null) {
            customer = new Customer(phone, orderId);
        } else {
            customer.setPassword(orderId);
        }

        customerRepository.save(customer);

        Order order = new Order();
        order.setOrderId(orderId);
        order.setPhone(phone);
        order.setTimer(timer);
        order.setStatus("PREPARING");
        order.setCreatedTime(LocalDateTime.now());

        return orderRepository.save(order);
    }

    // LOGIN CUSTOMER
    public Order login(String phone, String password) {

        Customer customer = customerRepository.findById(phone).orElse(null);

        if (customer == null) {
            return null;
        }

        if (!customer.getPassword().equals(password)) {
            return null;
        }

        return orderRepository.findAll()
                .stream()
                .filter(o -> o.getOrderId().equals(password))
                .findFirst()
                .orElse(null);
    }

    // GENERATE ORDER ID
    private String generateOrderId() {
        Random random = new Random();
        int number = 100 + random.nextInt(900);
        return "A" + number;
    }

    public Order markReady(String orderId) {
        Order order = orderRepository.findAll()
                .stream()
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);

        if(order != null){
            order.setStatus("READY");
            orderRepository.save(order);
        }

        return order;
    }

    public List<Order> getAllOrders(){
        return orderRepository.findAll()
                .stream()
                .filter(o -> !o.getStatus().equals("COMPLETED"))
                .toList();
    }

    public Order getOrderById(String orderId) {
        return orderRepository.findAll()
                .stream()
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    public Order addExtraTime(String orderId, int extraMinutes) {
        Order order = orderRepository.findAll()
                .stream()
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);

        if(order != null){
            order.setTimer(order.getTimer() + extraMinutes);
            orderRepository.save(order);
        }

        return order;
    }

    public Order markCompleted(String orderId) {
        Order order = orderRepository.findAll()
                .stream()
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);

        if(order != null){
            order.setStatus("COMPLETED");
            order.setCompletedAt(LocalDateTime.now());
            orderRepository.save(order);
        }

        return order;
    }

    public void deleteOrder(String orderId) {
        markCompleted(orderId);
    }

    public List<Order> getCompletedOrders() {
        return orderRepository.findAll()
                .stream()
                .filter(o -> o.getStatus().equals("COMPLETED"))
                .sorted((a, b) -> b.getCompletedAt().compareTo(a.getCompletedAt()))
                .toList();
    }

    public int deleteAllCompleted() {
        List<Order> completedOrders = orderRepository.findAll()
                .stream()
                .filter(o -> o.getStatus().equals("COMPLETED"))
                .toList();

        if(!completedOrders.isEmpty()){
            orderRepository.deleteAll(completedOrders);
        }

        return completedOrders.size();
    }

    // ANALYTICS
    public Map<String, Object> getAnalytics() {
        List<Order> allOrders = orderRepository.findAll();
        List<Order> completedOrders = allOrders.stream()
                .filter(o -> o.getStatus().equals("COMPLETED"))
                .toList();
        List<Order> todayOrders = allOrders.stream()
                .filter(o -> o.getCreatedTime().toLocalDate().equals(LocalDate.now()))
                .toList();
        List<Order> todayCompleted = todayOrders.stream()
                .filter(o -> o.getStatus().equals("COMPLETED"))
                .toList();

        double avgPrepTime = completedOrders.isEmpty() ? 0 : 
            completedOrders.stream()
                .mapToDouble(o -> {
                    long seconds = java.time.temporal.ChronoUnit.SECONDS.between(o.getCreatedTime(), o.getCompletedAt());
                    return seconds / 60.0; // convert to minutes
                })
                .average()
                .orElse(0);

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalOrders", allOrders.size());
        analytics.put("completedOrders", completedOrders.size());
        analytics.put("todayOrders", todayOrders.size());
        analytics.put("todayCompleted", todayCompleted.size());
        analytics.put("averagePreparationTime", Math.round(avgPrepTime));
        
        return analytics;
    }
}
