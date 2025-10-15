package com.github.solanej.handler;

import com.github.solanej.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 *
 * @author solanej
 * @since 2025/09/08
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有异常
     *
     * @param e 异常
     * @return 全局统一返回类
     */
    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        log.error("Service Fatal:", e);
        return R.fatal();
    }
}
