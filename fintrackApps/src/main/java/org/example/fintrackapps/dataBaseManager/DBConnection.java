package org.example.fintrackapps.dataBaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class DBConnection {
    private String url;
    private Connection conn;

    public DBConnection(String path) throws SQLException {
        String basePath = System.getProperty("user.dir");
        String dbPath = basePath + path;
        this.url = "jdbc:sqlite:"+dbPath;
        this.conn = DriverManager.getConnection(this.url);
    }

    public Connection getConn() throws SQLException {
        try {
            if (conn == null || conn.isClosed()) {
                this.conn = DriverManager.getConnection(this.url);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public void closeConn() throws SQLException {
        getConn().close();
    }

    public void CUDQuery(String query,String[] values,String dataType) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(query);
        if (!query.isEmpty()){
            if (values.length > 0){
                String[] dataTypeArr = dataType.split(" ");
                for (int i = 0; i < values.length ; i++){
                    setData(dataTypeArr[i],values[i],i+1,pstmt);
                }
            }
            int affectedRow = pstmt.executeUpdate();
            System.out.println("Rows Affected: "+affectedRow);
        }else{
            System.out.println("Query kosong!");
        }
        pstmt.close();

    }

    public ArrayList<Object[]> RQuery(String query, boolean showResult) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        String[][] namaKolom = getNamaKolom(query);
        String[] col = namaKolom[0];
        String[] colType = namaKolom[1];

        ArrayList<Object[]> result = new ArrayList<>();

        while (rs.next()){
            Object[] temp = new Object[col.length];
            for (int i = 0; i < col.length; i++){
                Object data = getColumnData(colType[i],col[i],rs);
                if (showResult) {if ( i == 0) System.out.print("|");System.out.print(data + "|");}
                temp[i] = data;
            }
            result.add(temp);
        }

        stmt.close();
        rs.close();
        return result;
    }

    public ArrayList<Object[]> RQuery(String query) throws SQLException {
        ArrayList<Object[]> result = RQuery(query,false);
        return result;
    }

    // hanya boleh target 1 cell
    public ArrayList<Object[]> getDataQuery(String query, String[] values, String dataType) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(query);
        String[] type = dataType.split(" ");

        for (int i = 0; i < values.length; i++){
            setData(type[i],values[i],i+1,pstmt);
        }
        ResultSet rs1 = pstmt.executeQuery();

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        int columnCount = rs.getMetaData().getColumnCount(); // Get the number of columns
        Object[] result = new Object[columnCount];

        String[][] namaKolom = getNamaKolom(query);
        String[] col = namaKolom[0];
        String[] colType = namaKolom[1];

        ArrayList<Object[]> resultArr = new ArrayList<>();
        while (rs1.next()){
            for (int i = 0; i < col.length; i++){
                Object data = getColumnData(colType[i],col[i],rs1);
                result[i] = data;
                if ( i == 0) System.out.print("|");
                System.out.print(data+"|");
            }
            resultArr.add(result);
            System.out.println();
        }

        stmt.close();
        rs1.close();
        rs.close();
        return resultArr;
    }

    private void setData(String columnType, String data, int position, PreparedStatement pstmt) {
        try {
            if (Objects.equals(columnType, "TEXT")) {
                pstmt.setString(position, data);
            } else if (Objects.equals(columnType, "INTEGER")) {
                pstmt.setInt(position, Integer.parseInt(data));
            } else if (Objects.equals(columnType, "REAL") || Objects.equals(columnType, "FLOAT")) {
                pstmt.setFloat(position, Float.parseFloat(data));
            } else if (Objects.equals(columnType, "BLOB")) {
                pstmt.setByte(position, Byte.parseByte(data));
            } else if (Objects.equals(columnType, "BIGINT")) {
                pstmt.setLong(position, Long.parseLong(data));
            } else if (Objects.equals(columnType, "NUMERIC")) {
                pstmt.setDouble(position, Double.parseDouble(data));
            } else if (Objects.equals(columnType, "DATETIME") || Objects.equals(columnType, "DATE") || Objects.equals(columnType, "TIMESTAMP")) {
                Timestamp timestamp = Timestamp.valueOf(data);
                pstmt.setTimestamp(position, timestamp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid format for DATETIME. Expected format: yyyy-MM-dd HH:mm:ss");
        }
    }
    private static Object getColumnData(String columnType, String columnName, ResultSet rs) throws SQLException {
        try {
            // TEXT -> String
            if (Objects.equals(columnType, "TEXT")) {
                return rs.getString(columnName);
            }
            // INTEGER -> Integer
            else if (Objects.equals(columnType, "INTEGER")) {
                return rs.getInt(columnName);
            }
            // REAL or FLOAT -> Float or Double
            else if (Objects.equals(columnType, "REAL") || Objects.equals(columnType, "FLOAT")) {
                return rs.getFloat(columnName); // You can return Double if needed by using rs.getDouble(columnName)
            }
            // BLOB -> byte array (byte[])
            else if (Objects.equals(columnType, "BLOB")) {
                return rs.getBytes(columnName);
            }
            // BIGINT -> Long
            else if (Objects.equals(columnType, "BIGINT")) {
                return rs.getLong(columnName);
            } else if (Objects.equals(columnType, "DATETIME") || Objects.equals(columnType, "DATE") || Objects.equals(columnType, "TIMESTAMP")) {
                return rs.getDate(columnName);
            }
            // NUMERIC -> Double
            else if (Objects.equals(columnType, "NUMERIC")) {
                return rs.getDouble(columnName);
            }

        } catch (SQLException e){
            e.getErrorCode();
        }

        return null;
    }

    private String[][] getNamaKolom(String query) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        ResultSetMetaData metaData = rs.getMetaData(); // Get ResultSetMetaData to retrieve column information
        int columnCount = metaData.getColumnCount(); // Get the number of columns

        String[] col = new String[columnCount]; // membuat array dengan ukuran yang sama dengan jumlah kolom
        String[] colType = new String[columnCount]; // membuat array untuk menyimpan tipe data kolom

        // Loop through each column and add the column name to the list
        for (int i = 1; i <= columnCount; i++) {
            col[i-1] = metaData.getColumnName(i);
            colType[i-1] = metaData.getColumnTypeName(i);
        }
        stmt.close();
        rs.close();
        return new String[][] {col,colType};
    }

}

