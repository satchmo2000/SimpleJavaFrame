package Services;

import Utils.EncryptUtil;
import Utils.MD5Util;

import java.sql.Date;
import java.sql.Types;
import java.util.List;
import java.util.Locale;

//ClientServices用户登录及通用基础父类(L2)
public class ClientServicesX extends ClientServicesBase{

    public enum enumErrorCode {
        /// <summary>
        /// 是管理员
        /// </summary>
        enumIsAdmin(1),
        /// <summary>
        /// 正常
        /// </summary>
        enumSuccess(0),
        /// <summary>
        /// 连接数据库失败
        /// </summary>
        enumConnectError(-1),
        /// <summary>
        /// 访问超时
        /// </summary>
        enumTimeOut(-2),
        /// <summary>
        /// 无数据
        /// </summary>
        enumNoData(-3),
        /// <summary>
        /// 无权操作
        /// </summary>
        enumNoRight(-4),
        /// <summary>
        /// 参数越界
        /// </summary>
        enumParamOutRange(-5),
        /// <summary>
        /// 用户未激活
        /// </summary>
        enumNotActive(-6),
        /// <summary>
        /// 用户被锁定或停用
        /// </summary>
        enumIsLock(-7),
        /// <summary>
        /// 错误的格式
        /// </summary>
        enumWrongFormat(-8),
        /// <summary>
        /// 定义丢失
        /// </summary>
        enumLostDefine(-9),
        /// <summary>
        /// 无效的命令
        /// </summary>
        enumWrongCommand(-10),
        /// <summary>
        /// 准备不充分
        /// </summary>
        enumNotEnough(-11),
        /// <summary>
        /// 数据非空（设备未完全清退或完成）
        /// </summary>
        enumNotNull(-12),
        /// <summary>
        /// 缺少设备配置
        /// </summary>
        enumNoDevice(-13),
        /// <summary>
        /// 缺少上报护筒标高
        /// </summary>
        enumNoPCHeight(-14),
        /// <summary>
        /// 缺少进度值
        /// </summary>
        enumNoProgress(-15),
        /// <summary>
        /// 名称重复
        /// </summary>
        enumDoubleName(-16),
        /// <summary>
        /// 已经存在编码
        /// </summary>
        enumDoubleUnicode(-17),
        /// <summary>
        /// 已经存在用户名
        /// </summary>
        enumDoubleUserName(-18),
        /// <summary>
        /// 已经存在邮箱
        /// </summary>
        enumDoubleEmail(-19),
        /// <summary>
        /// 重复的企业注册号
        /// </summary>
        enumDoubleCompanyUnicode(-21),
        /// <summary>
        /// 重复的企业名称
        /// </summary>
        enumDoubleCompanyName(-22),
        /// <summary>
        /// 没有验证码
        /// </summary>
        enumNoCheckCode(-31),
        /// <summary>
        /// 验证码已失效
        /// </summary>
        enumInvalidCheckCode(-32),
        /// <summary>
        /// 错误的验证码
        /// </summary>
        enumWrongCheckCode(-33),
        /// <summary>
        /// 设备重复注册
        /// </summary>
        enumDoubleManchine(-98),
        /// <summary>
        /// 异常错误
        /// </summary>
        enumException(-99),
        /// <summary>
        /// TokenId丢失
        /// </summary>
        enumNoTokenId(-100),
        /// <summary>
        /// 无效的存储过程
        /// </summary>
        enumErrorProcess(-101);

        private int nValue = 0;

        enumErrorCode(int i) {
            this.nValue = i;
        }

        public int getInt() {
            return nValue;
        }

        public static enumErrorCode FromInt(int nInt) {
            for (enumErrorCode type : values()) {
                if (type.getInt() == nInt)
                    return type;
            }
            return enumErrorCode.enumException;
        }
    }

