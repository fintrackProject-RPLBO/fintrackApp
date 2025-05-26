package org.example.fintrackapps.tableManager;

import org.example.fintrackapps.dataBaseManager.DBConnection;
import org.example.fintrackapps.dataBaseManager.Encryption;
import org.example.fintrackapps.dataBaseManager.Session;

import java.sql.SQLException;
import java.util.ArrayList;

public class UserData {
    DBConnection db;
    Encryption encrypt;
    JumlahUangUser jumlahUangUser = JumlahUangUser.getInstance();
    Session session = Session.getInstance();
    LogManager log = LogManager.getInstance();
    private ArrayList<Object[]> allData;

    private static UserData instance;
    private UserData() throws SQLException {
        this.db = new DBConnection("/fintrackDatabase.db");
        this.encrypt = new Encryption(10);
    }

    public static UserData getInstance() throws SQLException {
        if (instance == null){
            instance = new UserData();
        }
        return instance;
    }

    public ArrayList<Object[]> getUserData() throws SQLException {
        ArrayList<Object[]> result = db.RQuery("SELECT * FROM userData");
        return result;
    }

    public ArrayList<Object[]> getAllData() throws SQLException {
        allData = getUserData();
        return allData;
    }


    public boolean isExist(String username) throws SQLException {
        ArrayList<Object[]> usernameDatabase = db.RQuery("SELECT username FROM userData");

        for (Object[] i : usernameDatabase){
            if (i[0].toString().equals(username)){
                return true;
            }
        }
        return  false;
    }

    public int login(String username,String password) throws SQLException {
        String encryptedPassword = encrypt.encryption(password);

        if (isExist(username)){
            ArrayList<Object[]> data = db.getDataQuery("SELECT * FROM userData WHERE username = ? AND password = ?",new String[] {username,encryptedPassword},"TEXT TEXT");
            System.out.println(data.size());
            if (!data.isEmpty()){
                if(data.get(0)[0].toString().equals(username) && data.get(0)[1].toString().equals(encryptedPassword)){
                    System.out.println("login berhasil!");
                    return 0;
                }
            }
            else{
                System.out.println("password salah");
                return 1;
            }
        }


        System.out.println("akun tidak di temukan!");
        return 2;
    }

    public boolean register(String username,String password) throws SQLException {
        String encryptedPassword = encrypt.encryption(password);
        ArrayList<Object[]> data = db.getDataQuery("SELECT * FROM userData WHERE username = ? AND password = ?",new String[] {username,encryptedPassword},"TEXT TEXT");
        System.out.println(data.size());
        if (data.size() > 0){
            System.out.println("Username already registered please use other username");
        }else{
            db.CUDQuery("INSERT INTO userData VALUES (?,?)",new String[] {username, encryptedPassword}, "TEXT TEXT");
            db.CUDQuery("INSERT INTO jumlahUangUser VALUES(?,?)", new String[] {"0.0",username}, "NUMERIC TEXT");
//            JumlahUangUser.getInstance().addJumlahUangUser(0.0);
            ArrayList<Object[]> checkingData = db.getDataQuery("SELECT * FROM userData WHERE username = ? AND password = ?",new String[] {username,encryptedPassword},"TEXT TEXT");

            if (checkingData.size() > 0){
                return true;
            }
        }
        return  false;
    }

    public boolean deleteAccount(String username,String password) throws SQLException {
        String encryptedPassword = encrypt.encryption(password);
        if(isExist(username)){
            ArrayList<Object[]> data = db.getDataQuery("SELECT * FROM userData WHERE username = ? AND password = ?", new String[] {username,encryptedPassword},"TEXT TEXT");
            if(data.size() > 0){
                db.CUDQuery("DELETE FROM userData WHERE username = ? AND password = ?",new String[]{username,encryptedPassword},"TEXT TEXT");
                if (isExist(username) == false){
                    return true;
                }
            }
        }else{
            System.out.println("username tidak ditemukan");
        }

        return false;
    }

    public String getUserPassword(String user) throws SQLException {
        ArrayList<Object[]> data = getAllData();

        for (Object[] i : data){
            if(i[0].toString().toLowerCase().equals(user.toLowerCase())){
                return i[1].toString();
            }
        }

        return  "";
    }

    public void changePassword(String username, String oldPassword, String newPassword) throws SQLException {
        db.CUDQuery("UPDATE userData SET password = ? WHERE username = ? AND password = ?", new String[] {newPassword,username,oldPassword}, "TEXT TEXT TEXT");
    }
    public void changeUsername(String username, String password) throws SQLException {
        db.CUDQuery("UPDATE userData SET username = ? WHERE username = ? AND password = ?", new String[] {username, session.getUsername(),password}, "TEXT TEXT TEXT");
        jumlahUangUser.updateUsername(session.getUsername(), username);
        log.updateUsername(session.getUsername(),username);
        session.setUsername(username);
    }

}
