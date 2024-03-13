package Services;

import Models.Entities.FileItem;
import Models.Entities.Task.LogisticCarItem;
import Models.JsonObjectEx;
import Models.SessionManager;
import com.alibaba.fastjson.JSONObject;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

//ClientServices的编写规范类(L3)
public class ClientServicesM extends ClientServicesX {

    public enum enumBase{
        enumException(9999);

        private int nValue = 0;

        enumBase(int i) {
            this.nValue = i;
        }

        public int getInt() {
            return nValue;
        }

        public static enumBase FromInt(int nInt) {
            for (enumBase type : values()) {
                if (type.getInt() == nInt)
                    return type;
            }
            return enumBase.enumException;
        }
    }

    public enum enumInputType{
        /// <summary>
        /// 入库单
        /// </summary>
        enumInput(0),
        /// <summary>
        /// 出库单
        /// </summary>
        enumOutput(1),
        /// <summary>
        /// 盘点
        /// </summary>
        enumCheck(2),
        /// <summary>
        /// 订单
        /// </summary>
        enumOrder(9),
        /// <summary>
        /// 出入库单
        /// </summary>
        enumInputOutput(10),
        /// <summary>
        /// 调拨单
        /// </summary>
        enumAllot(11),
        /// <summary>
        /// 收料单
        /// </summary>
        enumReceive(20),
        /// <summary>
        /// 无效参数
        /// </summary>
        enumException(9999);

        private int nValue = 0;

        enumInputType(int i) {
            this.nValue = i;
        }

        public int getInt() {
            return nValue;
        }

        public static enumInputType FromInt(int nInt) {
            for (enumInputType type : values()) {
                if (type.getInt() == nInt)
                    return type;
            }
            return enumInputType.enumException;
        }
    }

    public ClientServicesM(String strTokenId){
        super(strTokenId);
    }

    public ClientServicesM(String strConnection, String strUser, String strPassword){
        super(strConnection, strUser,strPassword);
    }

    public ClientServicesM(String strTokenId, String strConnection, String strUser, String strPassword){
        super(strConnection, strUser, strPassword, strTokenId);
    }

    //<editor-folder desc="节点收缩描述">

    //</editor-folder>

    //<editor-folder desc="常用功能调用规范">

    public enumErrorCode InsertWithReturnId(Integer nWayId, Integer nSecondCompanyId, String strUnicode, Date dtSend, Date dtArrive, String strName, String strPhone, String strLinkMan, String strLinkPhone, String strLinkAddr, Result outValue){
        SqlParameter paramId = new SqlParameter("ID", Types.INTEGER, true);

        enumErrorCode enumRet = ExecCommandbyProcX("RT_LOGISTICINFO_INSERT", new SqlParameter[] {
                new SqlParameter("WAYID" , nWayId ),
                new SqlParameter("SECONDCOMPANYID" , nSecondCompanyId) ,
                new SqlParameter("UNICODE" , strUnicode) ,
                new SqlParameter("SENDDATE" , dtSend) ,
                new SqlParameter("ARRIVEDATE" , dtArrive) ,
                new SqlParameter("NAME" , strName ),
                new SqlParameter("PHONE" , strPhone ),
                new SqlParameter("LINKMAN" , strLinkMan ),
                new SqlParameter("LINKPHONE" , strLinkPhone ) ,
                new SqlParameter("LINKADDR" , strLinkAddr) ,
                paramId
        });
        if(enumRet == enumErrorCode.enumSuccess){
            outValue.setIntValue(paramId.Value.getIntValue());
        }
        return enumRet;
    }

    public enumErrorCode InsertDevice(Integer nSecondCompanyId , String strName, String strLicense, Integer nDeviceType, Date dtStart , Double dPrice , Double dSlottingFee , String strMemo, Integer nStatusId, Result outValue){
        SqlParameter paramId = new SqlParameter("ID", Types.INTEGER, true);

        enumErrorCode enumRet = ExecCommandbyProcX("BS_DEVICE_INSERT", new SqlParameter[] {
                new SqlParameter("SECONDCOMPANYID" , nSecondCompanyId) ,
                new SqlParameter("NAME" , strName ),
                new SqlParameter("LICENSE" , strLicense) ,
                new SqlParameter("DEVICETYPE" , nDeviceType ),
                new SqlParameter("STARTDATE" , dtStart) ,
                new SqlParameter("PRICE" , dPrice) ,
                new SqlParameter("SLOTTINGFEE" , dSlottingFee) ,
                new SqlParameter("MEMO" , strMemo ) ,
                new SqlParameter("STATUSID" , nStatusId) ,
                paramId
        });
        if(enumRet == enumErrorCode.enumSuccess){
            outValue.setIntValue(paramId.Value.getIntValue());
        }
        return enumRet;
    }