    public enum enumSystemParameter{
        /// <summary>
        /// 是否为调试模式
        /// </summary>
        enumDebugFlag(0),
        /// <summary>
        /// 获取客户端版本
        /// </summary>
        enumProgramVersion(1),
        /// <summary>
        /// 获取基础数据版本
        /// </summary>
        enumDataVersion(2),
        /// <summary>
        /// 获取通告数据版本
        /// </summary>
        enumAttentionVersion(3),
        /// <summary>
        /// 获取功能版本
        /// </summary>
        enumFunctionVersion(4),
        /// <summary>
        /// 数据库版本，格式：更新日期
        /// </summary>
        enumDataBaseVersion(5),
        /// <summary>
        /// Tortoise同步命令行
        /// </summary>
        enumTortoiseSVNCommand(6),
        /// <summary>
        /// 同步代码目录
        /// </summary>
        enumCodePath(7),
        /// <summary>
        /// 定位间隔时间（秒）
        /// </summary>
        enumLocationTimeSpan(8) ,
        /// <summary>
        /// 会计月份起始日
        /// </summary>
        enumDayStart(9),
        /// <summary>
        ///是否验证手机唯一性
        /// </summary>
        enumIsCheckPhoneNum(10) ,
        /// <summary>
        /// 项目开始时间
        /// </summary>
        enumProjectStartDate(11) ,
        /// <summary>
        /// 施工开始时间
        /// </summary>
        enumWorkStartDate(12) ,
        /// <summary>
        /// 显示所有桩号标记
        /// </summary>
        enumShowAllPole(13) ,
        /// <summary>
        /// 设备设备及人员的位置
        /// </summary>
        enumShowLocation(14) ,
        /// <summary>
        /// 计划参考日期=2016-3-28
        /// </summary>
        enumRefPlanDate(15) ,
        /// <summary>
        /// 程序子版本（正式版、试用版）
        /// </summary>
        enumProgramSubVersion(16) ,
        /// <summary>
        /// 测试企业账号的使用期限（30天）
        /// </summary>
        enumTestDays(17) ,
        /// <summary>
        /// 计算日时间偏移（8小时）
        /// </summary>
        enumHourOffset(18) ,
        /// <summary>
        /// 月结开始时间（23日）
        /// </summary>
        enumMonthDay(19) ,
        /// <summary>
        /// App自动登录（0：手动登录，1：自动登录，2：自动登录，但屏蔽特殊功能）
        /// </summary>
        enumAppAutoLogin(21),
        /// <summary>
        /// 用户签名模式（0：按用户配置，1：所有允许，2：所有禁止）
        /// </summary>
        enumUserSignMethod(22),
        /// <summary>
        /// 地图模型（0：百度，1：高德）
        /// </summary>
        enumMapType(23),
        /// <summary>
        /// 出库单生成模式（0：由品控部手动生成，1：由销售提出申请时自动生成）
        /// </summary>
        enumAutoInput(24),
        /// <summary>
        /// 中交的程序版本
        /// </summary>
        enumProgramVersion_ZJ(1001),
        /// <summary>
        /// 19局的程序版本
        /// </summary>
        enumProgramVersion_19(1002),
        /// <summary>
        /// 无效参数
        /// </summary>
        enumException(9999);

        private int nValue = 0;

        enumSystemParameter(int i) {
            this.nValue = i;
        }

        public int getInt() {
            return nValue;
        }

        public static enumSystemParameter FromInt(int nInt) {
            for (enumSystemParameter type : values()) {
                if (type.getInt() == nInt)
                    return type;
            }
            return enumSystemParameter.enumException;
        }
    }

    protected String m_strRealName;

    public ClientServicesX(String strTokenId){
        super(strTokenId);
    }

    public ClientServicesX(String strConnection, String strUser, String strPassword){
        super(strConnection, strUser,strPassword);
    }

    public ClientServicesX(String strTokenId, String strConnection, String strUser, String strPassword){
        super(strConnection, strUser, strPassword, strTokenId);
    }

    public void PrintLoginInfo(){
        System.out.println(String.format("Login Name=%s,TokenId=%s", m_strRealName, m_strTokenId));
    }

