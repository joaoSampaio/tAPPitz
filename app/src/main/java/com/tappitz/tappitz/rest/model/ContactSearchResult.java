package com.tappitz.tappitz.rest.model;

/**
 * Created by sampaio on 16-10-2015.
 */
public class ContactSearchResult {
    private String name;
    private String eMail;
    private int id;
    private String username;
    private boolean isInvited = false;

    public ContactSearchResult(String name, String email, int id, String username) {
        this.name = name;
        this.eMail = email;
//        this.isFollower = isFollower;
        this.id = id;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
