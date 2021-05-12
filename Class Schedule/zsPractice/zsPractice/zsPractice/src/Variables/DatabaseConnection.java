/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Variables;

import java.sql.DriverManager;
import java.sql.Connection;

/**
 *
 * @author FIRE CP
 */
public class DatabaseConnection {
    
    private static Connection connectDB;
    
    public DatabaseConnection() {     
    }
    
    public static void database() {
        System.out.println("Connecting to the database");   
        String driver = "com.mysql.cj.jdbc.Driver";
        String db = "U04bZK";
        String url = "jdbc:mysql://52.206.157.109/" + db;
        String user = "U04bZK";
        String pass = "53688196894";        
        try { 
            Class.forName(driver);
            connectDB = DriverManager.getConnection(url,user,pass);
        }
        catch (ClassNotFoundException ce) {
            ce.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Connection getConnect() {
        return connectDB;
    }
    
    public static void closeConnect() {
        try {
            connectDB.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("connection closed");
        }
    }
}
