package com.tappitz.app.model;

/**
 * Created by sampaio on 17-10-2015.
 */
public class ListViewContactItem {

    public final static int HASCONTACT = 0;
    public final static int HEADER = 1;
    private Contact contact;
    private int type;
    private String msgSeparator;


    public ListViewContactItem(Contact contact) {
        this.contact = contact;
        this.type = HASCONTACT;
    }

    public ListViewContactItem(String msgSeparator) {
        this.msgSeparator = msgSeparator;
        this.type = HEADER;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsgSeparator() {
        return msgSeparator;
    }

    public void setMsgSeparator(String msgSeparator) {
        this.msgSeparator = msgSeparator;
    }
}
