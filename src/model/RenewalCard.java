/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Date;

/**
 *
 * @author Kouede Loic
 */
public class RenewalCard {
    private String ssn;
    private String staffID;
    private String cardNumber;
    private Date renewalDate;
    private Date expiryDate;

    public RenewalCard() {
    }

    public RenewalCard(String ssn, String staffID, String cardNumber, Date renewalDate, Date expiryDate) {
        this.ssn = ssn;
        this.staffID = staffID;
        this.cardNumber = cardNumber;
        this.renewalDate = renewalDate;
        this.expiryDate = expiryDate;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Date getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(Date renewalDate) {
        this.renewalDate = renewalDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return "RenewalCard{" + "ssn=" + ssn + ", staffID=" + staffID + ", cardNumber=" + cardNumber + ", renewalDate=" + renewalDate + '}';
    }
}
