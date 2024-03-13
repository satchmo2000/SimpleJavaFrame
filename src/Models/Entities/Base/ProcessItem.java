package Models.Entities.Base;

/**
 * 流程定义
 */
public class ProcessItem {
    public Integer ID;
    public String NAME;
    public String MEMO;

    public Integer getID(){return ID;}
    public String getNAME(){return NAME;}
    public String getMEMO(){return MEMO;}

    @Override
    public String toString(){
        return String.format("%s(id=%d)", NAME, ID);
    }

}
