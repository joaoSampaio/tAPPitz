package com.tappitz.tappitz.rest.model;

import com.tappitz.tappitz.Global;

public class PhotoOutbox {

//    private String url;
    private int id;
    private String text;

    public PhotoOutbox( int id, String text){
        this.id = id;
        this.text = text;
    }





    public String getUrl() {
        return Global.ENDPOINT + "/pictures/+id";
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
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
