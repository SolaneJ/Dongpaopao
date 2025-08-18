package com.github.solanej;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData {

    private int code;

    private String message;

    private Object data;

    public static ResponseData success() {
        return new ResponseData(200, "Success", null);
    }

    public static ResponseData success(Object data) {
        return new ResponseData(200, "Success", data);
    }

    public static ResponseData failed(String message) {
        return new ResponseData(400, message, null);
    }

    public static ResponseData tooManyRequest() {
        return new ResponseData(429, "请求过于频繁，请稍后再试！", null);
    }

    public static ResponseData error(String message) {
        return new ResponseData(500, message, null);
    }
}
