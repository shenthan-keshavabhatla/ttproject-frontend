package com.ttproject.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ttproject.backend.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {
}