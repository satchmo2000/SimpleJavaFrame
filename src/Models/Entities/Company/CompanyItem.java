package Models.Entities.Company;

import java.util.Date;
import java.util.List;

public class CompanyItem {
    public Integer ID;
    public Integer PID;
    public Date CREATEDATE;
    public String UNICODE;
    public String NAME;
    public Integer COMPANYTYPE;
    public String COMPANYTYPENAME;
    public Integer DEPARTMENTTYPE;
    public String DEPARTMENTTYPENAME;
    public Integer BUSINESSTYPE;
    public String BUSINESSTYPENAME;
    public Integer NATUREID;
    public String NATURENAME;
    public String BRIEF;
    public String LICENSE;
    public String REPRESENT;
    public String LINKMAN;
    public String LINKPHONE;
    public String MOBILE;
    public String FAX;
    public String ADDRESS;
    public String POST;
    public String EMAIL;
    public String MEMO;
    public Integer SCOPEID;
    public String SCOPENAME;
    public String PHOTO;
    public String URL;
    public String COMPANYPATH;
    public Integer OFFICEID;
    public String OFFICENAME;
    public String DEFAULTTASK;
    public String LOCKNAME;
    public Integer ISLOCK;
    public Date LOCKDATE;
    public Integer MONTHDAY;
    public Integer DAYHOUR;
    public String ACTIVENAME;
    public Integer ISACTIVE;
    public String STATUSNAME;

    public int HASCHILD;
    public int ISOPEN;

    /**
     * 获取ID
     */
    public Integer getID(){return ID;}

    /**
     * 获取父ID
     */
    public Integer getPID(){return PID;}

    /**
     * 获取创建时间
     */
    public Date getCREATEDATE(){return CREATEDATE;}

    /**
     * 获取企业编码
     */
    public String getUNICODE(){return UNICODE;}

    /**
     * 获取企业名称
     */
    public String getNAME(){return NAME;}

    /**
     * 获取企业类别
     */
    public Integer getCOMPANYTYPE(){return COMPANYTYPE;}

    /**
     * 获取企业类别名称
     */
    public String getCOMPANYTYPENAME(){return COMPANYTYPENAME;}

    /**
     * 获取部门类别（1：职能大区、2：业务大区、3：职能部门、4：业务部门）
     */
    public Integer getDEPARTMENTTYPE(){return DEPARTMENTTYPE;}

    /**
     * 获取部门类别名称（职能大区、业务大区、职能部门、业务部门）
     */
    public String getDEPARTMENTTYPENAME(){return DEPARTMENTTYPENAME;}

    /**
     * 获取行业类别
     */
    public Integer getBUSINESSTYPE(){return BUSINESSTYPE;}

    /**
     * 获取行业类别名称
     */
    public String getBUSINESSTYPENAME(){return BUSINESSTYPENAME;}

    /**
     * 获取企业性质类别
     */
    public Integer getNATUREID(){return NATUREID;}

    /**
     * 获取企业性质类别名称
     */
    public String getNATURENAME(){return NATURENAME;}

    /**
     * 获取企业简称
     */
    public String getBRIEF(){return BRIEF;}

    /**
     * 获取企业注册编码
     */
    public String getLICENSE(){return LICENSE;}

    /**
     * 获取企业负责人信息
     */
    public String getREPRESENT(){return REPRESENT;}

    /**
     * 获取企业联系人信息
     */
    public String getLINKMAN(){return LINKMAN;}

    /**
     * 获取企业联系电话
     */
    public String getLINKPHONE(){return LINKPHONE;}

    /**
     * 获取企业移动电话
     */
    public String getMOBILE(){return MOBILE;}

    /**
     * 获取企业传真
     */
    public String getFAX(){return FAX;}

    /**
     * 获取企业地址
     */
    public String getADDRESS(){return ADDRESS;}

    /**
     * 获取企业邮政编码
     */
    public String getPOST(){return POST;}

    /**
     * 获取企业邮箱
     */
    public String getEMAIL(){return EMAIL;}

    /**
     * 获取企业备注说明
     */
    public String getMEMO(){return MEMO;}

    /**
     * 获取企业经营范围标识
     */
    public Integer getSCOPEID(){return SCOPEID;}

    /**
     * 获取企业经营范围名称
     */
    public String getSCOPENAME(){return SCOPENAME;}

    /**
     * 获取企业图片（或LOGO）
     */
    public String getPHOTO(){return PHOTO;}

    /**
     * 获取企业门户网站地址
     */
    public String getURL(){return URL;}

    /**
     * 获取企业归属路径
     */
    public String getCOMPANYPATH(){return COMPANYPATH;}

    /**
     * 获取部门对应在办公平台下的标识
     */
    public Integer getOFFICEID(){return OFFICEID;}

    /**
     * 获取部门对应在办公平台下的名称
     */
    public String getOFFICENAME(){return OFFICENAME;}

    /**
     * 获取企业的默认任务
     */
    public String getDEFAULTTASK(){return DEFAULTTASK;}

    /**
     * 获取企业的锁定状态名称
     */
    public String getLOCKNAME(){return LOCKNAME;}

    /**
     * 获取企业是否处于锁定状态
     */
    public Integer getISLOCK(){return ISLOCK;}

    /**
     * 获取企业的锁定日期
     */
    public Date getLOCKDATE(){return LOCKDATE;}

    /**
     * 获取企业的月结算日
     */
    public Integer getMONTHDAY(){return MONTHDAY;}

    /**
     * 获取企业的白天/晚上的时间分界线
     */
    public Integer getDAYHOUR(){return DAYHOUR;}

    /**
     * 获取企业的是否激活的状态名称
     */
    public String getACTIVENAME(){return ACTIVENAME;}

    /**
     * 获取企业是否处于激活状态
     */
    public Integer getISACTIVE(){return ISACTIVE;}

    /**
     * 获取企业的状态名字
     */
    public String getSTATUSNAME(){return STATUSNAME;}

    /**
     * 获取部门下是否存在子部门
     */
    public int getHASCHILD(){return HASCHILD;}

    /**
     * 获取节点是否处于打开状态
     */
    public int getISOPEN(){return ISOPEN;}

    @Override
    public String toString(){
        return String.format("%s(id=%d)", NAME, ID);
    }

    public String getRowClass(){
        if (ISLOCK == 1){
            return "error";
        }
        else if (ISACTIVE == 1){
            return "success";
        }
        else
            return "";
    }
}