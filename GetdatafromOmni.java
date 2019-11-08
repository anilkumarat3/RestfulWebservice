package com.newgen;

import com.newgen.dmsapi.DMSCallBroker;
import com.newgen.dmsapi.DMSXmlList;
import com.newgen.dmsapi.DMSXmlResponse;
import com.newgen.omni.jts.txn.mssql.odsap.NGOSAPQuery;
import com.newgen.omni.wf.util.app.NGEjbClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * ***********************************************************************************************************
 * NEWGEN SOFTWARE TECHNOLOGIES LIMITED
 *
 * Group	: SDC Module	: API For ChatBoot File Name	: OmniData.java Author	: Anil
 * Kumar A Date written	: 23/07/2019
 * ***********************************************************************************************************
 */
public class GetdatafromOmni {

    public static String cabinetName = "";
    public static String strUsername = "";
    public static String strPassword = "";
    public static String strServerIp = "";
    public static String strServerPort = "";
    public static String jtsport = "";
    public static String strLogPath = "";
    public static String userdbid = "";
    public static String strFilePath = "";
    public static String DocName = "";
    public static String jts = null;
    public DMSXmlList xmlList = null;
    NGEjbClient ejbObj;
    GenerateLog log = new GenerateLog();

    public DMSXmlList getdatafromapp(String Query, int count) throws IOException {
        log.genlog("Inside reading the data from omniflow application-------------");
        readINIFile();
        connectCabinet();
        xmlList = getdat(Query, count);
        //disconnectCabinet();
        return xmlList;
    }

    public void readINIFile() throws FileNotFoundException, IOException {
        log.genlog("Inside Reading the INI file");
        String strIniPath = System.getProperty("user.dir") + File.separator + "DBProperties.ini";
        //String strIniPath = "E:\\Anil\\NetbeansProjects\\WebAPI\\Dbfile.ini";

        File ini = new File(strIniPath);
        try {
            if (ini.exists()) {
                Properties property = new Properties();
                FileInputStream fInputStream = new FileInputStream(ini);
                property.load(fInputStream);
                fInputStream.close();
                cabinetName = property.getProperty("CabinetName");
                strUsername = property.getProperty("UserName");
                strPassword = property.getProperty("Password");
                strServerIp = property.getProperty("JbossIp");
                jtsport = property.getProperty("JbossPort");
                strFilePath = property.getProperty("strFilePath");
                DocName = property.getProperty("DocName");
                log.genlog("cabinetName: " + cabinetName + "  strUsername: " + strUsername + "  strPassword:  " + strPassword + "  strServerIp:  " + strServerIp + "  jtsport:  " + jtsport);
                log.genlog("strServerIp:::" + strServerIp);
                log.genlog("jtsport::" + jtsport);
            }
        } catch (Exception e) {
            log.genlog("Exception occured during reaing INI file-------------");
        }
    }

    public DMSXmlList getdat(String Query, int count) {
        String strInputXml = "";
        String strOutputXml = "";
        DMSXmlResponse xmlResponse = null;
        log.genlog("Query::::" + Query);
        // log.genlog("sQuery:::" + Query);
        strOutputXml = getdata(Query, count);
        xmlResponse = new DMSXmlResponse(strOutputXml);
        log.genlog("xmlResponse  MainCode:::::" + xmlResponse.getVal("MainCode"));
        if (xmlResponse.getVal("MainCode").equals("0")) {
            log.genlog("entered into if loop....");
            int i = 1;
            xmlList = xmlResponse.createList("DataList", "Data");
        }

        return xmlList;
    }

