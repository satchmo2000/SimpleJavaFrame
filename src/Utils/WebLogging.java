package Utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WebLogging {
    private String strFile = "";
    private FileWriter write = null;
    public WebLogging(String strLogFile){
        try {
            strFile = strLogFile;
            Path path = Paths.get(strLogFile);
            boolean bExist = Files.exists(path);
            write = new FileWriter(strLogFile, bExist);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void WriteLine(String strLine){
        if (write != null){
            try {
                DateTime dtNow = new DateTime();

                String strWriteLine = String.format("%s %s\r\n", dtNow.toDateString("yyyy-MM-dd hh:mm:ss"), strLine);
                write.write(strWriteLine);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public void WriteLine(String strMethod, String strLine){
        if (write != null){
            try {
                DateTime dtNow = new DateTime();

                String strWriteLine = String.format("[%s]%s %s\r\n", strMethod, dtNow.toDateString("yyyy-MM-dd hh:mm:ss"), strLine);
                write.write(strWriteLine);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public void Close(){
        try {
            if (write != null) {
                write.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
