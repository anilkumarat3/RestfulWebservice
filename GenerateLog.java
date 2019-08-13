/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 *
 * @author OM33909T
 */
public class GenerateLog {

    java.util.Date dtCurDate = new java.util.Date();
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
    String strCurDate = sdfDate.format(dtCurDate);
    String strLogPath = "";

    public void genlog(String input) {
        try {
            sdfDate = new SimpleDateFormat("dd_MM_yyyy");
            SimpleDateFormat sdfTime = new SimpleDateFormat("hh_mm_ss");
            strCurDate = sdfDate.format(dtCurDate);
            strLogPath = "F:\\Web Services\\OmniData\\" + File.separator + "Log";
            File fileExists = new File(strLogPath);
            if (fileExists.exists() == false) {
                fileExists.mkdirs();
            }
            strLogPath = strLogPath + File.separator + strCurDate + ".log";
            File file = new File(strLogPath);
            FileOutputStream objLogfile = new FileOutputStream(file, true);
            objLogfile.write((input + "\r\n").getBytes());
            objLogfile.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
