package Utils;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;

//填充模式为PKCS5PADDING，但JS中使用的是CryptoJS.pad.Pkcs7，有待于确认
public class EncryptUtil {

    //根据秒值生成登录时刻的实际时间（格式：yyyyMMddHHmmss）
    public static String RestoreDateString(Integer nSecond){
        DateTime dtNow = new DateTime();
        if (dtNow.get(DateTime.enumAddType.enumSecond) < nSecond) {
            dtNow.Add(DateTime.enumAddType.enumSecond, -10);
        }

        String strDate = dtNow.toDateString("yyyyMMddHHmm");
        if (nSecond < 10) {
            strDate += "0";
        }
        strDate += nSecond.toString();

        return strDate;
    }

    //循环生成新的md5值，使末位值与初始的md5末位值一致
    public static String RandomMd5(String md5c) {
        String md5e = MD5Util.getMD5(md5c).toLowerCase(Locale.ROOT);
        System.out.println(String.format("First MD5 is %s" , md5e));
        String md5e_last = md5e.substring(31, 32);
        md5e = MD5Util.getMD5(md5e).toLowerCase(Locale.ROOT);
        Integer index = 1;
        while (!md5e.substring(31, 32).equalsIgnoreCase(md5e_last)) {
            md5e = MD5Util.getMD5(md5e).toLowerCase(Locale.ROOT);
            index++;
            //System.out.println(String.format("Loop %d is %s", index, md5e));
        }
        System.out.println(String.format("Loop Count=%d,md5=%s", index, md5e));
        return md5e;
    }

    public static String MakeSignEx(String publicKey, String PasswordMd5, String LoginDate){
        //密码片段越短越安全，建议截取后4位（取决于MD5破解库的查重率）
        String md5c = PasswordMd5.substring(28, 32) + LoginDate + publicKey;
        String md5e = RandomMd5(md5c);

        return md5e;
    }

    public static String MakeSign(String publicKey, String Password, String LoginDate){
        String strPasswordMd5 = MD5Util.getMD5(Password).toLowerCase(Locale.ROOT);
        return MakeSignEx(publicKey, strPasswordMd5, LoginDate);
    }

    private static final String INIT_VECTOR = "abcdefghabcdefgh"; // 初始化向量，应为16位

    //涉及密码为16位，取实际密码的前后8位，若原密码不足8位，则密码被多次复制使总长度大于8位
    private static  String Expand16(String data){
        Integer nDataLength = data.length();
        if(nDataLength == 0)
            return "";
        else{
            while(nDataLength < 8) {
                data += data;
                nDataLength *= 2;
            }
        }
        data = data.substring(0, 8) + data.substring(nDataLength - 8, nDataLength);
        return data;
    }

    private static String EncryptbyAES(String message, String password){
        try {
            if(password.length() == 0)
                return message;

            password = Expand16(password);

            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(password.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private static String DecryptbyAES(String message, String password){
        try {
            if(password.length() == 0)
                return message;

            password = Expand16(password);

            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(password.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(message));

            return new String(original);

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static String Encrypt(String message, String password, boolean bEncrypt){
        if(bEncrypt)
            return EncryptbyAES(message, password);
        else
            return DecryptbyAES(message, password);
    }
}
