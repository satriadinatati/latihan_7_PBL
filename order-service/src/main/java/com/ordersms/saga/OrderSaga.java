package com.ordersms.saga;

import java.util.HashMap;
import java.util.Map;

import com.ordersms.entity.Order;
import com.ordersms.exception.NotEnoughCreditException;
import com.ordersms.exception.NotEnoughStockException;
import com.ordersms.exception.ResourceNotFoundException;
import com.ordersms.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ordersms.dto.CustomerDto;
import com.ordersms.dto.OperationEnum;
import com.ordersms.dto.ProductDto;

@Component
public class OrderSaga {

	@Autowired
	private OrderRepository orderRepository;

//	@Value(value = "${monolith.url}")
//	private String monolithBaseUri;

	@Value(value = "${cutomerservice.url}")
	private String customerServiceUri;

	@Value(value = "${productservice.url}")
	private String productServiceUri;

	@Autowired
	private RestTemplate restTemplate;

	public Order createOrderSaga(final Order newOrder) throws JsonProcessingException {
		ProductDto product;
		CustomerDto customer;
		try {
			customer = fetchCustomer(newOrder);
			product = fetchProduct(newOrder);
		} catch (Exception e) {
			throw new ResourceNotFoundException();
		}
		if (!product.hasEnoughStock(newOrder.getProductQuantity())) {
			throw new NotEnoughStockException();
		}
		if (!customer.hasEnoughCredit(newOrder.getTotalCost())) {
			throw new NotEnoughCreditException();
		}
		try {
			modifyProductStock(product.getId(), OperationEnum.DEDUCT.toString(), newOrder.getProductQuantity());
			modifyCustomerCredit(customer.getId(), OperationEnum.DEDUCT.toString(), newOrder.getTotalCost());
		} catch (final NotEnoughCreditException e) {
			/*
			 * here there is no transaction as before in the monolith, therefore can happen
			 * that after checking there is enough customer credit, when actually deducting
			 * the credit from the monolith, another order took place and there is no credit
			 * anymore. For such scenario, a NotEnoughCreditException will be thrown; the
			 * problem is we have to do the saga compensation for stock, as it was already
			 * deducted from monolith's database
			 */
			modifyProductStock(product.getId(), OperationEnum.ADD.toString(), newOrder.getProductQuantity());
			// of course after performing the compensation, rethrow the business exeption
			throw e;

		} catch (final RestClientException e) {
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Product/Customer service not available",
					e.getCause());
		}
		return orderRepository.save(newOrder);
	}

	private CustomerDto fetchCustomer(final Order newOrder) {
		return restTemplate.getForObject(customerServiceUri + "customers/" + newOrder.getCustomerId(), CustomerDto.class);
	}

	private ProductDto fetchProduct(final Order newOrder) {
		return restTemplate.getForObject(productServiceUri + "products/" + newOrder.getProductId(), ProductDto.class);
	}

	private void modifyCustomerCredit(final long customerId, final String operation, final double totalCost)
			throws RestClientException, JsonProcessingException {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		final Map<String, String> creditUpdate = new HashMap<>();
		creditUpdate.put("operation", operation);
		creditUpdate.put("amount", String.valueOf(totalCost));
		HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(creditUpdate, headers);

		restTemplate.exchange(customerServiceUri + "patchcustomers/" + customerId, HttpMethod.POST, requestEntity,
				CustomerDto.class);

	}

	private void modifyProductStock(final long productId, final String operation, final int productQuantity)
			throws RestClientException, JsonProcessingException {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		final Map<String, String> stockUpdate = new HashMap<>();
		stockUpdate.put("operation", operation);
		stockUpdate.put("amount", String.valueOf(productQuantity));
		HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(stockUpdate, headers);

		restTemplate.exchange(productServiceUri + "patchproducts/" + productId, HttpMethod.POST, requestEntity,
				ProductDto.class);
	}
}