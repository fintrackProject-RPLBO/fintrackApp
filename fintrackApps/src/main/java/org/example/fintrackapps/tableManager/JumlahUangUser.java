package org.example.fintrackapps.tableManager;

import org.example.fintrackapps.dataBaseManager.DBConnection;
import org.example.fintrackapps.dataBaseManager.Session;

import java.sql.SQLException;
import java.util.ArrayList;

public class JumlahUangUser {
    DBConnection db;
    private static JumlahUangUser instance;
    Session session = Session.getInstance();

    public static JumlahUangUser getInstance() throws SQLException {
        if (instance == null){
            instance = new JumlahUangUser();
        }
        return instance;
    }

    public JumlahUangUser() throws SQLException {
        this.db = new DBConnection("/fintrackDatabase.db");
    }

    public void addJumlahUangUser(Double amount) throws SQLException {
        Double addAmount = getJumlahUang()+amount;
        db.CUDQuery("UPDATE jumlahUangUser SET jumlahUang = ? WHERE username = ?", new String[] {addAmount.toString(),session.getUsername()}, "NUMERIC TEXT");
    }


    public void editJumlahUangUser(Double amount) throws SQLException {
        db.CUDQuery("UPDATE jumlahUangUser SET jumlahUang = ? WHERE username = ?", new String[] {amount.toString(),session.getUsername()}, "NUMERIC TEXT");
    }

    public Double getJumlahUang() throws SQLException {
        ArrayList<Object[]> jumlahUangUser = db.getDataQuery("SELECT * FROM jumlahUangUser WHERE username = ?", new String[] {session.getUsername()},"TEXT" );
        Double jumlahUang = Double.parseDouble(jumlahUangUser.get(0)[0].toString());
        return jumlahUang;
    }

    public void deleteJumlahUang() throws SQLException {
        db.CUDQuery("DELETE FROM jumlahUangUser WHERE username = ?", new String[] {session.getUsername()}, "TEXT");
    }

    public void updateUsername(String oldUsername,String newUsername) throws SQLException {
        db.CUDQuery("UPDATE jumlahUangUser SET username = ? WHERE username = ?", new String[] {newUsername,oldUsername}, "TEXT TEXT");

    }

}
