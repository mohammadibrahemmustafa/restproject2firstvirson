/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.web.sync;

    
import configrationfileitems.Field;
import configrationfileitems.ForgeinKeyInformation;
import configrationfileitems.Table;
import databaseConnector.MySqlConnection;
import databaseConnector.enuRecordState;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.json.JSONArray;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import json.JSONMaker;
import json.JSONParser;
import webServiceDatabase.ConfigrationFileDatabase;

/**
 *
 * @author Bcc
 */
@Path("/Calc")
public class Calculater {
    
    int firstIdInsertedInTable;
    
    @Path("{x}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sumAsText(@PathParam("x") String x){
//        try {
//            MySqlConnection mySqlConnection=new MySqlConnection();
//            
//            return mySqlConnection.getLastIdFromTable("test_table");
//           
//        } catch (SQLException ex) {
//            
//            return ex.getMessage();
//        }
        
        return x;
    }
    // this method for parse config file json
    @Path("mohammad")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String parseConfigFileJSON(String postJsonObject){
        JSONObject tablesJSON=new JSONObject(postJsonObject);
        //get database Name
        String databaseName=tablesJSON.getString("dbName");
        String databasePassword=tablesJSON.getString("dbPassword");
        System.out.println(databaseName);
        JSONArray tableArray=tablesJSON.getJSONArray("tables");
        JSONParser parser=new JSONParser();
        ArrayList<Table> tables=parser.parseTablesJSON(tableArray);
        /*for (int i = 0; i < tableArray.length(); i++) {
            JSONArray table=tableArray.getJSONArray(i);
            
            JSONObject tableNameContainer=table.getJSONObject(0);
            
            
            tables.add(new Table(
                    tableNameContainer.getString("name"),
                    fieldJSONParser(table.getJSONArray(1)),
                    forgeinKeyJSONParser(table.getJSONArray(2))
                        )
                    );
            
        }
        */
        System.out.println(tables);
        ConfigrationFileDatabase configrationFileDatabase=new ConfigrationFileDatabase();
        configrationFileDatabase.insertDBElement(databaseName, databasePassword, tables);
        
        
        //System.out.println(tableArray.toString());
        return "test";
    }
    // the method will recive the data from any client and will connect to database and will sync all the data
    //and return the non synchroniztion data
    @Path("inserted")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String parseInsertedElementsJSON(String tableString){
        //JSONArray records=new JSONArray(recordsJsonString);
        /*JSONArray allDataJSON=new JSONArray(allDataString);
        System.out.println("all data"+allDataJSON);
        */
        JSONArray result=new JSONArray();
        JSONObject tableDataJSON=new JSONObject(tableString);
        System.out.println("table "+tableDataJSON);
        String tableName=tableDataJSON.getString("tableName");
        //************* meta data********************************

        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        Date date=null;
        try {
            date = ft.parse(tableDataJSON.getString("lastSyncDate"));
            System.out.println(""+ft.format(date));
        } catch (ParseException ex) {
            Logger.getLogger(Calculater.class.getName()).log(Level.SEVERE, null, ex);
        }
        //String currentTime = ft.format(lastSyncDate);
        //metaDataJSONParser(allDataJSON.getString("metaData"));
        OldAndNewIdsArrayJSON oldAndNewIdsArrayJSON=new OldAndNewIdsArrayJSON();


        String values= insertedRecordsJSONParser(tableDataJSON.getString("insertedRecords"),oldAndNewIdsArrayJSON);
        String deletedIdsValues=deletedRecordsIdsJSONParser(tableDataJSON.getString("deletedRecords"));
        System.out.println(values);
        System.out.println(deletedIdsValues);
        MySqlConnection mySqlConnection=new MySqlConnection();

        //System.out.println(records);
        if (deletedIdsValues!=null&&!deletedIdsValues.isEmpty()) {
            mySqlConnection.deleteRecordValues(tableName,"("+deletedIdsValues+")");
        }
        JSONObject tableInfoJSON= getAllNonSyncDataFromDatabaseAfterDate(tableName,date,oldAndNewIdsArrayJSON,values);
        System.out.println(tableInfoJSON);
        result.put(tableInfoJSON);
        
        return result.toString();
    }
    //this method will parse user info and check if the user can connect to the database and the return value will
    //contain the the connection state (succed or failed) and the last sync date
    @Path("userInfo")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String parseUserInfoJSON(String userInfoString){
        JSONObject userInfoJSON=new JSONObject(userInfoString);
        String databaseName=userInfoJSON.getString("databaseName");
        String username=userInfoJSON.getString("username");
        String hashPassword=userInfoJSON.getString("password");
        //query to user db
        boolean reslut= validateUser(databaseName, username, hashPassword);
        
        JSONObject connectionState=new JSONObject();
        connectionState.put("connectionState", reslut);
        if (reslut) {
            connectionState.put("lastSyncDate","2017-05-07 01:14:22");
            ConfigrationFileDatabase configrationFileDatabase=new ConfigrationFileDatabase();
            JSONMaker maker=new JSONMaker();
            JSONArray tables = maker.dbStructureToJSON(configrationFileDatabase.getDBStructure(databaseName));
            connectionState.put("tables", tables);
        }
        
        return connectionState.toString();
    }
    
