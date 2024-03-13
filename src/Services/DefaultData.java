package Services;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DefaultData<T> {
    private final boolean bDebug = false;

    public int Count = 0;
    public int PageId = 0;
    public int PageCount = 0;
    public int PageSize = 20;
    public List<T> Data;
    public String ClassName;

    public int getCount(){return Count;}
    public int getPageId(){return PageId;}
    public int getPageCount(){return MakePageCount(Count);}

    private int MakePageCount(int nCount){
        int nPageCount = 0;
        if (PageSize > 0){
            nPageCount = Count / PageSize;
            if (Count % PageSize > 0)
                nPageCount++;
        }
        else
            nPageCount = 0;
        return nPageCount;
    }

    public int getPageSize(){return PageSize;}
    public List<T> getData(){return Data;}
    public String getClassName(){return ClassName;}

    public DefaultData(int nPageId, int nPageSize) {
        PageId = nPageId;
        PageSize = nPageSize;
        Type superclass = getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = null;
        if (superclass instanceof ParameterizedType) {
            parameterizedType = (ParameterizedType) superclass;
            Type[] typeArray = parameterizedType.getActualTypeArguments();
            if (typeArray != null && typeArray.length > 0) {
                Class clazz = (Class) typeArray[0];
                ClassName = clazz.getName();
                Data = new ArrayList<>();
            }
        }
    }

    protected int ParseProc(CallableStatement proc, boolean bHasRet) {
        int nRet = 0;
        try {
            if (proc.execute()) {

                ResultSet rs;
                rs = proc.getResultSet();
                int nRowCount = ParseDataSet(rs);
                rs.close();
                if (proc.getMoreResults()) {
                    ResultSet rs2 = proc.getResultSet();
                    if (rs2.next()) {
                        nRowCount = rs2.getInt(1);
                    }
                    rs2.close();
                }
                Count = nRowCount;
                PageCount = MakePageCount(nRowCount);

                if (bHasRet) {
                    nRet = proc.getInt("RET");
                }
            } else {
                //没有记录集，超时
                nRet = -2;
            }
            return nRet;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return nRet;
    }

    //<editor-folder desc="private function">

    //定义DataSet->List转换时的默认值
    private final String defaultStr = "";
    private final Date defaultDate = new Date(0);
    private final BigDecimal defaultDecimal = new BigDecimal(0);
    private final Timestamp defaultTimestamp=new Timestamp(new Date(0).getTime());

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

    private int GetFieldIndex(String strName, Field[] fields) {
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals(strName))
                return i;
        }
        return -1;
    }

    private int ParseDataSet(ResultSet rs){
        try {
            if(bDebug) {
                System.out.println("byList");
                System.out.println(new Timestamp(System.currentTimeMillis()));
            }

            ResultSetMetaData metaData = rs.getMetaData();

            List<MapItem> listMap = new ArrayList<>();

            Class cls = Class.forName(ClassName);
            T t0 = (T)cls.newInstance();

            Field[] fields0 = t0.getClass().getDeclaredFields();

            for(Integer i = 0;i < metaData.getColumnCount();i ++){
                String columnName = metaData.getColumnName(i + 1);
                Integer nIndex = GetFieldIndex(columnName, fields0);
                listMap.add(new MapItem(i, nIndex));
            }
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
                                    field.set(t, defaultStr);
                                } else if (fieldClass == Timestamp.class){
                                    field.set(t, defaultTimestamp);
                                }
                            }else {
                                field.set(t, obj);
                            }
                        }catch(Exception e){
                            String strError = String.format("Error %s.%s[Object(%s)->Field(%s)]", ClassName, field.getName(),  obj.getClass().toString(), field.getType().toString());
                            if(bDebug){
                                System.out.println(strError);
                            }
                        }
                    }
                }
                Data.add(t);
                nRowCount ++;
            }
            if(bDebug) {
                System.out.println(new Timestamp(System.currentTimeMillis()));
            }
            return  nRowCount;

        }catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e){
            e.printStackTrace();
        }
        return 0;
    }

    //</editor-folder>

    //<editor-folder desc="测试代码">

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
        public SqlParameter(String Name , Integer Value){
            this.Type = Types.INTEGER;
            this.Name = Name;
            this.Value.setIntValue(Value);
            this.IsOutput = false;
        }
        public SqlParameter(String Name , Double Value){
            this.Type = Types.DOUBLE;
            this.Name = Name;
            this.Value.setDoubleValue(Value);
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
            else
                switch (this.Type){
                    case Types.INTEGER:
                        return String.format("%s(INTEGER)=%d" , this.Name, Value.getIntValue());
                    case Types.DOUBLE:
                        return String.format("%s(DOUBLE)=%f" , this.Name, Value.getDoubleValue());
                    case Types.VARCHAR:
                        return String.format("%s(VARCHAR)=%s" , this.Name, Value.getStrValue());
                    case Types.DATE:
                        return String.format("%s(DATE)=%tD %tT" , this.Name, Value.getDateValue(), Value.getDateValue());
                    default:
                        return String.format("%s(?)=[%s]" , this.Name, Value.toString());
                }
        }
    }

    private  String MakeProcDefine(String strProcedure, SqlParameter[] params, boolean bWithToken){
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

    public int GetCommListbyProc(Connection m_conn,String strTokenId, String strProcedure, SqlParameter[] params) {
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

            int nRet = ParseProc(proc, bHasToken);
            proc.close();
            return nRet;
        } catch (SQLException e) {
            e.printStackTrace();
            return -99;
        }
    }

    //</editor-folder>
}