package org.example.fintrackapps.tableManager;

import org.example.fintrackapps.dataBaseManager.DBConnection;
import org.example.fintrackapps.dataBaseManager.Session;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryTable {
    DBConnection db;
    private static CategoryTable instance;
    Session session = Session.getInstance();

    public static CategoryTable getInstance() throws SQLException {
        if (instance == null){
            instance = new CategoryTable();
        }
        return instance;
    }

    public CategoryTable() throws SQLException {
        this.db = new DBConnection("/fintrackDatabase.db");
    }

    public ArrayList<Object[]> getAllDataKategori() throws SQLException {
        ArrayList<Object[]> temp = new ArrayList<>();
        ArrayList<Object[]> data = db.RQuery("SELECT * FROM kategori");

        for (Object[] i : data){
            if(i[2].toString().equals(Session.getInstance().getUsername())){
                temp.add(i);
            }
        }
        return temp;
    }

    public boolean isExist(String kategori) throws SQLException {
        ArrayList<Object[]> data = getAllDataKategori();
        for (Object[] i : data){
            if (i[0].toString().toLowerCase().strip().equals(kategori.toLowerCase().strip())){
                return true;
            }
        }
        return false;
    }

    public Boolean addKategori(Double limit, String namaKategori,String color,String range) throws SQLException {
        String user = session.getUsername();
        if (user == null){
            return false;
        }else{
            if (isExist(namaKategori)){
                return false;
            }
            else{
                addKategori(limit,namaKategori,user,color,range);
                return true;
            }
        }
    }

    public Boolean addKategori(Double limit, String namaKategori,String username,String color, String range) throws SQLException {
        db.CUDQuery("INSERT INTO kategori VALUES (?,?,?,?,?)",new String[] {namaKategori,limit.toString(),username,color,range}, "TEXT NUMERIC TEXT TEXT TEXT");
        return true;
    }

    public boolean editKategori(String category,Double limit,String color,String range) throws SQLException {
        String user = session.getUsername();
        if (user == null){
            return false;
        }else{
            String kategori = session.getClickedDataKategori()[0].toString();
            db.CUDQuery("UPDATE kategori SET category = ?, priceLimit = ?,color = ?,range = ? WHERE user = ? AND category = ?",new String[] {kategori,limit.toString(),color,range,session.getUsername(),kategori}, "TEXT NUMERIC TEXT TEXT TEXT TEXT");
            return true;
        }
    }

    public String getRange(String category) throws SQLException {
        ArrayList<Object[]> data = getAllDataKategori();
        for (Object[] i : data){
            if (i[0].toString().equals(category)){
                return  i[4].toString();
            }
        }
        return null;
    }

    public boolean deleteKategori() throws SQLException {
        String user = session.getUsername();
        if (user == null){
            return false;
        }else{
            String kategori = session.getClickedDataKategori()[0].toString();
            if (kategori != null || !kategori.isEmpty()){
                db.CUDQuery("DELETE FROM kategori WHERE category = ?",new String[] {kategori}, "TEXT");
                return true;
            }
            else{
                return false;
            }
        }
    }

    public boolean clearKategori() throws SQLException {
        String user = session.getUsername();
//        if (user == null){return false;}
//        else{
        db.CUDQuery("DELETE FROM kategori WHERE user = ?", new String[] {user}, "TEXT");
        return true;
//        }

    }

    public Double getLimitKategori(String kategori) throws SQLException {
        ArrayList<Object[]> data = getAllDataKategori();
        for (Object[] i : data){
            if (i[0].toString().equals(kategori)){
                return Double.parseDouble(i[1].toString());
            }
        }
        return -1.0;
    }

    public Integer getKategoriLength() throws SQLException {
        return getAllDataKategori().size();
    }

    public ArrayList<String> getAllColor() throws SQLException {
        ArrayList<Object[]> data = getAllDataKategori();
        ArrayList<String> result = new ArrayList<>();


        for (int i = 0; i < data.size(); i++){
            result.add(data.get(i)[3].toString());
        }

        return result;
    }

    public String getColor(String target) throws SQLException {
        ArrayList<Object[]> data = getAllDataKategori();

        for (int i = 0; i < data.size(); i++){
            if (data.get(i)[0].toString().equals(target)){
                return data.get(i)[3].toString();
            }
        }
        return null;
    }

}
