package com.tappitz.tappitz.rest.model;

/**
 * Created by sampaio on 16-10-2015.
 */
public class GenericContact {

    private String name;
    private String eMail;
    private int id;
    private String username;

    public GenericContact(String email, int id, String name, String username) {
        this.eMail = email;
        this.id = id;
        this.name = name;
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return eMail;
    }

    public void setEmail(String email) {
        this.eMail = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
