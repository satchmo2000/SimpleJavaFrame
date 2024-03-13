package Services;

import Utils.DBConfig;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/*
    相较于C#,Java的烦点：属性值、枚举值及类型转换、输出与引用参数，
    两个Integer对象比较的是地址，所以不能直接用“==”进行比较，而是用a.equals(b)或用 a == b.intValue()进行比较
    存储过程不支持默认参数，同时输入参数必须有值并且与定义相同顺序（可以为null）
    存储过程中执行insert、update语句，导致该语句之后的结果集无法被获取
    泛化变量不能直接被实例化或获取类名称（原因：编译时不确定类名），类中类标识为static才能通过类名来实例化，变通方法如下：
        1、传递类名，通过类名来实现泛化变量的实例化
        2、另外参见UserDefaultTest，避免传递类名称
    记录集处理消耗时间：
    其中， 获取字段定义（参数定义+字段定义）：10秒，
        数据转换（Map模式）（字段映射：15毫秒，数据变换：100毫秒）
        数据转换（List模式）：字段映射：2毫秒，数据变换：30毫秒

    兼容问题：SpringMVC与Tomcat10不兼容的解决办法：把Tomcat10的servlet-api.jar导入到项目的lib

    注意事项：通过Project Setting/Libraries加载的lib或自定义配置文件，需要手动加到输出目录中（Artifacts内操作）

    form.post乱码问题（来源：https://blog.csdn.net/weixin_39634022/article/details/114134028）
        1、js内encodeURI()、decodeURI()                    方法中包含的URI()的编码和解码不会对本身属于URI的特殊字符进行编码，例如冒号等；
        2、js内encodeURIComponent()、decodeURIComponent()  方法中包含URICompent()的编码和解码则会对它发现的任何非标准字符进行编码进行编码；
        3、Java内new String (strData.getBytes("iso8859-1"),"UTF-8");
        4、Tomcat内，更改Tomact的conf目录下的server.xml文件。
            <Connector port=... redirectPort="8443" URIEncoding="UTF-8"/>               不推荐，因为更改了服务器且并不灵活
            <Connector port=... redirectPort="8443" useBodyEncodingForURI="UTF-8"/>     不推荐，request的setCharacterEncoding设置什么编码，连接器就用什么编码，虽然比上一种更改灵活，但依然会导致我们的应用程序牢牢依赖于服务器

    <script type=... src=...></script>  双标签必须完整，否则导致脚本不执行
 */

//ClientServices数据库连接及核心基类(L1)
public class ClientServicesBase {
    private boolean bDebug = false;

    private final String defaultStr = "";
    private final Date defaultDate = new Date(0);
    private final BigDecimal defaultDecimal = new BigDecimal(0);
    private final Timestamp defaultTimestamp=new Timestamp(new Date(0).getTime());

    public enum enumDBType{
        enumSQLServer(0) ,
        enumOracle(1) ,
        enumMySql(2) ;

        private int nValue = 0;

        enumDBType(int i) {
            this.nValue = i;
        }

        public int getInt() {
            return nValue;
        }

        public static enumDBType FromInt(int nInt) {
            for (enumDBType type : values()) {
                if (type.getInt() == nInt)
                    return type;
            }
            return enumDBType.enumSQLServer;
        }
    }

    //用于记录集与结果List之间的字段映射
    private static class MapItem {
        //记录集索引
        public Integer Key;
        //结果List的字段位置（不存在=-1）
        public Integer Value;

        public MapItem(Integer Key, Integer Value){
            this.Key = Key;
            this.Value = Value;
        }
    }

    private static class ProcAttrItem {
        public String Name;

        //56:整形，61：日期，108：小数，93：字符串
        public Integer FieldType;
        public boolean IsOutput;
        @Override
        public String toString(){
            return "[" + FieldType.toString() + "]" + this.Name + (this.IsOutput ? "(output)" : "");
        }
    }

    public static class SqlParameter{
        public Integer Type;
        public String Name;
        Result Value = new Result();
        boolean IsOutput;

        public SqlParameter(String Name , String Value){
            this.Type = Types.VARCHAR;
            this.Name = Name;
            this.Value.setStrValue(Value);
            this.IsOutput = false;
        }
        public SqlParameter(String Name , Boolean Value){
            this.Type = Types.INTEGER;
            this.Name = Name;
            this.Value.setIntValue(Value ? 1 : 0);
            this.IsOutput = false;
        }
        public SqlParameter(String Name , Integer Value){
            this.Type = Types.INTEGER;
            this.Name = Name;
            this.Value.setIntValue(Value);
            this.IsOutput = false;
        }
        public SqlParameter(String Name , Long Value){
            this.Type = Types.INTEGER;
            this.Name = Name;
            this.Value.setIntValue(Value.intValue());
            this.IsOutput = false;
        }
        public SqlParameter(String Name , Double Value){
            this.Type = Types.DOUBLE;
            this.Name = Name;
            this.Value.setDoubleValue(Value);
            this.IsOutput = false;
        }
        public SqlParameter(String Name , BigDecimal Value){
            this.Type = Types.DECIMAL;
            this.Name = Name;
            this.Value.setDoubleValue(Value.doubleValue());
            this.IsOutput = false;
        }
        public SqlParameter(String Name , Date Value){
            this.Type = Types.DATE;
            this.Name = Name;
            this.Value.setDateValue(Value);
            this.IsOutput = false;
        }

        public SqlParameter(String Name , Integer Type, boolean IsOutput){
            this.Type = Type;
            this.Name = Name;
            this.IsOutput = IsOutput;
        }

