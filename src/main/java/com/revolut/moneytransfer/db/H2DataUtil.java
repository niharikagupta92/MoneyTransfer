package com.revolut.moneytransfer.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class H2DataUtil {

    private static final H2DataUtil db=new H2DataUtil();
    private H2DataUtil(){

    }

    public static H2DataUtil getInstance(){
        return db;
    }

    public ArrayList<HashMap> selectQuery(String query) {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            con = H2DataServer.getConnection();

            preparedStatement = con.prepareStatement(query);
            rs = preparedStatement.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            int columns = metaData.getColumnCount();

            ArrayList<HashMap> res = new ArrayList<>();
            while(rs.next()){
                HashMap<String, Object> row = new HashMap<>(columns);
                for(int i=1;i<=columns;i++)
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                res.add(row);
            }
            return res;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(preparedStatement!=null)
                    preparedStatement.close();
                if(con!=null)
                    con.close();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public int insertUpdateQuery(String query) {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        int rs = 0;
        try {
            con = H2DataServer.getConnection();

            preparedStatement = con.prepareStatement(query);
            rs = preparedStatement.executeUpdate();

            preparedStatement.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }
}
