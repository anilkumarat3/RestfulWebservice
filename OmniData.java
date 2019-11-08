package com.newgen;

import JSON.JSONException;
import JSON.JSONObject;
import com.newgen.dmsapi.DMSXmlList;
import com.newgen.dmsapi.DMSXmlResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import sun.misc.BASE64Decoder;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ws.rs.Consumes;

/**
 * ***********************************************************************************************************
 * NEWGEN SOFTWARE TECHNOLOGIES LIMITED
 *
 * Group	: SDC Module	: API For ChatBoot File Name	: OmniData.java Author	: Anil
 * Kumar A Date written	: 23/07/2019
 * ***********************************************************************************************************
 */
@Path("/omniflow")
public class OmniData {

    GenerateLog log = new GenerateLog();
    Dbconnection db = new Dbconnection();
    GetdatafromOmni data = new GetdatafromOmni();
    Beans bean = new Beans();
    ArrayList<Beans> omniData = new ArrayList<>();
    ArrayList<Beans> partialRes = new ArrayList<>();
    String ColName1 = "";
    String ColName2 = "";
    String ColName3 = "";
    String AUTHENTICATION_HEADER_KEY = "Authorization";
    String AUTHENTICATION_HEADER_PREFIX = "Base ";
    String userName = "";
    String Password = "";
    String secretKey = "";

