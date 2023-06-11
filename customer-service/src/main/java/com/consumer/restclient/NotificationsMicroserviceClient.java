package com.consumer.restclient;


import com.consumer.entity.Notification;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationsMicroserviceClient {

	@Value(value = "${notificationservice.url}")
	private String notificationsMsBaseUri;

	@Autowired
	private RestTemplate restTemplate;

	private static final String SERVICE_NAME = "customer_service";

//	https://www.baeldung.com/rest-template
	@CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "getDefaultNotificationMessage")
	public String sendNotification(Notification notification) {
		HttpEntity<Notification> requestEntity = new HttpEntity<>(notification, getJsonHeaders());
		String message = restTemplate.postForObject(notificationsMsBaseUri + "notifications", requestEntity, String.class);
		return message;
//		restTemplate.exchange(notificationsMsBaseUri + "notifications/", HttpMethod.POST, requestEntity, String.class);
	}

	private HttpHeaders getJsonHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

//	It's important to remember that a fallback method should be placed in the same class
//	and must have the same method signature with just ONE extra target exception parameter.
	public String getDefaultNotificationMessage(Notification notification, Exception e) {
		return "Notifications not created successfully!";
	}
}
