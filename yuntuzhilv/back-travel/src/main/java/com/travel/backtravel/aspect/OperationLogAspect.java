package com.travel.backtravel.aspect;

import com.travel.backtravel.annotation.OperationLog;
import com.travel.backtravel.entity.SystemLog;
import com.travel.backtravel.service.SystemLogService;
import com.travel.backtravel.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final SystemLogService systemLogService;
    private final JwtUtil jwtUtil;

    @Around("@annotation(com.travel.backtravel.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        OperationLog anno = method.getAnnotation(OperationLog.class);

        long startTime = System.currentTimeMillis();
        SystemLog systemLog = new SystemLog();
        systemLog.setContent(anno.value());
        systemLog.setModule(anno.module().isEmpty() ? inferModule(point.getTarget().getClass().getSimpleName()) : anno.module());
        systemLog.setOperatorType(anno.type().isEmpty() ? inferOperatorType(method.getName()) : anno.type());
        systemLog.setStatus("success");
        systemLog.setCreateTime(LocalDateTime.now());

        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                systemLog.setIpAddress(getClientIp(request));
                String token = resolveToken(request);
                if (token != null) {
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    systemLog.setOperatorId(userId != null ? userId : 0L);
                }
            }
        } catch (Exception e) {
            log.warn("获取日志上下文信息失败: {}", e.getMessage());
        }

        try {
            Object result = point.proceed();
            long elapsed = System.currentTimeMillis() - startTime;
            systemLog.setExecutionTime((int) elapsed);
            saveLogAsync(systemLog);
            return result;
        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - startTime;
            systemLog.setExecutionTime((int) elapsed);
            systemLog.setStatus("fail");
            systemLog.setErrorMessage(ex.getMessage());
            saveLogAsync(systemLog);
            throw ex;
        }
    }

    @Async
    protected void saveLogAsync(SystemLog systemLog) {
        try {
            systemLogService.saveLog(systemLog);
        } catch (Exception e) {
            log.error("保存操作日志失败: {}", e.getMessage());
        }
    }

    private String inferOperatorType(String methodName) {
        String lower = methodName.toLowerCase();
        if (lower.contains("add") || lower.contains("insert") || lower.contains("save") || lower.contains("create")) {
            return "add";
        }
        if (lower.contains("update") || lower.contains("edit") || lower.contains("modify") || lower.contains("change")) {
            return "update";
        }
        if (lower.contains("delete") || lower.contains("remove")) {
            return "delete";
        }
        return "update";
    }

    private String inferModule(String className) {
        if (className.contains("Attraction")) return "景点管理";
        if (className.contains("Hotel")) return "酒店管理";
        if (className.contains("Traffic")) return "交通管理";
        if (className.contains("User")) return "用户管理";
        if (className.contains("Itinerary")) return "行程管理";
        if (className.contains("Booking")) return "预订管理";
        if (className.contains("Feedback")) return "反馈管理";
        if (className.contains("Log")) return "日志管理";
        if (className.contains("Order")) return "订单管理";
        return "系统管理";
    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        token = request.getHeader("token");
        if (token != null && !token.isEmpty()) {
            return token;
        }
        token = request.getParameter("token");
        return token;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