    @POST
    @Path("/getdata")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ArrayList<Beans> getDatails(InputStream inputStream, @Context HttpServletRequest request) throws Exception, SQLException, IOException, JSONException {
        JSONObject responseData = new JSONObject();
        String VPNumber = "";
        String VendorCode = "";
        String InvoiceNo = "";
        String PurchOrderNo = "";
        String InvoiceDate = "";
        String startInvoiceDate = "";
        String endInvoiceDate = "";
        String Token = "";
        // log.genlog("VPNumber: " + VPNumber + "VendorCode: " + VendorCode + "InvoiceNo: " + InvoiceNo + "startInvoiceDate : " + startInvoiceDate + "PurchOrderNo:  " + PurchOrderNo + "endInvoiceDate:  " + endInvoiceDate);

        Enumeration<String> authHeader = request.getHeaders(AUTHENTICATION_HEADER_KEY);

        if (authHeader.hasMoreElements()) {
            String strIniPath = System.getProperty("user.dir") + File.separator + "DBProperties.ini";
            File ini = new File(strIniPath);
            if (ini.exists()) {
                Properties property = new Properties();
                FileInputStream fInputStream = new FileInputStream(ini);
                property.load(fInputStream);
                fInputStream.close();
                userName = property.getProperty("OmniUsername");
                Password = property.getProperty("OmniUserpassword");
                secretKey = property.getProperty("SecretKey");

            }
            log.genlog("Inside Authorization Header -----------------------" + authHeader);
            String authToken = authHeader.nextElement();
            authToken = authToken.replaceFirst(AUTHENTICATION_HEADER_PREFIX, "");
            String[] authParts = authToken.split("\\s+");
            String authInfo = authParts[1];
            log.genlog(authInfo);
            byte[] bytes = null;
            try {
                bytes = new BASE64Decoder().decodeBuffer(authInfo);
            } catch (IOException e) {
            }
            String decodedAuth = "";
            decodedAuth = new String(bytes);
            StringTokenizer tokenizer = new StringTokenizer(decodedAuth, ":");
            String username = tokenizer.nextToken();
            String password = tokenizer.nextToken();
            if (username.equalsIgnoreCase(userName) && password.equalsIgnoreCase(Password)) {
                System.out.println("username::" + username + "password::" + password);
                log.genlog("username::::" + username + "password:::" + password);
                try {
                    JSONObject jsonRequestData = new JSONObject(readDataFromStream(inputStream));
                    VPNumber = jsonRequestData.getString("VPNumber");
                    VendorCode = jsonRequestData.getString("VendorCode");
                    InvoiceNo = jsonRequestData.getString("InvoiceNo");
                    PurchOrderNo = jsonRequestData.getString("PurchOrderNo");
                    InvoiceDate = jsonRequestData.getString("InvoiceDate");
                    startInvoiceDate = jsonRequestData.getString("startInvoiceDate");
                    endInvoiceDate = jsonRequestData.getString("endInvoiceDate");
                    Token = jsonRequestData.getString("Token");
                    log.genlog("VPNumber: " + VPNumber + "VendorCode: " + VendorCode + "InvoiceNo: " + InvoiceNo + "startInvoiceDate : " + startInvoiceDate + "PurchOrderNo:  " + PurchOrderNo + "endInvoiceDate:  " + endInvoiceDate);

                } catch (Exception e) {
                    omniData = invalidInputParameter();
                    log.genlog("Exception occured during reading the input parameters   " + e);

                }
                if (Token == null ||Token.isEmpty()) {
                    omniData = invalidToken();
                } else {

                    //Validate the Token
                    String Query = "SELECT token FROM Tb_token where token= '" + Token + "';";
                    log.genlog("Query::::" + Query);
                    GetdatafromOmni data = new GetdatafromOmni();
                    data.readINIFile();
                    data.connectCabinet();
                    String valid_token = "";
                    valid_token = data.NGIGetDatas(Query, 1);
                    //System.out.println("Afetr Decryption---------->>>");
                    if (Token.equals(valid_token)) {
					 /*try {
                    Base64.Encoder enc = Base64.getEncoder();
                    Base64.Decoder dec = Base64.getDecoder();
                    String encoded = enc.encodeToString(queryString.getBytes());
                    System.out.println("Encodede String ::::" + encoded);
                    log.genlog("Encodede String ::::" + encoded);

                    String decoded = new String(dec.decode(encoded));
                    System.out.println("Decoded String:::" + decoded);
                    log.genlog("Decoded String:::" + decoded);

                    int count = decoded.length() - decoded.replaceAll("\\&", "").length();
                    System.out.println("count:" + count);
                    log.genlog("count:" + count);
                    if (count == 0) {
                        log.genlog("Inside count 0000:" + count);
                        System.out.println("decoded:" + decoded);
                        String ar[] = decoded.split("=");
                        System.out.println("FirstString:" + ar[0] + "Second String:" + ar[1]);
                        log.genlog("FirstString:" + ar[0] + "Second String:" + ar[1]);
                        if (ar[0].equalsIgnoreCase("VPnumber")) {
                            VPNumber = ar[1];
                            System.out.println("VPNumber:::" + VPNumber);
                            log.genlog("FirstString:" + ar[0] + "Second String:" + ar[1]);

                        } else if (ar[0].equalsIgnoreCase("PurchOrderNo")) {
                            PurchOrderNo = ar[1];
                            System.out.println("PurchOrderNo:::" + PurchOrderNo);
                            log.genlog("FirstString:" + ar[0] + "Second String:" + ar[1]);

                        } else if (ar[0].equalsIgnoreCase("InvoiceNo")) {
                            InvoiceNo = ar[1];
                            System.out.println("InvoiceNo::::" + InvoiceNo);
                            log.genlog("InvoiceNo::::" + InvoiceNo);

                        }
                    } else if (count == 1) {
                        System.out.println("decoded:" + decoded);
                        String ar[] = decoded.split("&");
                        System.out.println("FirstString:" + ar[0] + "Second String:" + ar[1]);
                        String Fstring[] = ar[0].split("=");
                        String SString[] = ar[1].split("=");
                        if (Fstring[0].equalsIgnoreCase("InvoiceNo") && SString[0].equalsIgnoreCase("PurchOrderNo")) {
                            InvoiceNo = Fstring[1];
                            PurchOrderNo = SString[1];
                            System.out.println("InvoiceNo:::" + InvoiceNo + "PurchOrderNo:::::" + PurchOrderNo);
                            log.genlog("InvoiceNo:::" + InvoiceNo + "PurchOrderNo:::::" + PurchOrderNo);

                        } else if (Fstring[0].equalsIgnoreCase("VendorCode") && SString[0].equalsIgnoreCase("InvoiceNo")) {
                            VendorCode = Fstring[1];
                            InvoiceNo = SString[1];
                            System.out.println("VendorCode:::" + VendorCode + "InvoiceNo:::::" + InvoiceNo);
                            log.genlog("VendorCode:::" + VendorCode + "InvoiceNo:::::" + InvoiceNo);

                        } else if (Fstring[0].equalsIgnoreCase("VendorCode") && SString[0].equalsIgnoreCase("InvoiceDate")) {
                            VendorCode = Fstring[1];
                            InvoiceDate = SString[1];
                            System.out.println("VendorCode:::" + VendorCode + "InvoiceDate:::::" + InvoiceDate);
                            log.genlog("VendorCode:::" + VendorCode + "InvoiceDate:::::" + InvoiceDate);

                        } else if (Fstring[0].equalsIgnoreCase("InvoiceNo") && SString[0].equalsIgnoreCase("VendorCode")) {
                            InvoiceNo = Fstring[1];
                            VendorCode = SString[1];
                            System.out.println("InvoiceNo:::" + InvoiceNo + "VendorCode:::::" + VendorCode);
                            log.genlog("InvoiceNo:::" + InvoiceNo + "VendorCode:::::" + VendorCode);

                        }

                    } else if (count == 2) {
                        System.out.println("Inside Count 3-------");
                        System.out.println("decoded:" + decoded);
                        System.out.println("Count------>" + count);
                        String ar[] = decoded.split("&");
                        System.out.println("FirstString:" + ar[0] + "Second String:" + ar[1] + "Third String :::" + ar[2]);
                        String Fstring[] = ar[0].split("=");
                        String SString[] = ar[1].split("=");
                        String TString[] = ar[2].split("=");
                        if (Fstring[0].equalsIgnoreCase("VendorCode") && SString[0].equalsIgnoreCase("startInvoiceDate") && TString[0].equalsIgnoreCase("endInvoiceDate")) {
                            VendorCode = Fstring[1];
                            startInvoiceDate = SString[1];
                            endInvoiceDate = TString[1];
                            System.out.println("VendorCode:" + VendorCode + "startInvoiceDate:: " + startInvoiceDate + "endInvoiceDate:: " + endInvoiceDate);
                            log.genlog("VendorCode:" + VendorCode + "startInvoiceDate:: " + startInvoiceDate + "endInvoiceDate:: " + endInvoiceDate);

                        } else if (Fstring[0].equalsIgnoreCase("InvoiceNo") && SString[0].equalsIgnoreCase("startInvoiceDate") && TString[0].equalsIgnoreCase("endInvoiceDate")) {
                            InvoiceNo = Fstring[1];
                            startInvoiceDate = SString[1];
                            endInvoiceDate = TString[1];
                            System.out.println("InvoiceNo:" + InvoiceNo + "startInvoiceDate:: " + startInvoiceDate + "endInvoiceDate:: " + endInvoiceDate);
                            log.genlog("InvoiceNo:" + InvoiceNo + "startInvoiceDate:: " + startInvoiceDate + "endInvoiceDate:: " + endInvoiceDate);

                        }

                    } else {
                        omniData = invalidInputParameter();
                    }
                } catch (Exception E) {
                    System.out.println("Exception Occured-------->" + E);
                    log.genlog("Exception Occured-------->" + E);
                }*/
                        boolean Flag = true;
                        String passColName = "";
                        String passColName1 = "";
                        String passColName2 = "";
                        String passColName3 = "";
                        try {
                            if (VPNumber != null && Flag == true) {
                                log.genlog("Inside VPNumber");
                                log.genlog("Inside VPNumber" + VPNumber);
                                passColName = "VPnumber";
                                log.genlog("Inside VP Number ");
                                omniData = getDataFromDB(VPNumber, passColName);
                                Flag = false;
                            } else if (VendorCode != null && InvoiceNo != null && Flag == true) {
                                log.genlog("Inside Query Vendor Code and Invoice Number:VendorCode: " + VendorCode + "InvoiceNo:" + InvoiceNo);
                                passColName1 = "VendorCode";
                                passColName2 = "InvoiceNo";
                                omniData = getDataFromDB2(VendorCode, InvoiceNo, passColName1, passColName2);
                                Flag = false;
                            } else if (VendorCode != null && InvoiceDate != null && Flag == true) {
                                log.genlog("Inside Query Vendor Code and Invoice Number:VendorCode: " + VendorCode + "InvoiceDate:" + InvoiceDate);
                                passColName1 = "VendorCode";
                                passColName2 = "InvoiceDate";
                                omniData = getDataFromDB2(VendorCode, InvoiceDate, passColName1, passColName2);
                                Flag = false;
                            } else if (VendorCode != null && startInvoiceDate != null && endInvoiceDate != null && Flag == true) {
                                log.genlog("inside 3");
                                passColName1 = "VendorCode";
                                passColName2 = "startInvoiceDate";
                                passColName3 = "endInvoiceDate";
                                omniData = getDataFromDB3(VendorCode, startInvoiceDate, endInvoiceDate, passColName1, passColName2, passColName3);
                                Flag = false;
                            } else if (PurchOrderNo != null && startInvoiceDate != null && endInvoiceDate != null && Flag == true) {
                                log.genlog("inside 3");
                                passColName1 = "PurchOrderNo";
                                passColName2 = "startInvoiceDate";
                                passColName3 = "endInvoiceDate";
                                omniData = getDataFromDB3(PurchOrderNo, startInvoiceDate, endInvoiceDate, passColName1, passColName2, passColName3);
                                Flag = false;
                            } else if (InvoiceNo != null && PurchOrderNo != null && Flag == true) {
                                log.genlog("Inside 5");
                                log.genlog("INside Invoice and PONO");
                                passColName1 = "InvoiceNo";
                                passColName2 = "PurchOrderNo";
                                omniData = getDataFromDB2(InvoiceNo, PurchOrderNo, passColName1, passColName2);
                                Flag = false;
                            } else if (InvoiceNo != null && Flag == true) {
                                log.genlog("Inside 4");
                                passColName = "InvoiceNo";
                                omniData = getDataFromDB(InvoiceNo, passColName);
                                Flag = false;
                            } else if (PurchOrderNo != null && Flag == true) {
                                log.genlog("Inside 6");
                                passColName = "purchaseorder";
                                omniData = getDataFromDB(PurchOrderNo, passColName);
                                Flag = false;
                            } else if (VendorCode != null && Flag == true) {
                                log.genlog("Inside 7");
                                passColName = "VendorCode";
                                omniData = getDataFromDB(VendorCode, passColName);
                                Flag = false;
                            } else {
                                omniData = invalidInputParameter();
                            }
                            log.genlog("Result::" + omniData);
                        } catch (Exception e) {
                            log.genlog("Out of Combinations in input parameter value----");
                        }

                    } else {
                        omniData = invalidToken();
                    }

                }
            } else {
                omniData = invalidUsernamePassword();
            }
        } else {
            omniData = unabletoaccessresource();
        }

        String Query = "delete from Tb_token where token='" + Token + "';";
        try {
            data.readINIFile();
            data.connectCabinet();
            log.genlog("Query to delete the token------------:" + Query);
            data.setData(Query);
            data.disconnectCabinet();

        } catch (Exception e) {
            log.genlog(" exception occured during deleting  the data" + Query);
        }
        return omniData;

    }

