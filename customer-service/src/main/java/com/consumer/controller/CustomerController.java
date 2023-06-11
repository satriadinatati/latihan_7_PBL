package com.consumer.controller;


import com.consumer.dto.OperationEnum;
import com.consumer.entity.Customer;
import com.consumer.entity.Notification;
import com.consumer.exception.ResourceNotFoundException;
import com.consumer.repository.CustomerRepository;
import com.consumer.restclient.NotificationsMicroserviceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
class CustomerController {

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private NotificationsMicroserviceClient notificationsMsClient;

    @GetMapping("/customers")
    List<Customer> getAll() {
        return repository.findAll();
    }

    @PostMapping("/customers")
    ResponseEntity<Customer> createCustomer(@RequestBody Customer newCustomer) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(newCustomer));
    }

    @GetMapping("/customers/{id}")
    ResponseEntity<Customer> getOne(@PathVariable Long id) {
        final Customer customer = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
        return ResponseEntity.ok(customer);
    }

    @PutMapping("/customers/{id}")
    ResponseEntity<Customer> updateCustomer(@RequestBody Customer updatedCustomer, @PathVariable Long id) {
        final Customer customer = repository.findById(id).map(c -> {
            c.setName(updatedCustomer.getName());
            c.setCredit(updatedCustomer.getCredit());
            return repository.save(c);
        }).orElseThrow(() -> new ResourceNotFoundException());
        return ResponseEntity.ok(customer);
    }

    @RequestMapping(value = "/patchcustomers/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> partialUpdateGeneric(@RequestBody Map<String, String> creditUpdate,
                                                  @PathVariable("id") Long id) {
        try {
            final Customer customer = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
            final double creditQuantity = Double.valueOf(creditUpdate.get("amount"));
            final OperationEnum operation = OperationEnum.valueOf(creditUpdate.get("operation"));
            if (operation == OperationEnum.ADD) {
                customer.addCredit(creditQuantity);
                /*
                 * notify customer some credit was added; in the real world we would pass an
                 * email, not the customer name, but that data was not included in the model
                 */
                String notifStatus = notificationsMsClient.sendNotification( new Notification(customer.getName(), creditQuantity));
                System.out.println(notifStatus);
//				notificationService.sendEmailNotification(customer);
            } else {
                customer.deductCredit(creditQuantity);
            }
            return ResponseEntity.ok(repository.save(customer));
        } catch (final IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("wrong operation");
        }
    }

    @DeleteMapping("/customers/{id}")
    void deleteCustomer(@PathVariable Long id) {
        repository.deleteById(id);
    }
}