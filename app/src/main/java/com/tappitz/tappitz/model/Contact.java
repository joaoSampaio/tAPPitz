package com.tappitz.tappitz.model;


public class Contact {

    private String name;
    private String email;
//    private String phone;
    private boolean isFriend;
    private boolean isInvited;

    private boolean isInviteRequest;

    public Contact(String name, String email, boolean isFriend) {
        this.name = name;
        this.email = email;
//        this.phone = phone;
        this.isFriend = isFriend;
    }

    public Contact(String name, String email, boolean isFriend, boolean isInvited) {
        this.name = name;
        this.email = email;
        this.isFriend = isFriend;
        this.isInvited = isInvited;
    }

    public Contact(String name, String email) {
        this.name = name;
        this.email = email;
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
}