    public String getdata(String sQuery, int Column) {
        log.genlog("into getdata()");
        String strOutputXml = "";
        String strInputXml = "";
        try {
            ejbObj = NGEjbClient.getSharedInstance();
            //ejbObj.initialize(strJbossIp, strJbossPort, "JBoss");
            strInputXml = "<?xml version=\"1.0\"?>";
            strInputXml = (new StringBuilder()).append(strInputXml).append("<WFCustomBean_Input><Option>NGGetData</Option>").toString();
            strInputXml = (new StringBuilder()).append(strInputXml).append("<QryOption>NGGetData</QryOption>").toString();
            strInputXml = (new StringBuilder()).append(strInputXml).append("<EngineName>").append(cabinetName).append("</EngineName>").toString();
            strInputXml = (new StringBuilder()).append(strInputXml).append("<SessionId>").append(userdbid).append("</SessionId>").toString();
            strInputXml = (new StringBuilder()).append(strInputXml).append("<QueryString>").append(sQuery).append("</QueryString>").toString();
            strInputXml = (new StringBuilder()).append(strInputXml).append("<ColumnNo>").append(Column).append("</ColumnNo>").toString();
            strInputXml = (new StringBuilder()).append(strInputXml).append("</WFCustomBean_Input>").toString();
            log.genlog("strInputXml:::" + strInputXml);
            //ejbObj.initialize(strJbossIp, strJbossPort, "jboss");
            //strOutputXml = ejbObj.makeCall(strInputXml);
            // strOutputXml = DMSCallBroker.execute(strInputXml, jtsport, 3333, 0);
            strOutputXml = DMSCallBroker.execute(strInputXml.toString(), strServerIp, 3333, 0);
            log.genlog("strOutputXml getData():" + strOutputXml);
        } catch (Exception e) {
            log.genlog("Exception Occured During getting data from database");
        }
        return strOutputXml;
    }

    public void connectCabinet() {
        log.genlog("Inside the connecting the cabinet------");
        StringBuffer strBuffer = new StringBuffer();
        DMSXmlResponse xmlResponse = null;

        try {
            strBuffer.append("<?xml version=1.0?>");
            strBuffer.append("<NGOConnectCabinet_Input>");
            strBuffer.append("<Option>NGOConnectCabinet</Option>");
            strBuffer.append("<CabinetName>" + cabinetName + "</CabinetName>");
            strBuffer.append("<UserName>" + strUsername + "</UserName>");
            strBuffer.append("<UserPassword>" + strPassword + "</UserPassword>");
            strBuffer.append("<UserExist>N</UserExist>");
            strBuffer.append("<UserType>U</UserType>");
            strBuffer.append("<NGOConnectCabinet_Input>");
            //log.genlog("Input XML for Connecting the Cabinet::::::" + strBuffer.toString());
            String strOutputXml = DMSCallBroker.execute(strBuffer.toString(), strServerIp, 3333, 0);
            xmlResponse = new DMSXmlResponse(strOutputXml);
            // log.genlog("OutPut XML for Connecting the cabinet::::::" + strOutputXml);
            int Status = Integer.parseInt(xmlResponse.getVal("status"));
            if (xmlResponse.getVal("status").equals("0")) {
                userdbid = xmlResponse.getVal("UserDBId");
                log.genlog("sessionId" + userdbid);
                log.genlog("********Cabinet  connected succesfully********");
            } else {
                log.genlog("Cabinet  connection failed");
            }
        } catch (Exception e) {
            log.genlog("Exception occured during Connecting the Cabinet" + e);
        }

    }

