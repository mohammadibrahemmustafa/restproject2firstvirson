/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.web.sync;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import databaseConnector.*;

/**
 *
 * @author Bcc
 */
public class TestSyncProjectConnectMysqlAndSql {

    
    public static void sync(int tableId) throws SQLException{
        //get table name by his id
//        SqlLocalConnection s=new SqlLocalConnection();
//        String tableName = s.getTableNameByTableId(tableId);
        
        //get all for
//        Statement stmt1=s.con.createStatement();
//        
//        ResultSet rs1=stmt1.executeQuery("select TOP 1 row_id from inserted_element_table where table_id = "+tableId);
//        ResultSet rs2=null;
//        while (rs1.next()) {
//             
//            rs2= s.con.createStatement().executeQuery("Select name FROM "+tableName+" WHERE id >= "+rs1.getInt(1));
//        }
        
        //send the data to server db
        
//        MySqlConnection mySqlConnection=new MySqlConnection();
//        while (rs2.next()) {
//            mySqlConnection.doInsertForSync(tableName, rs2.getString(1));
//        }
        
  //      s.deleteAllRowsFromInsertedElementTable();
       
        /******************************************* Update section ***************************************/

     /*   SqlLocalConnection s=new SqlLocalConnection();
        String tableName = s.getTableNameByTableId(tableId);
        Statement stmt1=s.con.createStatement();
        
        ResultSet rs1=stmt1.executeQuery("SELECT id , name FROM updated_element_table uet "
                + " JOIN test_table t ON t.id = uet.row_id "
                + "WHERE table_id = "+tableId);
        MySqlConnection mysql =new MySqlConnection();
        while (rs1.next()) {
            mysql.doUpdate(tableName, rs1.getString(2), rs1.getInt(1));
        }
        s.deleteAllRowsFromUpdatedElementTable();
        /******************************************* Delete section ***************************************/
 
        
//        //get table name by his id
//    
//        SqlLocalConnection s=new SqlLocalConnection();
//        String tableName = s.getTableNameByTableId(tableId);
//        
//        //get all for sync
//        Statement stmt1=s.con.createStatement();
//        
//        ResultSet rs1=stmt1.executeQuery("SELECT row_id FROM deleted_element_table where table_id = "+tableId);
//        
//        //send id for server to delete the elements
//        MySqlConnection mySqlConnection=new MySqlConnection();
//        while (rs1.next()) {
//            mySqlConnection.doDeleteForSync(tableName, rs1.getInt(1));
//        }
//        s.deleteAllRowsFromDeletedElementTable();
    }
    public static void main(String[] args) throws SQLException {
        
        //s.doInsert("test_table", "manar");
        //s.getTableNameByTableId(245575913);
        //sync(245575913);
        //s.doDelete("test_table", 4);
        //s.doUpdate("test_table", "saeed", 5);
        
        
    }
}
