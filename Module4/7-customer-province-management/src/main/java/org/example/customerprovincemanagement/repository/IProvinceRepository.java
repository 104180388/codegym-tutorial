package org.example.customerprovincemanagement.repository;

import org.example.customerprovincemanagement.model.Province;
import org.springframework.data.repository.CrudRepository;

public interface IProvinceRepository extends CrudRepository<Province, Long> {
}
