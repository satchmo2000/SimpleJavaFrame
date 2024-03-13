package Services;

import java.sql.Date;

public class Result {

    private int nErrorCode;
    private int nValue;
    private double dValue;
    private boolean bValue;
    private Date dtValue;
    private String strValue;

    public int getErrorCode(){return nErrorCode;}

    public void setErrorCode(int nErrorCode){this.nErrorCode = nErrorCode;}

    public int getIntValue(){return  nValue;}

    public void setIntValue(int nValue){this.nValue = nValue;}

    public double getDoubleValue(){return  dValue;}

    public void setDoubleValue(double dValue){this.dValue = dValue;}
    public boolean getBoolValue(){return bValue;}
    public void setBoolValue(boolean bValue){this.bValue = bValue;}
    public Date getDateValue(){return  dtValue;}

    public void setDateValue(Date dtValue){this.dtValue = dtValue;}

    public String getStrValue(){return IsNull(strValue, "");}

    public void setStrValue(String strValue){this.strValue = strValue;}

    @Override
    public String toString(){
        return String.format("ErrorCode=%d,Int=%d,Double=%f,Bool=%b,Str=%s", nErrorCode, nValue, dValue, bValue, strValue);
    }

    public ClientServicesX.enumErrorCode toErrorCode(){
        return ClientServicesX.enumErrorCode.FromInt(nErrorCode);
    }

    public<T> T IsNull(T t, T v){
        return t == null ? v : t;
    }
}