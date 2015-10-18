package com.tappitz.tappitz.rest.model;


public class AnswerContactRequest {

    private String id;
    private boolean answer;

    public AnswerContactRequest(String id, boolean answer) {
        this.id = id;
        this.answer = answer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }
}
