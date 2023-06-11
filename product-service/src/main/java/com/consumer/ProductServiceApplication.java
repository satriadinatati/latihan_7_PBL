package com.consumer;

import com.consumer.entity.Product;
import com.consumer.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductServiceApplication implements CommandLineRunner {

    @Autowired
    ProductRepository productRepository;

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // Load some data in db
        final Product chair = new Product("Chair", 12);
        final Product desk = new Product("Desk", 6);
        productRepository.save(chair);
        productRepository.save(desk);

    }
}
