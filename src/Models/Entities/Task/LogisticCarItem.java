package Models.Entities.Task;
import Models.Entities.FileItem;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class LogisticCarItem {
    public Integer ID;
    public Date AUDITDATE;
    public Integer COMPANYID;
    public String UNICODE;
    public String NAME;
    public String TKNAME;
    public BigDecimal START_X;
    public BigDecimal START_Y;
    public BigDecimal STOP_X;
    public BigDecimal STOP_Y;
    public Integer TYPEID;
    public String FILENAME;
    public BigDecimal GPS_X;
    public BigDecimal GPS_Y;
    public Date CREATEDATE;
    public BigDecimal DISTANCE;
    public Integer DURATION;
    public Date CURRENTDATE;
    public BigDecimal KEEPDAYS;
    public BigDecimal KEEPDAYS0;
    public Integer ATTR_13;
    public Integer WAYID2;
    public BigDecimal X;
    public BigDecimal Y;
    public BigDecimal R;

    public Integer getID(){return ID;}
    public Date getAUDITDATE(){return AUDITDATE;}
    public Integer getCOMPANYID(){return COMPANYID;}
    public String getUNICODE(){return UNICODE;}
    public String getNAME(){return NAME;}
    public String getTKNAME(){return TKNAME;}
    public BigDecimal getSTART_X(){return START_X;}
    public Double getSTART_XDouble(){return START_X.doubleValue();}
    public BigDecimal getSTART_Y(){return START_Y;}
    public Double getSTART_YDouble(){return START_Y.doubleValue();}
    public BigDecimal getSTOP_X(){return STOP_X;}
    public Double getSTOP_XDouble(){return STOP_X.doubleValue();}
    public BigDecimal getSTOP_Y(){return STOP_Y;}
    public Double getSTOP_YDouble(){return STOP_Y.doubleValue();}
    public Integer getTYPEID(){return TYPEID;}
    public String getFILENAME(){return FILENAME;}
    public BigDecimal getGPS_X(){return GPS_X;}
    public Double getGPS_XDouble(){return GPS_X.doubleValue();}
    public BigDecimal getGPS_Y(){return GPS_Y;}
    public Double getGPS_YDouble(){return GPS_Y.doubleValue();}
    public Date getCREATEDATE(){return CREATEDATE;}
    public BigDecimal getDISTANCE(){return DISTANCE;}
    public Double getDISTANCEDouble(){return DISTANCE.doubleValue();}
    public Integer getDURATION(){return DURATION;}
    public Date getCURRENTDATE(){return CURRENTDATE;}
    public BigDecimal getKEEPDAYS(){return KEEPDAYS;}
    public Double getKEEPDAYSDouble(){return KEEPDAYS.doubleValue();}
    public BigDecimal getKEEPDAYS0(){return KEEPDAYS0;}
    public Double getKEEPDAYS0Double(){return KEEPDAYS0.doubleValue();}
    public Integer getATTR_13(){return ATTR_13;}
    public Integer getWAYID2(){return WAYID2;}
    public BigDecimal getX(){return X;}
    public Double getXDouble(){return X.doubleValue();}
    public BigDecimal getY(){return Y;}
    public Double getYDouble(){return Y.doubleValue();}
    public BigDecimal getR(){return R;}
    public Double getRDouble(){return R.doubleValue();}

    public List<FileItem> Files;

    //public List<FileItem> getFiles(){return Files;}

    @Override
    public String toString(){
        return String.format("%s(id=%d)", NAME, ID);
    }
}
