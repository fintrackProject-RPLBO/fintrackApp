package org.example.fintrackapps.tableManager;

import org.example.fintrackapps.dataBaseManager.DBConnection;
import org.example.fintrackapps.dataBaseManager.Session;
import org.example.fintrackapps.uiController.MethodCollection;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

public class CatatanKeuanganTable {
    DBConnection db;
    Session session = Session.getInstance();
    CategoryTable categoryTable = CategoryTable.getInstance();
    JumlahUangUser jumlahUangUser = JumlahUangUser.getInstance();
    MethodCollection method = new MethodCollection();
//    LogManager log = LogManager.getInstance();

    private static CatatanKeuanganTable instance;
    public static CatatanKeuanganTable getInstance() throws SQLException {
        if (instance == null){
            instance = new CatatanKeuanganTable();
        }
        return instance;
    }

    public CatatanKeuanganTable() throws SQLException {
        this.db = new DBConnection("/fintrackDatabase.db");
    }

    public ArrayList<Object[]> getAllDataCatatan() throws SQLException {
        ArrayList<Object[]> temp = new ArrayList<>();
        ArrayList<Object[]> data = db.RQuery("SELECT * FROM catatanKeuangan");

        for (Object[] i : data){
            if(i[4].toString().equals(Session.getInstance().getUsername())){
                temp.add(i);
            }
        }
        return temp;
    }

    public ArrayList<Object[]> getDataCatatan(String dateData) throws SQLException {
        return db.getDataQuery("SELECT * FROM catatanKeuangan WHERE user = ? AND updateDate = ?", new String[] {session.getUsername(),dateData},"TEXT TEXT");
    }


    public boolean isExist(String category,Double price, String date,String description) throws SQLException {
        String user = session.getUsername();
        ArrayList<Object[]> catatanDatabase = db.RQuery("SELECT  * FROM catatanKeuangan");

        for (Object[] i : catatanDatabase){
            String kategori = i[0].toString();
            Double harga = (Double) i[1];
            String tanggal = i[2].toString().split(" ")[0];
            String deskripsi = i[3].toString();
            String username = i[4].toString();

            if (kategori.equals(category) && Objects.equals(harga, price) && tanggal.equals(date) && deskripsi.equals(description) && username.equals(user)){
                return true;
            }
        }
        return  false;
    }

    public boolean addCatatan(String category,Double price,String date, String description,String dateTime) throws SQLException {
        String user = session.getUsername();
        if (user == null){
            return false;
        }else{
            db.CUDQuery("INSERT INTO catatanKeuangan VALUES (?,?,?,?,?,?,?)",new String[] {category,price.toString(),date,description,user,dateTime,LocalDate.now().toString()}, "TEXT NUMERIC TEXT TEXT TEXT TEXT TEXT");
            db.CUDQuery("INSERT INTO catatanKeuanganLog VALUES (?,?,?,?,?,?,?)",new String[] {category,price.toString(),date,description,user,dateTime,LocalDate.now().toString()}, "TEXT NUMERIC TEXT TEXT TEXT TEXT TEXT");

            return true;
        }
    }

    public boolean editCatatan(String category,Double price,String date, String description,String dateTime) throws SQLException {
        String user = session.getUsername();
        if (user == null){
            return false;
        }else{
            String dataTime = session.getClickedDataCatatan()[5].toString();
            db.CUDQuery("UPDATE catatanKeuangan SET kategori = ?, harga = ?, tanggal = ?, deskripsi = ?, user = ?, updateDate = ? WHERE updateDate = ? AND user = ?",new String[] {category,price.toString(),date,description,user,dateTime,dataTime,user}, "TEXT NUMERIC TEXT TEXT TEXT TEXT TEXT TEXT");
            db.CUDQuery("UPDATE catatanKeuanganLog SET kategori = ?, harga = ?, tanggal = ?, deskripsi = ?, user = ?, updateDate = ? WHERE updateDate = ? AND user = ?",new String[] {category,price.toString(),date,description,user,dateTime,dataTime,user}, "TEXT NUMERIC TEXT TEXT TEXT TEXT TEXT TEXT");
            return true;
        }
    }

    public boolean deleteCatatan() throws SQLException {
        String user = session.getUsername();
        String dataTime = session.getClickedDataCatatan()[5].toString();
        return deleteCatatan(user,dataTime);
    }

