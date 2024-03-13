package Models.Entities;

import java.util.Date;

public class DictionaryItem {
    public Integer ID;
    public String NAME;

    public Integer getID(){return ID;}
    public String getNAME(){return NAME;}

    @Override
    public String toString(){
        return String.format("%s(id=%d)", NAME, ID);
    }
}
