package Models;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionManager {
    private HttpServletRequest content = null;
    private String strTokenId = "TokenId";
    private String strLoginDate = "LoginDate";
    private String strUserName = "UserName";
    private String strLoginName = "LoginName";
    private String strRoleName = "RoleName";
    private String strStaffName = "StaffName";
    private String strCompanyId = "CompanyId";
    private String strCompanyName = "CompanyName";
    private String strCompanyTypeName = "CompanyTypeName";
    private String strCompanyCode = "CompanyCode";
    private String strDepartmentId = "DepartmentId";
    private String strDepartmentName = "DepartmentName";
    private String strPrjId = "ProjectId";
    private String strPrjName = "ProjectName";
    private String strPrjType = "ProjectType";
    private String strDemoMenuId = "DemoMenuId";
    private String strIsMobile = "IsMobile";
    private String strTaskInfo = "TaskInfo";
    private String strGPSX = "GPSX";
    private String strGPSY = "GPSY";
    private String strMapType = "MapType";
    private String strAutoInput = "AutoInput";
    private String strPriKey = "PriKey";
    private String strPubKey = "PubKey";

    public SessionManager(HttpServletRequest request){
        content = request;
    }

    public boolean IsLogined(){
        String strTokenId = (String)getAttribute(this.strTokenId);
        return strTokenId.length() > 0;
    }

    public void Clear(){
        if (content != null){
            content.getSession().invalidate();
        }
    }

    public boolean IsTimeout(){
        String strTokenId = (String)getAttribute(this.strTokenId);
        return strTokenId.length() == 0;
    }

    //<editor-folder desc="public boolean IsMobile">
    public boolean getIsMobile(){
        Object obj = getAttribute(this.strIsMobile);
        if(obj != null){
            return (boolean)obj;
        }
        return false;
    }

    public void setIsMobile(boolean bIsMobile){
        setAttribute(this.strIsMobile, bIsMobile);
    }
    //</editor-folder>

    //<editor-folder desc="public int MapType">
    public boolean getMapType(){
        Object obj = getAttribute(this.strMapType);
        if(obj != null){
            return (boolean)obj;
        }
        return false;
    }

    public void setMapType(int nMapType){
        setAttribute(this.strMapType, nMapType);
    }
    //</editor-folder>

    //<editor-folder desc="public boolean AutoInput">
    public boolean getAutoInput(){
        Object obj = getAttribute(this.strAutoInput);
        if(obj != null){
            return (boolean)obj;
        }
        return false;
    }

    public void setAutoInput(boolean bAutoInput){
        setAttribute(this.strAutoInput, bAutoInput);
    }
    //</editor-folder>

    //<editor-folder desc="public String TokenId">
    public String getTokenId(){
        Object obj = getAttribute(this.strTokenId);
        if(obj != null){
            return (String)obj;
        }
        return "";
    }

    public void setTokenId(String strTokenId){
        setAttribute(this.strTokenId, strTokenId);
    }
    //</editor-folder>


    //<editor-folder desc="public String LoginDate">
    public String getLoginDate(){
        Object obj = getAttribute(this.strLoginDate);
        if(obj != null){
            return (String)obj;
        }
        return "";
    }

    public void setLoginDate(String strLoginDate){
        setAttribute(this.strLoginDate, strLoginDate);
    }
    //</editor-folder>


    //<editor-folder desc="public String LoginName">
    public String getLoginName(){
        Object obj = getAttribute(this.strLoginName);
        if(obj != null){
            return (String)obj;
        }
        return "";
    }

    public void setLoginName(String strLoginName){
        setAttribute(this.strLoginName, strLoginName);
    }
    //</editor-folder>

    //<editor-folder desc="public String UserName">
    public String getUserName(){
        Object obj = getAttribute(this.strUserName);
        if(obj != null){
            return (String)obj;
        }
        return "";
    }

    public void setUserName(String strUserName){
        setAttribute(this.strUserName, strUserName);
    }
    //</editor-folder>

    //<editor-folder desc="public String RoleName">
    public String getRoleName(){
        Object obj = getAttribute(this.strRoleName);
        if(obj != null){
            return (String)obj;
        }
        return "";
    }

    public void setRoleName(String strRoleName){
        setAttribute(this.strRoleName, strRoleName);
    }
    //</editor-folder>

    //<editor-folder desc="public String StaffName">
    public String getStaffName(){
        Object obj = getAttribute(this.strStaffName);
        if(obj != null){
            return (String)obj;
        }
        return "";
    }

    public void setStaffName(String strStaffName){
        setAttribute(this.strStaffName, strStaffName);
    }
    //</editor-folder>

    //<editor-folder desc="public String CompanyName">
    public String getCompanyName(){
        Object obj = getAttribute(this.strCompanyName);
        if(obj != null){
            return (String)obj;
        }
        return "";
    }

    public void setCompanyName(String strCompanyName){
        setAttribute(this.strCompanyName, strCompanyName);
    }
    //</editor-folder>

    //<editor-folder desc="public Integer CompanyId">
    public Integer getCompanyId(){
        Object obj = getAttribute(this.strCompanyId);
        if(obj != null){
            return (Integer)obj;
        }
        return 0;
    }

    public void setCompanyId(Integer nCompanyId){
        setAttribute(this.strCompanyId, nCompanyId);
    }
    //</editor-folder>

    //<editor-folder desc="public String DepartmentName">
    public String getDepartmentName(){
        Object obj = getAttribute(this.strDepartmentName);
        if(obj != null){
            return (String)obj;
        }
        return "";
    }

    public void setDepartmentName(String strDepartmentName){
        setAttribute(this.strDepartmentName, strDepartmentName);
    }
    //</editor-folder>

    //<editor-folder desc="public Integer DepartmentId">
    public Integer getDepartmentId(){
        Object obj = getAttribute(this.strDepartmentId);
        if(obj != null){
            return (Integer)obj;
        }
        return 0;
    }

    public void setDepartmentId(Integer nDepartmentId){
        setAttribute(this.strDepartmentId, nDepartmentId);
    }
    //</editor-folder>

    //<editor-folder desc="public String CompanyCode">
    public String getCompanyCode(){
        Object obj = getAttribute(this.strCompanyCode);
        if(obj != null){
            return (String)obj;
        }
        return "";
    }

    public void setCompanyCode(String strCompanyCode){
        setAttribute(this.strCompanyCode, strCompanyCode);
    }
    //</editor-folder>

    //<editor-folder desc="public String CompanyTypeName">
    public String getCompanyTypeName(){
        Object obj = getAttribute(this.strCompanyTypeName);
        if(obj != null){
            return (String)obj;
        }
        return "";
    }

    public void setCompanyTypeName(String strCompanyTypeName){
        setAttribute(this.strCompanyTypeName, strCompanyTypeName);
    }
    //</editor-folder>

    public void SelectProject(Integer nId, String strName, Integer nType){
        setAttribute(this.strPrjId , nId);
        setAttribute(this.strPrjName, strName);
        setAttribute(this.strPrjType , nType);
    }

    public Integer getProjectId(){
        Object obj = getAttribute(this.strPrjId);
        if(obj != null){
            return (Integer)obj;
        }
        return 0;
    }

    public void setProjectId(Integer nId){
        setAttribute(this.strPrjId, nId);
    }

    public String getProjectName(){
        Object obj = getAttribute(this.strPrjName);
        if(obj != null){
            return (String)obj;
        }
        return "";
    }

    public void setProjectName(String strName){
        setAttribute(this.strPrjName, strName);
    }

    public Integer getProjectType(){
        Object obj = getAttribute(this.strPrjType);
        if(obj != null){
            return (Integer)obj;
        }
        return 0;
    }

    public void setProjectType(Integer nType){
        setAttribute(this.strPrjType, nType);
    }

    public Object GetTaskInfo(Integer nTkId){
        String strSessionId = String.format("{%s}-{%d}", strTaskInfo, nTkId);
        return getAttribute(strSessionId);
    }

    public void SetTaskInfo(Integer nTkId , Object obj){
        String strSessionId = String.format("{%s}-{%d}", strTaskInfo, nTkId);
        setAttribute(strSessionId, obj);
    }

    public String getPriKey(){
        Object obj = getAttribute(this.strPriKey);
        if(obj != null){
            return (String)obj;
        }
        return "";
    }

    public void setPriKey(String strName){
        setAttribute(this.strPriKey, strName);
    }

    public String getPubKey(){
        Object obj = getAttribute(this.strPubKey);
        if(obj != null){
            return (String)obj;
        }
        return "";
    }

    public void setPubKey(String strName){
        setAttribute(this.strPubKey, strName);
    }

    public Integer getDemoMenuId(){
        Object obj = getAttribute(this.strDemoMenuId);
        if(obj != null){
            return (Integer)obj;
        }
        return 0;
    }

    public void setStrDemoMenuId(Integer nDemoMenuId){
        setAttribute(this.strDemoMenuId, nDemoMenuId);
    }

    private Object getAttribute(String strAtrributeName){
        if(content != null){
            HttpSession hs = content.getSession();
            Object obj = hs.getAttribute(strAtrributeName);
            if(obj != null){
                return obj;
            }
        }
        return null;
    }

    private void setAttribute(String strAttributeName, Object obj){
        if(content != null){
            HttpSession hs = content.getSession();
            hs.setAttribute(strAttributeName, obj);
        }
    }
}
