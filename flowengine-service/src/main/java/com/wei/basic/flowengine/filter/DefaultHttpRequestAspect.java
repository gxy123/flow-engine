package com.wei.basic.flowengine.filter;

import com.alibaba.fastjson.JSONObject;
import com.wei.client.base.CommonCode;
import com.wei.client.base.CommonResult;
import com.wei.common.annotaion.Validate;
import com.wei.common.annotaion.ValidateType;
import com.wei.common.util.DistribID;
import com.wei.common.util.HttpRequestUtil;
import com.wei.common.util.IPv4Util;
import com.wei.common.util.SessionUtil;
import com.wei.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author wangqiaobin
 * @date 2016/12/10
 */
@Slf4j
@Aspect
@Configuration
public class DefaultHttpRequestAspect {

    public static final Integer ANT_LIMIT = 1500;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private DistribID distribID = new DistribID();

    @Pointcut("execution(* com.wei.basic.flowengine.web.api..*.*(..)) @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void pointcut() {
    }

    private Long increment(String key, Long expire) {
        long count = stringRedisTemplate.opsForValue().increment(key, 1);
        if (count == 1) {
            stringRedisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return count;
    }

    @Around("pointcut()")
    public Object interceptor(ProceedingJoinPoint point) throws Throwable {
        long begin = System.currentTimeMillis();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();
        //跨域
        response.setHeader("P3P", "CP=\"IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT\"");
        String traceId = String.valueOf(distribID.nextId());
        MDC.put("ip", IPv4Util.getIpAddr(request));
        MDC.put("traceId", traceId);
        MethodSignature signature = (MethodSignature) point.getSignature();
        //获取被拦截的方法
        Method method = signature.getMethod();
        //获取request中的所有cookie
        Map<String, String> cookies = HttpRequestUtil.getHttpServletRequestCookie(request);
        Map<String, String> param = HttpRequestUtil.getRequestGetData(request);

        Long userId = null;
        log.info("[API] request={} user_id={} method={} origin={} param={} cookie={} ", request.getRequestURI(), userId, request.getMethod(),
                request.getHeader("Origin"),
                "POST".equals(request.getMethod()) ? HttpRequestUtil.getRequestPostData(request) : JSONObject.toJSONString(param),
                JSONObject.toJSONString(cookies));
        //风控处理
        Long limit = increment("request_limit_" + IPv4Util.getIpAddr(request), 60L);
        if (limit > ANT_LIMIT) {
            log.warn("be_attacked ip=" + IPv4Util.getIpAddr(request) + " user_id=" + userId);
            return CommonResult.errorReturn("禁止访问");
        }
        Validate annotation = method.getAnnotation(Validate.class);
        boolean validated = true;
        if (annotation != null && annotation.action().equals(ValidateType.DEFAULT)) {
            //默认校验规则
        }
        if (annotation != null && annotation.action().equals(ValidateType.BACK)) {
            //后台接口校验规则
        }
        if (validated) {

        }
        if (annotation != null && !validated && !annotation.action().equals(ValidateType.MISS)) {
            return CommonResult.errorReturn(CommonCode.LOGIN_ERROR);
        }
        SessionUtil.setRequestContext(null, null, null, null, null);
        Object proceed;
        try {
            proceed = point.proceed();
        } finally {
            SessionUtil.clear();
        }
        if (proceed != null && proceed.getClass().isAssignableFrom(CommonResult.class)) {
            ((CommonResult) proceed).setTraceId(traceId);
        }
        long taking = System.currentTimeMillis() - begin;
        log.info("request={} user_id={} method={} taking={} resp={}", request.getRequestURI(), userId, request.getMethod(), taking, StringUtil.getLimitLengthString(JSONObject.toJSONString(proceed), 100, "..."));
        return proceed;
    }
}
