package Utils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    public String LoadText(String filePath){
        try {
            FileInputStream fin = new FileInputStream(filePath);
            InputStreamReader reader = new InputStreamReader(fin);
            BufferedReader buffReader = new BufferedReader(reader);
            String strTmp = "";
            StringBuilder sb = new StringBuilder();
            while ((strTmp = buffReader.readLine()) != null) {
                sb.append(strTmp);
                sb.append("\n");
            }
            buffReader.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return "";
    }

    public static String readFileAsString(String fileName)throws Exception{
        Path path = Paths.get(fileName);
        if(Files.exists(path))
            return new String(Files.readAllBytes(path));
        else
            return "";
    }

    public static String MapPath(HttpServletRequest request, String strUrl){
        //来源：https://www.manongdao.com/article-2260835.html
        // 获取当前servlet的ServletContext
        ServletContext servletContext = request.getServletContext();
        // 获取当前servlet文件所在的工作目录
        return servletContext.getRealPath(strUrl);
    }

    public static String UrlPath(HttpServletRequest request, String strUrl) {
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        return String.format("%s%s%s", basePath, request.getContextPath(), strUrl);
    }
}