package Services;

import Models.JsonObjectEx;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;

public class BaseView {
    private HttpServletRequest request;
    private Model model;
    //JSP页面的根地址
    private String strAppRoot = "app";

    //首页视图地址
    private String strHomePath = "/index";

    public BaseView(){
    }

    public BaseView(HttpServletRequest request, Model model){
        this.request = request;
        this.model = model;
    }

    public BaseView(HttpServletRequest request){
        this.request = request;
    }

    public<T> T IsNull(T t, T v){
        return t == null ? v : t;
    }

    //<editor-folder desc="页面提示+刷新">

    public String RedirectHomeView(){
        return String.format("redirect:%s", strHomePath);
    }

    public String RedirectView(String strPath){
        return String.format("redirect:%s", strPath);
    }

    //模仿.Net下的Model对象
    //返回地址格式：app/Controller名称/Action名称
    public <T> String ReturnView(T t){
        if(model != null) {
            model.addAttribute("Model", t);
        }
        else {
            String strURI = requestDispatcherPath(request);
            System.out.println(String.format("No Model in %s%s.", strAppRoot, strURI));
        }
        return ReturnView(strAppRoot);
    }

    //返回地址格式：app/Controller名称/Partial/Action名称
    public<T> String ReturnPartialView(T t){
        if(model != null) {
            model.addAttribute("Model", t);
        }
        else {
            String strURI = requestDispatcherPath(request);
            Integer nPos = strURI.lastIndexOf('/');
            if(nPos > 0){
                System.out.println(String.format("No Model in %s%s/Partial%s.", strAppRoot, strURI.substring(0, nPos), strURI.substring(nPos)));
            }
            else
                System.out.println(String.format("No Model in %s/Partial%s.", strAppRoot, strURI));
        }
        return ReturnPartialView(strAppRoot);
    }

    public String ReturnView(){
        return ReturnView(strAppRoot);
    }

    //返回格式：app/{Controller}/{Action}
    public String ReturnView(String strBaseURI){
        String strURI = requestDispatcherPath(request);
        return String.format("%s%s", strBaseURI, strURI);
    }

    public String ReturnPartialView(){
        return ReturnPartialView(strAppRoot);
    }

    //返回格式：app/{Controller}/Partial/{Action}
    public String ReturnPartialView(String strBaseURI){
        //获取request.requestDispatcherPath属性
        String strURI = requestDispatcherPath(request);

        Integer nPos = strURI.lastIndexOf('/');
        if(nPos > 0){
            return String.format("%s%s/Partial%s", strBaseURI, strURI.substring(0, nPos), strURI.substring(nPos));
        }
        else
            return String.format("%s/Partial%s", strBaseURI, strURI);
    }

    public Object ReturnWithAlert(ClientServicesX.enumErrorCode enumRet){
        StringBuilder sb = new StringBuilder();
        sb.append("<script>");
        sb.append(String.format("alert('%s');" , String.format("操作错误，错误码=%d", enumRet.getInt())));
        sb.append(String.format("location.href='%s';" , request.getHeader("referer")));
        sb.append("</script>");
        return sb.toString();
    }

    public Object ReturnWithAlert(String strContent){
        StringBuilder sb = new StringBuilder();
        sb.append("<script>");
        sb.append(String.format("alert('%s');" , strContent));
        sb.append(String.format("location.href='%s';" , request.getHeader("referer")));
        sb.append("</script>");
        return sb.toString();
    }

    public Object ReturnLoginWithAlert(ClientServicesX.enumErrorCode enumRet) {
        try {
            String strUrl = request.getRequestURI();
            String strContextPath = request.getContextPath();
            if(strUrl.startsWith(strContextPath)){
                strUrl = strUrl.substring(strContextPath.length(), strUrl.length());
            }
            String strParam = request.getQueryString();
            if(strParam != null && strParam.length() > 0)
                strUrl += String.format("?%s", strParam);
            model.addAttribute("url", strUrl);

            if (enumRet == ClientServicesX.enumErrorCode.enumNoRight) {
                return ReturnWithAlert(enumRet);
            } else {
                String strErrMsg = "系统异常，请与管理员联系！";

                if (enumRet == ClientServicesX.enumErrorCode.enumNoTokenId)
                    strErrMsg = "登录令牌失效，请重新登录后再继续！";
                else if (enumRet == ClientServicesX.enumErrorCode.enumConnectError)
                    strErrMsg = "数据库连接超时，请重新登录后再继续！";
                else if (enumRet == ClientServicesX.enumErrorCode.enumTimeOut)
                    strErrMsg = "登录超时，请重新登录后再继续！";
                else if(enumRet == ClientServicesX.enumErrorCode.enumErrorProcess)
                    strErrMsg = "数据读取不规范（存储过程中获取数据之前含有INSERT、UPDATE、DELETE等语句）！";
                model.addAttribute("msg", URLEncoder.encode(strErrMsg, "UTF-8"));
                return RedirectHomeView();
            }
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
            return RedirectHomeView();
        }
    }

    public Object ReturnJsonWithMsg(boolean bRet , String strMsg){

        JsonObjectEx json = new JsonObjectEx();
        json.putAll(bRet, strMsg);
        return json;
    }

    public Object ReturnJsonWithMsg(boolean bRet , String strSuccess, String strFail){

        JsonObjectEx json = new JsonObjectEx();
        json.putAll(bRet, bRet ? strSuccess : strFail);
        return json;
    }

    private String requestDispatcherPath(HttpServletRequest request){
        String strURI = request.getRequestURI();
        try {
            Field[] fields = request.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equalsIgnoreCase("requestDispatcherPath")) {
                    field.setAccessible(true);
                    strURI = (String) field.get(request);
                }
            }
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }
        String strContextPath = request.getContextPath();
        if(strContextPath.length() > 0 && strURI.startsWith(strContextPath)){
            strURI = strURI.substring(strContextPath.length(), strURI.length());
        }
        return strURI;
    }

    //</editor-folder>

}