package com.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.notification.dto.Notification;

import java.util.concurrent.TimeUnit;

@Component
public class NotificationService {

	Logger logger = LoggerFactory.getLogger(NotificationService.class);

	public void sendEmailNotification(final Notification notification) {
		// here there would be some implementation to send an email with credit change
		// details
		try {
			TimeUnit.SECONDS.sleep(1);
			logger.info("Sending email to notify customer {}, new account balance is {}", notification.getCustomerEmail(),
					notification.getCredit());
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}