    //<editor-folder desc="用户登录">
    //注册用户
    public enumErrorCode RegUser(String strRealName, String strUserName , String strPassword, boolean bNeedMd5){
        if(bNeedMd5){
            strPassword = MD5Util.getMD5(strPassword);
        }

        SqlParameter paramTokenId = new SqlParameter("TOKENID", Types.VARCHAR, true);
        SqlParameter paramRet = new SqlParameter("RET", Types.INTEGER, true);

        enumErrorCode enumRet = ExecCommandbyProcX("HR_USER_REGISTER", new SqlParameter[]{
                new SqlParameter("REALNAME" , strRealName) ,
                new SqlParameter("USERNAME" , strUserName) ,
                new SqlParameter("PASSWORD", strPassword),
                new SqlParameter("IP", m_strIP),
                new SqlParameter("PORT", m_nPort),
                paramTokenId ,
                paramRet
        });
        m_strTokenId = paramTokenId.Value.getStrValue();
        m_strRealName = strRealName;

        return enumRet;
    }

    //正常登陆
    public enumErrorCode Login(String strUserName , String strPassword, boolean bNeedMd5){
        if(bNeedMd5 && strPassword.length() != 32){
            strPassword = MD5Util.getMD5(strPassword);
        }

        SqlParameter paramTokenId = new SqlParameter("TOKENID", Types.VARCHAR, true);
        SqlParameter paramRealName = new SqlParameter("REALNAME", Types.VARCHAR, true);
        SqlParameter paramRet = new SqlParameter("RET", Types.INTEGER, true);

        enumErrorCode enumRet = ExecCommandbyProcX("SYS_LOGIN", new SqlParameter[]{
                new SqlParameter("USERNAME" , strUserName) ,
                new SqlParameter("PASSWORD", strPassword),
                new SqlParameter("IP", m_strIP),
                new SqlParameter("PORT", m_nPort),
                new SqlParameter("UNICODE", "") ,
                new SqlParameter("CHKCODE", "") ,
                paramTokenId ,
                paramRealName ,
                paramRet
        });
        m_strTokenId = paramTokenId.Value.getStrValue();
        m_strRealName = paramRealName.Value.getStrValue();

        return enumRet;
    }

    //通过验证码登陆
    public enumErrorCode LoginbyCheckCode(String strUserName , String strCheckCode){

        SqlParameter paramTokenId = new SqlParameter("TOKENID", Types.VARCHAR, true);
        SqlParameter paramRet = new SqlParameter("RET", Types.INTEGER, true);

        enumErrorCode enumRet = ExecCommandbyProcX("SYS_LOGINFIND", new SqlParameter[]{
                new SqlParameter("USERNAME" , strUserName) ,
                new SqlParameter("CHKCODE", strCheckCode),
                new SqlParameter("IP", m_strIP),
                new SqlParameter("PORT", m_nPort),
                paramTokenId ,
                paramRet
        });
        m_strTokenId = paramTokenId.Value.getStrValue();

        return enumRet;
    }

    //微信登陆
    public enumErrorCode WeixinLogin(String strOpenId){
        SqlParameter paramTokenId = new SqlParameter("TOKENID", Types.VARCHAR, true);
        SqlParameter paramRealName = new SqlParameter("REALNAME", Types.VARCHAR, true);
        SqlParameter paramRet = new SqlParameter("RET", Types.INTEGER, true);

        enumErrorCode enumRet = ExecCommandbyProcX("SYS_WEIXINLOGIN", new SqlParameter[]{
                new SqlParameter("OPENID" , strOpenId) ,
                new SqlParameter("IP", m_strIP),
                new SqlParameter("PORT", m_nPort),
                paramTokenId ,
                paramRealName ,
                paramRet
        });
        m_strTokenId = paramTokenId.Value.getStrValue();
        m_strRealName = paramRealName.Value.getStrValue();

        return enumRet;
    }

