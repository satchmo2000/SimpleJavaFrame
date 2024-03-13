package Models.Entities;

import java.util.List;

public class DefaultPageDataItem<T> extends DefaultViewItem {
    public Integer PID;
    public Integer TypeId;
    public String Key;
    public String StartDate;
    public String StopDate;

    public int SelectCompanyId;
    public int SelectUserId;

    public List<DictionaryItem> ParamList;

    public Integer Param1;
    public Integer Param2;

    public List<T> Data;
    public T DataSum;

    public Integer getPID(){return PID;}
    public Integer getTypeId(){return TypeId;}
    public String getKey(){return Key;}
    public String getStartDate(){return StartDate;}
    public String getStopDate(){return StopDate;}

    public int getSelectCompanyId(){return SelectCompanyId;}
    public int getSelectUserId(){return SelectUserId;}

    public List<DictionaryItem> getParamList(){return ParamList;}

    public Integer getParam1(){return Param1;}
    public Integer getParam2(){return Param2;}

    public List<T> getData(){return Data;}
    public T getDataSum(){return DataSum;}

    public DefaultPageDataItem(Integer nCount, Integer nPage, Integer nCountPerPage) {
        super(nCount, nPage, nCountPerPage);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Count=%d,Data=%s", Count, Data.toString()));
        return sb.toString();
    }

}
