package org.example.customerprovincemanagement.service;

import org.example.customerprovincemanagement.model.Customer;
import org.example.customerprovincemanagement.model.Province;

import java.util.List;

public interface ICustomerService extends IGenerateService<Customer> {
    Iterable<Customer> findAllByProvince(Province province);
}
