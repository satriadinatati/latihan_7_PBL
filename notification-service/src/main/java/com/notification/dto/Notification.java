package com.notification.dto;


public class Notification {

    private String customerEmail;

    private double credit;

    public Notification() {
    }

    public Notification(String customerEmail, double credit) {
        this.customerEmail = customerEmail;
        this.credit = credit;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(final String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(final double credit) {
        this.credit = credit;
    }

}