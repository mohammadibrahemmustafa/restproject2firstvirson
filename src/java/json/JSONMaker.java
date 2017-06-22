/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import configrationfileitems.Field;
import configrationfileitems.ForgeinKeyInformation;
import configrationfileitems.Table;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Bcc
 */
public class JSONMaker {
    public JSONArray dbStructureToJSON(ArrayList<Table> tables){
        
        JSONArray tableArray=new JSONArray();  
        for (Table table : tables) {
            JSONArray tableJSON=new JSONArray();
            JSONObject tableName=new JSONObject();
            tableName.put("name", table.getTableName());
            JSONArray fieldsJSON=new JSONArray();
            ArrayList<Field> fields=table.getFields();
            for (Field field : fields) {
                JSONObject fieldName=new JSONObject();
                fieldName.put("fieldName",field.getFieldName());
                JSONObject fieldType=new JSONObject();
                fieldType.put("fieldType",field.getFieldType());
                fieldsJSON.put(fieldName);
                fieldsJSON.put(fieldType);
            }
            JSONArray forgeinKeyJSON=new JSONArray();
            ArrayList<ForgeinKeyInformation> forgeinKeys=table.getForgeinKeys();
            for (ForgeinKeyInformation  forgeinKey: forgeinKeys) {
                JSONObject columnBasedTableName=new JSONObject();
                columnBasedTableName.put("columnBasedTableName",forgeinKey.getColumnBasedTableName());
                JSONObject forgeinKeyTableName=new JSONObject();
                forgeinKeyTableName.put("forgeinKeyTableName",forgeinKey.getForgeinKeyTableName());
                JSONObject forgeinKeyColumnName=new JSONObject();
                forgeinKeyColumnName.put("forgeinKeyColumnName",forgeinKey.getForgeinKeyColumnName());
                forgeinKeyJSON.put(columnBasedTableName);
                forgeinKeyJSON.put(forgeinKeyTableName);
                forgeinKeyJSON.put(forgeinKeyColumnName);
            }
            tableJSON.put(tableName);
            tableJSON.put(fieldsJSON);
            tableJSON.put(forgeinKeyJSON);
            tableArray.put(tableJSON);
        }
        return tableArray;
    }
}
