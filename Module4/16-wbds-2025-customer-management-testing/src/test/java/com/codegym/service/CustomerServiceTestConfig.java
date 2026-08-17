package com.codegym.service;

import com.codegym.wbds2025customermanagementtesting.repository.CustomerRepository;
import com.codegym.wbds2025customermanagementtesting.service.CustomerService;
import com.codegym.wbds2025customermanagementtesting.service.impl.CustomerServiceImplWithSpringData;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerServiceTestConfig {

    @Bean
    public CustomerRepository customerRepository() {
        return Mockito.mock(CustomerRepository.class);
    }

    @Bean
    public CustomerService customerService() {
        return new CustomerServiceImplWithSpringData(customerRepository());
    }
}
