package com.tappitz.tappitz.rest.model;

public class ErrorLogEntry {

    private final String error;
    private final String phoneData;

    public ErrorLogEntry(String error, String phoneData) {
        this.error = error;
        this.phoneData = phoneData;
    }

    public String getError() {
        return error;
    }

    public String getPhoneData() {
        return phoneData;
    }
}
