package org.example.fintrackapps.dataBaseManager;

import java.util.ArrayList;

public class Session {
    String username;
    Object[] clickedDataCatatan;
    Object[] clickedDataKategori;
    ArrayList<Object[]> logSession;
    private static Session instance;

    // Private constructor prevents instantiation from other classes
    private Session() {}

    // Global access point
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void unsetUsername(){this.username = null;}

    public void setClickedDataCatatan(Object[] clickedData){this.clickedDataCatatan = clickedData;}
    public void unsetClickedDataCatatan(){this.clickedDataCatatan = null;}
    public Object[] getClickedDataCatatan(){return  this.clickedDataCatatan;}

    public void setClickedDataKategori(Object[] clickedDataKategori) {
        this.clickedDataKategori = clickedDataKategori;
    }
    public void unsetClickedDataKategori() {
        this.clickedDataKategori = null;
    }
    public Object[] getClickedDataKategori() {
        if (clickedDataKategori[1].toString().equals("-")){
            clickedDataKategori[1] = -1.0;
        }
        return clickedDataKategori;
    }

    public void setLogSession(ArrayList<Object[]> logSession){this.logSession = logSession;}
    public void unsetLogSession(){this.logSession = null;}
    public ArrayList<Object[]> getLogSession(){return  this.logSession;}


    public void clearSession(){
        unsetClickedDataKategori();
        unsetClickedDataCatatan();
        unsetUsername();
    }

}
