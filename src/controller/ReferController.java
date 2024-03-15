package controller;

import Models.*;
import Models.Entities.Base.ProcessItem;
import Models.Entities.Company.CompanyItem;
import Models.Entities.DefaultPageDataItem;
import Models.Entities.Task.TaskItem;
import Services.BaseView;
import Services.ClientServicesM;
import Services.ClientServicesX;
import Services.Result;
import Utils.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//@Controller注解：采用注解的方式，可以明确地定义该类为处理请求的Controller类；
@Controller
//@RequestMapping()注解：用于定义一个请求映射，value为请求的url，值为 /helloworld 说明，该请求首页请求，method用以指定该请求类型，一般为get和post；
@RequestMapping("EmptyRoot")
public class ReferController {
    @RequestMapping("/index")
    public String Index(String url, HttpServletRequest request, Model model) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        SessionManager sm = new SessionManager(request);

        DBConfig config = new DBConfig();
        ClientServicesX client = new ClientServicesX("");
        String strUrl = (url == null) ? "" : url;
        if(strUrl.equalsIgnoreCase("/") || strUrl.equalsIgnoreCase("")){
            //判断TokenId是否失效
            if(sm.getTokenId().length() > 0){
                client.setTokenId(sm.getTokenId());
                Result outValue = new Result();
                ClientServicesX.enumErrorCode enumRet = client.IsTimeOut(outValue);
                if(enumRet == ClientServicesX.enumErrorCode.enumSuccess && !outValue.getBoolValue()){
                    String strFirstPage = config.getProperty("FirstPage", "redirect:/User/showUser.do");
                    Integer nProjectId = sm.getProjectId();
                    if(nProjectId > 0){
                        model.addAttribute("tkid", nProjectId);
                    }
                    else {
                        model.addAttribute("msg", URLEncoder.encode("登录成功！", "UTF-8"));
                    }
                    return strFirstPage;
                }
            }
        }
        model.addAttribute("Url", strUrl);

        String strLogo = config.getProperty("Logo", config.getProperty("Logo0", "rtlogo_full.png"));
        String strTitle = config.getProperty("Title",config.getProperty("Title0", "RT工程项目管理云平台"));
        String strWebSite = config.getProperty("WebSite",config.getProperty("WebSite0", "model.edesigner.cn"));
        String strCompanyReg = config.getProperty("CompanyReg", "0");
        String strShowQRCode = config.getProperty("ShowQRCode","0");
        String strShowQRCode1 = config.getProperty("ShowQRCode1", "0");
        String strShowQRCode1001 = config.getProperty("ShowQRCode1001", "0");
        String strShowQRCode1002 = config.getProperty("ShowQRCode1002", "0");

        model.addAttribute("CompanyReg", Integer.parseInt(strCompanyReg));
        model.addAttribute("Logo", strLogo);
        model.addAttribute("ProjectTitle", strTitle);
        model.addAttribute("WebSite", strWebSite);
        model.addAttribute("ShowQRCode", Integer.parseInt(strShowQRCode));
        model.addAttribute("ShowQRCode1", Integer.parseInt(strShowQRCode1));
        model.addAttribute("ShowQRCode1001", Integer.parseInt(strShowQRCode1001));
        model.addAttribute("ShowQRCode1002", Integer.parseInt(strShowQRCode1002));

        Result outCheck = new Result();
        if(client.CheckDB(outCheck)){
            Result outValue = new Result();
            if(client.GetSystemParameter(ClientServicesX.enumSystemParameter.enumProgramVersion, outValue) == ClientServicesX.enumErrorCode.enumSuccess){
                model.addAttribute("Version1", outValue.getStrValue());
            }
            if(client.GetSystemParameter(ClientServicesX.enumSystemParameter.enumProgramVersion_ZJ, outValue) == ClientServicesX.enumErrorCode.enumSuccess){
                model.addAttribute("Version1001", outValue.getStrValue());
            }
            if(client.GetSystemParameter(ClientServicesX.enumSystemParameter.enumProgramVersion_19, outValue) == ClientServicesX.enumErrorCode.enumSuccess){
                model.addAttribute("Version1002", outValue.getStrValue());
            }
        }
        else{
            model.addAttribute("DBError", outCheck.getStrValue());
        }

