package Services;

import Utils.DateTime;

public class DateRange {
    public DateTime Start;
    public DateTime Stop;

    public DateTime getStart(){return Start;}
    public DateTime getStop(){return Stop;}

    public java.sql.Date getSqlStart(){return new java.sql.Date(Start.getTime());}
    public java.sql.Date getSqlStop(){return new java.sql.Date(Stop.getTime());}

    public DateRange(int nMonthId, DateTime startdate, DateTime stopdate){
        DateTime dtNow = new DateTime();
        DateTime dtDefaultStart = nMonthId == 1 ?
                dtNow.AddVirtual(DateTime.enumAddType.enumDay, nMonthId - dtNow.getDay()) :
                dtNow.AddVirtual(DateTime.enumAddType.enumDay, nMonthId - dtNow.getDay()).
                        AddVirtual(DateTime.enumAddType.enumMonth, -1);
        DateTime dtDefaultStop = dtDefaultStart.AddVirtual(DateTime.enumAddType.enumMonth, 1).
                AddVirtual(DateTime.enumAddType.enumDay, -1);

        Start = IsNull(startdate , dtDefaultStart);
        Stop = IsNull(stopdate , dtDefaultStop);
    }

    public DateRange(int nMonthId, DateTime startdate, DateTime stopdate, int nAddMonths){
        DateTime dtNow = new DateTime();
        DateTime dtDefaultStart = nMonthId == 1 ?
                dtNow.AddVirtual(DateTime.enumAddType.enumDay, nMonthId - dtNow.getDay()) :
                dtNow.AddVirtual(DateTime.enumAddType.enumDay, nMonthId - dtNow.getDay()).
                        AddVirtual(DateTime.enumAddType.enumMonth, -1);
        DateTime dtDefaultStop = dtDefaultStart.AddVirtual(DateTime.enumAddType.enumMonth, 1).
                AddVirtual(DateTime.enumAddType.enumDay, -1);

        if (nAddMonths != 0)
            Start = IsNull(startdate , dtDefaultStart.AddVirtual(DateTime.enumAddType.enumMonth, nAddMonths));
        else
            Start = IsNull(startdate , dtDefaultStart);

        if (nAddMonths > 0)
            Stop = IsNull(stopdate , dtDefaultStop.AddVirtual(DateTime.enumAddType.enumMonth, nAddMonths));
        else
            Stop = IsNull(stopdate , dtDefaultStop);
    }

    public<T> T IsNull(T t, T v){
        return t == null ? v : t;
    }
}