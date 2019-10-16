package com.newgen;

import static com.newgen.GetdatafromOmni.cabinetName;
import static com.newgen.GetdatafromOmni.strUsername;
import com.newgen.dmsapi.DMSXmlList;
import com.newgen.dmsapi.DMSXmlResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import sun.misc.BASE64Decoder;

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

    @GET
    @Path("/getdata")
    @Produces(MediaType.APPLICATION_JSON)

    public ArrayList<Beans> getDatails(@Context HttpServletRequest request, @QueryParam("VPNumber") String VPNumber, @QueryParam("VendorCode") String VendorCode, @QueryParam("InvoiceNo") String InvoiceNo, @QueryParam("InvoiceDate") String InvoiceDate, @QueryParam("startInvoiceDate") String startInvoiceDate, @QueryParam("endInvoiceDate") String endInvoiceDate, @QueryParam("PurchOrderNo") String PurchOrderNo) throws ClassNotFoundException, SQLException, IOException {
        log.genlog("VPNumber: " + VPNumber + "VendorCode: " + VendorCode + "InvoiceNo: " + InvoiceNo + "startInvoiceDate : " + startInvoiceDate + "PurchOrderNo:  " + PurchOrderNo + "endInvoiceDate:  " + endInvoiceDate);
        Enumeration<String> authHeader = request.getHeaders(AUTHENTICATION_HEADER_KEY);
        log.genlog("authHeader::" + authHeader);
        log.genlog("AUTHENTICATION_HEADER_KEY::::" + AUTHENTICATION_HEADER_KEY);
        log.genlog("AUTHENTICATION_HEADER_PREFIX::::" + AUTHENTICATION_HEADER_PREFIX);
        log.genlog("authHeader::::" + authHeader);

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
            }
            log.genlog("Inside Authorization Header -----------------------" + authHeader);
            String authToken = authHeader.nextElement();
            log.genlog(authToken);
            authToken = authToken.replaceFirst(AUTHENTICATION_HEADER_PREFIX, "");
            log.genlog(authToken);
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
            log.genlog("decodedAuth::::" + decodedAuth);
            StringTokenizer tokenizer = new StringTokenizer(decodedAuth, ":");
            String username = tokenizer.nextToken();
            String password = tokenizer.nextToken();
            log.genlog("username::::" + username + "password:::" + password);
            if (username.equalsIgnoreCase(userName) && password.equalsIgnoreCase(Password)) {
                System.out.println("username::" + username + "password::" + password);
                log.genlog("Inside Returning the Servlets option::::::::::");
                log.genlog("username::::" + username + "password:::" + password);

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
                omniData = invalidUsernamePassword();
            }
        } else {
            omniData = unabletoaccessresource();
        }

        //JSONArray jsArray = new JSONArray(omniData);
        return omniData;

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
        // Statement st = db.getConnection();
        //ResultSet rs = st.executeQuery(Query);
        DMSXmlResponse xmlResponse = null;
        GetdatafromOmni obj = new GetdatafromOmni();
        DMSXmlList list = obj.getdatafromapp(Query);
        DMSXmlList xmlList = null;
        ArrayList<Beans> partialRes = new ArrayList<>();
        log.genlog("list::" + list);
        if (list != null) {
            for (xmlList = list; xmlList.hasMoreElements(); xmlList.skip()) {
                Beans bean = new Beans();
                //log.genlog("RS :" + rs.getString(1));
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
            // partialRes.add(ab);
        }
        /* while (rs.next()) {
            Beans bean = new Beans();
            log.genlog("RS :" + rs.getString(1));
            bean.setVPNumber(rs.getString(1));
            bean.setVendorCode(rs.getString(2));
            bean.setVendorName(rs.getString(3));
            bean.setInvoiceNo(rs.getString(4));
            bean.setStatus(rs.getString(5));
            bean.setCurrency(rs.getString(6));
            bean.setRejection_Comments(rs.getString(7));
            bean.setParkDocument_No(rs.getString(8));
            bean.setInvoice_Date(rs.getString(9));
            bean.setPreviousStage(rs.getString(10));
            bean.setDueDate(rs.getString(11));
            bean.setPaymentDate(rs.getString(12));
            bean.setProc_Comments(rs.getString(13));
            bean.setGrin_hold_commnets(rs.getString(14));
            bean.setUserSPOC_emailID(rs.getString(15));
            bean.setBucket_reason(rs.getString(16));
            bean.setBucket_subreason(rs.getString(17));
            bean.setInvoice_amount(rs.getString(18));
            bean.setPONO(rs.getString(19));
            partialRes.add(bean);
        }*/

        if (partialRes.isEmpty()) {
            partialRes = nodatafound();
        }
        //db.getConnection().close();
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
        // Statement st = db.getConnection();
        //ResultSet rs = st.executeQuery(Query);
        ArrayList<Beans> partialRes = new ArrayList<>();
        DMSXmlResponse xmlResponse = null;
        GetdatafromOmni obj = new GetdatafromOmni();
        DMSXmlList list = obj.getdatafromapp(Query);
        DMSXmlList xmlList = null;
        log.genlog("list::" + list);
        if (list != null) {
            for (xmlList = list; xmlList.hasMoreElements(); xmlList.skip()) {
                Beans bean = new Beans();
                //log.genlog("RS :" + rs.getString(1));
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
            // partialRes.add(ab);
        }

        if (partialRes.isEmpty()) {
            partialRes = nodatafound();
        }
        // db.getConnection().close();
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
        Statement st = db.getConnection();
        ResultSet rs = st.executeQuery(Query);
        DMSXmlResponse xmlResponse = null;
        GetdatafromOmni obj = new GetdatafromOmni();
        DMSXmlList list = obj.getdatafromapp(Query);
        DMSXmlList xmlList = null;
        ArrayList<Beans> partialRes = new ArrayList<>();
        log.genlog("list::" + list);
        if (list != null) {
            for (xmlList = list; xmlList.hasMoreElements(); xmlList.skip()) {
                Beans bean = new Beans();
                //log.genlog("RS :" + rs.getString(1));
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
            // partialRes.add(ab);
        }
        if (partialRes.isEmpty()) {
            partialRes = nodatafound();
        }
        //db.getConnection().close();
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

}
