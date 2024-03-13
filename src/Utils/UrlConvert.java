package Utils;

import java.io.UnsupportedEncodingException;

public class UrlConvert {
    public static String GetUnicode(String strData) throws UnsupportedEncodingException {
        byte sourceKey [] = strData.getBytes("iso8859-1");
        return new String (sourceKey,"UTF-8");
    }

    /**
     * 获取字符串的unicode编码序列
     * @param s string
     * @return unicode编码后的字符串
     */
    public static String toUNICODE(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) < 256) {//ASC11表中的字符码值不够4位,补00
                sb.append("\\u00");
            }
            else {
                sb.append("\\u");
            }
            sb.append(Integer.toHexString(s.charAt(i)));
        }
        return sb.toString();
    }


    public static void main(String[] args) throws UnsupportedEncodingException {
        String s = "简介";
        System.out.println(s+" --的unicode编码是："+gbEncoding(s));
        System.out.println(gbEncoding(s) + " --转换成中文是："+decodeUnicode(gbEncoding(s)));

        //System.out.println(gbEncoding(s) + " --转换成中文是："+decodeUnicode("\\u7b80\\u4ecb"));
    }

    /*
     * 中文转unicode编码
     */
    public static String gbEncoding(final String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }
    /*
     * unicode编码转中文
     */
    public static String decodeUnicode(final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }


}
