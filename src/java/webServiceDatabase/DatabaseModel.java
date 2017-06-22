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
public class DatabaseModel {
    public int insertDatabase(String dbName,String dbPassword,int dbMasterID){
        try {
            Connection connection= DBWebServiceConnection.getDBWebServiceConnection();
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO database_table VALUES (default,'"+dbName+"','"+dbPassword+"',"+dbMasterID+")",Statement.RETURN_GENERATED_KEYS);
            stmt.executeUpdate();
            ResultSet rs=stmt.getGeneratedKeys();
            rs.next();
            int dbId=rs.getInt(1);
            return dbId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    public int getDBId(String dbName){
        try {
            Connection connection= DBWebServiceConnection.getDBWebServiceConnection();
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT id FROM database_table WHERE name = '" +dbName+"'" );
            if (rs!=null) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
