package com.tappitz.app.model;


import java.util.ArrayList;
import java.util.List;

public class Contact {

    private String name;
    private String username;
    private String email;
    private int id;
    private boolean isFriend;
    private boolean isFollower;
    private boolean amIFollowing;
    private int color;

    private boolean isInviteRequest;

    public Contact(String name, String username, String email, int id) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.id = id;
    }

    public Contact(String name, String username, String email, int id, boolean isFriend) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.id = id;
        this.isFriend = isFriend;
    }

    public Contact(String name, String email, int id, boolean isFriend) {
        this.name = name;
        this.email = email;
//        this.phone = phone;
        this.isFriend = isFriend;
        this.id = id;
        this.username = email;
    }

    public Contact(String name, String email, int id, boolean isFriend, boolean isFollower) {
        this.name = name;
        this.email = email;
        this.isFriend = isFriend;
        this.isFollower = isFollower;
        this.id = id;
        this.username = email;
    }

    public Contact(String name, String email, int id) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.username = email;
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

    public boolean isFriend() {
        return isFriend;
    }

    public void setIsFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }

    public boolean isFollower() {
        return isFollower;
    }

    public void setIsFollower(boolean isInvited) {
        this.isFollower = isInvited;
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

    public boolean isAmIFollowing() {
        return amIFollowing;
    }

    public void setAmIFollowing(boolean amIFollowing) {
        this.amIFollowing = amIFollowing;
    }

    public String getLetters(){
        String letter = "";
        String[] names = getName().split(" ");
        for (String name: names) {
            if(name.length()> 1)
                letter+= name.substring(0,1);
            else if(name.length() == 1)
                letter+= name;
        }
        return letter.toUpperCase();
    }

    public static void removeContact(List<Contact> contacts, int id){
        Contact contact = null;
        for (Contact c: contacts) {
            if(c.getId() == id){
                contact = c;
            }
        }
        if (contact != null)
            contacts.remove(contact);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public static boolean isContactInList(List<Contact> contacts, String email){
        for (Contact c : contacts){
            if(c.getEmail().equals(email))
                return true;
        }
        return false;
    }

    public static void removeDuplicates(List<Contact> contacts){

        List<Contact> contactsNoDuplicates = new ArrayList<>();

        for (Contact c: contacts){
            if(!isContactInList(contactsNoDuplicates, c.getEmail())){
                contactsNoDuplicates.add(c);
            }
        }
        contacts.clear();
        contacts.addAll(contactsNoDuplicates);

    }


}
