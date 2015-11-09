package com.tappitz.tappitz.rest.model;

public class PhotoInbox {

    private String url;
    private int id;
    private String text;
    private String senderName;
    private String date;
    private boolean hasVoted;
    private String myComment;
    private int choice;


    public PhotoInbox(String url, int id, String text,String date, boolean hasVoted,  String myComment, String senderName, int choice) {
        this.date = date;
        this.hasVoted = hasVoted;
        this.id = id;
        this.myComment = myComment;
        this.senderName = senderName;
        this.text = text;
        this.url = url;
        this.choice = choice;
    }

    public String getDate() {
        return date;
    }

    public boolean isHasVoted() {
        return hasVoted;
    }

    public String getMyComment() {
        return myComment;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getUrl() {
        return url;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getChoice() {
        return choice;
    }


//    [
//    {
//        "url": "https://dl.dropboxusercontent.com/u/68830630/tAppitz/t1.jpg",
//            "id": "11",
//            "text": "Que imagem linda, concordão?"
//    },
//    {
//        "url": "https://dl.dropboxusercontent.com/u/68830630/tAppitz/t2.jpg",
//            "id": "12",
//            "text": "texto ramdom , bla bla ..."
//    },
//    {
//        "url": "https://dl.dropboxusercontent.com/u/68830630/tAppitz/t3.jpg",
//            "id": "13",
//            "text": "Alguem conhce este livro? Devo ler isto? Penso que parece bom. Se alguem souber que comunique"
//    },
//    {
//        "url": "https://dl.dropboxusercontent.com/u/68830630/tAppitz/t4.jpg",
//            "id": "14",
//            "text": "Curtes"
//    },
//    {
//        "url": "https://dl.dropboxusercontent.com/u/68830630/tAppitz/t5.jpg",
//            "id": "15",
//            "text": "Que imagem linda, concordão?"
//    }
//    ]

}
