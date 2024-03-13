package Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTime extends Date {

    public enum enumAddType{
        enumYear(1) ,
        enumMonth(2) ,
        enumWeekofYear(3) ,
        enumWeekofMonth(4) ,
        enumDay(5),
        enumHour(11) ,
        enumMinute(12),
        enumSecond(13),
        enumMilliSecond(14);

        private int nValue = 0;

        enumAddType(int i) {
            this.nValue = i;
        }

        public int getInt() {
            return nValue;
        }

        public static enumAddType FromInt(int nInt) {
            for (enumAddType type : values()) {
                if (type.getInt() == nInt)
                    return type;
            }
            return enumAddType.enumDay;
        }

    }

    private Calendar calendar = Calendar.getInstance();

    public DateTime(){
        super();
        calendar.setTime(this);
    }

    public DateTime(int nYear, int nMonth, int nDay){
        //相对1900年进行初始化日期，月份（0-11）
        super(new Date(nYear - 1900, nMonth - 1, nDay).getTime());
        calendar.setTime(this);
    }

    public DateTime(String strDefault) throws ParseException {
        super(new SimpleDateFormat("yyyy-MM-dd").parse(strDefault).getTime());
        calendar.setTime(this);
    }

    public DateTime(Date dt){
        super(dt.getTime());
        calendar.setTime(this);
    }

    public DateTime Add(enumAddType enumType, Integer nAdd){
        calendar.add(enumType.getInt(), nAdd);
        Date dt = calendar.getTime();
        return new DateTime(dt);
    }

    public DateTime AddVirtual(enumAddType enumType, Integer nAdd){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this);
        calendar.add(enumType.getInt(), nAdd);
        return new DateTime(calendar.getTime());
    }

    public int get(enumAddType enumType){
        return calendar.get(enumType.getInt());
    }

    public Date toTime(){
        return new Date(this.getTime());
    }

    public Date toDatePart() {
        try {
            String strDatePart = toDateString("yyyy-MM-dd");
            return new DateTime(strDatePart).toTime();
        }catch (ParseException e){
            return this.toTime();
        }
    }

    public java.sql.Date toSqlTime(){
        return new java.sql.Date(this.getTime());
    }

    public String toDateString(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(this);
    }

    public String toDateString(String strFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
        return sdf.format(this);
    }

    public static Date MinValue(){
        try {
            String time = "1900-1-1";
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.parse(time);
        }catch(ParseException e){
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * 获取两个时间之间的毫秒数
     * @param date1
     * @param date2
     * @return
     */
    public static long getTimeMilliseconds(Date date1, Date date2){
        long rtnLong = 0;
        //判断两个参数是否为空，为空直接返回
        if (date1 == null || date1 == null) {
            return rtnLong;
        }
        //使用第一个参数减去第二个参数
        rtnLong = date1.getTime() - date2.getTime();
        //如果结果小于0
        if (rtnLong < 0){
            //再使用第二个参数减去第一个参数
            rtnLong = date2.getTime() - date1.getTime();
        }
        return rtnLong;
    }

    public static double getTimeDays(Date date1, Date date2){
        long tms = getTimeMilliseconds(date1, date2);
        return tms / 1000 / 3600 / 24;
    }
}