    public boolean deleteCatatan(String username,String dataTime) throws SQLException {
        if (username == null){
            return false;
        }else{
            if (dataTime.contains("surplus")){
                jumlahUangUser.addJumlahUangUser(Double.parseDouble(getDataCatatan(dataTime).get(0)[1].toString())*-1.0);
            }
            if(dataTime.contains("limit") || (!dataTime.contains("limit") && !dataTime.contains("surplus"))){
                jumlahUangUser.addJumlahUangUser(Double.parseDouble(getDataCatatan(dataTime).get(0)[1].toString()));
            }
            if (dataTime != null || !dataTime.isEmpty()){
                db.CUDQuery("DELETE FROM catatanKeuangan WHERE user = ? AND updateDate = ?",new String[] {username, dataTime}, "TEXT TEXT");
                db.CUDQuery("DELETE FROM catatanKeuanganLog WHERE user = ? AND updateDate = ?",new String[] {username, dataTime}, "TEXT TEXT");

                return true;
            }
            else{
                return false;
            }
        }
    }


    public boolean clearCatatan() throws SQLException {
        String user = session.getUsername();
        db.CUDQuery("DELETE FROM catatanKeuangan WHERE user = ?", new String[] {user}, "TEXT ");
        return true;
    }

    public Double countingTotalSpend(String kategori, String date) throws SQLException {
        ArrayList<Object[]> data = getAllDataCatatan();
        double Counter = 0.0;
        String range = categoryTable.getRange(kategori);

        for (Object[] i : data){
            String[] dateSplit = i[2].toString().split("-");
            String[] dateSplitInput = date.split("-");
            if (i[0].toString().equals(kategori) && !i[5].toString().contains("surplus")){
                if (range.equals("Harian")){
                    if (i[2].toString().equals(date)){
                        Counter += Double.parseDouble(i[1].toString());
                    }
                }
                else if (range.equals("Mingguan")){
                    if (method.dayPassed(Arrays.toString(dateSplit),Arrays.toString(dateSplitInput)) <= 7){
                        if (i[2].toString().equals(date)){
                            Counter += Double.parseDouble(i[1].toString());
                        }
                    }
                }
                else if (range.equals("Bulanan")){
                    if (dateSplit[0].equals(dateSplitInput[0]) && dateSplit[1].equals(dateSplitInput[1])){
                        Counter += Double.parseDouble(i[1].toString());
                    }
                }
            }
        }

//        System.out.println(Counter+"<---");
        return Counter;
    }

    public Double countingTotalSpend(String kategori) throws SQLException {
        ArrayList<Object[]> data = getAllDataCatatan();
        double Counter = 0.0;
        for (Object[] i : data){
            if (i[0].toString().equals(kategori) && !i[5].toString().contains("surplus")){
                Counter += Double.parseDouble(i[1].toString());
            }
        }
//        System.out.println(Counter+"<---");
        return Counter;
    }

    public Double countingTotalSpendPerMonth(String kategori, String month) throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        String monthNow = LocalDate.now().getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH);

        ArrayList<Object[]> data = getAllDataCatatan();
        double Counter = 0.0;
        for (Object[] i : data){
            LocalDate localdate = LocalDate.parse(i[2].toString(), formatter);
            String localMonth = localdate.getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH);
            if (i[0].toString().equals(kategori) && localMonth.equals(month) && !i[5].toString().contains("surplus")){
                Counter += Double.parseDouble(i[1].toString());
            }
        }
//        System.out.println(Counter+"<---");
        return Counter;
    }

    public Double countingTotalSpend() throws SQLException {
        ArrayList<Object[]> data = getAllDataCatatan();
        double Counter = 0.0;
        for (Object[] i : data){
            if (!i[5].toString().contains("surplus")){
                Counter += Double.parseDouble(i[1].toString());
            }
        }
        return Counter;
    }

    public LocalDate getOldestDate() throws SQLException {
        ArrayList<Object[]> data = getAllDataCatatan();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate result = null;


        for (Object[] i: data){
            LocalDate date = LocalDate.parse(i[6].toString(), formatter);
            if (result == null){
                result = date;
            }
            else if (date.isBefore(result)){
                result = date;
            }
        }

        return result;
    }

    public LocalDate getNewestDate() throws SQLException {
        ArrayList<Object[]> data = getAllDataCatatan();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate result = null;


        for (Object[] i: data){
            LocalDate date = LocalDate.parse(i[6].toString(), formatter);
            if (result == null){
                result = date;
            }
            else if (date.isAfter(result)){
                result = date;
            }
        }

        return result;

    }

}
