package me.check24.demo;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import me.check24.demo.domain.CustomerRepository;
import me.check24.demo.domain.dto.CustomerDto;
import me.check24.demo.domain.entity.Customer;

/**
 * This {@link CustomerController} contains all relevant CRUD endpoints for
 * creating, reading, updating and deleting single customer objects. In
 * addition, there this provides an endpoint to fetch all customers. <br/>
 * <br/>
 * Keep in mind, that these endpoints are secured.
 */
@RestController
public class CustomerController {

	@Autowired
	CustomerRepository customerRepository;

	@PutMapping(path = "api/customer", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CustomerDto> createOrUpdateCustomer(@RequestBody CustomerDto customerDto) {
		if (customerDto.getId() != null && !customerRepository.existsById(customerDto.getId())) {
			return new ResponseEntity<>(customerDto, HttpStatus.NOT_FOUND);
		}
		// TODO: corner case: customerDto was deleted between check and this save-action
		Customer customer = customerRepository.save(customerDto.toCustomer());
		return new ResponseEntity<>(CustomerDto.fromEntity(customer),
				customerDto.getId() == null ? HttpStatus.CREATED : HttpStatus.ACCEPTED);
	}

	@DeleteMapping(path = "api/customer/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CustomerDto> deleteCustomer(@PathVariable(name = "id") UUID id) {
		try {
			customerRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (EmptyResultDataAccessException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping(path = "api/customer/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CustomerDto> getCustomer(@PathVariable(name = "id") UUID id) {
		Optional<Customer> c = customerRepository.findById(id);
		return c.isPresent() ? new ResponseEntity<>(CustomerDto.fromEntity(c.get()), HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@GetMapping(path = "api/customer", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<CustomerDto>> getAllCustomers() {
		return new ResponseEntity<>(StreamSupport.stream(customerRepository.findAll().spliterator(), false)
				.map(CustomerDto::fromEntity).collect(Collectors.toSet()), HttpStatus.OK);
	}

}
