package org.example.customerprovincemanagement.repository;

import org.example.customerprovincemanagement.model.Customer;
import org.example.customerprovincemanagement.model.Province;
import org.springframework.data.repository.CrudRepository;

public interface ICustomerRepository extends CrudRepository<Customer, Long> {
    Iterable<Customer> findAllByProvince(Province province);

}
