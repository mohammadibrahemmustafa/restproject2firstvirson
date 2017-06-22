/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseConnector;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Config;


/**
 *
 * @author Bcc
 */
public class MySqlConnection extends DbConnection {

    public MySqlConnection() {
        try {
            MysqlDataSource mysql = new MysqlDataSource();
            mysql.setURL("jdbc:mysql://localhost:3306/"+Config.DATABASE_NAME);
            mysql.setUser("root");
            mysql.setPassword("");
            
            con = mysql.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //*************************** new method**************************
    // get the result set contain inserted element greater than last sync date
    public ResultSet getInsertedElementsByLastSyncDate(String tableName,Date date){
        return getElementsDependOnSyncStateAndLastSyncDate(tableName, enuRecordState.INSERTED, date);
    }
    // get the result set contain deleted element greater than last sync date
    public ResultSet getDeletedElementsByLastSyncDate(String tableName,Date date){
        return getElementsDependOnSyncStateAndLastSyncDate(tableName, enuRecordState.DELETED, date);
    }
    // get the result set contain updated element greater than last sync date
    public ResultSet getUpdatedElementsByLastSyncDate(String tableName,Date date){
        return getElementsDependOnSyncStateAndLastSyncDate(tableName, enuRecordState.UPDATED, date);
    }
    //get element depend on state and greater than last sync date
    private ResultSet getElementsDependOnSyncStateAndLastSyncDate(String tableName,enuRecordState state,Date date){
        try {
            Statement s = con.createStatement();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
            System.out.println("SELECT * FROM "+tableName+" WHERE syncState = "+state.ordinal()+" AND transactionDate >= '"+simpleDateFormat+"'");
            ResultSet rs = s.executeQuery("SELECT * FROM "+tableName+" WHERE syncState = "+state.ordinal()+" AND transactionDate >= '"+simpleDateFormat.format(date)+"'");
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
        
    }
    
    // i use this method to insert all non sync data 
    public ResultSet insertRecordValues(String tableName,String values){
        try {
            return insertValuesToTable(tableName, values);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // we only change the sync state in the server db to deleted and change the transactiondate to now() function in the mysql db
    public void deleteRecordValues(String tableName,String values){
        try {
            System.out.println("UPDATE " + tableName + " SET syncState="+enuRecordState.DELETED.ordinal()+",transactionDate=NOW() WHERE id IN " + values);
            PreparedStatement stmt = con.prepareStatement("UPDATE " + tableName + " SET syncState="+enuRecordState.DELETED.ordinal()+",transactionDate=NOW() WHERE id IN " + values);
            stmt.executeUpdate();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    //get max id from table
    public int getLastIdFromTable(String tableName){
        int id=0;
        try {
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery("SELECT MAX(id) FROM "+tableName);
            while (rs.next()) {                
                id=rs.getInt(1);
            }
        } catch (SQLException e) {
            
            e.printStackTrace();
        }
        return id;
    }
    //i dont' know what the hell is that :v خلو بيلزم 
    public int getLastSyncStateIdFromTable(String tableName){
        int id=0;
        try {
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery("SELECT MAX(id) FROM "+tableName+" WHERE syncState = "+enuRecordState.SYNCHRONIZED.ordinal());
            while (rs.next()) {                
                id=rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
    @Override
    //get all inserted element from table depend on state
    public ResultSet getInsertedElementFromTable(String tableName){
        try {
            return getElementFromTableDebendOnState(tableName, enuRecordState.INSERTED);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    //get all deleted element from table depend on state
    @Override
    public ResultSet getDeletedElementFromTable(String tableName) {
        try {
            return getElementFromTableDebendOnState(tableName, enuRecordState.DELETED);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //get all updated element from table depend on state
    @Override
    public ResultSet getUpdatedElementFromTable(String tableName) {
        try {
            return getElementFromTableDebendOnState(tableName, enuRecordState.UPDATED);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    //get all element depend on sync state , i use this method in the prev three method 
    //يعني التوابع التلاتة اللي فوق 
    private ResultSet getElementFromTableDebendOnState(String tableName,enuRecordState state)throws SQLException{
        Statement s = con.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM "+tableName+" WHERE syncState="+state.ordinal());
        return rs;
    }
    //insert values to table (one row or more)
    private ResultSet insertValuesToTable(String tableName,String values) throws SQLException{
        PreparedStatement stmt = con.prepareStatement("INSERT INTO "+tableName+" VALUES "+values,Statement.RETURN_GENERATED_KEYS);
        stmt.executeUpdate();
        ResultSet result=stmt.getGeneratedKeys();
        return result; 
    }
    //this method can change from one state to another
    private void changeSyncState(String tableName,enuRecordState newState,enuRecordState oldState){ 
        try {
            PreparedStatement stmt = con.prepareStatement("UPDATE "+tableName+" SET syncState = "+newState.ordinal()+" WHERE syncState = "+oldState.ordinal());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //this method can change from inserted to synchronized
    public void changeSyncStateFromInsetedToSync(String tableName){
        changeSyncState(tableName, enuRecordState.SYNCHRONIZED, enuRecordState.INSERTED);
    }
    //*************************** old method**************************
    // i didn't use these method here :3 كمان خلوهن بيلزمو  :v 
    @Override
    public void doSelect(String tableName) {
    }

    @Override
    public void doInsert(String tableName,String name) {
    }
    @Override
    public void doDelete(String tableName, int id) {
    }
    @Override
    public void doUpdate(String tableName,String name, int id) {
    }
}
