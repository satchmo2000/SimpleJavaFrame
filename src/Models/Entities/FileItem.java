package Models.Entities;

import java.math.BigDecimal;
import java.util.Date;

public class FileItem {
    public Integer ID;
    public Integer INPUTID;
    public Date CREATEDATE;
    public String NAME;
    public Integer TYPEID;
    public String TYPENAME;
    public String FILENAME;
    public Integer FILESIZE;
    public String MEMO;
    public BigDecimal DISTANCE;
    public Integer DURATION;
    public BigDecimal GPS_X;
    public BigDecimal GPS_Y;

    public Integer getID(){return ID;}
    public Integer getINPUTID(){return INPUTID;}
    public Date getCREATEDATE(){return CREATEDATE;}
    public String getNAME(){return NAME;}
    public Integer getTYPEID(){return TYPEID;}
    public String getTYPENAME(){return TYPENAME;}
    public String getFILENAME(){return FILENAME;}
    public Integer getFILESIZE(){return FILESIZE;}
    public String getMEMO(){return MEMO;}
    public BigDecimal getDISTANCE(){return DISTANCE;}
    public Double getDISTANCEDouble(){return DISTANCE.doubleValue();}
    public Integer getDURATION(){return DURATION;}
    public BigDecimal getGPS_X(){return GPS_X;}
    public Double getGPS_XDouble(){return GPS_X.doubleValue();}
    public BigDecimal getGPS_Y(){return GPS_Y;}
    public Double getGPS_YDouble(){return GPS_Y.doubleValue();}

    @Override
    public String toString(){
        return String.format("%s(id=%d)", NAME, ID);
    }
}
