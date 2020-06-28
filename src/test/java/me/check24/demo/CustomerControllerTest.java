package me.check24.demo;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import me.check24.demo.domain.dto.CustomerDto;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CustomerControllerTest {

	@LocalServerPort
	protected int port;

	@Autowired
	protected TestRestTemplate restTemplate;

	@Test
	@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
	void testCustomerCrud() {

		HttpHeaders headers = createHeaders();

		// check existing customer objects
		ResponseEntity<CustomerDto[]> response = restTemplate.exchange(getBaseUrlWithPort() + "/api/customer",
				HttpMethod.GET, new HttpEntity<>(headers), CustomerDto[].class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals(0, response.getBody().length, "Did not expect any customer objects yet.");

		// create customer
		CustomerDto customerToCreate = new CustomerDto();
		customerToCreate.setName("Test Customer #1");

		ResponseEntity<CustomerDto> customerCreationResponse = restTemplate.exchange(
				getBaseUrlWithPort() + "/api/customer", HttpMethod.PUT, new HttpEntity<>(customerToCreate, headers),
				CustomerDto.class);

		// check created customer
		Assertions.assertEquals(HttpStatus.CREATED, customerCreationResponse.getStatusCode());
		CustomerDto createdCustomer = customerCreationResponse.getBody();
		Assertions.assertNotNull(createdCustomer);
		Assertions.assertNotNull(createdCustomer.getId());
		Assertions.assertEquals(customerToCreate.getName(), createdCustomer.getName());

		// check if created customer is still there
		assertThatCustomerExistsAndEquals(createdCustomer, headers);

		// update customer
		CustomerDto customerToUpdate = new CustomerDto();
		customerToUpdate.setId(createdCustomer.getId());
		customerToUpdate.setName("Updated Test Customer #1");

		ResponseEntity<CustomerDto> customerUpdateResponse = restTemplate.exchange(
				getBaseUrlWithPort() + "/api/customer", HttpMethod.PUT, new HttpEntity<>(customerToUpdate, headers),
				CustomerDto.class);

		// check updated customer
		Assertions.assertEquals(HttpStatus.ACCEPTED, customerUpdateResponse.getStatusCode());
		CustomerDto updatedCustomer = customerUpdateResponse.getBody();
		Assertions.assertNotNull(updatedCustomer);
		Assertions.assertEquals(customerToUpdate.getId(), updatedCustomer.getId());
		Assertions.assertEquals(customerToUpdate.getName(), updatedCustomer.getName());

		// check if updated customer is still there
		assertThatCustomerExistsAndEquals(updatedCustomer, headers);

		// delete customer

		ResponseEntity<CustomerDto> customerDeletionResponse = restTemplate.exchange(
				getBaseUrlWithPort() + "/api/customer/" + updatedCustomer.getId().toString(), HttpMethod.DELETE,
				new HttpEntity<>(headers), CustomerDto.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, customerDeletionResponse.getStatusCode());

		// check if updated customer is now gone
		Assertions.assertThrows(AssertionError.class,
				() -> assertThatCustomerExistsAndEquals(updatedCustomer, headers));

	}

	private HttpHeaders createHeaders() {
		return new HttpHeaders() {
			private static final long serialVersionUID = -6118084207904100772L;
			{
				String auth = "admin:admin";
				byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodedAuth);
				System.out.println(authHeader);
				set("Authorization", authHeader);
			}
		};
	}

	@Test
	void testFindeWithInvalidId() {
		CustomerDto customer = new CustomerDto();
		customer.setId(UUID.randomUUID());
		customer.setName("Nonexistant customer");

		ResponseEntity<CustomerDto> customerReadUpdatedResponse = restTemplate.exchange(
				getBaseUrlWithPort() + "/api/customer/" + customer.getId().toString(), HttpMethod.GET,
				new HttpEntity<>(createHeaders()), CustomerDto.class);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, customerReadUpdatedResponse.getStatusCode());
	}

	@Test
	@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
	void testDeleteWithInvalidId() {
		CustomerDto customer = new CustomerDto();
		customer.setId(UUID.randomUUID());
		customer.setName("Nonexistant customer");

		ResponseEntity<CustomerDto> customerReadUpdatedResponse = restTemplate.exchange(
				getBaseUrlWithPort() + "/api/customer/" + customer.getId().toString(), HttpMethod.DELETE,
				new HttpEntity<>(createHeaders()), CustomerDto.class);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, customerReadUpdatedResponse.getStatusCode());
	}

	@Test
	@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
	void testCreateMultiple() {
		CustomerDto[] customers = new CustomerDto[] { createCustomer("Test Customer #1"),
				createCustomer("Test Customer #2"), createCustomer("Test Customer #3") };
		HttpHeaders headers = createHeaders();

		// create multiple customers
		for (CustomerDto customer : customers) {
			ResponseEntity<CustomerDto> customerCreationResponse = restTemplate.exchange(
					getBaseUrlWithPort() + "/api/customer", HttpMethod.PUT, new HttpEntity<>(customer, headers),
					CustomerDto.class);

			// check if customer was created properly
			Assertions.assertEquals(HttpStatus.CREATED, customerCreationResponse.getStatusCode());
			CustomerDto createdCustomer = customerCreationResponse.getBody();
			Assertions.assertNotNull(createdCustomer);
			Assertions.assertNotNull(createdCustomer.getId());
			Assertions.assertEquals(customer.getName(), createdCustomer.getName());
		}

		// get all + verify
		ResponseEntity<CustomerDto[]> response = restTemplate.exchange(getBaseUrlWithPort() + "/api/customer",
				HttpMethod.GET, new HttpEntity<>(headers), CustomerDto[].class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

		CustomerDto[] customersCreated = response.getBody();
		Assertions.assertEquals(customers.length, customersCreated.length);
		for (CustomerDto createdCustomer : customersCreated) {
			Assertions.assertNotNull(createdCustomer.getId());
			Assertions
					.assertTrue(Arrays.stream(customers).anyMatch(c -> c.getName().equals(createdCustomer.getName())));
		}
	}

	private CustomerDto createCustomer(String name) {
		CustomerDto customer = new CustomerDto();
		customer.setName(name);
		return customer;
	}

	private void assertThatCustomerExistsAndEquals(CustomerDto customer, HttpHeaders headers) {
		ResponseEntity<CustomerDto> customerReadUpdatedResponse = restTemplate.exchange(
				getBaseUrlWithPort() + "/api/customer/" + customer.getId().toString(), HttpMethod.GET,
				new HttpEntity<>(headers), CustomerDto.class);
		Assertions.assertEquals(HttpStatus.OK, customerReadUpdatedResponse.getStatusCode());
		Assertions.assertEquals(customer, customerReadUpdatedResponse.getBody());
	}

	private String getBaseUrlWithPort() {
		return "http://localhost:" + port;
	}

}
