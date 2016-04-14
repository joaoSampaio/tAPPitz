package com.tappitz.app.rest.model;

/**
 * Created by sampaio on 16-10-2015.
 */
public class ContactSendId {
    int contactId;
    String operationType;

    public ContactSendId(int id, String operationType) {
        this.contactId = id;
        this.operationType = operationType;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public int getId() {
        return contactId;
    }

    public void setId(int id) {
        this.contactId = id;
    }
}
