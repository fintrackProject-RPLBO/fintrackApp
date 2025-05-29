package org.example.fintrackapps.tableManager;

import org.example.fintrackapps.dataBaseManager.DBConnection;
import org.example.fintrackapps.dataBaseManager.Session;
import org.example.fintrackapps.uiController.MethodCollection;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class LogManager {
    DBConnection db;
    Session session = Session.getInstance();
    JumlahUangUser jumlahUangUser = JumlahUangUser.getInstance();
    CatatanKeuanganTable catatanKeuanganTable = CatatanKeuanganTable.getInstance();
    MethodCollection method = new MethodCollection();

    private static LogManager instance;

    public LogManager() throws SQLException {
        this.db = new DBConnection("/fintrackDatabase.db");
    }

    public static LogManager getInstance() throws SQLException {
        if (instance == null){
            instance = new LogManager();
        }
        return instance;
    }

    public void loggingExpiredData() throws SQLException {
        ArrayList<Object[]> data = catatanKeuanganTable.getAllDataCatatan();

        if (jumlahUangUser.getAutoReset().equals("TRUE")){
            for (Object[] i : data){
                String username = i[4].toString();
                String lastUpdated = i[5].toString();
                String makeDate = i[6].toString();
                LocalDate dataDate = method.strToLocalDate(makeDate);

                long days = ChronoUnit.DAYS.between(dataDate,LocalDate.now());

                if (days > 30){
                    catatanKeuanganTable.deleteCatatan(username,lastUpdated);
                }
            }
        }
    }

    public ArrayList<Object[]> getAllDataCatatan() throws SQLException {
        ArrayList<Object[]> temp = new ArrayList<>();
        ArrayList<Object[]> data = db.RQuery("SELECT * FROM catatanKeuanganLog");

        for (Object[] i : data){
            if(i[4].toString().equals(Session.getInstance().getUsername())){
                temp.add(i);
            }
        }
        return temp;
    }


    public void updateUsername(String newUsername, String oldUsername) throws SQLException {
        db.CUDQuery("UPDATE catatanKeuanganLog SET user = ? WHERE user = ?", new String[] {newUsername,oldUsername}, "TEXT TEXT");
    }

    public Double countingTotalSpend(String kategori) throws SQLException {
        ArrayList<Object[]> data = getAllDataCatatan();
        double Counter = 0.0;
        for (Object[] i : data){
            if (i[0].toString().equals(kategori) && !i[5].toString().contains("surplus")){
                Counter += Double.parseDouble(i[1].toString());
            }
        }

        return Counter;
    }

    public Double countingTotalSpend(String kategori, String MonthYear) throws SQLException {
        ArrayList<Object[]> data = getAllDataCatatan();
        double Counter = 0.0;
        for (Object[] i : data){
            if (i[0].toString().equals(kategori) && !i[5].toString().contains("surplus") && method.getMonthAndYear(i[2].toString()).equals(MonthYear)){
                Counter += Double.parseDouble(i[1].toString());
            }
        }

        return Counter;
    }
}
