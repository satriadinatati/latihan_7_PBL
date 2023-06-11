package com.consumer.controller;


import com.consumer.dto.OperationEnum;
import com.consumer.entity.Product;
import com.consumer.exception.ResourceNotFoundException;
import com.consumer.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
class ProductController {

	@Autowired
	private ProductRepository repository;

	@GetMapping("/products")
	List<Product> getAll() {
		return repository.findAll();
	}

	@PostMapping("/products")
	ResponseEntity<Product> createProduct(@RequestBody Product newProduct) {
		return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(newProduct));
	}

	@GetMapping("/products/{id}")
	ResponseEntity<Product> getOne(@PathVariable Long id) {
		final Product product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
		return ResponseEntity.ok(product);
	}

	@PutMapping("/products/{id}")
	ResponseEntity<Product> updateProduct(@RequestBody Product updatedProduct, @PathVariable Long id) {
		final Product product = repository.findById(id).map(p -> {
			p.setName(updatedProduct.getName());
			p.setStock(updatedProduct.getStock());
			return repository.save(p);
		}).orElseThrow(() -> new ResourceNotFoundException());
		return ResponseEntity.ok(product);
	}

	@RequestMapping(value = "/patchproducts/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> partialUpdateGeneric(@RequestBody Map<String, String> stockUpdate,
			@PathVariable("id") Long id) {
		try {
			final Product product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
			final int stockQuantity = Integer.valueOf(stockUpdate.get("amount"));
			final OperationEnum operation = OperationEnum.valueOf(stockUpdate.get("operation"));
			if (operation == OperationEnum.ADD) {
				product.addStock(stockQuantity);
			} else {
				product.deductStock(stockQuantity);
			}
			return ResponseEntity.ok(repository.save(product));
		} catch (final IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("wrong operation");
		}
	}

	@DeleteMapping("/products/{id}")
	void deleteProduct(@PathVariable Long id) {
		repository.deleteById(id);
	}
}