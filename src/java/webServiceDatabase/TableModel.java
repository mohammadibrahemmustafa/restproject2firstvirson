/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webServiceDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Bcc
 */
public class TableModel {
    public int insertTable(String tableName,int dbID){
        try {
            Connection connection= DBWebServiceConnection.getDBWebServiceConnection();
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO table_table VALUES (default,'"+tableName+"',"+dbID+")",Statement.RETURN_GENERATED_KEYS);
            stmt.executeUpdate();
            ResultSet rs=stmt.getGeneratedKeys();
            rs.next();
            int tableId=rs.getInt(1);
            return tableId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    public  int getTableId(String tableName){
        try {
            Connection connection= DBWebServiceConnection.getDBWebServiceConnection();
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT id FROM table_table WHERE name = '" +tableName+"'" );
            if (rs!=null) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public ResultSet getTableIdAndName(int dbId){
        try {
            Connection connection= DBWebServiceConnection.getDBWebServiceConnection();
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT id , name FROM table_table WHERE database_id = " +dbId );
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