        @Override
        public String toString(){
            if(this.IsOutput){
                switch (this.Type){
                    case Types.INTEGER:
                        return String.format("%s(INTEGER,output)=%d" , this.Name, Value.getIntValue());
                    case Types.DOUBLE:
                        return String.format("%s(DOUBLE,output)=%f" , this.Name, Value.getDoubleValue());
                    case Types.VARCHAR:
                        return String.format("%s(VARCHAR,output)=%s" , this.Name, Value.getStrValue());
                    case Types.DATE:
                        return String.format("%s(DATE,output)=%tD %tT" , this.Name, Value.getDateValue(), Value.getDateValue());
                    default:
                        return String.format("%s(?,output)=[%s]" , this.Name, Value.toString());
                }
            }
            else {
                switch (this.Type) {
                    case Types.INTEGER:
                        return String.format("%s(INTEGER)=%d", this.Name, Value.getIntValue());
                    case Types.DOUBLE:
                        return String.format("%s(DOUBLE)=%f", this.Name, Value.getDoubleValue());
                    case Types.VARCHAR:
                        return String.format("%s(VARCHAR)=%s", this.Name, Value.getStrValue());
                    case Types.DATE:
                        return String.format("%s(DATE)=%tD %tT", this.Name, Value.getDateValue(), Value.getDateValue());
                    default:
                        return String.format("%s(?)=[%s]", this.Name, Value.toString());
                }
            }
        }
    }

    protected String m_strDriverName;
    protected String m_strConnectionFormat;
    protected Connection m_conn;
    protected String m_strConnection;
    protected String m_strUser;
    protected String m_strPassword;
    protected String m_strTokenId;
    protected String m_strEncodingTokenId;
    protected String m_strLoginDate;
    protected String m_strIP;
    protected Integer m_nPort;

    //strConnectionString = "jdbc:sqlserver://<server>:<port>;databaseName=<database>;integratedSecurity=false;";
    public  ClientServicesBase(String strTokenId){
        m_strDriverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        m_strConnectionFormat = "jdbc:sqlserver://%s:1433;databaseName=%s;integratedSecurity=false;sslProtocol=TLSv1;";

        DBConfig dbxml = new DBConfig();
        m_strConnection = String.format(m_strConnectionFormat ,
                dbxml.getDbServer(),
                dbxml.getDbName());

        m_strUser = dbxml.getDbUser();
        m_strPassword = dbxml.getDbPwd();
        m_strTokenId = strTokenId;
        m_strEncodingTokenId = "";
        m_strIP = "127.0.0.1";
        m_nPort = 0;
    }

    public ClientServicesBase(String strConnection, String strUser, String strPassword) {
        m_strDriverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        m_strConnectionFormat = "jdbc:sqlserver://%s:1433;databaseName=%s;integratedSecurity=false;sslProtocol=TLSv1;";

        m_strConnection = strConnection;
        m_strUser = strUser;
        m_strPassword = strPassword;
        m_strTokenId = "";
        m_strEncodingTokenId = "";
        m_strIP = "127.0.0.1";
        m_nPort = 0;
    }

    public ClientServicesBase(String strTokenId, String strConnection, String strUser, String strPassword) {
        m_strDriverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        m_strConnectionFormat = "jdbc:sqlserver://%s:1433;databaseName=%s;integratedSecurity=false;sslProtocol=TLSv1;";

        m_strConnection = strConnection;
        m_strUser = strUser;
        m_strPassword = strPassword;
        m_strTokenId = strTokenId;
        m_strEncodingTokenId = "";
        m_strIP = "127.0.0.1";
        m_nPort = 0;
    }

    public void SelectDBType(enumDBType enumType){
        switch (enumType){
            case enumOracle:
                m_strDriverName = "oracle.jdbc.driver.OracleDriver";
                m_strConnectionFormat = "jdbc:oracle:thin:@%s:1521:%s";
                break;
            case enumMySql:
                m_strDriverName = "com.mysql.cj.jdbc.Driver";
                m_strConnectionFormat = "jdbc:mysql://%s:3306/%s";
                break;
            default:
                m_strDriverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                m_strConnectionFormat = "jdbc:sqlserver://%s:1433;databaseName=%s;integratedSecurity=false;sslProtocol=TLSv1;";
                break;
        }

        DBConfig dbxml = new DBConfig();
        m_strConnection = String.format(m_strConnectionFormat ,
                dbxml.getDbServer(),
                dbxml.getDbName());
    }

    public void setIPPort(String strIP, Integer nPort){
        m_strIP = strIP;
        m_nPort = nPort;
    }

    public String getIP(){return this.m_strIP;}

    public Integer getPort(){return this.m_nPort;}

    public String getTokenId(){return this.m_strTokenId;}
    public String getEncodingTokenId(){return this.m_strEncodingTokenId;}
    public String getLoginDate(){return this.m_strLoginDate;}

    public void setTokenId(String strTokenId){m_strTokenId = strTokenId;}