    public enumErrorCode GetCompanyInfo2byLoginUser(Result outCompany, Result outDepartment){
        SqlParameter paramCompanyId = new SqlParameter("COMPANYID", Types.INTEGER, true);
        SqlParameter paramCompanyName = new SqlParameter("COMPANYNAME", Types.VARCHAR, true);
        SqlParameter paramDepartmentId = new SqlParameter("DEPARTMENTID", Types.INTEGER, true);
        SqlParameter paramDepartmentName = new SqlParameter("DEPARTMENTNAME", Types.VARCHAR, true);
        enumErrorCode enumRet = ExecCommandbyProcX("HR_COMPANYINFO2_GETBYUSERID", new SqlParameter[]{
                paramCompanyId,
                paramCompanyName,
                paramDepartmentId,
                paramDepartmentName
        });
        if(enumRet == enumErrorCode.enumSuccess){
            outCompany.setIntValue(paramCompanyId.Value.getIntValue());
            outCompany.setStrValue(paramCompanyName.Value.getStrValue());
            outDepartment.setIntValue(paramDepartmentId.Value.getIntValue());
            outDepartment.setStrValue(paramDepartmentName.Value.getStrValue());
        }
        return enumRet;
    }

    //退出登陆
    public void Logout(){
        ExecCommandbyProc("", "SYS_LOGOUT", new SqlParameter[]{
                new SqlParameter("TOKENID", m_strTokenId)
        });
    }

    public enumErrorCode GetLoginName(Result outValue){
        SqlParameter param1 = new SqlParameter("USERNAME", Types.VARCHAR, true);
        enumErrorCode enumRet = ExecCommandbyProcX("HR_LOGINNAME_GET" , new SqlParameter[]{
                param1
        });
        if(enumRet == enumErrorCode.enumSuccess){
            outValue.setStrValue(param1.Value.getStrValue());
        }
        return enumRet;
    }

    public enumErrorCode GetUserPassword(Integer nUserId, Result outValue){
        SqlParameter param1 = new SqlParameter("PASSWORD", Types.VARCHAR, true);
        enumErrorCode enumRet = ExecCommandbyProcX("SYS_PASSWORD_GET" , new SqlParameter[]{
                new SqlParameter("USERID", nUserId) ,
                param1
        });
        if(enumRet == enumErrorCode.enumSuccess){
            outValue.setStrValue(param1.Value.getStrValue());
        }
        return enumRet;
    }

    public boolean CheckSign(String strPubKey, Integer nSecond, String strSign){
        Result outPassword = new Result();
        enumErrorCode enumGetPassword =  GetUserPassword(0, outPassword);
        if(enumGetPassword == enumErrorCode.enumSuccess) {
            String strPasswordMd5 = outPassword.getStrValue();
            strPasswordMd5 = strPasswordMd5.toLowerCase(Locale.ROOT);

            String strDate = EncryptUtil.RestoreDateString(nSecond);
            m_strLoginDate = strDate;

            String strMessage = m_strTokenId.toLowerCase(Locale.ROOT);
            String strEncodingPassword = strPasswordMd5.substring(0, 16) + strDate;
            m_strEncodingTokenId = EncryptUtil.Encrypt(strMessage,strEncodingPassword, true);
            System.out.println(String.format("EncryptTokenId(%s,%s)=%s", strMessage, strEncodingPassword,m_strEncodingTokenId));

            String strMd5e = EncryptUtil.MakeSignEx(strPubKey, strPasswordMd5, strDate);
            return strMd5e.equalsIgnoreCase(strSign);
        }
        else {
            return false;
        }
    }

    //T=TokenItem
    public<T> List<T> GetLoginActiveIPList(String strClassName, Result outValue){
        return GetCommListbyProc("HR_LOGINIP_GETACTIVE", strClassName, new SqlParameter[]{}, outValue);
    }

    //T=PendingTaskItem
    public<T> List<T> GetUserPendingMessage(String strClassName, Result outValue){
        return GetCommListbyProc("HR_USER_GETPENDINGTASK", strClassName, new SqlParameter[]{}, outValue);
    }

