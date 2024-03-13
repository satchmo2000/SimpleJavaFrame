package Models.Entities;

public class DefaultViewItem {
    public Integer Count;
    public Integer PageId;
    public Integer PageCount;
    public Integer PageSize;

    public Integer StartId;

    public Integer getCount(){return Count;}
    public Integer getPageId(){return PageId;}
    public Integer getPageCount(){return PageCount;}
    public Integer getPageSize(){return PageSize;}

    public Integer getStartId(){return StartId;}

    public DefaultViewItem(){
        Count = 0;
        PageId = 0;
        PageCount = 0;
        PageSize = 0;
    }

    public DefaultViewItem(Integer nCount, Integer nPage , Integer nCountPerPage){
        Count = nCount;
        PageId = nPage;
        PageSize = nCountPerPage;

        StartId = (nPage - 1) * nCountPerPage;
        PageCount = nCount / nCountPerPage;
        if(nCount % nCountPerPage > 0){
            PageCount ++;
        }
    }
}
