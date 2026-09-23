package com.example.apptuhorasalud.domain.models;

public class User {

    private Integer id;
    private int document;
    private String name;
    private String lastname;
    private String birthDate;
    private String email;
    private String password;


    public User(Integer id, int document, String name, String lastname, String birthDate, String email, String password) {
        this.id = id;
        this.document = document;
        this.name = name;
        this.lastname = lastname;
        this.birthDate = birthDate;
        this.email = email;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getDocument() {
        return document;
    }

    public void setDocument(int document) {
        this.document = document;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
