package com.tappitz.tappitz.rest.model;

/**
 * Created by sampaio on 16-10-2015.
 */
public class ContactSearchResult {
    private String name;
    private String eMail;
    private int id;
    private boolean isInvited;

    public ContactSearchResult(String name, String email, int id, boolean isInvited) {
        this.name = name;
        this.eMail = email;
        this.isInvited = isInvited;
        this.id = id;
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

    public boolean isInvited() {
        return isInvited;
    }

    public void setIsInvited(boolean isInvited) {
        this.isInvited = isInvited;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
