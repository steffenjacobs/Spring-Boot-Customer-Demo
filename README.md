# Spring Boot Demo Project

This is a simple [Spring Boot](https://spring.io/projects/spring-boot), [Spring Data JPA](https://spring.io/projects/spring-data-jpa) and [Spring Security](https://spring.io/projects/spring-security) demonstration project. 

Use `./gradlew build` to build the project.

There are four HTTP endpoints:

## Retrieve all customers
    curl --location --request GET 'localhost:8080/api/customer' \
    --header 'Authorization: Basic YWRtaW46YWRtaW4=' \

## Retrieve an existing customer by its respective UUID
    curl --location --request GET 'localhost:8080/api/customer/123e4567-e89b-12d3-a456-556642440000' \
    --header 'Authorization: Basic YWRtaW46YWRtaW4=' \

## Create a new customer
    curl --location --request PUT 'localhost:8080/api/customer' \
    --header 'Authorization: Basic YWRtaW46YWRtaW4=' \
    --header 'Content-Type: application/json' \
    --data-raw '{ "name": "Test Customer #1" }'

## Delete an existing customer
    curl --location --request DELETE 'localhost:8080/api/customer/123e4567-e89b-12d3-a456-556642440000' \
    --header 'Authorization: Basic YWRtaW46YWRtaW4=' \
