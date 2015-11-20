package com.tappitz.tappitz.rest.model;

/**
 * Created by sampaio on 16-10-2015.
 */
public class GenericContact {

    private String name;
    private String eMail;
    private int id;

    public GenericContact(String email, int id, String name) {
        this.eMail = email;
        this.id = id;
        this.name = name;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    {
//        "status": "true",
//            "data": [
//        {
//            "name":"Andre Gomes",
//                "email":"andre@gmail.com"
//        },
//        {
//            "name":"Beatriz Alves",
//                "email":"bea@gmail.com"
//        },
//        {
//            "name":"Bruno sampaio",
//                "email":"bruno@gmail.com"
//        },
//        {
//            "name":"Carla sampaio",
//                "email":"carla@gmail.com"
//        },
//        {
//            "name":"carlos Silves",
//                "email":"joao@gmail.com"
//        },
//        {
//            "name":"Joana Silves",
//                "email":"jojo@gmail.com"
//        },
//        {
//            "name":"Joao sampaio",
//                "email":"joao@gmail.com"
//        },
//        {
//            "name":"Helio Vito",
//                "email":"vito@gmail.com"
//        },
//        {
//            "name":"Luisa Sampaio",
//                "email":"lu@gmail.com"
//        },
//        {
//            "name":"Marta Gomes",
//                "email":"marta@gmail.com"
//        },
//        {
//            "name":"Vitoria Mangas",
//                "email":"vit@gmail.com"
//        },
//        {
//            "name":"Rui Vitória",
//                "email":"rui@gmail.com"
//        }
//        ]
//    }

}
