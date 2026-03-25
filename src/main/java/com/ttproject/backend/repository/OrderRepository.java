package com.ttproject.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ttproject.backend.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}