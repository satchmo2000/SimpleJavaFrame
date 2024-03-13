package Models;

import com.alibaba.fastjson.JSONObject;

public class JsonObjectEx extends JSONObject {
    public void putAll(Boolean code, String err_msg, Object data) {
        putAll(new JSONObject() {{
            put("code", code);
            put("err_msg" ,err_msg);
            put("data", data);
        }});
    }

    public void putAll(Boolean code, String err_msg) {
        putAll(new JSONObject() {{
            put("code", code);
            put("err_msg" ,err_msg);
        }});
    }
    public void putAll(Integer code, String err_msg, Object data) {
        putAll(new JSONObject() {{
            put("code", code);
            put("err_msg" ,err_msg);
            put("data", data);
        }});
    }

    public void putAll(Integer code, String err_msg) {
        putAll(new JSONObject() {{
            put("code", code);
            put("err_msg" ,err_msg);
        }});
    }
}
