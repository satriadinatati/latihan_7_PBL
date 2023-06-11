package com.ordersms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@NotNull
	private long customerId;

	@Positive
	private double totalCost;

	@NotNull
	private long productId;

	@Positive
	private int productQuantity;

	public Order() {
	}

	public Order(final long customerId, final double totalCost, final long productId, final int productQuantity) {
		this.customerId = customerId;
		this.totalCost = totalCost;
		this.productId = productId;
		this.productQuantity = productQuantity;
	}

	public long getId() {
		return id;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(final long customerId) {
		this.customerId = customerId;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(final double totalCost) {
		this.totalCost = totalCost;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(final long productId) {
		this.productId = productId;
	}

	public int getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(final int productQuantity) {
		this.productQuantity = productQuantity;
	}
}