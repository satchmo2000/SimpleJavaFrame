package Services;

import Models.Entities.DictionaryItem;

import java.util.List;

public class DefaultPageData<T> extends DefaultData<T> {
    public Integer PID;
    public Integer TypeId;
    public String Key;
    public String StartDate;
    public String StopDate;

    public List<DictionaryItem> ParamList;

    public Integer Param1;
    public Integer Param2;

    public Integer getPID(){return PID;}
    public Integer getTypeId(){return TypeId;}
    public String getKey(){return Key;}
    public String getStartDate(){return StartDate;}
    public String getStopDate(){return StopDate;}

    public List<DictionaryItem> getParamList(){return ParamList;}

    public Integer getParam1(){return Param1;}
    public Integer getParam2(){return Param2;}

    public DefaultPageData(int nPageId, int nPageSize){
        super(nPageId, nPageSize);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Count=%d,Data=%s", Count, Data.toString()));
        return sb.toString();
    }
}