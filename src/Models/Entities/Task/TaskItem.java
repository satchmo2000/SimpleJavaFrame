package Models.Entities.Task;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;

public class TaskItem implements Comparable<TaskItem> {
    public Integer ID;
    public Integer PID;
    public String UNICODE;
    public String NAME;
    public Date CREATEDATE;
    public Date PLANDATE;
    public BigDecimal PLANTIME;
    public Integer ISTEST;
    public Integer ISLOCK;
    public Integer ISAUTOSTOP;
    public Integer MEASUREMETHOD;
    public Integer OFFSET_DAY;
    public Integer OFFSET_PLAN;
    public Integer OFFSET_HOUR;
    public Integer OFFSET_WEEKID;
    public Integer OFFSET_MONTHID;
    public Date STARTDATE;
    public Date STOPDATE;
    public Integer COMPANYID;
    public String COMPANYNAME;
    public Integer USERID;
    public String USERNAME;
    public String LINKPHONE;
    public String EMAIL;
    public Integer APPLYUSER;
    public String APPLYUSERNAME;
    public Integer TYPEID;
    public String TYPENAME;
    public Integer PROCESSID;
    public Integer STATUSID;
    public String STATUSNAME;
    public Integer STATUSID_T;
    public String STATUSNAME_T;
    public Integer ISTRIAL;
    public Integer ISCONSTRUCT;
    public BigDecimal NUM;
    public BigDecimal PRICE;
    public BigDecimal TOTAL;
    public BigDecimal MINRATE;
    public BigDecimal MAXRATE;
    public BigDecimal WEIGHT;
    public BigDecimal VALUE;
    public BigDecimal RESULT;
    public Integer TIID;
    public String TINAME;
    public String RIGHTUSER;
    public String MEMO;
    public String ICOPATH;
    public Integer AREAID;
    public String AREANAME;
    public Integer ISOWNER;
    public Integer EST0COUNT;
    public String TASKPATH;
    public String ESTIMEDESC;
    public BigDecimal ESTIME;
    public BigDecimal SPENDTIME;
    public BigDecimal FINISHEDTIME;
    public BigDecimal KEEPTIME;
    public BigDecimal DELAYTIME;
    public BigDecimal DIFFTIME;
    public BigDecimal WORKRATE;
    public Integer DOCCOUNT;

    public BigDecimal AVGRATE;
    public BigDecimal TACHNICSTOTAL;
    public BigDecimal TACHNICSINPUTTOTAL;
    public BigDecimal INCOMETOTAL;
    public BigDecimal DEGOFF;
    public Integer X1;
    public Integer Y1;
    public Integer X2;
    public Integer Y2;
    public BigDecimal MEASURE_X1;
    public BigDecimal MEASURE_Y1;
    public BigDecimal MEASURE_X2;
    public BigDecimal MEASURE_Y2;
    public BigDecimal GPS_X;
    public BigDecimal GPS_Y;
    public BigDecimal GPS_X1;
    public BigDecimal GPS_Y1;
    public BigDecimal GPS_X2;
    public BigDecimal GPS_Y2;
    public BigDecimal ADJUST_X;
    public BigDecimal ADJUST_Y;
    public BigDecimal ADJUST_Z;
    public String ATTR_1;
    public String ATTR_2;
    public String ATTR_3;
    public String ATTR_4;
    public String ATTR_5;
    public String ATTR_6;
    public String ATTR_7;
    public String ATTR_8;
    public String ATTR_9;
    public String ATTR_10;
    public String ATTR_11;
    public String ATTR_12;
    public String ATTR_13;
    public BigDecimal PARAM_1;
    public BigDecimal PARAM_2;
    public BigDecimal PARAM_3;
    public BigDecimal PARAM_4;

