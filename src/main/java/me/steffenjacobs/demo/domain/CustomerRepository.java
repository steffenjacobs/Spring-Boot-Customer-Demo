package me.steffenjacobs.demo.domain;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import me.steffenjacobs.demo.domain.entity.Customer;

public interface CustomerRepository extends CrudRepository<Customer, UUID> {

}
