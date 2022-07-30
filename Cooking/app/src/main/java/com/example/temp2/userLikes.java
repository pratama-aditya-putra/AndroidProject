package com.example.temp2;

public class userLikes {
    String idUser;
    String idResep;
    //String status;

    public userLikes() {
    }

    public userLikes(String idUser, String idResep /*,String status*/) {
        this.idUser = idUser;
        this.idResep = idResep;
        //this.status = status;
    }
/*
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }*/

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdResep() {
        return idResep;
    }

    public void setIdResep(String idResep) {
        this.idResep = idResep;
    }
}
