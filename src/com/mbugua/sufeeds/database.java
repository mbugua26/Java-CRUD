/*
 Andrew Mbugua
 BBIT
 222336
 10th November 2026
 */
package com.mbugua.sufeeds;

import java .sql.Connection;
import java .sql.DriverManager;
import java .sql.SQLException;



public class database {
    // Database name must follow :db_first_last_studentNumber
    private static final String URL = "jdbc:mysql://localhost:3306/db_andrew_mbugua_222336";
    private static final String USER = "root";
    private static final String Password = "Andy6827";

    public static Connection getConnection() throws SQLException {
         return DriverManager.getConnection(URL,USER,Password);



        }

    public static void main (String[] args){
        try(Connection conn=getConnection()) {
            System.out.println("Connection!");}
        catch(SQLException e) {
            System.out.println("Connection failed:" + e.getMessage());




        }



     }

     }