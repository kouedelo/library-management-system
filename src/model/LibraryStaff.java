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
public class LibraryStaff {
    private String fName;
    private String lNname;
    private Date birthDate;
    private String staffID;
    private String staffType;
    private String username;
    private String password;

    public LibraryStaff(String fName, String lNname, Date birthDate, String staffID, String staffType, String username, String password) {
        this.fName = fName;
        this.lNname = lNname;
        this.birthDate = birthDate;
        this.staffID = staffID;
        this.staffType = staffType;
        this.username = username;
        this.password = password;
    }

    public LibraryStaff() {
    }

    public String getFName() {
        return fName;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    public String getLNname() {
        return lNname;
    }

    public void setLNname(String lNname) {
        this.lNname = lNname;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getStaffType() {
        return staffType;
    }

    public void setStaffType(String staffType) {
        this.staffType = staffType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LibraryStaff{" + "fName=" + fName + ", lNname=" + lNname + ", birthDate=" + birthDate + ", staffID=" + staffID + ", staffType=" + staffType + ", username=" + username + ", password=" + password + '}';
    }
}
 