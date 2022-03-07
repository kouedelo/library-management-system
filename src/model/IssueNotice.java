/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Date;

/**
 *
 * @author Kouede Loic
 */
public class IssueNotice {
    private String ssn;
    private String staffID;
    private Date dateOfNotice;
    private String ISBN;

    public IssueNotice() {
    }

    public IssueNotice(String ssn, String staffID, Date dateOfNotice, String ISBN) {
        this.ssn = ssn;
        this.staffID = staffID;
        this.dateOfNotice = dateOfNotice;
        this.ISBN = ISBN;
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

    public Date getDateOfNotice() {
        return dateOfNotice;
    }

    public void setDateOfNotice(Date dateOfNotice) {
        this.dateOfNotice = dateOfNotice;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    @Override
    public String toString() {
        return "IssueNotice{" + "ssn=" + ssn + ", staffID=" + staffID + ", dateOfNotice=" + dateOfNotice + ", ISBN=" + ISBN + '}';
    }
    
}