    public Integer getID(){return ID;}
    public Integer getPID(){return PID;}
    public String getUNICODE(){return UNICODE;}
    public String getNAME(){return NAME;}
    public Date getCREATEDATE(){return CREATEDATE;}
    public Date getPLANDATE(){return PLANDATE;}
    public Double getPLANTIMEDouble(){return PLANTIME == null ? null : PLANTIME.doubleValue();}
    public BigDecimal getPLANTIME(){return PLANTIME;}
    public Integer getISTEST(){return ISTEST;}
    public Integer getISLOCK(){return ISLOCK;}
    public Integer getISAUTOSTOP(){return ISAUTOSTOP;}
    public Integer getMEASUREMETHOD(){return MEASUREMETHOD;}
    public Integer getOFFSET_DAY(){return OFFSET_DAY;}
    public Integer getOFFSET_PLAN(){return OFFSET_PLAN;}
    public Integer getOFFSET_HOUR(){return OFFSET_HOUR;}
    public Integer getOFFSET_WEEKID(){return OFFSET_WEEKID;}
    public Integer getOFFSET_MONTHID(){return OFFSET_MONTHID;}
    public Date getSTARTDATE(){return STARTDATE;}
    public Date getSTOPDATE(){return STOPDATE;}
    public Integer getCOMPANYID(){return COMPANYID;}
    public String getCOMPANYNAME(){return COMPANYNAME;}
    public Integer getUSERID(){return USERID;}
    public String getUSERNAME(){return USERNAME;}
    public String getLINKPHONE(){return LINKPHONE;}
    public String getEMAIL(){return EMAIL;}
    public Integer getAPPLYUSER(){return APPLYUSER;}
    public String getAPPLYUSERNAME(){return APPLYUSERNAME;}
    public Integer getTYPEID(){return TYPEID;}
    public String getTYPENAME(){return TYPENAME;}
    public Integer getPROCESSID(){return PROCESSID;}
    public Integer getSTATUSID(){return STATUSID;}
    public String getSTATUSNAME(){return STATUSNAME;}
    public Integer getSTATUSID_T(){return STATUSID_T;}
    public String getSTATUSNAME_T(){return STATUSNAME_T;}
    public Integer getISTRIAL(){return ISTRIAL;}
    public Integer getISCONSTRUCT(){return ISCONSTRUCT;}
    public BigDecimal getNUM(){return NUM;}
    public Double getNUMDouble(){return NUM == null ? null : NUM.doubleValue();}
    public BigDecimal getPRICE(){return PRICE;}
    public Double getPRICEDouble(){return PRICE == null ? null : PRICE.doubleValue();}
    public BigDecimal getTOTAL(){return TOTAL;}
    public Double getTOTALDouble(){return TOTAL == null ? null : TOTAL.doubleValue();}
    public BigDecimal getMINRATE(){return MINRATE;}
    public Double getMINRATEDouble(){return MINRATE == null ? null : MINRATE.doubleValue();}
    public BigDecimal getMAXRATE(){return MAXRATE;}
    public Double getMAXRATEDouble(){return MAXRATE == null ? null : MAXRATE.doubleValue();}
    public BigDecimal getWEIGHT(){return WEIGHT;}
    public Double getWEIGHTDouble(){return WEIGHT == null ? null : WEIGHT.doubleValue();}
    public BigDecimal getVALUE(){return VALUE;}
    public Double getVALUEDouble(){return VALUE == null ? null : VALUE.doubleValue();}
    public BigDecimal getRESULT(){return RESULT;}
    public Double getRESULTDouble(){return RESULT == null ? null : RESULT.doubleValue();}
    public Integer getTIID(){return TIID;}
    public String getTINAME(){return TINAME;}
    public String getRIGHTUSER(){return RIGHTUSER;}
    public String getMEMO(){return MEMO;}
    public String getICOPATH(){return ICOPATH;}
    public Integer getAREAID(){return AREAID;}
    public String getAREANAME(){return AREANAME;}
    public Integer getISOWNER(){return ISOWNER;}
    public Integer getEST0COUNT(){return EST0COUNT;}
    public String getTASKPATH(){return TASKPATH;}
    public String getESTIMEDESC(){return ESTIMEDESC;}
    public BigDecimal getESTIME(){return ESTIME;}
    public Double getESTIMEDouble(){return ESTIME == null ? null : ESTIME.doubleValue();}
    public BigDecimal getSPENDTIME(){return SPENDTIME;}
    public Double getSPENDTIMEDouble(){return SPENDTIME == null ? null : SPENDTIME.doubleValue();}
    public BigDecimal getFINISHEDTIME(){return FINISHEDTIME;}
    public Double getFINISHEDTIMEDouble(){return FINISHEDTIME == null ? null : FINISHEDTIME.doubleValue();}
    public BigDecimal getKEEPTIME(){return KEEPTIME;}
    public Double getKEEPTIMEDouble(){return KEEPTIME == null ? null : KEEPTIME.doubleValue();}
    public BigDecimal getDELAYTIME(){return DELAYTIME;}
    public BigDecimal getDIFFTIME(){return DIFFTIME;}
    public BigDecimal getWORKRATE(){return WORKRATE;}
    public Double getWORKRATEDouble(){return WORKRATE == null ? null : WORKRATE.doubleValue();}
    public BigDecimal getAVGRATE(){return AVGRATE;}
    public Double getAVGRATEDouble(){return AVGRATE == null ? null : AVGRATE.doubleValue();}
    public BigDecimal getTACHNICSTOTAL(){return TACHNICSTOTAL;}
    public Double getTACHNICSTOTALDouble(){return TACHNICSTOTAL == null ? null : TACHNICSTOTAL.doubleValue();}
    public BigDecimal getTACHNICSINPUTTOTAL(){return TACHNICSINPUTTOTAL;}
    public Double getTACHNICSINPUTTOTALDouble(){return TACHNICSINPUTTOTAL == null ? null : TACHNICSINPUTTOTAL.doubleValue();}
    public BigDecimal getINCOMETOTAL(){return INCOMETOTAL;}
    public Double getINCOMETOTALDouble(){return INCOMETOTAL == null ? null : INCOMETOTAL.doubleValue();}
    public BigDecimal getDEGOFF(){return DEGOFF;}
    public Double getDEGOFFDouble(){return DEGOFF == null ? null : DEGOFF.doubleValue();}
    public Integer getX1(){return X1;}
    public Integer getY1(){return Y1;}
    public Integer getX2(){return X2;}
    public Integer getY2(){return Y2;}
    public BigDecimal getMEASURE_X1(){return MEASURE_X1;}
    public Double getMEASURE_X1Double(){return MEASURE_X1 == null ? null : MEASURE_X1.doubleValue();}
    public BigDecimal getMEASURE_Y1(){return MEASURE_Y1;}
    public Double getMEASURE_Y1Double(){return MEASURE_Y1 == null ? null : MEASURE_Y1.doubleValue();}
    public BigDecimal getMEASURE_X2(){return MEASURE_X2;}
    public Double getMEASURE_X2Double(){return MEASURE_X2 == null ? null : MEASURE_X2.doubleValue();}
    public BigDecimal getMEASURE_Y2(){return MEASURE_Y2;}
    public Double getMEASURE_Y2Double(){return MEASURE_Y2 == null ? null : MEASURE_Y2.doubleValue();}
    public BigDecimal getGPS_X(){return GPS_X;}
    public Double getGPS_XDouble(){return GPS_X == null ? null : GPS_X.doubleValue();}
    public BigDecimal getGPS_Y(){return GPS_Y;}
    public Double getGPS_YDouble(){return GPS_Y == null ? null : GPS_Y.doubleValue();}
    public BigDecimal getGPS_X1(){return GPS_X1;}
    public Double getGPS_X1Double(){return GPS_X1 == null ? null : GPS_X1.doubleValue();}
    public BigDecimal getGPS_Y1(){return GPS_Y1;}
    public Double getGPS_Y1Double(){return GPS_Y1 == null ? null : GPS_Y1.doubleValue();}
    public BigDecimal getGPS_X2(){return GPS_X2;}
    public Double getGPS_X2Double(){return GPS_X2 == null ? null : GPS_X2.doubleValue();}
    public BigDecimal getGPS_Y2(){return GPS_Y2;}
    public Double getGPS_Y2Double(){return GPS_Y2 == null ? null : GPS_Y2.doubleValue();}
    public BigDecimal getADJUST_X(){return ADJUST_X;}
    public Double getADJUST_XDouble(){return ADJUST_X == null ? null : ADJUST_X.doubleValue();}
    public BigDecimal getADJUST_Y(){return ADJUST_Y;}
    public Double getADJUST_YDouble(){return ADJUST_Y == null ? null : ADJUST_Y.doubleValue();}
    public BigDecimal getADJUST_Z(){return ADJUST_Z;}
    public Double getADJUST_ZDouble(){return ADJUST_Z == null ? null : ADJUST_Z.doubleValue();}
    public String getATTR_1(){return ATTR_1;}
    public String getATTR_2(){return ATTR_2;}
    public String getATTR_3(){return ATTR_3;}
    public String getATTR_4(){return ATTR_4;}
    public String getATTR_5(){return ATTR_5;}
    public String getATTR_6(){return ATTR_6;}
    public String getATTR_7(){return ATTR_7;}
    public String getATTR_8(){return ATTR_8;}
    public String getATTR_9(){return ATTR_9;}
    public String getATTR_10(){return ATTR_10;}
    public String getATTR_11(){return ATTR_11;}
    public String getATTR_12(){return ATTR_12;}
    public String getATTR_13(){return ATTR_13;}
    public BigDecimal getPARAM_1(){return PARAM_1;}
    public Double getPARAM_1Double(){return PARAM_1 == null ? null : PARAM_1.doubleValue();}
    public BigDecimal getPARAM_2(){return PARAM_2;}
    public Double getPARAM_2Double(){return PARAM_2 == null ? null : PARAM_2.doubleValue();}
    public BigDecimal getPARAM_3(){return PARAM_3;}
    public Double getPARAM_3Double(){return PARAM_3 == null ? null : PARAM_3.doubleValue();}
    public BigDecimal getPARAM_4(){return PARAM_4;}
    public Double getPARAM_4Double(){return PARAM_4 == null ? null : PARAM_4.doubleValue();}
    public Integer getDOCCOUNT(){return DOCCOUNT;}