    //T=UserLinkItem
    public<T> List<T> GetUrlLinkList(String strClassName, Integer nId, Result outValue){
        return GetCommListbyProc("HR_URLLINK_GET", strClassName, new SqlParameter[]{
                new SqlParameter("ID" , nId)
        }, outValue);
    }

    //T=CompanyItem
    public<T> List<T> GetCompanyInfo(String strClassName, Result outValue){
        return GetCommListbyProc("HR_COMPANY_GETCURRENT", strClassName, new SqlParameter[]{}, outValue);
    }

    //通过验证码修改用户密码
    public enumErrorCode ResetPasswordbyUserName(String strUserName , String strCheckCode, String strPassword){

        SqlParameter paramRet = new SqlParameter("RET", Types.INTEGER, true);

        return ExecCommandbyProcX("HR_USER_RESETPASSWORD", new SqlParameter[]{
                new SqlParameter("USERNAME" , strUserName) ,
                new SqlParameter("CHKCODE", strCheckCode),
                new SqlParameter("PASSWORD", strPassword),
                paramRet
        });
    }

    public enumErrorCode ChangePassword(String strOldPassword, String strNewPassword){
        if(strOldPassword.length() != 32)strOldPassword = MD5Util.getMD5(strOldPassword);
        if(strNewPassword.length() != 32)strNewPassword = MD5Util.getMD5(strNewPassword);

        return ExecCommandbyProcX("SYS_PASSWORD_CHANGE", new SqlParameter[]{
                new SqlParameter("OLDPWD", strOldPassword) ,
                new SqlParameter("NEWPWD", strNewPassword)
        });
    }

    public enumErrorCode SetPassword(Integer nId, String strNewPassword){
        if(strNewPassword.length() != 32)strNewPassword = MD5Util.getMD5(strNewPassword);

        return ExecCommandbyProcX("SYS_PASSWORD_SET", new SqlParameter[]{
                new SqlParameter("USERID" , nId) ,
                new SqlParameter("PASSWORD", strNewPassword)
        });
    }

    public enumErrorCode SetOwnerPassword(String strNewPassword){
        if(strNewPassword.length() != 32)strNewPassword = MD5Util.getMD5(strNewPassword);

        return ExecCommandbyProcX("SYS_PASSWORD_SETOWNER", new SqlParameter[]{
                new SqlParameter("PASSWORD", strNewPassword)
        });
    }

    //判断用户登陆超时否？
    public enumErrorCode IsTimeOut(Result outValue){
        Integer nRet = (m_strTokenId.length() == 0) ? -2 : ExecCommandbyProc("HR_USERID_GETREFRESH", new SqlParameter[]{
                new SqlParameter("REFRESH", 0)
        });
        outValue.setBoolValue(nRet == -1 || nRet == -2);
        if(nRet > 0)
            return enumErrorCode.enumSuccess;
        else
            return enumErrorCode.FromInt(nRet);
    }

    //判断是否为测试用户
    public enumErrorCode IsTestUser(Result outValue){
        SqlParameter paramIsTest = new SqlParameter("ISTEST" , Types.INTEGER, true);
        enumErrorCode enumRet = ExecCommandbyProcX("HR_USER_ISTEST", new SqlParameter[]{
                paramIsTest
        });
        if(enumRet == enumErrorCode.enumSuccess)
            outValue.setBoolValue(paramIsTest.Value.getIntValue() == 1);
        else
            outValue.setBoolValue(false);
        return enumRet;
    }

    //判断是否为超级用户
    public enumErrorCode IsSuperUser2(Result outValue){
        enumErrorCode enumRet = ExecCommandbyProcX("SYS_ISSUPER2PROC");
        if(enumRet == enumErrorCode.enumIsAdmin) {
            outValue.setBoolValue(true);
            return enumErrorCode.enumSuccess;
        }
        else {
            outValue.setBoolValue(false);
            return enumRet;
        }
    }

