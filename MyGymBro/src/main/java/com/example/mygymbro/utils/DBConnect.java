package com.example.mygymbro.utils; // Assicurati che il package sia giusto

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
private static String DB_Url = "jdbc:mysql://localhost:3306/";
private static String DB_User = "root";
private static String DB_Pass = "";

public static Connection getConnection()  {
    try{
        Connection conn = DriverManager.getConnection(DB_Url,DB_User,DB_Pass);
        System.out.println("Connected to database successfully");
        return conn;
    }catch(SQLException e){
        System.out.println("Connection Failed! Check output console");
        e.printStackTrace();
        return null;
    }
}

}