        RSAEncrypt rsa = new RSAEncrypt();
        RSAEncrypt.genKeyPair();
        String strPubKey = rsa.getPubKey();
        String strPriKey = rsa.getPriKey();
        model.addAttribute("PubKey", strPubKey);
        sm.setPriKey(strPriKey);
        sm.setPubKey(strPubKey);

        return "index";
    }

    @RequestMapping(value="/Login",method=RequestMethod.POST)
    public String Login(String UserName, String Password, String RSAPassword, String Second, String Sign, String Url, HttpServletRequest request, Model model) throws UnsupportedEncodingException {
        SessionManager sm = new SessionManager(request);

        String strSecond = IsNull(Second, "");
        String strSign = IsNull(Sign, "");

        //传输密码的模式不安全，可以通过中转站获取密码后再通过服务器验证后返回给客户端
        String strRSAPassword = IsNull(RSAPassword, "");
        if(strRSAPassword.length() > 0){
            try {
                String strPriKey = sm.getPriKey();
                Password = RSAEncrypt.decrypt(strRSAPassword, strPriKey);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        ClientServicesX client = new ClientServicesX("");
        String strIP = request.getRemoteAddr();
        if(strIP.equalsIgnoreCase("0:0:0:0:0:0:0:1")){
            strIP = "127.0.0.1";
        }
        Integer nPort = request.getRemotePort();
        client.setIPPort(strIP, nPort);

        ClientServicesX.enumErrorCode enumRet = client.Login(UserName, Password , true);
        if(enumRet == ClientServicesX.enumErrorCode.enumSuccess){
            if(strSign.length() > 0 && strSecond.length() > 0){
                String strPubKey = sm.getPubKey();
                Integer nSecond = Integer.parseInt(strSecond);
                String strDate = EncryptUtil.RestoreDateString(nSecond);
                System.out.println(String.format("Check Date=%s,Restore Date=%s", new DateTime().toDateString("yyyyMMddHHmmss"), strDate));

                boolean bValidPubKey = client.CheckSign(strPubKey, nSecond, strSign);
                if(!bValidPubKey){
                    //无效公钥
                    String strErrorMessage = "无效的公钥（检查电脑的时间是否与Internet时间同步）或输入密码错误，解密失败！";
                    System.out.println(strErrorMessage);
                    model.addAttribute("msg", URLEncoder.encode(strErrorMessage,"UTF-8"));

                    return new BaseView().RedirectHomeView();
                }
                else{
                    //客户端验证解密TokenId（采用密码前16位加密TokenId所得）
                    String strEncodingTokenId = client.getEncodingTokenId();
                    String strDecodePassword = MD5Util.getMD5(Password).toLowerCase(Locale.ROOT).substring(0, 16) + strDate;

                    String strTokenId = EncryptUtil.Encrypt(strEncodingTokenId, strDecodePassword, false);
                    System.out.println(String.format("DecryptTokenId(%s,%s)=%s", strEncodingTokenId, strDecodePassword, strTokenId));
                }
                sm.setLoginDate(client.getLoginDate());
            }
            sm.setTokenId(client.getTokenId().toLowerCase(Locale.ROOT));

            //判断隶属企业是否被激活
            Result outCompany = new Result();
            List<CompanyItem> listCompany = client.GetCompanyInfo(CompanyItem.class.getName(), outCompany);
            if(outCompany.toErrorCode() == ClientServicesX.enumErrorCode.enumSuccess && listCompany.size() > 0){

                String strErrorMessage = "";

                CompanyItem ci = listCompany.get(0);
                Date dtNow = new Date();
                Date dtCreate = new DateTime(ci.CREATEDATE).Add(DateTime.enumAddType.enumMonth, 1);
                if (ci.ISLOCK == 1){
                    //被停用
                    enumRet = ClientServicesX.enumErrorCode.enumLostDefine;
                    strErrorMessage = "该用户所在企业已经被停用！";
                }
                else if (ci.ISACTIVE == 0  && dtNow.compareTo(dtCreate) < 0){
                    //判断试用期是否超过30天
                    enumRet = ClientServicesX.enumErrorCode.enumWrongFormat;
                    strErrorMessage = "该用户所在企业试用期已过！";
                }

                if(enumRet == ClientServicesX.enumErrorCode.enumWrongFormat ||
                        enumRet ==  ClientServicesX.enumErrorCode.enumLostDefine) {

                    model.addAttribute("msg", URLEncoder.encode(strErrorMessage, "UTF-8"));
                    return new BaseView().RedirectHomeView();
                }

                Result outTask = new Result();
                List<TaskItem> listTask = client.GetNeighborTaskList(TaskItem.class.getName(), 0, -2, outTask);
                if (outTask.toErrorCode() == ClientServicesX.enumErrorCode.enumSuccess && listTask.size() == 1){
                    TaskItem ti0 = listTask.get(0);
                    sm.SelectProject(ti0.ID, ti0.NAME, ti0.TYPEID);
                }
                else
                    sm.SelectProject(0, "", 0);

                Result outLoginName = new Result();
                ClientServicesX.enumErrorCode enumLoginName = client.GetLoginName(outLoginName);
                if (enumLoginName == ClientServicesX.enumErrorCode.enumSuccess){
                    sm.setLoginName(outLoginName.getStrValue());
                }

                Result outDepartment = new Result();
                ClientServicesX.enumErrorCode enumLoginInfo = client.GetCompanyInfo2byLoginUser(outCompany, outDepartment);

                if (enumLoginInfo == ClientServicesX.enumErrorCode.enumSuccess){
                    sm.setCompanyId(outCompany.getIntValue());
                    sm.setCompanyName(outCompany.getStrValue());
                    sm.setDepartmentId(outDepartment.getIntValue());
                    sm.setDepartmentName(outDepartment.getStrValue());
                }
            }

            Result outType = new Result();
            //指定地图（0：百度，1：高德）
            client.GetSystemParameter(ClientServicesX.enumSystemParameter.enumMapType, outType);
            sm.setMapType(Integer.parseInt(outType.getStrValue()));

            //指定出库单模式（0：由品控部手动生成，1：由销售提出申请时自动生成）
            client.GetSystemParameter(ClientServicesX.enumSystemParameter.enumAutoInput, outType);
            sm.setAutoInput(Integer.parseInt(outType.getStrValue()) == 1);

            DBConfig config = new DBConfig();
            String strFirstPage = config.getProperty("FirstPage", "redirect:/User/showUser.do");
            Integer nProjectId = sm.getProjectId();
            if(nProjectId > 0){
                model.addAttribute("tkid", nProjectId);
            }
            else {
                //model.addAttribute("msg", URLEncoder.encode("登录成功！", "UTF-8"));
            }
            if(Url != null && Url.length() > 0)
                return new BaseView().RedirectView(Url);
            else
                return strFirstPage;
        }
        else {
            String strErrorMessage = "登录异常，请与管理员联系！";
            if (enumRet == ClientServicesX.enumErrorCode.enumConnectError){
                strErrorMessage = "连接数据库失败，请与管理员联系！";
            }else if(enumRet == ClientServicesX.enumErrorCode.enumNotActive){
                strErrorMessage = "用户未激活，请通过邮件激活或与管理员联系！";
            }else if(enumRet == ClientServicesX.enumErrorCode.enumNoRight){
                strErrorMessage = "登录密码错，请重试！";
            }else if(enumRet == ClientServicesX.enumErrorCode.enumNoData){
                strErrorMessage = "输错密码超过3次，请明天再来试试！";
            }else if(enumRet == ClientServicesX.enumErrorCode.enumTimeOut){
                strErrorMessage = "没有这个用户，再试就成黑户了！";
            }else if(enumRet == ClientServicesX.enumErrorCode.enumIsLock){
                strErrorMessage = "该用户被管理员限制，请稍后再试！";
            }else if(enumRet == ClientServicesX.enumErrorCode.enumWrongFormat){
                strErrorMessage = "该用户所在企业试用期已过！";
            }else if(enumRet ==  ClientServicesX.enumErrorCode.enumLostDefine){
                strErrorMessage = "该用户所在企业已经被停用！";
            }
            model.addAttribute("msg", URLEncoder.encode(strErrorMessage,"UTF-8"));

            return new BaseView().RedirectHomeView();
        }
    }

    @RequestMapping(value="/LoginOut", method = RequestMethod.GET)
    public String LoginOut(HttpServletRequest request,HttpServletResponse httpServletResponse) {
        SessionManager sm = new SessionManager(request);
        ClientServicesX client = new ClientServicesX(sm.getTokenId());
        client.Logout();
        sm.Clear();

        return new BaseView().RedirectHomeView();
    }

    private final int nPageSize = 20;

    @RequestMapping(value="/ProcessMgr", method = RequestMethod.GET)
    public String ProcessMgr(Integer page, String key, HttpServletRequest request, Model model){
        SessionManager sm = new SessionManager(request);
        ClientServicesM client = new ClientServicesM(sm.getTokenId());

        String strKey = IsNull(key, "");
        Integer nPage = IsNull(page, 1);
        Integer nStart = (nPage - 1) * nPageSize;

        Result outValue = new Result();

        List<ProcessItem> list = client.GetProcessListFilter(ProcessItem.class.getName(), strKey, nStart, nPageSize, outValue);

        if(outValue.toErrorCode() == ClientServicesX.enumErrorCode.enumSuccess){
            DefaultPageDataItem<ProcessItem> dpdi = new DefaultPageDataItem<ProcessItem>(outValue.getIntValue(), nPage, nPageSize);
            dpdi.Data = list;
            dpdi.Key = strKey;
            return new BaseView(request,model).ReturnView(dpdi);
        }
        else{
            return (String) new BaseView(request, model).ReturnLoginWithAlert(outValue.toErrorCode());
        }
    }

    @RequestMapping(value="/DefineJson", method = RequestMethod.GET)
    @ResponseBody
    public Object DefineJson(String text, HttpServletRequest request, Model model){
        JsonObjectEx json = new JsonObjectEx();
        String strProcedure = IsNull(text, "");

        if (strProcedure.length() > 0){
            SessionManager sm = new SessionManager(request);
            ClientServicesX client = new ClientServicesX(sm.getTokenId());

            Result outValue = new Result();
            boolean bRet = client.GetProcDefine(strProcedure, true, outValue);

            if (bRet){
                json.putAll(true, "读取成功！", outValue.getStrValue());
            }else{
                json.putAll(false, "读取失败，检查存在过程中是否有更新语句！返回值：" + outValue.toErrorCode().toString());
            }
        }
        else {
            json.putAll(false, "请提供存储过程后再尝试！");
        }
        return json;
    }

    //<editor-folder desc="QRCode">

    @RequestMapping("/WebQRCode")
    public void WebQRCode(HttpServletRequest request, HttpServletResponse response){
        String strUrl = FileUtil.UrlPath(request, "/index");
        CreateQRCodeImage(strUrl, request, response);
    }

    @RequestMapping("/ZJAPP")
    public void ZJAPP(String version, HttpServletRequest request, HttpServletResponse response){
        String strUrl = FileUtil.UrlPath(request, String.format("/update/zjwork-v%s.apk", version));
        CreateQRCodeImage(strUrl, request, response);
    }

    @RequestMapping("DDAPP")
    public void DDAPP(String version, HttpServletRequest request, HttpServletResponse response){
        String strUrl = FileUtil.UrlPath(request, String.format("/update/ddwork-v%s.apk", version));
        CreateQRCodeImage(strUrl, request, response);
    }

    @RequestMapping("/POLEAPP")
    public void POLEAPP(String version, HttpServletRequest request, HttpServletResponse response){
        String strUrl = FileUtil.UrlPath(request, String.format("/update/work-v%s.apk", version));
        CreateQRCodeImage(strUrl, request, response);
    }

    @RequestMapping("/QRImage")
    public void CreateQRCodeImage(String Url, HttpServletRequest request, HttpServletResponse response){
        try {
            String strContextPath = request.getContextPath();
            String imgUrl = strContextPath + "/Images/logo.png";
            String imgFile = FileUtil.MapPath(request, imgUrl);

            BufferedImage image = QRCodeUtil.createImage(Url, imgFile, true);

            response.setDateHeader("Expires", 0L);
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");

            response.setContentType("image/jpeg");
            ServletOutputStream out = response.getOutputStream();
            ImageIO.write(image, "jpg", out);

            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //</editor-folder>

    public<T> T IsNull(T t, T v){
        return t == null ? v : t;
    }
}