    //访问低版本SQLServer（不支持TLS12/13），客户端不支持TLS10，相应的解决办法如下：
    //locate java.security（apt install mlocate）
    //jdk.tls.disableAlgorithms，删除TLSv1,TLSv1.1,3DES_EDE_CBC（大概在737行）
    public Boolean CheckDB(Result outValue) {
        try {
            //检测对应的驱动lib是否在输出lib里，手动导入的sqljdbc42无法自动进入输出lib里
            Class.forName(m_strDriverName);
            m_conn = DriverManager.getConnection(m_strConnection, m_strUser, m_strPassword);
            if (!m_conn.isClosed()) {
                m_conn.close();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            outValue.setStrValue(e.getMessage());
        } catch(Exception e){
            e.printStackTrace();
            outValue.setStrValue(e.getMessage());
        }
        return false;
    }

    protected Boolean ConnectDB() {
        try {
            //检测对应的驱动lib是否在输出lib里，手动导入的sqljdbc42无法自动进入输出lib里
            Class.forName(m_strDriverName);
            m_conn = DriverManager.getConnection(m_strConnection, m_strUser, m_strPassword);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    protected Boolean CloseDB() {
        if (m_conn != null) {
            try {
                if (!m_conn.isClosed()) {
                    m_conn.close();
                }
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public void setDebug(boolean bDebug){
        this.bDebug = bDebug;
    }

    //<editor-folder desc="public boolean Get[SQL|Proc]Define(String strSql, boolean bForJava, Result outValue)">

    //根据SQL获取记录集的字段定义
    public boolean GetSQLDefine(String strSql, boolean bForJava, Result outValue) {
        if (ConnectDB()) {
            try {
                Statement stmt = m_conn.createStatement();
                ResultSet rs = stmt.executeQuery(strSql);
                boolean bRet = GetDefineFromResultSet("ClassName", rs, outValue, bForJava);
                rs.close();
                stmt.close();
                CloseDB();
                return bRet;

            } catch (SQLException e) {
                System.out.println(String.format("Exception by SQL=%s", strSql));
                e.printStackTrace();
            }
        }
        System.out.println(String.format("Error to Connect db for SQL %s", strSql));
        return false;
    }

    //根据存储过程的执行获取记录集的字段定义
    //存储过程的基本格式：Create ProcedureName（@TOKENID VARCHAR(36),...@RET INT OUTPUT)
    public boolean GetProcDefine(String strProcedure, boolean bForJava, Result outValue) {
        return GetProcDefine(m_strTokenId, strProcedure, bForJava, outValue);
    }

    public boolean GetProcDefine(String strTokenId, String strProcedure, boolean bForJava, Result outValue) {
        if (ConnectDB()) {
            try {
                List<ProcAttrItem> listAttrs = GetProcedureAttr(strProcedure, outValue, false);

                StringBuilder sb = new StringBuilder();
                if(listAttrs != null) {
                    boolean bFirst = true;
                    for (ProcAttrItem pai : listAttrs) {
                        if (bFirst)
                            bFirst = false;
                        else
                            sb.append(",");
                        sb.append(String.format("%s=?", pai.Name));
                    }
                }

                String strParam = sb.toString();
                String strProc = String.format("{call %s(%s)}", strProcedure, strParam);
                CallableStatement proc = m_conn.prepareCall(strProc);
                boolean bHasRet = false;
                if(listAttrs != null) {
                    for (ProcAttrItem pai : listAttrs) {
                        String strAttrName = pai.Name;
                        if (strAttrName.startsWith("@")) strAttrName = pai.Name.substring(1);
                        if (strAttrName.equalsIgnoreCase("TOKENID"))
                            proc.setString(strAttrName, strTokenId);
                        else {
                            if(strAttrName.equalsIgnoreCase("RET")){
                                bHasRet = true;
                            }
                            if (pai.FieldType == 56) {
                                //整形
                                proc.setInt(strAttrName, 0);
                            } else if (pai.FieldType == 108) {
                                //小数
                                proc.setDouble(strAttrName, 0);
                            } else if (pai.FieldType == 61) {
                                //日期
                                proc.setDate(strAttrName, null);
                            } else {
                                //字符串
                                proc.setString(strAttrName, null);
                            }
                            if (pai.IsOutput) {
                                if (pai.FieldType == 56)
                                    proc.registerOutParameter(strAttrName, Types.INTEGER);
                                else if (pai.FieldType == 108)
                                    proc.registerOutParameter(strAttrName, Types.DECIMAL);
                                else if (pai.FieldType == 61)
                                    proc.registerOutParameter(strAttrName, Types.DATE);
                                else
                                    proc.registerOutParameter(strAttrName, Types.VARCHAR);
                            }
                        }
                    }
                }
                boolean bRet = false;
                if(proc.execute()) {
                    ResultSet rs = proc.executeQuery();
                    bRet = GetDefineFromResultSet(strProcedure, rs, outValue, bForJava);
                    rs.close();
                }
                else{
                    int nRet = 0;
                    if(bHasRet){
                        nRet = proc.getInt("RET");
                        outValue.setErrorCode(nRet);
                    }
                    else {
                        outValue.setErrorCode(-101);
                    }
                }
                proc.close();
                CloseDB();

                return bRet;

            } catch (SQLException e) {
                System.out.println(String.format("Exception by Procedure=%s", strProcedure));
                e.printStackTrace();
                outValue.setErrorCode(-99);
            }
        }
        System.out.println(String.format("Error to Connect db for Procedure %s", strProcedure));
        return false;
    }
    //</editor-folder>

    //<editor-folder desc="public <T> List<T> GetCommListby[SQL|Proc]([String strTokenId,] String strSql, String strClassName, [Integer nId|SqlParameter[] params|], Result outValue)">

    //<editor-folder desc="List<T> Model">

    //根据SQL获取记录集，并存于List<T>中，字段一一对应，状态存于outValue中
    //strClassName = T.getClass().getName()
    public <T> List<T> GetCommListbySQL(String strSql, String strClassName, Result outValue) {
        if (ConnectDB()) {
            try {
                Statement stmt = m_conn.createStatement();
                ResultSet rs = stmt.executeQuery(strSql);

                List<T> arrList = MakeResultSetList(strClassName, rs, outValue);
                rs.close();
                stmt.close();

                outValue.setErrorCode(0);
                CloseDB();
                return arrList;
            } catch (SQLException e) {
                System.out.println(String.format("Exception by SQL=%s", strSql));
                e.printStackTrace();
                outValue.setErrorCode(-99);
            }
            return null;
        }
        System.out.println(String.format("Error to Connect db for SQL %s", strSql));
        outValue.setErrorCode(-1);
        return null;
    }

    public <T> List<T> GetCommListbySQL(String strSql, String strClassName, SqlParameter[] params, Result outValue) {
        if (ConnectDB()) {
            try {
                PreparedStatement pstmt = m_conn.prepareStatement(strSql);
                if(params != null){
                    Integer nIndex = 1;
                    for(SqlParameter param : params){
                        if (param.Type == Types.INTEGER)
                            pstmt.setInt(nIndex, param.Value.getIntValue());
                        else if (param.Type == Types.DOUBLE)
                            pstmt.setDouble(nIndex, param.Value.getDoubleValue());
                        else if (param.Type == Types.DATE)
                            pstmt.setDate(nIndex, param.Value.getDateValue());
                        else
                            pstmt.setString(nIndex, param.Value.getStrValue());

                        nIndex ++;
                    }
                }

                ResultSet rs = pstmt.executeQuery();
                List<T> arrList = MakeResultSetList(strClassName, rs, outValue);
                rs.close();
                pstmt.close();

                outValue.setErrorCode(0);
                CloseDB();
                return arrList;
            } catch (SQLException e) {
                System.out.println(String.format("Exception by SQL=%s", strSql));
                e.printStackTrace();
                outValue.setErrorCode(-99);
            }
            return null;
        }
        System.out.println(String.format("Error to Connect db for SQL %s", strSql));
        outValue.setErrorCode(-1);
        return null;
    }

    //根据存储过程获取记录集，并存于List<T>中，字段一一对应，状态RET存于outValue中
    //存储过程的基本格式：Create ProcedureName（@TOKENID VARCHAR(36),...@RET INT OUTPUT)
    //strClassName = T.getClass().getName()
    public <T> List<T> GetCommListbyProcNoTokenId(String strProcedure, String strClassName, Result outValue) {
        return GetCommListbyProc("", strProcedure, strClassName, new SqlParameter[]{}, outValue);
    }

    public <T> List<T> GetCommListbyProcNoTokenId(String strProcedure, String strClassName, Integer nId, Result outValue) {
        return GetCommListbyProc("", strProcedure, strClassName , new SqlParameter[]{new SqlParameter("ID", nId)}, outValue);
    }

    public <T> List<T> GetCommListbyProcNoTokenId(String strProcedure, String strClassName, SqlParameter[] params, Result outValue) {
        return GetCommListbyProc("", strProcedure, strClassName, params, outValue);
    }

    public <T> List<T> GetCommListbyProc(String strProcedure, String strClassName, Result outValue) {
        if(m_strTokenId.isEmpty()){
            outValue.setIntValue(-2);
            return null;
        }
        else
            return GetCommListbyProc(m_strTokenId, strProcedure, strClassName, new SqlParameter[]{}, outValue);
    }

    public <T> List<T> GetCommListbyProc(String strProcedure, String strClassName, Integer nId, Result outValue) {
        if(m_strTokenId.isEmpty()){
            outValue.setIntValue(-2);
            return null;
        }
        else
            return GetCommListbyProc(m_strTokenId, strProcedure, strClassName , new SqlParameter[]{new SqlParameter("ID", nId)}, outValue);
    }

    public <T> List<T> GetCommListbyProc(String strProcedure, String strClassName, SqlParameter[] params, Result outValue) {
        if(m_strTokenId.isEmpty()){
            outValue.setIntValue(-2);
            return null;
        }
        else
            return GetCommListbyProc(m_strTokenId, strProcedure, strClassName, params, outValue);
    }

    public <T> List<T> GetCommListbyProc(String strTokenId, String strProcedure, String strClassName, Result outValue) {
        if(m_strTokenId.isEmpty()){
            outValue.setIntValue(-2);
            return null;
        }
        else
            return GetCommListbyProc(strTokenId, strProcedure, strClassName , new SqlParameter[]{}, outValue);
    }

    public <T> List<T> GetCommListbyProc(String strTokenId, String strProcedure, String strClassName, Integer nId, Result outValue) {
        if(m_strTokenId.isEmpty()){
            outValue.setIntValue(-2);
            return null;
        }
        else
            return GetCommListbyProc(strTokenId, strProcedure, strClassName , new SqlParameter[]{new SqlParameter("ID", nId)}, outValue);
    }

    public <T> List<T> GetCommListbyProc(String strTokenId, String strProcedure, String strClassName, SqlParameter[] params, Result outValue) {
        if (ConnectDB()) {
            try {
                boolean bHasToken = strTokenId.length() > 0;
                String strProc = MakeProcDefine(strProcedure , params , bHasToken);
                CallableStatement proc = m_conn.prepareCall(strProc);

                Result outAttr = new Result();
                List<ProcAttrItem> listAttrs = new ArrayList<ProcAttrItem>();
                if(bDebug) {
                    listAttrs =GetProcedureAttr(strProcedure, outAttr, true);
                }
                int nCheckIndex = 0;

                if(bHasToken) {
                    proc.setString("TOKENID", strTokenId);
                    nCheckIndex ++;
                }

                if(params != null) {
                    for (SqlParameter param : params) {
                        if(bDebug && nCheckIndex < listAttrs.stream().count()){
                            ProcAttrItem pai = listAttrs.get(nCheckIndex);
                            if(!param.Name.equalsIgnoreCase(pai.Name)){
                                System.out.println(String.format("%s from %s is not ordered,the corrected name is %s",
                                        param.Name,
                                        strProcedure,
                                        pai.Name));
                            }
                        }
                        if (param.Type == Types.INTEGER)
                            proc.setInt(param.Name, param.Value.getIntValue());
                        else if (param.Type == Types.DOUBLE)
                            proc.setDouble(param.Name, param.Value.getDoubleValue());
                        else if (param.Type == Types.DATE)
                            proc.setDate(param.Name, param.Value.getDateValue());
                        else
                            proc.setString(param.Name, param.Value.getStrValue());
                        nCheckIndex ++;
                    }
                }
                if(bHasToken) {
                    proc.registerOutParameter("RET", Types.INTEGER);
                }
                proc.registerOutParameter(1, Types.INTEGER);

                List<T> arrList = new ArrayList<>();

                if(proc.execute()) {
                    ResultSet rs = proc.getResultSet();

                    arrList = MakeResultSetList(strClassName, rs, outValue);
                    rs.close();

                    int nRowCount = outValue.getIntValue();
                    if (proc.getMoreResults()) {
                        ResultSet rs2 = proc.getResultSet();
                        if (rs2.next()) {
                            nRowCount = rs2.getInt(1);
                        }
                        rs2.close();
                    }
                    outValue.setIntValue(nRowCount);

                    int nRet = 0;
                    if(bHasToken) {
                        nRet = proc.getInt("RET");
                    }

                    outValue.setErrorCode(nRet);
                }
                else{
                    //没有记录集，超时

                    int nRet = 0;
                    if(bHasToken) {
                        nRet = proc.getInt("RET");
                    }
                    if(nRet < 0)
                        outValue.setErrorCode(nRet);
                    else
                        outValue.setErrorCode(-101);
                }
                proc.close();
                CloseDB();
                return arrList;
            } catch (SQLException e) {
                System.out.println(String.format("Exception by Procedure=%s", strProcedure));
                e.printStackTrace();
                outValue.setErrorCode(-99);
            }
            return null;
        }
        System.out.println(String.format("Error to Connect db for Procedure %s", strProcedure));
        outValue.setErrorCode(-1);
        return null;
    }

    //</editor-folder>

    //<editor-folder desc="DefaultData Model">

    public int GetCommListbyProc(String strProcedure, DefaultData data) {
        return GetCommListbyProc(m_strTokenId, strProcedure, new SqlParameter[]{}, data);
    }

    public int GetCommListbyProc(String strProcedure, Integer nId, DefaultData data) {
        return GetCommListbyProc(m_strTokenId, strProcedure, new SqlParameter[]{new SqlParameter("ID", nId)}, data);
    }

    public int GetCommListbyProc(String strProcedure, SqlParameter[] params, DefaultData data) {
        return GetCommListbyProc(m_strTokenId, strProcedure, params, data);
    }

    public int GetCommListbyProc(String strTokenId, String strProcedure, DefaultData data) {
        return GetCommListbyProc(strTokenId, strProcedure, new SqlParameter[]{}, data);
    }

    public int GetCommListbyProc(String strTokenId, String strProcedure, Integer nId, DefaultData data) {
        return GetCommListbyProc(strTokenId, strProcedure, new SqlParameter[]{new SqlParameter("ID", nId)}, data);
    }

    public int GetCommListbyProc(String strTokenId, String strProcedure, SqlParameter[] params, DefaultData data){
        if (ConnectDB()) {
            try {
                boolean bHasToken = strTokenId.length() > 0;
                String strProc = MakeProcDefine(strProcedure , params , bHasToken);
                CallableStatement proc = m_conn.prepareCall(strProc);
                if(bHasToken) {
                    proc.setString("TOKENID", strTokenId);
                }

                if(params != null) {
                    for (SqlParameter param : params) {
                        if (param.Type == Types.INTEGER)
                            proc.setInt(param.Name, param.Value.getIntValue());
                        else if (param.Type == Types.DOUBLE)
                            proc.setDouble(param.Name, param.Value.getDoubleValue());
                        else if (param.Type == Types.DATE)
                            proc.setDate(param.Name, param.Value.getDateValue());
                        else
                            proc.setString(param.Name, param.Value.getStrValue());
                    }
                }
                if(bHasToken) {
                    proc.registerOutParameter("RET", Types.INTEGER);
                }
                proc.registerOutParameter(1, Types.INTEGER);

                int nRet = data.ParseProc(proc, bHasToken);

                proc.close();
                CloseDB();

                return nRet;
            } catch (SQLException e) {
                System.out.println(String.format("Exception by Procedure=%s", strProcedure));
                e.printStackTrace();
                return -99;
            }
        }
        return -1;
    }

    //</editor-folder>

    //</editor-folder>

    //<editor-folder desc="public Integer ExecCommandby[SQL|Proc]([String strTokenId, ]String strProcedure, [Integer nId|SqlParameter[] params|])">

    public Integer ExecCommandbySQL(String strSql) {
        if (ConnectDB()) {
            try {
                Statement stmt = m_conn.createStatement();
                Boolean bRet = stmt.execute(strSql);
                stmt.close();
                CloseDB();
                return bRet ? 0 : -2;
            } catch (SQLException e) {
                System.out.println(String.format("Exception by SQL=%s", strSql));
                e.printStackTrace();
                return -99;
            }
        }
        System.out.println(String.format("Error to Connect db for SQL %s", strSql));
        return -1;
    }

    public Integer ExecCommandbySQL(String strSql, SqlParameter[] params) {
        if (ConnectDB()) {
            try {
                PreparedStatement pstmt = m_conn.prepareStatement(strSql);
                if(params != null){
                    Integer nIndex = 1;
                    for(SqlParameter param : params){
                        if (param.Type == Types.INTEGER)
                            pstmt.setInt(nIndex, param.Value.getIntValue());
                        else if (param.Type == Types.DOUBLE)
                            pstmt.setDouble(nIndex, param.Value.getDoubleValue());
                        else if (param.Type == Types.DATE)
                            pstmt.setDate(nIndex, param.Value.getDateValue());
                        else
                            pstmt.setString(nIndex, param.Value.getStrValue());

                        nIndex ++;
                    }
                }

                int nCount = pstmt.executeUpdate();
                System.out.println("Rows affected: " + nCount);
                pstmt.close();
                CloseDB();
                return 0;
            } catch (SQLException e) {
                System.out.println(String.format("Exception by SQL=%s", strSql));
                e.printStackTrace();
                return -99;
            }
        }
        System.out.println(String.format("Error to Connect db for SQL %s", strSql));
        return -1;
    }

    //执行存储过程（无返回记录集），返回RET值，其他输出变量存于params中
    //存储过程的基本格式：Create ProcedureName（@TOKENID VARCHAR(36),...@RET INT OUTPUT)
    public Integer ExecCommandbyProcNoTokenId(String strProcedure) {
        return ExecCommandbyProc("EmptyTokenId", strProcedure, new SqlParameter[]{});
    }

    public Integer ExecCommandbyProcNoTokenId(String strProcedure, Integer nId){
        return ExecCommandbyProc("EmptyTokenId" , strProcedure, nId);
    }

    public Integer ExecCommandbyProcNoTokenId(String strProcedure, SqlParameter[] params) {
        return ExecCommandbyProc("EmptyTokenId", strProcedure, params);
    }

    public Integer ExecCommandbyProc(String strProcedure) {
        return ExecCommandbyProc(m_strTokenId, strProcedure, new SqlParameter[]{});
    }

    public Integer ExecCommandbyProc(String strProcedure, Integer nId){
        return ExecCommandbyProc(m_strTokenId , strProcedure, nId);
    }

    public Integer ExecCommandbyProc(String strProcedure, SqlParameter[] params) {
        return ExecCommandbyProc(m_strTokenId, strProcedure, params);
    }

    public Integer ExecCommandbyProc(String strTokenId, String strProcedure) {
        return ExecCommandbyProc(strTokenId, strProcedure, new SqlParameter[]{});
    }

    public Integer ExecCommandbyProc(String strTokenId, String strProcedure, Integer nId) {
        return ExecCommandbyProc(strTokenId, strProcedure, new SqlParameter[]{new SqlParameter("ID", nId)});
    }

    public Integer ExecCommandbyProc(String strTokenId, String strProcedure, SqlParameter[] params) {
        if (ConnectDB()) {
            try {
                boolean bHasToken = strTokenId.length() > 0;
                String strProc = MakeProcDefine(strProcedure , params , bHasToken);
                CallableStatement proc = m_conn.prepareCall(strProc);
                if(bHasToken) {
                    proc.setString("TOKENID", strTokenId);
                }

                if(params != null) {
                    for (SqlParameter param : params) {
                        if (param.IsOutput) {
                            //输出
                            if (param.Type == Types.INTEGER)
                                proc.registerOutParameter(param.Name, Types.INTEGER);
                            else if (param.Type == Types.DOUBLE)
                                proc.registerOutParameter(param.Name, Types.DOUBLE);
                            else if (param.Type == Types.DATE)
                                proc.registerOutParameter(param.Name, Types.DATE);
                            else
                                proc.registerOutParameter(param.Name, Types.VARCHAR);
                        } else {
                            //输入
                            if (param.Type == Types.INTEGER)
                                proc.setInt(param.Name, param.Value.getIntValue());
                            else if (param.Type == Types.DOUBLE)
                                proc.setDouble(param.Name, param.Value.getDoubleValue());
                            else if (param.Type == Types.DATE)
                                proc.setDate(param.Name, param.Value.getDateValue());
                            else
                                proc.setString(param.Name, param.Value.getStrValue());
                        }
                    }
                }

                if(bHasToken) {
                    proc.registerOutParameter("RET", Types.INTEGER);
                }
                proc.registerOutParameter(1, Types.INTEGER);

                proc.execute();
                int nRet = bHasToken ? proc.getInt("RET") : 0;

                if(params != null) {
                    for (SqlParameter param : params) {
                        if (param.IsOutput) {
                            if(param.Name.equalsIgnoreCase("RET")){
                                nRet = proc.getInt(param.Name);
                            }
                            else if (param.Type == Types.INTEGER) {
                                int nInt = proc.getInt(param.Name);
                                param.Value.setIntValue(nInt);
                            } else if (param.Type == Types.DOUBLE) {
                                Double dDbl = proc.getDouble(param.Name);
                                param.Value.setDoubleValue(dDbl);
                            } else if (param.Type == Types.DATE) {
                                Date dDt = proc.getDate(param.Name);
                                param.Value.setDateValue(dDt);
                            } else {
                                String strStr = proc.getString(param.Name);
                                param.Value.setStrValue(strStr);
                            }
                        }
                    }
                }

                proc.close();
                CloseDB();
                return nRet;
            } catch (SQLException e) {
                //检查存储过程的属于是否完备
                Result outValue = new Result();
                List<ProcAttrItem> listAttrs = GetProcedureAttr(strProcedure, outValue, true);
                if(outValue.getIntValue() > 0 && params != null) {
                    Integer nLostCount = 0;
                    StringBuilder sb = new StringBuilder();
                    for (ProcAttrItem pai : listAttrs) {
                        if(pai.Name.equalsIgnoreCase("TOKENID") ||
                            pai.Name.equalsIgnoreCase("RET"))
                            continue;
                        else if(FindSqlParameterIndex(params, pai.Name) < 0){
                            if(nLostCount > 0)
                                sb.append(",");
                            sb.append(pai.Name);
                            nLostCount ++;
                        }
                    }
                    if(nLostCount > 0){
                        System.out.println(String.format("Lost SqlParameter:%s", sb.toString()));
                    }
                }
                e.printStackTrace();
            }
        }
        System.out.println(String.format("Error to Connect db for Procedure %s", strProcedure));
        return -1;
    }

    //</editor-folder>

    //<editor-folder desc="private function">

    //从SqlParameter[]中找到指定参数名称的索引号
    private Integer FindSqlParameterIndex(SqlParameter[] params, String strName){
        Integer nIndex = 0;
        for(SqlParameter param : params){
            if(param.Name.equalsIgnoreCase(strName))
                return nIndex;
            else
                nIndex++;
        }
        return -1;
    }

    //返回存储过程的参数列表，参数个数存入outValue
    private List<ProcAttrItem> GetProcedureAttr(String strProcedure, Result outValue, boolean bRemoveAt) {
        List<ProcAttrItem> list = new ArrayList<>();
        try {
            Statement stmt = m_conn.createStatement();
            String strSql = String.format("select name,user_type_id,is_output from sys.parameters where object_id=object_id('%s')", strProcedure);
            ResultSet rs = stmt.executeQuery(strSql);
            while (rs.next()) {
                ProcAttrItem pai = new ProcAttrItem();
                pai.Name = rs.getString(1);
                pai.FieldType = rs.getInt(2);
                pai.IsOutput = rs.getInt(3) == 1;
                if(bRemoveAt && pai.Name.startsWith("@")){
                    pai.Name = pai.Name.substring(1);
                }
                list.add(pai);
            }
            rs.close();
            stmt.close();
            outValue.setIntValue(list.size());
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        outValue.setIntValue(-1);
        return null;
    }

    private  String MakeProcDefine(String strProcedure, SqlParameter[] params,boolean bWithToken){
        StringBuilder sb = new StringBuilder();
        boolean bFirst = true;
        if(bWithToken) {
            sb.append("@TOKENID=?");
            bFirst = false;
        }

        boolean bExistRetParam = false;
        if(params != null) {
            for (SqlParameter param : params) {
                if(param.Name.equalsIgnoreCase("RET")){
                    bExistRetParam = true;
                }
                if(bFirst)
                    bFirst = false;
                else
                    sb.append(",");
                sb.append(String.format("@%s=?", param.Name));
            }
        }

        if(bWithToken && !bExistRetParam) {
            sb.append(",@RET=?");
        }

        String strParam = sb.toString();
        return String.format("{?=call %s(%s)}", strProcedure, strParam);
    }

    private int GetFieldIndex(String strName, Field[] fields) {
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals(strName))
                return i;
        }
        return -1;
    }

    //将记录集返回至List<T>中，记录条数存于outValue中
    //className=T.getClass().getName()
    private <T> List<T> MakeResultSetList(String className, ResultSet rs, Result outValue) {
        try {
            if(bDebug) {
                System.out.println("byList");
                System.out.println(new Timestamp(System.currentTimeMillis()));
            }

            ResultSetMetaData metaData = rs.getMetaData();

            List<MapItem> listMap = new ArrayList<>();

            Class cls = Class.forName(className);
            T t0 = (T)cls.newInstance();
            //Class<?> cls0 = loadClass(className);

            Field[] fields0 = t0.getClass().getDeclaredFields();

            for(Integer i = 0;i < metaData.getColumnCount();i ++){
                String columnName = metaData.getColumnName(i + 1);
                Integer nIndex = GetFieldIndex(columnName, fields0);
                listMap.add(new MapItem(i, nIndex));
            }
            List<T> list = new ArrayList<>();
            Integer nRowCount = 0;
            while(rs.next()){
                T t = (T)cls.newInstance();
                Field[] fields = t.getClass().getDeclaredFields();
                for (MapItem mi : listMap) {
                    Integer nColumnIndex = mi.Key;
                    Integer nFieldIndex = mi.Value;
                    if (nFieldIndex >= 0) {
                        Field field = fields[nFieldIndex];
                        field.setAccessible(true);
                        Object obj= rs.getObject(nColumnIndex + 1);
                        try {
                            if(obj == null){
                                Class fieldClass=field.getType();
                                if (fieldClass == Integer.class ) {
                                    field.set(t, defaultDecimal.intValue());
                                }else if (fieldClass == Long.class) {
                                    field.set(t, defaultDecimal.longValue());
                                }else if (fieldClass == Float.class) {
                                    field.set(t, defaultDecimal.doubleValue());
                                }else if (fieldClass == BigDecimal.class) {
                                    field.set(t, defaultDecimal);
                                } else if (fieldClass == Date.class) {
                                    field.set(t, defaultDate);
                                } else if (fieldClass == String.class){
                                    field.set(t, defaultStr); // 设置值
                                } else if (fieldClass == Timestamp.class){
                                    field.set(t, defaultTimestamp);
                                }
                            }else {
                                field.set(t, obj);
                            }
                        }catch(Exception e){
                            String strError = String.format("Error %s.%s[Object(%s)->Field(%s)]", className, field.getName(),  obj.getClass().toString(), field.getType().toString());
                            if(bDebug){
                                System.out.println(strError);
                            }
                        }
                    }
                }
                list.add(t);
                nRowCount ++;
            }
            outValue.setIntValue(nRowCount);
            if(bDebug) {
                System.out.println(new Timestamp(System.currentTimeMillis()));
            }
            return  list;

        }catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e){
            e.printStackTrace();
        }
        return null;
    }

    //功能=MakeResultSetList，保留代码（相对于MakeResultSetList，未对空值进行适应性处理）
    private  <T> List<T> MakeResultSetListbyMap(String className, ResultSet rs, Result outValue) {
        try {
            if(bDebug) {
                System.out.println("byMap");
                System.out.println(new Timestamp(System.currentTimeMillis()));
            }

            ResultSetMetaData metaData = rs.getMetaData();

            Map<Integer, Integer> map = new HashMap<>();

            Class cls = Class.forName(className);
            T t0 = (T) cls.newInstance();
            Field[] fields0 = t0.getClass().getDeclaredFields();

            for (Integer i = 0; i < metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i + 1);
                Integer nIndex = GetFieldIndex(columnName, fields0);
                map.put(i, nIndex);
            }
            if(bDebug) {
                System.out.println(new Timestamp(System.currentTimeMillis()));
            }
            List<T> list = new ArrayList<>();
            Integer nRowCount = 0;
            while (rs.next()) {
                T t = (T) cls.newInstance();
                Field[] fields = t.getClass().getDeclaredFields();
                for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                    Integer nColumnIndex = entry.getKey();
                    //Integer nColumnType = metaData.getColumnType(nColumnIndex + 1);
                    Integer nFieldIndex = entry.getValue();
                    if (nFieldIndex >= 0) {
                        Field f = fields[nFieldIndex];
                        f.setAccessible(true);
                        f.set(t, rs.getObject(nColumnIndex + 1));
                    }
                }
                list.add(t);
                nRowCount++;
            }
            outValue.setIntValue(nRowCount);

            return list;

        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    //根据记录集获取字段定义，存入outValue
    private boolean GetDefineFromResultSet(String className, ResultSet rs, Result outValue, boolean bForJava){
        try {
            ResultSetMetaData metaData = rs.getMetaData();

            StringBuilder sbMeta = new StringBuilder();
            sbMeta.append(String.format("public class %s{\n", className));

            int nCount = metaData.getColumnCount();
            boolean bExistId = false;
            boolean bExistName = false;
            for (int i = 0; i < nCount; i++) {
                String columnName = metaData.getColumnName(i + 1);
                if(columnName.equalsIgnoreCase("ID"))
                    bExistId = true;
                else if(columnName.equalsIgnoreCase("NAME"))
                    bExistName = true;
                int columnType = metaData.getColumnType(i + 1);
                String columnTypeName = metaData.getColumnTypeName(i + 1);
                String strLine = "\tpublic ";
                switch (columnType) {
                    case 2:
                        strLine += bForJava ? "BigDecimal" : "decimal";
                        break;
                    case 3: //decimal
                        strLine += bForJava ? "BigDecimal" : "decimal";
                        break;
                    case 4:
                        strLine += bForJava ? "Integer" : "int";
                        break;
                    case -5: //bigint
                        strLine += bForJava ? "Long" : "long";
                        break;
                    case -9: //nvarchar
                        strLine += bForJava ? "String" : "string";
                        break;
                    case 12:
                        strLine += bForJava ? "String" : "string";
                        break;
                    case 93:
                        strLine += bForJava ? "Date" : "datetime";
                        break;
                    default:
                        strLine += String.format("[%s=%d]", columnTypeName, columnType);
                        break;
                }
                if(bForJava)
                    strLine += String.format(" %s;\n", columnName);
                else
                    strLine += String.format(" %s { get; set; }\n", columnName);
                sbMeta.append(strLine);
            }
            if(bForJava){
                sbMeta.append("\n");
                for (int i = 0; i < nCount; i++) {
                    String columnName = metaData.getColumnName(i + 1);
                    int columnType = metaData.getColumnType(i + 1);
                    String columnTypeName = metaData.getColumnTypeName(i + 1);
                    String strLine = "\tpublic ";
                    String strLine2 = "\tpublic ";
                    switch (columnType) {
                        case 2:
                            strLine += bForJava ? "BigDecimal" : "decimal";
                            strLine2 += bForJava ? "Double" : "decimal";
                            break;
                        case 3:
                            strLine += bForJava ? "BigDecimal" : "decimal";
                            strLine2 += bForJava ? "Double" : "decimal";
                            break;
                        case 4:
                            strLine += bForJava ? "Integer" : "int";
                            strLine2 += bForJava ? "Integer" : "int";
                            break;
                        case -5:
                            strLine += bForJava ? "Long" : "long";
                            strLine2 += bForJava ? "Long" : "long";
                            break;
                        case -9:
                            strLine += bForJava ? "String" : "string";
                            strLine2 += bForJava ? "String" : "string";
                            break;
                        case 12:
                            strLine += bForJava ? "String" : "string";
                            strLine2 += bForJava ? "String" : "string";
                            break;
                        case 93:
                            strLine += bForJava ? "Date" : "datetime";
                            strLine2 += bForJava ? "Date" : "datetime";
                            break;
                        default:
                            strLine += String.format("[%s=%d]", columnTypeName, columnType);
                            strLine2 += String.format("[%s=%d]", columnTypeName, columnType);
                            break;
                    }
                    strLine += String.format(" get%s(){return %s;}\n", columnName, columnName);
                    sbMeta.append(strLine);
                    if(columnType == 2) {
                        strLine2 += String.format(" get%sDouble(){return %s == null ? null : %s.doubleValue();}\n", columnName, columnName, columnName);
                        sbMeta.append(strLine2);
                    }
                }
            }
            if(bExistId || bExistName){
                sbMeta.append("\n");
                sbMeta.append("\t@Override\n");
                sbMeta.append("\tpublic String toString(){\n");
            }
            if(bExistName && bExistId){
                sbMeta.append("\t\treturn String.format(\"%s(id=%d)\", NAME, ID);\n");
            }else if(bExistName){
                sbMeta.append("\t\treturn String.format(\"name=%s\", NAME);\n");
            }else if(bExistId){
                sbMeta.append("\t\treturn String.format(\"id=%d\", ID);\n");
            }
            if(bExistId || bExistName) {
                sbMeta.append("\t}\n");
            }
            sbMeta.append("}\n");
            outValue.setStrValue(sbMeta.toString());
            if(bDebug) {
                System.out.println(sbMeta);
            }
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    //</editor-folder>
}
