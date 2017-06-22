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
public class ForgeinKeyModel {
    public void insertForgeinKey(String columnName,int tableId,int field_id){
        try {
            Connection connection= DBWebServiceConnection.getDBWebServiceConnection();
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO forgeinkey_table VALUES (default,'"+columnName+"',"+tableId+","+field_id+")",Statement.RETURN_GENERATED_KEYS);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ResultSet getForgeinKeyInfo(int tableId){
        try {
            Connection connection= DBWebServiceConnection.getDBWebServiceConnection();
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT  fi.name AS fieldName,t.name AS tableName, fo.column_name AS forgeinKeyName FROM forgeinkey_table fo JOIN fields_table fi ON fi.id = fo.field_id JOIN table_table t ON t.id = fi.table_id WHERE fo.table_id = " +tableId );
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
