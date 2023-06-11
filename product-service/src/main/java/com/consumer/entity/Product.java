package com.consumer.entity;

import com.consumer.exception.NotEnoughStockException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Entity(name = "products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Size(min = 3, max = 20)
	private String name;

	@PositiveOrZero
	private int stock;

	public Product() {
	}

	public Product(final String name, final int stock) {
		this.name = name;
		this.stock = stock;
	}

	public void addStock(final int quantity) {
		this.stock += quantity;
	}

	public void deductStock(final int quantity) {
		if (quantity > this.stock) {
			throw new NotEnoughStockException();
		}
		this.stock -= quantity;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(final int stock) {
		this.stock = stock;
	}
}