    //the will parse the meta data from json and return the meta data for table
    private ArrayList<String> metaDataJSONParser(String metaDataJSONString){
        JSONObject metaData=new JSONObject(metaDataJSONString);
        ArrayList<String> fields=new ArrayList<>();
        int count=metaData.names().length();
        for (int i = 0; i < count; i++) {
            fields.add(metaData.getString(Integer.toString(i+1)));
        }
        return  fields;
    }
    //the will parse the inserted records from json and return the string value contain the new records from client
    //like (3,ali,1,2017-5-9 15:15:15),(3,ali,1,2017-5-9 15:15:15)
    private String insertedRecordsJSONParser(String insertedRecordsJSONString,OldAndNewIdsArrayJSON oldAndNewIdsArrayJSON){
        JSONArray records=new JSONArray(insertedRecordsJSONString);
        String recordsQueryValues="";
        int length=records.length();
        //get the id of the first inserted id 
        for (int i = 0; i < length; i++) {
            JSONArray record=records.getJSONArray(i);
            if (i!=length-1) {
                recordsQueryValues+=convertRecordString(record.toString(),oldAndNewIdsArrayJSON)+",";
            }else{
                recordsQueryValues+=convertRecordString(record.toString(),oldAndNewIdsArrayJSON);
            }
        }
        return recordsQueryValues;
    }
    //this will parse record value from json to  record value
    private String convertRecordString(String record,OldAndNewIdsArrayJSON oldAndNewIdsArrayJSON){
        int lastComma=record.lastIndexOf(',')
            ,firstComma=record.indexOf(',');
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String currentTime = ft.format(date);
        String oldIds=record.substring(1,firstComma);
        oldAndNewIdsArrayJSON.oldIdsJSON.put(Integer.parseInt(oldIds));
        System.out.println(record.substring(firstComma+1,lastComma+1));
        String result="(default,"+record.substring(firstComma+1,lastComma+1)+"NOW())";
        return result;
    }
    //this will check if the user can access to the database
    private boolean validateUser(String databaseName,String username,String password){
        return true;
    }
    //this will convert deletedIds json to value that i can use for mysql query
    // [2,5,6,8]  will be => (2,5,6,8) 
    private String deletedRecordsIdsJSONParser(String deletedIds){
        int temp= deletedIds.length()-1;
        return deletedIds.substring(1,temp);
    }
    //this method will connect to the database and make json contain all non synchronized data
    private JSONObject getAllNonSyncDataFromDatabaseAfterDate(String tableName,Date date,OldAndNewIdsArrayJSON oldAndNewIdsArrayJSON , String values){
        try {
            MySqlConnection mySqlConnection=new MySqlConnection();
            
          
            //ResultSet insertedRs= mySqlConnection.getInsertedElementsByLastSyncDate("names", date);
            ResultSet deletedRs= mySqlConnection.getDeletedElementsByLastSyncDate(tableName, date);
            ResultSet updatedRs= mySqlConnection.getUpdatedElementsByLastSyncDate(tableName, date);
            
            ResultSetMetaData metaData =updatedRs.getMetaData();
            int columnCount=metaData.getColumnCount();
            JSONObject tableJSON=new JSONObject();
            JSONArray tableCoulmnsNamesJSON=new JSONArray();
            JSONArray DeletedJSON=new JSONArray();
            JSONArray allRecordsUpdatedJSON=new JSONArray();
            JSONArray allRecordsInsertedJSON=getAllInsertedRecordsAfterDate(tableName,mySqlConnection, columnCount, date);
            if (values!=null&&!values.isEmpty()) {
                ResultSet newIdsResultSet= mySqlConnection.insertRecordValues(tableName, values);
                fillNewIds(newIdsResultSet, oldAndNewIdsArrayJSON);
            }
            for (int i = 1; i <= columnCount; i++) {
                tableCoulmnsNamesJSON.put(metaData.getColumnName(i));
            }
            System.out.println("######################");
            /*while (insertedRs.next()){
                JSONArray insertedJSON=new JSONArray();
                for (int i = 1; i <= columnCount; i++) {
                    insertedJSON.put(insertedRs.getObject(i));
                
             //   insertedJSON.put();
                }
                allRecordsInsertedJSON.put(insertedJSON);
            }*/
            while(deletedRs.next()){
                DeletedJSON.put(deletedRs.getInt(1));
            }
            while(updatedRs.next()){
                JSONArray updatedJSON=new JSONArray();
                for (int i = 1; i <= columnCount; i++) {
                    updatedJSON.put(updatedRs.getObject(i));
                }
                allRecordsUpdatedJSON.put(updatedJSON);
            }
            tableJSON.put("tableName", tableName);
            tableJSON.put("metaData", tableCoulmnsNamesJSON.toString());
            tableJSON.put("inserted", allRecordsInsertedJSON.toString());
            tableJSON.put("deleted", DeletedJSON.toString());
            tableJSON.put("updated", allRecordsUpdatedJSON.toString());
            
            tableJSON.put("oldIds", oldAndNewIdsArrayJSON.oldIdsJSON.toString());
            tableJSON.put("newIds", oldAndNewIdsArrayJSON.newIdsJSON.toString());
            
            
            return tableJSON;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private JSONArray getAllInsertedRecordsAfterDate(String tableName, MySqlConnection mySqlConnection,int columnCount,Date date) throws SQLException{
       JSONArray allRecordsInsertedJSON=new JSONArray();
        ResultSet insertedRs= mySqlConnection.getInsertedElementsByLastSyncDate(tableName, date);
        ResultSetMetaData metaData=insertedRs.getMetaData();
        while (insertedRs.next()){
            JSONObject insertedJSON=new JSONObject();
            for (int i = 1; i <= columnCount; i++) {
                insertedJSON.put(metaData.getColumnName(i), insertedRs.getObject(i));
                //insertedJSON.put(insertedRs.getObject(i));

         //   insertedJSON.put();
            }
            allRecordsInsertedJSON.put(insertedJSON);
        }
        return allRecordsInsertedJSON;
    }
    
    private void fillNewIds(ResultSet resultSet,OldAndNewIdsArrayJSON oldAndNewIdsArrayJSON){
        try {
            while (resultSet.next()) {
                oldAndNewIdsArrayJSON.newIdsJSON.put(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
