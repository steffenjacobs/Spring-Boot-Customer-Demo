package me.check24.demo.domain.dto;

import java.util.UUID;

import me.check24.demo.domain.entity.Customer;

/**
 * DTO object associated to {@link Customer} entities. Prunes the creation date
 * away.
 */
public class CustomerDto {

	private UUID id;
	private String name;

	public CustomerDto() {
		super();
	}

	private CustomerDto(UUID id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static CustomerDto fromEntity(Customer c) {
		return new CustomerDto(c.getId(), c.getName());
	}

	public Customer toCustomer() {
		Customer c = new Customer();
		c.setId(id);
		c.setName(name);
		return c;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomerDto other = (CustomerDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

}
