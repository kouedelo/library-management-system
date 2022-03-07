/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Date;

/**
 *
 * @author user
 */
public class Member {

    private String ssn;
    private String Fname;
    private String Lname;
    private Date birthDate;
    private String phoneNumber;
    private String campusMailAddress;
    private String homeMailAddress;
    private Boolean isAProfessor;
    private String department;
    private int maxDays;

    public Member() {
    }

    public Member(String ssn, String Fname, String Lname, Date birthDate, String phoneNumber, String campusMailAddress, String homeMailAddress, Boolean isAProfessor, String department, int maxDays) {
        this.ssn = ssn;
        this.Fname = Fname;
        this.Lname = Lname;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.campusMailAddress = campusMailAddress;
        this.homeMailAddress = homeMailAddress;
        this.isAProfessor = isAProfessor;
        this.department = department;
        this.maxDays = maxDays;
    }

    public int getMaxDays() {
        return maxDays;
    }

    public void setMaxDays(int maxDays) {
        this.maxDays = maxDays;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getFname() {
        return Fname;
    }

    public void setFname(String Fname) {
        this.Fname = Fname;
    }

    public String getLname() {
        return Lname;
    }

    public void setLname(String Lname) {
        this.Lname = Lname;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCampusMailAddress() {
        return campusMailAddress;
    }

    public void setCampusMailAddress(String campusMailAddress) {
        this.campusMailAddress = campusMailAddress;
    }

    public String getHomeMailAddress() {
        return homeMailAddress;
    }

    public void setHomeMailAddress(String homeMailAddress) {
        this.homeMailAddress = homeMailAddress;
    }

    public Boolean getIsAProfessor() {
        return isAProfessor;
    }

    public void setIsAProfessor(Boolean isAProfessor) {
        this.isAProfessor = isAProfessor;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Member{" + "ssn=" + ssn + ", Fname=" + Fname + ", Lname=" + Lname + ", birthDate=" + birthDate + ", phoneNumber=" + phoneNumber + ", campusMailAddress=" + campusMailAddress + ", homeMailAddress=" + homeMailAddress + ", isAProfessor=" + isAProfessor + ", department=" + department + '}';
    }
   
   
}
