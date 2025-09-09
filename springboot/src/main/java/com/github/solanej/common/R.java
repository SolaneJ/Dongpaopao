package com.github.solanej.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 全局统一返回类
 *
 * @author solanej
 * @since 2025/09/08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class R {

    private Integer errCode;

    private String msg;

    private Object data;

    public static R success() {
        return new R();
    }

    public static R success(Object data) {
        return new R(0, "success", data);
    }

    public static R error(String msg) {
        return new R(11, msg, null);
    }

    public static R fatal() {
        return new R(99, null, null);
    }
}
