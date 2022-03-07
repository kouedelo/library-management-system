/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Timestamp;

/**
 *
 * @author Kouede Loic
 */
public class Borrow {
    private String isbn;
    private String ssn;
    private String staffID;
    private String borrowed_date;
    private String returned_date;
    private double fine;

    public Borrow() {
    }

    public Borrow(String isbn, String ssn, String staffID, String borrowed_date, String returned_date, double fine) {
        this.isbn = isbn;
        this.ssn = ssn;
        this.staffID = staffID;
        this.borrowed_date = borrowed_date;
        this.returned_date = returned_date;
        this.fine = fine;
    }

    public double getFine() {
        return fine;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
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

    public String getBorrowed_date() {
        return borrowed_date;
    }

    public void setBorrowed_date(String borrowed_date) {
        this.borrowed_date = borrowed_date;
    }

    public String getReturned_date() {
        return returned_date;
    }

    public void setReturned_date(String returned_date) {
        this.returned_date = returned_date;
    }

    @Override
    public String toString() {
        return "Borrow{" + "isbn=" + isbn + ", ssn=" + ssn + ", staffID=" + staffID + ", borrowed_date=" + borrowed_date + ", returned_date=" + returned_date + '}';
    }
    
}
