package com.example.mygymbro.dao;

import java.sql.Connection;
import java.sql.SQLException;
public class DAOUtils {

    public void closeConnection(Connection conn){

        conn.close();
    }


}