    //判断是否为管理员
    public enumErrorCode IsAdminUser(Result outValue){
        SqlParameter paramIsTest = new SqlParameter("ISADMIN" , Types.INTEGER, true);
        enumErrorCode enumRet = ExecCommandbyProcX("HR_USER_ISADMIN", new SqlParameter[]{
                paramIsTest
        });
        if(enumRet == enumErrorCode.enumSuccess)
            outValue.setBoolValue(paramIsTest.Value.getIntValue() == 1);
        else
            outValue.setBoolValue(false);
        return enumRet;
    }

    //T=UserItem
    public<T> List<T> GetCurUserInfo(String strClassName, Result outValue){
        return GetCommListbyProc("HR_USER_GETCUR", strClassName, outValue);
    }

    //T=SysOprItem
    public<T> List<T> GetSysOprLogTop10(String strClassName, Result outValue){
        return GetCommListbyProc("SYS_OPRLOG_GETTOPTASK10", strClassName, outValue);
    }

    //T=InputLogItem
    public<T> List<T> GetSysOprLogbyInputId(String strClassName, int nInputId, Result outValue){
        return GetCommListbyProc("RT_INPUTLOG_GETALL", strClassName, new SqlParameter[]{
                new SqlParameter("INPUTID" , nInputId)
        }, outValue);
    }

    public enumErrorCode GetLinkPhonebyUserName(String strUserName, String strChkCode, Result outValue){
        SqlParameter paramLinkPhone = new SqlParameter ("LINKPHONE" , Types.VARCHAR , true);
        SqlParameter paramRet = new SqlParameter("RET" , Types.INTEGER, true);
        enumErrorCode enumRet = ExecCommandbyProcX("HR_USERPHONE_GETBYUSERNAME" , new SqlParameter[]{
                new SqlParameter("USERNAME" , strUserName) ,
                new SqlParameter("CHKCODE" , strChkCode) ,
                paramLinkPhone ,
                paramRet
        });
        if(enumRet == enumErrorCode.enumSuccess){
            outValue.setStrValue(paramLinkPhone.Value.getStrValue());
        }
        return enumRet;
    }

    public enumErrorCode GetSystemParameter(enumSystemParameter enumId, Result outValue){
        SqlParameter param = new SqlParameter("VALUE", Types.VARCHAR, true);
        enumErrorCode enumRet = enumErrorCode.enumSuccess;
        if(m_strTokenId.length() == 0) {
            enumRet = ExecCommandbyProcXNoTokenId("SYS_PARAMETER_GET", new SqlParameter[]{
                    new SqlParameter("ID", enumId.getInt()),
                    param
            });
        }
        else{
            enumRet = ExecCommandbyProcX("SYS_PARAMETER_GETEX", new SqlParameter[]{
                    new SqlParameter("ID", enumId.getInt()),
                    param
            });
        }
        if(enumRet == enumErrorCode.enumSuccess){
            outValue.setStrValue(param.Value.getStrValue());
        }
        return enumRet;
    }

    public enumErrorCode UpdateSystemParameter(enumSystemParameter enumId, String strValue){
        return ExecCommandbyProcX("SYS_PARAMETER_GET", new SqlParameter[]{
                new SqlParameter("ID", enumId.getInt()),
                new SqlParameter("VALUE", strValue)
        });
    }

    public<T> List<T> GetPhoneMenu(String strClassName, Integer nTypeId, Result outValue){
        return GetCommListbyProc("SYS_MENU_GETALLFORPHONE", strClassName, new SqlParameter[]{
                new SqlParameter("TYPEID" , nTypeId)
        }, outValue);
    }

    public<T> List<T> GetSysMenu(String strClassName, Integer nUserId, Integer nPID, boolean bIsAll, Integer nTKId, Result outValue){
        return GetCommListbyProc("SYS_MENU_GETALL", strClassName, new SqlParameter[]{
                new SqlParameter("PID" , nPID) ,
                new SqlParameter("ISALL" , bIsAll ? 1 : 0) ,
                new SqlParameter("TKID" , nTKId),
                new SqlParameter("USERID" , nUserId)
        }, outValue);
    }

