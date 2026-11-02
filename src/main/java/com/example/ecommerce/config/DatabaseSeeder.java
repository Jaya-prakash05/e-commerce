package com.example.ecommerce.config;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DatabaseSeeder {

	private final ProductRepository productRepository;

	public DatabaseSeeder(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void seedProducts() {
		if (productRepository.count() > 0) {
			return;
		}

		List<Product> products = List.of(
				new Product("Bluetooth Speaker", "Portable wireless speaker with deep bass.", new BigDecimal("59.99"),
						150, "https://example.com/speaker.jpg"),
				new Product("Laptop Stand", "Aluminum stand for ergonomic desk setup.", new BigDecimal("39.50"), 80,
						"https://example.com/stand.jpg"),
				new Product("Smart Watch", "Fitness and smartwatch with notifications.", new BigDecimal("129.00"), 45,
						"https://example.com/watch.jpg"),
				new Product("Noise Cancelling Headphones", "Premium over-ear headphones with 30hr battery.",
						new BigDecimal("199.99"), 60, "https://example.com/headphones.jpg"));

		productRepository.saveAll(products);
	}
}
