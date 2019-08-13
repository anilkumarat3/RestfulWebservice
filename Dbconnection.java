package com.newgen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * ***********************************************************************************************************

 *
 * Group	: SDC Module	: API For ChatBoot File Name	: Dbconnection.java Author	:
 * Anil Kumar A Date written	: 23/07/2019
 * ***********************************************************************************************************
 */
public class Dbconnection {
    GenerateLog log = new GenerateLog();

    private static String username = "";
    private static String password = "";
    private static String databaseIP = "";
    private static String databasePort = "";
    private static String serviceName = "";

    public  Statement getConnection() throws ClassNotFoundException, SQLException, FileNotFoundException, IOException {
        //String initialFilePath = System.getProperty("user.dir") + File.separator + "DBProperties.ini"; it points JBoss bin directory
        String initialFilePath = "F:\\Web Services\\OmniData\\DBProperties.ini";
        log.genlog("Inside Database Connection.......");
        log.genlog("INI File path is------------>"+initialFilePath);
        FileInputStream IniFile = new FileInputStream(initialFilePath);
        Properties pro = new Properties();
        pro.load(IniFile);
        username = pro.getProperty("userName");
        password = pro.getProperty("password");
        databaseIP = pro.getProperty("databaseIP");
        databasePort = pro.getProperty("databasePort");
        serviceName = pro.getProperty("serviceName");
        log.genlog("username:"+username);
        log.genlog("password:"+password);
        log.genlog("databaseIP:"+databaseIP);
        log.genlog("databasePort:"+databasePort);
        log.genlog("serviceName:"+serviceName);
        Connection conn = null;
        String dbURL = "jdbc:sqlserver://" + databaseIP + ":" + databasePort + ";databaseName={" + serviceName + "}";
        log.genlog("dbURL::::::" + dbURL);
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        conn = DriverManager.getConnection(dbURL, username, password);
        log.genlog("Database Connected Succufully-------------");
        Statement st = conn.createStatement();
        return st;
    }
}
