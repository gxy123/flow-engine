package com.wei.basic.flowengine.filter;

import com.google.common.collect.Maps;
import com.wei.client.base.CommonCode;
import com.wei.client.base.CommonResult;
import com.wei.common.annotaion.Validate;
import com.wei.common.annotaion.ValidateType;
import com.wei.common.annotaion.ValidateWeight;
import com.wei.common.util.HttpClientUtil;
import com.wei.passport.client.define.PassportHostDef;
import com.wei.passport.client.define.UserTypeDef;
import com.wei.passport.client.value.ValidateVO;
import com.wei.passport.session.SessionUtil;
import com.wei.service.configure.EnvironmentDefine;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author wangqiaobin
 * @date 2016/12/10
 */
@Slf4j
@Aspect
@Configuration
public class DefaultHttpRequestAspect {

    @Resource
    private EnvironmentDefine environmentDefine;

    @Value("${spring.application.name}")
    private String serverName;

    @Pointcut("@annotation(com.wei.common.annotaion.Validate)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object interceptor(ProceedingJoinPoint point) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = attributes.getRequest();
        MethodSignature signature = (MethodSignature) point.getSignature();
        //获取被拦截的方法
        Method method = signature.getMethod();
        ValidateVO validate = null;
        Validate annotation = method.getAnnotation(Validate.class);
        boolean validated = true;
        if (annotation != null && annotation.action().equals(ValidateType.DEFAULT)) {
            //默认校验规则
            validate = remoteValidate(request, "website");
            validated = Objects.nonNull(validate) && validate.getUserType().equals(UserTypeDef.DEFAULT);
        }
        if (annotation != null && annotation.action().equals(ValidateType.EMPLOYER)) {
            //员工接口校验规则
            validate = remoteValidate(request, "jebe");
            validated = Objects.nonNull(validate) && validate.getUserType().equals(UserTypeDef.EMPLOYER);
        }
        if (annotation != null && annotation.action().equals(ValidateType.CLUSTER)) {
            //开放平台接口校验规则
            validate = remoteValidate(request, "saas");
            validated = Objects.nonNull(validate) && validate.getUserType().equals(UserTypeDef.CLUSTER);
        }
        if (annotation != null && annotation.action().equals(ValidateType.PROVIDER)) {
            //服务商接口校验规则
            validate = remoteValidate(request, "provider");
            validated = Objects.nonNull(validate) && validate.getUserType().equals(UserTypeDef.PROVIDER);
        }
        if (annotation != null && annotation.action().equals(ValidateType.SIGN)) {
            //签名校验
        }
        if (validated) {
            if (validate != null) {
                SessionUtil.setRequestContext(validate);
            }
        }
        if (annotation != null && !validated && !annotation.weight().equals(ValidateWeight.MISS)) {
            CommonResult<Object> result = CommonResult.errorReturn(CommonCode.LOGIN_ERROR);
            result.setTraceId(MDC.get("traceId"));
            return result;
        }
        //执行下一步
        Object proceed = point.proceed();
        if (proceed != null && proceed.getClass().isAssignableFrom(CommonResult.class)) {
            ((CommonResult) proceed).setTraceId(MDC.get("traceId"));
        }
        return proceed;
    }

    private ValidateVO remoteValidate(HttpServletRequest request, String action) {
        List<String> host;
        if (environmentDefine.isProd()) {
            host = PassportHostDef.prodHost;
        } else if (environmentDefine.isPre()) {
            host = PassportHostDef.predHost;
        } else {
            host = PassportHostDef.testHost;
        }
        Map<String, String> header = Maps.newHashMap();
        if (request.getCookies() != null) {
            String value = "";
            for (Cookie cookie : request.getCookies()) {
                value += cookie.getName() + "=" + cookie.getValue() + ";";
            }
            header.put("Cookie", value);
            header.put("ServerName", serverName);
        }
        CommonResult<ValidateVO> post = HttpClientUtil.post(host, String.format("/passport/auth?action=%s", action), "", header, ValidateVO.class);
        return post.getResult();
    }
}
