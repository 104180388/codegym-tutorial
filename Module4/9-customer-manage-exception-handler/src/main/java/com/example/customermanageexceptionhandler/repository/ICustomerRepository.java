package com.example.customermanageexceptionhandler.repository;

import com.example.customermanageexceptionhandler.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICustomerRepository extends JpaRepository<Customer, Long> {
}
