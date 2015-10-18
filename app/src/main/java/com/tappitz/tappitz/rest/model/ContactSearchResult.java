package com.tappitz.tappitz.rest.model;

/**
 * Created by sampaio on 16-10-2015.
 */
public class ContactSearchResult {
    private String name;
    private String email;
    private boolean isInvited;

    public ContactSearchResult(String name, String email, boolean isInvited) {
        this.name = name;
        this.email = email;
        this.isInvited = isInvited;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isInvited() {
        return isInvited;
    }

    public void setIsInvited(boolean isInvited) {
        this.isInvited = isInvited;
    }
}
