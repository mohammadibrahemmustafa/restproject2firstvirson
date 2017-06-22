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
public class FieldsModel {
    public void insertField(String fieldName,String typeName,int tableId){
        try {
            int typeId=TypeClass.getTypeId(typeName);
            if (typeId==-1) {
                throw new Exception("no type with this name");
            }
            Connection connection= DBWebServiceConnection.getDBWebServiceConnection();
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO fields_table VALUES (default,'"+fieldName+"',"+typeId+","+tableId+")",Statement.RETURN_GENERATED_KEYS);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int getFieldId(int dbId,String tableName,String fieldName){
        try {
            Connection connection= DBWebServiceConnection.getDBWebServiceConnection();
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT f.id FROM fields_table f JOIN table_table t ON t.id=f.table_id WHERE t.name='"+tableName+"' AND f.name ='"+fieldName+"' AND t.database_id ="+dbId);
            if (rs!=null) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }public ResultSet getFieldInfo(int tableId){
        try {
            Connection connection= DBWebServiceConnection.getDBWebServiceConnection();
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT f.name AS fieldName , t.name AS typeName FROM fields_table f JOIN type_table t ON t.id = f.type_id WHERE table_id="+tableId);
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private static class TypeClass{
        public static int getTypeId(String typeName){
            try {
                Connection connection= DBWebServiceConnection.getDBWebServiceConnection();
                Statement s = connection.createStatement();
                ResultSet rs = s.executeQuery("SELECT id FROM type_table WHERE name = '" +typeName+"'" );
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
}
