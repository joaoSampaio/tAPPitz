package com.tappitz.tappitz.model;


public class Contact {

    private String name;
    private String email;
    private int id;
//    private String phone;
    private boolean isFriend;
    private boolean isInvited;

    private boolean isInviteRequest;

    public Contact(String name, String email, int id, boolean isFriend) {
        this.name = name;
        this.email = email;
//        this.phone = phone;
        this.isFriend = isFriend;
        this.id = id;
    }

    public Contact(String name, String email, int id, boolean isFriend, boolean isInvited) {
        this.name = name;
        this.email = email;
        this.isFriend = isFriend;
        this.isInvited = isInvited;
        this.id = id;
    }

    public Contact(String name, String email, int id) {
        this.name = name;
        this.email = email;
        this.id = id;
    }

    public boolean isInviteRequest() {
        return isInviteRequest;
    }

    public void setIsInviteRequest(boolean isInviteRequest) {
        this.isInviteRequest = isInviteRequest;
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

//    public String getPhone() {
//        return phone;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setIsFriend(boolean isFriend) {
        this.isFriend = isFriend;
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
