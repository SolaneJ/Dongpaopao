package com.github.solanej.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.solanej.entity.Log;
import com.github.solanej.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * 日志切面类
 * 用于记录系统操作日志
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogAspect {

    private final LogService logService;
    private final ObjectMapper objectMapper;

    /**
     * 定义切点：拦截所有Controller层的方法
     */
    @Pointcut("execution(* com.github.solanej.controller.*.*(..))")
    public void logPointCut() {}

    /**
     * 环绕通知
     * 记录请求的方法、参数、执行时间和结果
     */
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 开始时间
        long startTime = System.currentTimeMillis();
        
        // 执行目标方法
        Object result = null;
        String errorInfo = null;
        String resultStr = "SUCCESS";
        
        try {
            result = point.proceed();
        } catch (Throwable throwable) {
            resultStr = "FAILURE";
            errorInfo = throwable.getMessage();
            throw throwable;
        } finally {
            // 结束时间
            long endTime = System.currentTimeMillis();
            // 执行时间
            final long finalExecutionTime = endTime - startTime;
            // 创建最终变量，用于lambda表达式引用
            final String finalResultStr = resultStr;
            final String finalErrorInfo = errorInfo;
            final ProceedingJoinPoint finalPoint = point;
            
            // 异步记录日志，不影响主流程
            CompletableFuture.runAsync(() -> {
                try {
                    recordLog(finalPoint, finalExecutionTime, finalResultStr, finalErrorInfo);
                } catch (Exception e) {
                    // 如果记录日志失败，记录到文件日志中
                    log.error("记录日志失败: {}", e.getMessage());
                }
            });
        }
        
        return result;
    }

    /**
     * 记录日志到数据库
     */
    private void recordLog(ProceedingJoinPoint point, long executionTime, String result, String errorInfo) {
        try {
            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            
            HttpServletRequest request = attributes.getRequest();
            
            // 获取方法签名
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            
            // 创建日志对象
            Log log = new Log();
            
            // 设置日志信息
            log.setModule(getModuleName(method));
            log.setOperationType(getOperationType(method));
            log.setDescription(method.getDeclaringClass().getName() + "." + method.getName());
            log.setRequestUrl(request.getRequestURL().toString());
            log.setRequestMethod(request.getMethod());
            log.setRequestParams(getRequestParams(point));
            log.setIp(request.getRemoteAddr());
            log.setExecutionTime(executionTime);
            log.setResult(result);
            log.setErrorInfo(errorInfo);
            
            // TODO: 从Session或Token中获取用户信息
            // 这里需要根据实际的用户认证机制来获取用户ID和用户名
            log.setUserId("unknown");
            log.setUserName("unknown");
            
            // 保存日志
            logService.saveLog(log);
            
        } catch (Exception e) {
            // 记录到文件日志
            log.error("记录日志异常: {}", e.getMessage());
        }
    }

    /**
     * 获取模块名称
     */
    private String getModuleName(Method method) {
        // 获取类名（去除Controller后缀）
        String className = method.getDeclaringClass().getSimpleName();
        if (className.endsWith("Controller")) {
            return className.substring(0, className.length() - 10);
        }
        return className;
    }

    /**
     * 获取操作类型
     */
    private String getOperationType(Method method) {
        String methodName = method.getName().toLowerCase();
        if (methodName.contains("login")) {
            return "LOGIN";
        } else if (methodName.contains("logout")) {
            return "LOGOUT";
        } else if (methodName.contains("add") || methodName.contains("create")) {
            return "CREATE";
        } else if (methodName.contains("update") || methodName.contains("edit")) {
            return "UPDATE";
        } else if (methodName.contains("delete") || methodName.contains("remove")) {
            return "DELETE";
        } else if (methodName.contains("query") || methodName.contains("get") || methodName.contains("list")) {
            return "QUERY";
        } else if (methodName.contains("upload")) {
            return "UPLOAD";
        } else if (methodName.contains("download")) {
            return "DOWNLOAD";
        }
        return "OTHER";
    }

    /**
     * 获取请求参数
     */
    private String getRequestParams(ProceedingJoinPoint point) {
        try {
            Object[] args = point.getArgs();
            if (args == null || args.length == 0) {
                return "";
            }
            
            // 如果参数过多或过大，截取部分显示
            if (args.length > 5) {
                return Arrays.toString(Arrays.copyOf(args, 5)) + "...";
            }
            
            // 尝试转换为JSON字符串
            return objectMapper.writeValueAsString(args);
        } catch (Exception e) {
            // 如果转换失败，使用toString()方法
            return Arrays.toString(point.getArgs());
        }
    }
}