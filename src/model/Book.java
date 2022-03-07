/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author user
 */
public class Book implements Serializable {

    private String isbn;
    private String subjectArea;
    private String title;
    private String author;
    private int volume;
    private String description;
    private int noOfCopies;
    private String bookType;
    private String reason;
    private int noOfCopiesOut; 

    public Book() {
    }

    public Book(String isbn, String subjectArea, String title, String author, int volume, String description, int noOfCopies, String bookType, String reason, int noOfCopiesOut) {
        this.isbn = isbn;
        this.subjectArea = subjectArea;
        this.title = title;
        this.author = author;
        this.volume = volume;
        this.description = description;
        this.noOfCopies = noOfCopies;
        this.bookType = bookType;
        this.reason = reason;
        this.noOfCopiesOut = noOfCopiesOut;
    }

    public int getNoOfCopiesOut() {
        return noOfCopiesOut;
    }

    public void setNoOfCopiesOut(int noOfCopiesOut) {
        this.noOfCopiesOut = noOfCopiesOut;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getSubjectArea() {
        return subjectArea;
    }

    public void setSubjectArea(String subjectArea) {
        this.subjectArea = subjectArea;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNoOfCopies() {
        return noOfCopies;
    }

    public void setNoOfCopies(int noOfCopies) {
        this.noOfCopies = noOfCopies;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public String getReason() {
        if (reason == null) {
            return "No Reason";
        }
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