    public enumErrorCode UpdateDevice(Integer nId,Integer nSecondCompanyId , String strName,String strLicense, Integer nDeviceType,String strModel, String strLink1, String strPhone1, String strLink2, String strPhone2, String strLink3, String strPhone3, Date dtServiceDate, Date dtEstimateDate, Double dPrice , Double dSlottingFee , String strMemo, Integer nStatusId){
        return ExecCommandbyProcX("BS_DEVICE_UPDATE", new SqlParameter[] {
                new SqlParameter("ID" , nId ),
                new SqlParameter("SECONDCOMPANYID" , nSecondCompanyId) ,
                new SqlParameter("NAME" , strName ),
                new SqlParameter("LICENSE" , strLicense) ,
                new SqlParameter("DEVICETYPE" , nDeviceType ),
                new SqlParameter("MODEL" , strModel) ,
                new SqlParameter("LINK1" , strLink1) ,
                new SqlParameter("PHONE1" , strPhone1) ,
                new SqlParameter("LINK2" , strLink2) ,
                new SqlParameter("PHONE2" , strPhone2) ,
                new SqlParameter("LINK3" , strLink3) ,
                new SqlParameter("PHONE3" , strPhone3) ,
                new SqlParameter("SERVICEDATE" , dtServiceDate) ,
                new SqlParameter("ESTIMATEDATE" , dtEstimateDate) ,
                new SqlParameter("PRICE" , dPrice) ,
                new SqlParameter("SLOTTINGFEE" , dSlottingFee) ,
                new SqlParameter("MEMO" , strMemo ) ,
                new SqlParameter("STATUSID" , nStatusId)
        });
    }

    public enumErrorCode DeleteDevice(Integer nId){
        return ExecCommandbyProcX("BS_DEVICE_DELETE", nId);
   }

    public enumErrorCode UnDeleteDevice(Integer nId){
        return ExecCommandbyProcX("BS_DEVICE_UNDELETE", nId);
    }

    public enumErrorCode MoveDeviceUp(Integer nId){
        return ExecCommandbyProcX("BS_DEVICE_MOVEUP", nId);
    }

    public enumErrorCode MoveDeviceDown(Integer nId){
        return ExecCommandbyProcX("BS_DEVICE_MOVEDOWN", nId);
    }

    //T=DeviceItem
    public<T> List<T> GetDeviceListFilter(String strClassName, Integer nTypeId, Integer nSecondCompanyId, String strFilter, Integer nStart, Integer nLimit, Result outValue){
        return GetCommListbyProc("BS_DEVICE_GETFILTER", strClassName, new SqlParameter[]{
                new SqlParameter("TYPEID" , nTypeId) ,
                new SqlParameter("SECONDCOMPANYID" , nSecondCompanyId) ,
                new SqlParameter("FILTER" , strFilter) ,
                new SqlParameter("START" , nStart) ,
                new SqlParameter("LIMIT" , nLimit)
        }, outValue);
    }

    //T=DeviceItem
    public<T> List<T> GetDeviceListFilterWithDateRange(String strClassName, Integer nTypeId, String strFilter, Date dtStart, Date dtStop, Result outValue){
        return GetCommListbyProc("BS_DEVICE_GETFILTER", strClassName, new SqlParameter[]{
                new SqlParameter("TYPEID" , nTypeId) ,
                new SqlParameter("FILTER" , strFilter) ,
                new SqlParameter("STARTDATE" , dtStart) ,
                new SqlParameter("STOPDATE" , dtStop)
        }, outValue);
    }

    //T=DeviceItem
    public<T> List<T> GetDeviceHistory(String strClassName, Integer nId, Result outValue){
        return GetCommListbyProc("BS_DEVICE_GETHISTORY", strClassName, nId, outValue);
    }

    //T=DeviceItem
    public<T> List<T> GetDeviceListRange(String strClassName, Date dtStart, Date dtStop, Result outValue){
        return GetCommListbyProc("BS_DEVICE_GETRANGE", strClassName, new SqlParameter[]{
                new SqlParameter("STARTDATE" , dtStart) ,
                new SqlParameter("STOPDATE" , dtStop)
        }, outValue);
    }