    public<T> List<T> GetNeighborTaskList(String strClassName, Integer nId, Integer nIncludePID, Result outValue){
        return GetCommListbyProc("TK_TASK_GETNEIGHBOR", strClassName, new SqlParameter[]{
                new SqlParameter("ID" , nId),
                new SqlParameter("INCLUDEPID", nIncludePID)
        }, outValue);
    }

    //<editor-folder desc="用户管理常用工具">

    public enumErrorCode InsertIPLocation(String strName, String strIP, String strIP2, String strMemo){
        return ExecCommandbyProcX("SYS_IPLOCATION_INSERT", new SqlParameter[] {
                new SqlParameter("NAME" , strName ),
                new SqlParameter("IP" , strIP ),
                new SqlParameter("IP2" , strIP2 ),
                new SqlParameter("MEMO" , strMemo )
        });
    }

    public enumErrorCode UpdateIPLocation(Integer nId, String strName, String strIP, String strIP2, String strMemo){
        return ExecCommandbyProcX("SYS_IPLOCATION_UPDATE", new SqlParameter[] {
                new SqlParameter("ID" , nId ),
                new SqlParameter("NAME" , strName ),
                new SqlParameter("IP" , strIP ),
                new SqlParameter("IP2" , strIP2 ),
                new SqlParameter("MEMO" , strMemo )
        });
    }

    public enumErrorCode DeleteIPLocation(Integer nId){
        return ExecCommandbyProcX("SYS_IPLOCATION_DELETE", nId);
    }

    //T=IPLocationItem
    public<T> List<T> GetIPLocationListFilter(String strClassName, String strFilter, Integer nStart, Integer nLimit, Result outValue){
        return GetCommListbyProc("SYS_IPLOCATION_GETFILTER", strClassName, new SqlParameter[]{
                new SqlParameter("FILTER", strFilter),
                new SqlParameter("START", nStart),
                new SqlParameter("LIMIT", nLimit)
        } ,outValue);
    }

    //T=LiveUserItem
    public<T> List<T> GetLastUserList(String strClassName, Result outValue){
        return GetCommListbyProc("HR_USER_GETLAST", strClassName, outValue);
    }

    //T=UserActiveFreqItem
    public<T> List<T> GetUserActiveFreqList(String strClassName, Result outValue){
        return GetCommListbyProc("HR_USER_GETACTIVEREPORT", strClassName, outValue);
    }

    //</editor-folder>

    //</editor-folder>

    //<editor-folder desc="通用函数">

    //通用单参数ID的命令执行函数
    public enumErrorCode ExecCommandbyProcX(String strProcedure, Integer nId){
        Integer nRet = ExecCommandbyProc(strProcedure, nId);
        return enumErrorCode.FromInt(nRet);
    }

    public enumErrorCode ExecCommandbyProcX(String strProcedure) {
        Integer nRet = ExecCommandbyProc(strProcedure);
        return enumErrorCode.FromInt(nRet);
    }

    public enumErrorCode ExecCommandbyProcX(String strProcedure, SqlParameter[] params) {
        Integer nRet = ExecCommandbyProc(strProcedure, params);
        return enumErrorCode.FromInt(nRet);
    }

    public enumErrorCode ExecCommandbyProcX(String strTokenId, String strProcedure, Integer nId) {
        Integer nRet = ExecCommandbyProc(strTokenId, strProcedure, nId);
        return enumErrorCode.FromInt(nRet);
    }

    public enumErrorCode ExecCommandbyProcX(String strTokenId, String strProcedure) {
        Integer nRet = ExecCommandbyProc(strTokenId, strProcedure);
        return enumErrorCode.FromInt(nRet);
    }

    public enumErrorCode ExecCommandbyProcX(String strTokenId, String strProcedure, SqlParameter[] params) {
        Integer nRet = ExecCommandbyProc(strTokenId, strProcedure, params);
        return enumErrorCode.FromInt(nRet);
    }