    public String NGIGetDatas(String sbQry, int iColCnt) {
        String result = "";
        try {
            String sInputXml = null;
            DMSXmlResponse xmlResponse = null;
            DMSXmlList xmlList;
            sInputXml = ("<?xml version=\"1.0\"?>");
            sInputXml = sInputXml + ("<WFCustomBean_Input><Option>NGGetData</Option>");
            sInputXml = sInputXml + ("<QryOption>NGGetData</QryOption>");
            sInputXml = sInputXml + ("<EngineName>" + cabinetName + "</EngineName>");
            sInputXml = sInputXml + ("<SessionId>" + userdbid + "</SessionId>");
            sInputXml = sInputXml + ("<QueryString>" + sbQry.toString() + "</QueryString>");
            sInputXml = sInputXml + ("<ColumnNo>" + iColCnt + "</ColumnNo>");
            sInputXml = sInputXml + ("</WFCustomBean_Input>");
            log.genlog("sInputXml------" + sInputXml);
            log.genlog("Input XML for NGGetdata ::::::" + sInputXml.toString());
            String strOutputXml = DMSCallBroker.execute(sInputXml.toString(), strServerIp, 3333, 0);
            xmlResponse = new DMSXmlResponse(strOutputXml);
            log.genlog("OutPut XML for NGGetdata :::::" + strOutputXml);
            if (xmlResponse.getVal("MainCode").equals("0")) {
                log.genlog("inside if loop");
                xmlList = xmlResponse.createList("DataList", "Data");
                log.genlog("xmlList:" + xmlList);
                for (; xmlList.hasMoreElements(); xmlList.skip()) //if(xmlListScoa.hasMoreElements())
                {
                    result = xmlList.getVal("Value1");
                    log.genlog("result::::" + result);
                }

            } else {
                log.genlog(" failed");
            }
        } catch (Exception e) {
            log.genlog("Exception occured " + e);
        }
        return result;

    }

    public void setData(String sQuery) {
        String strOutputXml = "";
        String strInputXml = "";
        try {
            String sInputXml = null;
            DMSXmlResponse xmlResponse = null;
            strInputXml = "<?xml version=\"1.0\"?>";
            strInputXml = strInputXml + "<WFCustomBean_Input><Option>IGSetData</Option>";
            strInputXml = strInputXml + "<QryOption>IGSetData</QryOption>";
            strInputXml = strInputXml + "<EngineName>" + cabinetName + "</EngineName>";
            strInputXml = strInputXml + "<SessionId>" + userdbid + "</SessionId>";
            strInputXml = strInputXml + "<Query>" + sQuery + "</Query>";
            strInputXml = strInputXml + "</WFCustomBean_Input>";
            log.genlog("sInputXml------" + strInputXml);
            log.genlog("Input XML for ngsetdata ::::::" + strInputXml.toString());
            String sOutXml = DMSCallBroker.execute(strInputXml.toString(), strServerIp, 3333, 0);
            xmlResponse = new DMSXmlResponse(sOutXml);
            log.genlog("OutPut XML for ngsetdata :::::" + sOutXml);
            if (xmlResponse.getVal("MainCode").equals("0")) {
                log.genlog("Executed sucusfully::::");
            } else {
                log.genlog("failed");
            }
        } catch (Exception e) {
            log.genlog("Exception in ExecuteSelectQry : " + e);
        }
    }

    public void disconnectCabinet() {
        log.genlog("Inside disconnecting the cabinet-------------");
        StringBuffer strBuffer = new StringBuffer();
        DMSXmlResponse xmlResponse = null;
        String strOutputXml = "";
        try {
            strBuffer.append("<?xml version=\"1.0\"?>");
            strBuffer.append("<NGODisconnectCabinet_Input>");
            strBuffer.append("<Option>NGODisconnectCabinet</Option>");
            strBuffer.append("<CabinetName>" + cabinetName + "</CabinetName>");
            strBuffer.append("<UserDBId>" + userdbid + "</UserDBId>");
            strBuffer.append("</NGODisconnectCabinet_Input>");
            log.genlog("Input XML for Dissconnecting the Cabinet----" + strBuffer.toString());
            strOutputXml = DMSCallBroker.execute(strBuffer.toString(), strServerIp, 3333, 0);
            log.genlog("Output XML for Dissconnecting the Cabinet-----" + strOutputXml);
            xmlResponse = new DMSXmlResponse(strOutputXml);
            if (xmlResponse.getVal("Status").equals("0")) {
                log.genlog("********cabinet disconnected successfully********");
            } else {
                log.genlog("error in disconnecting the cabinet");
            }
        } catch (Exception e) {
            log.genlog("Error in disconnecting the cabinet::::::" + e);
        }
    }

}
