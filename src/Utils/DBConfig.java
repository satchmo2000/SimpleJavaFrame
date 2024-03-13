package Utils;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
public class DBConfig {
    private static String dbServer;
    private static String dbName;
    private static String dbUser;
    private static String dbPwd;

    //所有参数集
    private static final Map<String, String> mapAttrs = new HashMap<>();

    //WEB-INF/classes/DBConfig.xml

    public void readXML(){
        String strXmlFile = "DBConfig.xml";
        SAXReader sr = new SAXReader();//获取读取xml的对象。
        Document doc = null;
        //取相对于/WEB-INF/classes/的位置
        URL urlXml = Thread.currentThread().getContextClassLoader().getResource(strXmlFile);
        //String pathWebInf = String.valueOf(urlXml);

        try {
            doc = sr.read(urlXml);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }//得到xml所在位置。然后开始读取。并将数据放入doc中
        Element el_root = Objects.requireNonNull(doc).getRootElement();//向外取数据，获取xml的根节点。
        Iterator it = el_root.elementIterator();//从根节点下依次遍历，获取根节点下所有子节点

        while(it.hasNext()){//遍历该子节点

            //propertys
            Object o = it.next();//再获取该子节点下的子节点
            Element el_row = (Element)o;
            //String s = el_row.getText();
            Iterator it_row = el_row.elementIterator();

            while(it_row.hasNext()){//遍历节点
                //property
                Element el_ename = (Element)it_row.next();//获取该节点下的所有数据。

                String strName = el_ename.attribute("name").getValue();
                String strValue = el_ename.attribute("value").getValue();
                mapAttrs.put(strName, strValue);

                if(strName.equalsIgnoreCase("Server")){
                    this.setDbServer(strValue);
                }else if(strName.equalsIgnoreCase("DB")){
                    this.setDbName(strValue);
                }else if(strName.equalsIgnoreCase("User")){
                    this.setDbUser(strValue);
                }else if(strName.equalsIgnoreCase("Password")){
                    this.setDbPwd(strValue);
                }
            }
        }

    }
    static {
        DBConfig dbxml = new DBConfig();
        dbxml.readXML();
/*
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
 */
    }

    public static Connection createConn(){
        DBConfig dbxml = new DBConfig();
        //String name = dbxml.dbName;
        //System.out.println(name);
        //System.out.println(dbxml.getDbServer() + dbxml.getDbName() + dbxml.getDbUser() + dbxml.getDbPwd());
        Connection conn = null;
        try {
            String strConnectionString = String.format("jdbc:sqlserver://%s:1433/%s?user=%s&password=%s" ,
                    dbxml.getDbServer(),
                    dbxml.getDbName(),
                    dbxml.getDbUser(),
                    dbxml.getDbPwd());

            conn = DriverManager.getConnection(strConnectionString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static PreparedStatement createPstmt(Connection conn, String sql){
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pstmt;
    }

    public static void close(Connection conn){
        if(conn == null)return;
        try {
            conn.close();
            conn = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(Statement stmt){
        try {
            stmt.close();
            stmt = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(ResultSet rs){
        try {
            rs.close();
            rs = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public  String getConnection(){
        return String.format("jdbc:sqlserver://%s:1433;databaseName=%s;integratedSecurity=false;",
                dbServer ,
                dbName);
    }
    public String getDbName() {
        return dbName;
    }
    public void setDbName(String dbName) {
        DBConfig.dbName = dbName;
    }
    public String getDbServer() {
        return dbServer;
    }
    public String getDbUser() {
        return dbUser;
    }
    public String getDbPwd() {
        return dbPwd;
    }
    public void setDbServer(String dbServer) {
        DBConfig.dbServer = dbServer;
    }
    public void setDbUser(String dbUser) {
        DBConfig.dbUser = dbUser;
    }
    public void setDbPwd(String dbPwd) {
        DBConfig.dbPwd = dbPwd;
    }

    public String getProperty(String strName){
        return getProperty(strName, "");
    }

    public String getProperty(String strName, String strDefault){
        String strValue = mapAttrs.get(strName);
        if(strValue == null || strValue.length() == 0)
            return  strDefault;
        else
            return  strValue;
    }
}