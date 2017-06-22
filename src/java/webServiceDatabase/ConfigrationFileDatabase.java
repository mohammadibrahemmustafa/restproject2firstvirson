/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webServiceDatabase;

import configrationfileitems.Field;
import configrationfileitems.ForgeinKeyInformation;
import configrationfileitems.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bcc
 */
public class ConfigrationFileDatabase {
    public void insertDBElement(String dbName,String dbPassword,ArrayList<Table> tables){
        DatabaseModel db=new DatabaseModel();
        int dbId= db.insertDatabase(dbName, dbPassword, 1);
        if (dbId != -1) {
            TableModel tableModel=new TableModel();
            for (Table table : tables) {
                int tableId=tableModel.insertTable(table.getTableName(), dbId);
                if (tableId!=-1) {
                    ArrayList<Field> fields = table.getFields();
                    FieldsModel fieldsModel=new FieldsModel();
                    for (Field field : fields) {
                        fieldsModel.insertField(field.getFieldName(),field.getFieldType(), tableId);
                    }
                    ArrayList<ForgeinKeyInformation> ForgeinKeys = table.getForgeinKeys();
                    ForgeinKeyModel forgeinKeyModel=new ForgeinKeyModel();
                    for (ForgeinKeyInformation forgeinKey : ForgeinKeys) {
                        int field_id = fieldsModel.getFieldId(dbId, forgeinKey.getForgeinKeyTableName(), forgeinKey.getColumnBasedTableName());
                        if (field_id!=-1) {
                            forgeinKeyModel.insertForgeinKey(forgeinKey.getForgeinKeyColumnName(), tableId,field_id);
                        }
                    }
                }
            }
        }
    }
    
    public ArrayList<Table> getDBStructure(String dbName){
        DatabaseModel databaseModel=new DatabaseModel();
        int dbId= databaseModel.getDBId(dbName);
        TableModel tableModel=new TableModel();
        ResultSet tableSet=tableModel.getTableIdAndName(dbId);
        if (tableSet!=null) {
            try {
                ArrayList<Table> tables=new ArrayList<>();
                while (tableSet.next()) {
                    int tableId=tableSet.getInt("id");
                    FieldsModel fieldsModel=new FieldsModel();
                    ResultSet fieldsSet= fieldsModel.getFieldInfo(tableId);
                    ArrayList<Field> fields=new ArrayList<>();
                    if (fieldsSet!=null) {
                        while (fieldsSet.next()){
                            Field field=new Field(fieldsSet.getString("fieldName"), fieldsSet.getString("typeName"));
                            fields.add(field);
                        }
                    }
                    ForgeinKeyModel forgeinKeyModel=new ForgeinKeyModel();
                    ResultSet forgeinkeySet= forgeinKeyModel.getForgeinKeyInfo(tableId);
                    ArrayList<ForgeinKeyInformation> forgeinKeyInformations=new ArrayList<>();
                    if (forgeinkeySet!=null) {
                        while (forgeinkeySet.next()){
                            ForgeinKeyInformation forgeinKeyInformation=new ForgeinKeyInformation(
                                    forgeinkeySet.getString("tableName"),
                                    forgeinkeySet.getString("fieldName"),
                                    forgeinkeySet.getString("forgeinKeyName"));
                            forgeinKeyInformations.add(forgeinKeyInformation);
                        }
                    }
                    
                    Table table=new Table(tableSet.getString("name"), fields, forgeinKeyInformations);
                    tables.add(table);
                }
                return tables;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}