    /// <summary>
    /// =INPUTMETHOD，仅用于手机端的输入模式（旧版本中使用该值来判断输入模式（1.0.36以前版本））
    /// </summary>
    public Integer SCREENMETHOD;
    /// <summary>
    /// 手机端的输入模式（0：南塔模式，1：调度模式）
    /// </summary>
    public Integer INPUTMETHOD;
    /// <summary>
    /// 按检验类别搜索（过滤）标识
    /// </summary>
    public Integer CHECKFILTER;
    /// <summary>
    /// 按桩类型过滤
    /// </summary>
    public Integer TYPEFILTER;
    /// <summary>
    /// 分组过滤
    /// </summary>
    public Integer GROUPFILTER;
    /// <summary>
    /// 仅在承台-1上显示编码（目前就19局项目用到）
    /// </summary>
    public Integer ONLYGROUPID;
    public Integer SHOWFOOT;

    public Integer getSCREENMETHOD(){return SCREENMETHOD;}
    public Integer getINPUTMETHOD(){return INPUTMETHOD;}
    public Integer getCHECKFILTER(){return CHECKFILTER;}
    public Integer getTYPEFILTER(){return TYPEFILTER;}
    public Integer getSHOWFOOT(){return SHOWFOOT;}

    @Override
    public String toString(){
        return String.format("%s(id=%d)", NAME, ID);
    }

    @Override
    public int compareTo(TaskItem o) {
        return o.NAME.compareTo(this.NAME);
    }

    public static class TaskItemComparator implements Comparator<TaskItem> {

        @Override
        public int compare(TaskItem o1, TaskItem o2) {
            return o2.NAME.compareTo(o1.NAME);
        }
    }
}