    public enumErrorCode ExecCommandbyProcXNoTokenId(String strProcedure, Integer nId) {
        Integer nRet = ExecCommandbyProc("EmptyTokenId", strProcedure, nId);
        return enumErrorCode.FromInt(nRet);
    }

    public enumErrorCode ExecCommandbyProcXNoTokenId(String strProcedure) {
        Integer nRet = ExecCommandbyProc("EmptyTokenId", strProcedure);
        return enumErrorCode.FromInt(nRet);
    }

    public enumErrorCode ExecCommandbyProcXNoTokenId(String strProcedure, SqlParameter[] params) {
        Integer nRet = ExecCommandbyProc("EmptyTokenId", strProcedure, params);
        return enumErrorCode.FromInt(nRet);
    }

    //</editor-folder>

    //<editor-folder desc="收集服务器负载">

    public enumErrorCode InsertServerLoad(String strIP,Integer nPort,Double dCPU,Double dMEMORY,Double dDISK){
        Integer nRet = ExecCommandbyProcNoTokenId("ASS_SERVERLOAD_INSERT", new SqlParameter[] {
                new SqlParameter("IP" , strIP ),
                new SqlParameter("PORT" , nPort ),
                new SqlParameter("CPU" , dCPU ),
                new SqlParameter("MEMORY" , dMEMORY ),
                new SqlParameter("DISK" , dDISK )
        });
        return enumErrorCode.FromInt(nRet);
    }

    public enumErrorCode InsertServerLoad2(String strIP,Integer nPort,Double dCPU,Double dMEMORY,Double dDISK){
        String strSql = "INSERT INTO ASS_SERVERLOAD(IP,PORT,CPU,MEMORY,[DISK])\n" +
                "\tVALUES(?,?,?,?,?)";
        Integer nRet = ExecCommandbySQL(strSql, new SqlParameter[] {
                new SqlParameter("IP" , strIP ),
                new SqlParameter("PORT" , nPort ),
                new SqlParameter("CPU" , dCPU ),
                new SqlParameter("MEMORY" , dMEMORY ),
                new SqlParameter("DISK" , dDISK )
        });
        return enumErrorCode.FromInt(nRet);
    }

    //T=ServerIPGroupItem
    public<T> List<T> GetServerLoadIPGroupWithDateRange(String strClassName, Date dtStart, Date dtStop, Result outValue){
        return GetCommListbyProc("ASS_SERVERLOAD_GETIPGROUP", strClassName, new SqlParameter[]{
                new SqlParameter("STARTDATE" , dtStart) ,
                new SqlParameter("STOPDATE" , dtStop)
        }, outValue);
    }

    //T=ServerLoadItem
    public<T> List<T> GetServerLoadListWithDateRange(String strClassName, Date dtStart, Date dtStop, Result outValue){
        return GetCommListbyProc("ASS_SERVERLOAD_GETRANGE", strClassName, new SqlParameter[]{
                new SqlParameter("STARTDATE" , dtStart) ,
                new SqlParameter("STOPDATE" , dtStop)
        }, outValue);
    }

    public<T> List<T> GetServerLoadListWithDateRange2(String strClassName, Date dtStart, Date dtStop, Result outValue){
        String strSQL = "SELECT     CONVERT(VARCHAR(13), CREATEDATE, 21) + ':00:00+00:00' AS CREATEDATE, IP, MAX(CPU) AS CPU, MAX(MEMORY) AS MEMORY, MAX([DISK]) AS DISK\n" +
                "\tFROM         ASS_SERVERLOAD\n" +
                "\tWHERE     (CREATEDATE BETWEEN ? AND ?) AND (DELETED = 0)\n" +
                "\tGROUP BY CONVERT(VARCHAR(13), CREATEDATE, 21), IP\n" +
                "\tORDER BY IP, CONVERT(VARCHAR(13), CREATEDATE, 21)";

        return GetCommListbySQL(strSQL, strClassName, new SqlParameter[]{
                new SqlParameter("STARTDATE" , dtStart) ,
                new SqlParameter("STOPDATE" , dtStop)
        }, outValue);
    }
    //</editor-folder>
}