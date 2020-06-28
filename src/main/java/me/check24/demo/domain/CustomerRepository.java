package me.check24.demo.domain;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import me.check24.demo.domain.entity.Customer;

public interface CustomerRepository extends CrudRepository<Customer, UUID> {

}