    //T=DeviceItem
    public<T> List<T> GetDeviceWorkList(String strClassName, Result outValue){
        return GetCommListbyProc("BS_DEVICE_GETWORK", strClassName, outValue);
    }

    //T=DictionaryItem
    public<T> List<T> GetDeviceTypeDicList(String strClassName, Result outValue){
        return GetCommListbyProcNoTokenId("DIC_DEVICETYPE_GETDIC", strClassName, outValue);
    }

    //T=ProcessItem
    public<T> List<T> GetProcessListFilter(String strClassName, String strFilter, Integer nStart, Integer nLimit, Result outValue){
        return GetCommListbyProc("BS_PROCESS_GETFILTER", strClassName, new SqlParameter[]{
                new SqlParameter("FILTER" , strFilter) ,
                new SqlParameter("START" , nStart) ,
                new SqlParameter("LIMIT" , nLimit)
        }, outValue);
    }

    //</editor-folder>

    //<editor-folder desc="对List<>进行求和、平均等操作">

    public Object LogisticCarDataGet(HttpServletRequest request, Model model){
        SessionManager sm = new SessionManager(request);
        ClientServicesM client = new ClientServicesM(sm.getTokenId());

        JsonObjectEx json = new JsonObjectEx();

        Result outCar = new Result();
        List<LogisticCarItem> listCar = client.GetLogisticCarList(LogisticCarItem.class.getName(), outCar);
        if (outCar.toErrorCode() == ClientServicesX.enumErrorCode.enumSuccess){
            if(listCar.size() > 0){
                for(LogisticCarItem lci: listCar){
                    Result outFile = new Result();
                    List<FileItem> listFile = client.GetInputFileListAll(FileItem.class.getName(), lci.ID, 0, outFile);
                    if (outFile.toErrorCode() == ClientServicesX.enumErrorCode.enumSuccess)
                        lci.Files = listFile.stream().filter((FileItem p)->(p.TYPEID == 2 || p.TYPEID == 5)).collect(Collectors.toList());
                    else
                        lci.Files = new ArrayList<FileItem>();
                }

                //计算中心点数据
                OptionalDouble dCX = listCar.stream().mapToDouble(LogisticCarItem::getGPS_XDouble).average();
                OptionalDouble dCY = listCar.stream().mapToDouble(LogisticCarItem::getGPS_YDouble).average();
                //计算最大值、最小值
                OptionalDouble dCX_Max = listCar.stream().mapToDouble(LogisticCarItem::getGPS_XDouble).max();
                OptionalDouble dCX_Min = listCar.stream().mapToDouble(LogisticCarItem::getGPS_XDouble).min();
                OptionalDouble dCY_Max = listCar.stream().mapToDouble(LogisticCarItem::getGPS_YDouble).max();
                OptionalDouble dCY_Min = listCar.stream().mapToDouble(LogisticCarItem::getGPS_YDouble).min();

                //计算偏差
                Double dSX = dCX_Max.getAsDouble() - dCX_Min.getAsDouble();
                Double dSY = dCY_Max.getAsDouble() - dCY_Min.getAsDouble();

                json.putAll(true, "成功", listCar);
                json.put("cx", dCX);
                json.put("cy", dCY);
                json.put("sx", dSX);
                json.put("sy", dSY);
                json.put("minx", dCX_Min);
                json.put("miny", dCY_Min);
                json.put("maxx", dCX_Max);
                json.put("maxy", dCY_Max);
            }
            else{
                json.putAll(false, "没有有效的物流坐标！");
            }
        }
        else {
            json.putAll(false, "访问失败，重新登录再试！");
        }
        return json;
    }

    //T=LogisticCarItem
    public<T> List<T> GetLogisticCarList(String strClassName, Result outValue){
        return GetCommListbyProc("RT_LOGISTICINFO_GETCARLIST", strClassName, outValue);
    }

    //T=FileItem
    public<T> List<T> GetInputFileListAll(String strClassName, Integer nInputId, Integer nTypeId, Result outValue){
        return GetCommListbyProc("RT_INPUTFILE_GETALL", strClassName, new SqlParameter[]{
                new SqlParameter("INPUTID" , nInputId),
                new SqlParameter("TYPEID", nTypeId)
        }, outValue);
    }

    //</editor-folder>
}