    @GET
    @Path("/getdata")
    @Produces(MediaType.APPLICATION_JSON)

    public ArrayList<Beans> getDatails1(@Context HttpServletRequest request, @Context HttpServletResponse response, @QueryParam("Token") String Token, @QueryParam("VPNumber") String VPNumber, @QueryParam("VendorCode") String VendorCode, @QueryParam("InvoiceNo") String InvoiceNo, @QueryParam("InvoiceDate") String InvoiceDate, @QueryParam("startInvoiceDate") String startInvoiceDate, @QueryParam("endInvoiceDate") String endInvoiceDate, @QueryParam("PurchOrderNo") String PurchOrderNo) throws ClassNotFoundException, SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        log.genlog("invalidresult inside GET method for the input parametes");
        omniData = invalidMethod();
        return omniData;

    }

    @POST
    @Path("/generatetoken")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Beans> GenarateToken(@Context HttpServletRequest request, @Context HttpServletResponse response) throws ClassNotFoundException, SQLException, IOException, JSONException {

        log.genlog("Inside Generate Token for Authentication::::::");
        Enumeration<String> authHeader = request.getHeaders(AUTHENTICATION_HEADER_KEY);
        if (authHeader.hasMoreElements()) {
            String strIniPath = System.getProperty("user.dir") + File.separator + "DBProperties.ini";
            File ini = new File(strIniPath);
            if (ini.exists()) {
                Properties property = new Properties();
                FileInputStream fInputStream = new FileInputStream(ini);
                property.load(fInputStream);
                fInputStream.close();
                userName = property.getProperty("OmniUsername");
                Password = property.getProperty("OmniUserpassword");
                secretKey = property.getProperty("SecretKey");

            }
            log.genlog("Inside Authorization Header -----------------------" + authHeader);
            String authToken = authHeader.nextElement();
            authToken = authToken.replaceFirst(AUTHENTICATION_HEADER_PREFIX, "");
            String[] authParts = authToken.split("\\s+");
            String authInfo = authParts[1];
            log.genlog(authInfo);
            byte[] bytes = null;
            try {
                bytes = new BASE64Decoder().decodeBuffer(authInfo);
            } catch (IOException e) {
            }
            String decodedAuth = "";
            decodedAuth = new String(bytes);
            StringTokenizer tokenizer = new StringTokenizer(decodedAuth, ":");
            String username = tokenizer.nextToken();
            String password = tokenizer.nextToken();
            if (username.equalsIgnoreCase(userName) && password.equalsIgnoreCase(Password)) {
                System.out.println("username::" + username + "password::" + password);
                log.genlog("username::::" + username + "password:::" + password);
                omniData = generateToken();
            } else {
                omniData = invalidUsernamePassword();
            }

        } else {
            omniData = unabletoaccessresource();
        }
        return omniData;
    }

    @GET
    @Path("/generatetoken")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Beans> GenarateToken1(@Context HttpServletRequest request,
            @Context HttpServletResponse response) throws ClassNotFoundException, SQLException, IOException, JSONException {
        log.genlog("invalidresult inside GET method for the input parametes");
        omniData = invalidMethod();
        return omniData;
    }

    @POST
    @Path("/topost")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String topost(InputStream inputStream) throws Exception {
        JSONObject responseData = new JSONObject();
        String VPNumber = "";
        String VendorCode = "";
        String InvoiceNo = "";
        String PurchOrderNo = "";
        String InvoiceDate = "";
        String startInvoiceDate = "";
        String endInvoiceDate = "";
        String Token = "";
        try {
            JSONObject jsonRequestData = new JSONObject(readDataFromStream(inputStream));
            VPNumber = jsonRequestData.getString("VPNumber");
            VendorCode = jsonRequestData.getString("VendorCode");
            InvoiceNo = jsonRequestData.getString("InvoiceNo");
            PurchOrderNo = jsonRequestData.getString("PurchOrderNo");
            InvoiceDate = jsonRequestData.getString("InvoiceDate");
            startInvoiceDate = jsonRequestData.getString("startInvoiceDate");
            endInvoiceDate = jsonRequestData.getString("endInvoiceDate");
            Token = jsonRequestData.getString("Token");

        } catch (Exception e) {

        }
        return "WELCOME to POST " + VPNumber + " " + VendorCode + " " + InvoiceNo + "" + PurchOrderNo + "" + InvoiceDate + "" + startInvoiceDate + "" + endInvoiceDate + "" + Token;
    }

    private String readDataFromStream(InputStream inputStream) throws IOException {
        InputStream in = inputStream;
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String read;
        while ((read = br.readLine()) != null) {
            sb.append(read);
        }
        br.close();
        return sb.toString();
    }

    public ArrayList getDataFromDB(String valueFromURL, String passColName) throws ClassNotFoundException, SQLException, IOException {
        log.genlog("Inside get Data from Databases");
        log.genlog("valueFromURL--------->" + valueFromURL + "PassColName---->" + passColName);
        try {
            if (passColName.equalsIgnoreCase("VPnumber")) {
                log.genlog("PassColName:" + passColName);
                ColName1 = "processinstanceid1";
                log.genlog("ColName:" + ColName1);
            } else if (passColName.equalsIgnoreCase("InvoiceNo")) {
                log.genlog("PassColName:" + passColName);
                ColName1 = "InvoiceNo";
                log.genlog("ColName:" + ColName1);
            } else if (passColName.equalsIgnoreCase("purchaseorder")) {
                log.genlog("PassColName:" + passColName);
                ColName1 = "PONO";
                log.genlog("ColName:" + ColName1);
            } else if (passColName.equalsIgnoreCase("VendorCode")) {
                log.genlog("PassColName:" + passColName);
                ColName1 = "A_VendorCode";
                log.genlog("ColName:" + ColName1);
            }
        } catch (Exception e) {
            log.genlog("Error in Selecting Database Coloumns Names:");
        }

        String Query = "SELECT a.processinstanceid1, a.A_VendorCode, a.A_VendorName, a.InvoiceNo, b.activityname, a.Currency,a.Reject_comments,a.A_Barcode,a.Invoice_Date,b.previousstage,a.DueDate, a.PaymentDt, a.ProcComm, a.Grin_Hold_Comments,a.User_SPOC_Id,a.GrinHoldReason,a.GrinHoldSubReason,a.ActInvAmt,a.PONO FROM EXTDC_VEN_PROCESS a WITH(nolock) , QUEUEVIEW b WITH(nolock) WHERE " + ColName1 + "='" + valueFromURL + "' AND a.processinstanceid1=b.processinstanceid";        // Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
        log.genlog("Query:" + Query);

        DMSXmlResponse xmlResponse = null;
        GetdatafromOmni obj = new GetdatafromOmni();
        DMSXmlList list = obj.getdatafromapp(Query, 19);
        DMSXmlList xmlList = null;
        ArrayList<Beans> partialRes = new ArrayList<>();
        log.genlog("list::" + list);
        if (list != null) {
            for (xmlList = list; xmlList.hasMoreElements(); xmlList.skip()) {
                Beans bean = new Beans();
                bean.setVPNumber(xmlList.getVal("Value1"));
                bean.setVendorCode(xmlList.getVal("Value2"));
                bean.setVendorName(xmlList.getVal("Value3"));
                bean.setInvoiceNo(xmlList.getVal("Value4"));
                bean.setStatus(xmlList.getVal("Value5"));
                bean.setCurrency(xmlList.getVal("Value6"));
                bean.setRejection_Comments(xmlList.getVal("Value7"));
                bean.setParkDocument_No(xmlList.getVal("Value8"));
                bean.setInvoice_Date(xmlList.getVal("Value9"));
                bean.setPreviousStage(xmlList.getVal("Value10"));
                bean.setDueDate(xmlList.getVal("Value11"));
                bean.setPaymentDate(xmlList.getVal("Value12"));
                bean.setProc_Comments(xmlList.getVal("Value13"));
                bean.setGrin_hold_commnets(xmlList.getVal("Value14"));
                bean.setUserSPOC_emailID(xmlList.getVal("Value15"));
                bean.setBucket_reason(xmlList.getVal("Value16"));
                bean.setBucket_subreason(xmlList.getVal("Value17"));
                bean.setInvoice_amount(xmlList.getVal("Value18"));
                bean.setPONO(xmlList.getVal("Value19"));
                partialRes.add(bean);
                log.genlog("partialRes:" + partialRes);
            }

        }
        if (partialRes.isEmpty()) {
            partialRes = nodatafound();
        }
        log.genlog("partialRes::::" + partialRes);
        return partialRes;
    }

    public ArrayList getDataFromDB2(String valueFromURL1, String valueFromURL2, String PassColName1, String PassColName2) throws ClassNotFoundException, SQLException, IOException {
        log.genlog("Inside Data from DataBase for Query 2:");
        log.genlog("valueFromURL1-->:" + valueFromURL1 + "valueFromURL2-->:" + valueFromURL2 + "PassColName1:-->" + PassColName1 + "PassColName2:-->" + PassColName2);

        try {
            if (PassColName1.equalsIgnoreCase("VendorCode") && PassColName2.equalsIgnoreCase("InvoiceNo")) {
                log.genlog("Inside Coloumn Update:");
                ColName1 = "A_VendorCode";
                ColName2 = "InvoiceNo";
                log.genlog("ColName1:" + ColName1 + "ColName2:" + ColName2);
            } else if (PassColName1.equalsIgnoreCase("VendorCode") && PassColName2.equalsIgnoreCase("InvoiceDate")) {
                ColName1 = "A_VendorCode";
                ColName2 = "Invoice_Date";
            } else if (PassColName1.equalsIgnoreCase("InvoiceNo") && PassColName2.equalsIgnoreCase("PurchOrderNo")) {
                ColName1 = "InvoiceNo";
                ColName2 = "PONO";
            }
        } catch (Exception e) {
            log.genlog("Exception during updating Database  Coloumns Names:::::anillllll:");
        }
        String Query = "SELECT a.processinstanceid1, a.A_VendorCode, a.A_VendorName, a.InvoiceNo, b.activityname, a.Currency,a.Reject_comments,a.A_Barcode,a.Invoice_Date,b.previousstage,a.DueDate, a.PaymentDt, a.ProcComm, a.Grin_Hold_Comments,a.User_SPOC_Id,a.GrinHoldReason,a.GrinHoldSubReason,a.ActInvAmt,a.PONO FROM EXTDC_VEN_PROCESS a WITH(nolock) , QUEUEVIEW b WITH(nolock) WHERE " + ColName1 + "='" + valueFromURL1 + "' and " + ColName2 + "='" + valueFromURL2 + "' AND a.processinstanceid1=b.processinstanceid";
        log.genlog(" Query Anilll:" + Query);

        ArrayList<Beans> partialRes = new ArrayList<>();
        DMSXmlResponse xmlResponse = null;
        GetdatafromOmni obj = new GetdatafromOmni();
        DMSXmlList list = obj.getdatafromapp(Query, 19);
        DMSXmlList xmlList = null;
        log.genlog("list::" + list);
        if (list != null) {
            for (xmlList = list; xmlList.hasMoreElements(); xmlList.skip()) {
                Beans bean = new Beans();
                bean.setVPNumber(xmlList.getVal("Value1"));
                bean.setVendorCode(xmlList.getVal("Value2"));
                bean.setVendorName(xmlList.getVal("Value3"));
                bean.setInvoiceNo(xmlList.getVal("Value4"));
                bean.setStatus(xmlList.getVal("Value5"));
                bean.setCurrency(xmlList.getVal("Value6"));
                bean.setRejection_Comments(xmlList.getVal("Value7"));
                bean.setParkDocument_No(xmlList.getVal("Value8"));
                bean.setInvoice_Date(xmlList.getVal("Value9"));
                bean.setPreviousStage(xmlList.getVal("Value10"));
                bean.setDueDate(xmlList.getVal("Value11"));
                bean.setPaymentDate(xmlList.getVal("Value12"));
                bean.setProc_Comments(xmlList.getVal("Value13"));
                bean.setGrin_hold_commnets(xmlList.getVal("Value14"));
                bean.setUserSPOC_emailID(xmlList.getVal("Value15"));
                bean.setBucket_reason(xmlList.getVal("Value16"));
                bean.setBucket_subreason(xmlList.getVal("Value17"));
                bean.setInvoice_amount(xmlList.getVal("Value18"));
                bean.setPONO(xmlList.getVal("Value19"));
                partialRes.add(bean);
                log.genlog("partialRes:" + partialRes);
            }

        }

        if (partialRes.isEmpty()) {
            partialRes = nodatafound();
        }

        return partialRes;
    }

    public ArrayList getDataFromDB3(String valueFromURL1, String valueFromURL2, String valueFromURL3, String PassColName1, String PassColName2, String PassColName3) throws ClassNotFoundException, SQLException, IOException {
        log.genlog("valueFromURL1:" + valueFromURL1 + "valueFromURL2:" + valueFromURL2 + "valueFromURL3:" + valueFromURL3 + "PassColName1:" + PassColName1 + "PassColName2:" + PassColName2 + "PassColName3:" + PassColName3);

        try {
            if (PassColName1.equalsIgnoreCase("PurchOrderNo") && PassColName2.equalsIgnoreCase("startInvoiceDate") && PassColName3.equalsIgnoreCase("endInvoiceDate")) {
                ColName1 = "PurchOrderNo";
                ColName2 = "Invoice_Date";
                ColName3 = "Invoice_Date";
                log.genlog("ColName1:" + ColName1 + "ColName2:" + ColName2);
            } else if (PassColName1.equalsIgnoreCase("VendorCode") && PassColName2.equalsIgnoreCase("startInvoiceDate") && PassColName3.equalsIgnoreCase("endInvoiceDate")) {
                ColName1 = "A_VendorCode";
                ColName2 = "Invoice_Date";
                ColName3 = "Invoice_Date";
            }
        } catch (Exception e) {
            log.genlog("Exception during updating the Database Coloumns::::::---anilllll");
        }
        String Query = "SELECT a.processinstanceid1, a.A_VendorCode, a.A_VendorName, a.InvoiceNo, b.activityname, a.Currency,a.Reject_comments,a.A_Barcode,a.Invoice_Date,b.previousstage,a.DueDate, a.PaymentDt, a.ProcComm, a.Grin_Hold_Comments,a.User_SPOC_Id,a.GrinHoldReason,a.GrinHoldSubReason,a.ActInvAmt,a.PONO FROM EXTDC_VEN_PROCESS a WITH(nolock) , QUEUEVIEW b WITH(nolock) WHERE " + ColName1 + "='" + valueFromURL1 + "' and " + ColName2 + " between '" + valueFromURL2 + "' and '" + valueFromURL3 + "' AND a.processinstanceid1=b.processinstanceid";        // Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");

        DMSXmlResponse xmlResponse = null;
        GetdatafromOmni obj = new GetdatafromOmni();
        DMSXmlList list = obj.getdatafromapp(Query, 19);
        DMSXmlList xmlList = null;
        ArrayList<Beans> partialRes = new ArrayList<>();
        log.genlog("list::" + list);
        if (list != null) {
            for (xmlList = list; xmlList.hasMoreElements(); xmlList.skip()) {
                Beans bean = new Beans();
                bean.setVPNumber(xmlList.getVal("Value1"));
                bean.setVendorCode(xmlList.getVal("Value2"));
                bean.setVendorName(xmlList.getVal("Value3"));
                bean.setInvoiceNo(xmlList.getVal("Value4"));
                bean.setStatus(xmlList.getVal("Value5"));
                bean.setCurrency(xmlList.getVal("Value6"));
                bean.setRejection_Comments(xmlList.getVal("Value7"));
                bean.setParkDocument_No(xmlList.getVal("Value8"));
                bean.setInvoice_Date(xmlList.getVal("Value9"));
                bean.setPreviousStage(xmlList.getVal("Value10"));
                bean.setDueDate(xmlList.getVal("Value11"));
                bean.setPaymentDate(xmlList.getVal("Value12"));
                bean.setProc_Comments(xmlList.getVal("Value13"));
                bean.setGrin_hold_commnets(xmlList.getVal("Value14"));
                bean.setUserSPOC_emailID(xmlList.getVal("Value15"));
                bean.setBucket_reason(xmlList.getVal("Value16"));
                bean.setBucket_subreason(xmlList.getVal("Value17"));
                bean.setInvoice_amount(xmlList.getVal("Value18"));
                bean.setPONO(xmlList.getVal("Value19"));
                partialRes.add(bean);
                log.genlog("partialRes:" + partialRes);
            }

        }
        if (partialRes.isEmpty()) {
            partialRes = nodatafound();
        }

        return partialRes;
    }

    public ArrayList invalidInputParameter() {
        ArrayList<InvalidInput> partialRes = new ArrayList<>();
        String invalidresult = "Invalid Input parameters";
        log.genlog("invalidresult:" + invalidresult);
        InvalidInput bean = new InvalidInput();
        bean.setInvalid_Input_Parameters(invalidresult);
        partialRes.add(bean);
        return partialRes;
    }

    public ArrayList unabletoaccessresource() {
        ArrayList<Useraccess> partialRes = new ArrayList<>();
        String useraccess = "User Cannot access the Resource";
        log.genlog("useraccess:" + useraccess);
        Useraccess bean = new Useraccess();
        bean.setUseracess(useraccess);
        partialRes.add(bean);
        return partialRes;
    }

    public ArrayList invalidUsernamePassword() {
        ArrayList<InvalidUsernamePassword> partialRes = new ArrayList<>();
        String Invalidusernameandpassword = "Invalid username and Password";
        log.genlog("invalidresult:" + Invalidusernameandpassword);
        InvalidUsernamePassword bean = new InvalidUsernamePassword();
        bean.setInvalidUsernamePassword(Invalidusernameandpassword);
        partialRes.add(bean);
        return partialRes;
    }

    public ArrayList nodatafound() {
        ArrayList<NoDataFound> partialRes = new ArrayList<>();
        String invalidresult = "Data is not available for the given input parameters";
        log.genlog("invalidresult:" + invalidresult);
        NoDataFound bean = new NoDataFound();
        bean.setNo_data_found(invalidresult);
        partialRes.add(bean);
        return partialRes;
    }

    public ArrayList invalidMethod() {
        ArrayList<InvalidMethod> partialRes = new ArrayList<>();
        String invalidresult = "Data is not available for the given this Method Use POST Method";
        log.genlog("invalidresult:" + invalidresult);
        InvalidMethod bean = new InvalidMethod();
        bean.setInvalid_Method(invalidresult);
        partialRes.add(bean);
        return partialRes;
    }

    public ArrayList generateToken() throws ClassNotFoundException, SQLException, IOException {
        ArrayList<GenarateToken> partialRes = new ArrayList<>();
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        int n = 15;
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        String Token = sb.toString();
        String Query = "insert into Tb_token(token) values('" + Token + "');";

        log.genlog(" Query Anilll:" + Query);
        try {
            data.readINIFile();
            data.connectCabinet();
            log.genlog("Query:" + Query);
            data.setData(Query);
            data.disconnectCabinet();

        } catch (Exception e) {
            log.genlog(" exception occured during inserting the data" + Query);
        }
        GenarateToken bean = new GenarateToken();
        bean.setToken(Token);
        partialRes.add(bean);
        return partialRes;

    }

    public ArrayList invalidToken() {
        ArrayList<InvalidToken> partialRes = new ArrayList<>();
        String invalidToken = "Invalid Token/Value of Token is NULL or invalid Input Parameters";
        log.genlog("invalidToken:" + invalidToken);
        InvalidToken bean = new InvalidToken();
        bean.setInvalidToken(invalidToken);
        partialRes.add(bean);
        return partialRes;
    }

}
