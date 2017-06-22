/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webServiceDatabase;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Connection;
import util.Config;

/**
 *
 * @author Bcc
 */
public class DBWebServiceConnection {
    private static Connection connection;
    private DBWebServiceConnection(){
        try {
            MysqlDataSource mysql = new MysqlDataSource();
            mysql.setURL("jdbc:mysql://localhost:3306/users");
            mysql.setUser("root");
            mysql.setPassword("");
            
            connection = mysql.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }}
    
    public static Connection getDBWebServiceConnection(){
        if (connection ==null) {
            new DBWebServiceConnection();
        }
        return connection;
    }
}
