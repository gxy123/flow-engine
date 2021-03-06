package com.wei.basic.flowengine.configer;

import com.wei.service.filter.HttpServletRequestBodyFilter;
import com.wei.service.filter.HttpServletRequestXssFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @version 1.0
 * @author: wangqiaobin
 * @date : 2018/8/25
 */
@Configuration
public class FilterConfiguration {

    @Bean
    public FilterRegistrationBean requestFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new HttpServletRequestBodyFilter());
        registration.addUrlPatterns("/*");
        registration.setName("cache_body_request");
        registration.setOrder(Integer.MAX_VALUE - 1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean xssFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new HttpServletRequestXssFilter());
        registration.addUrlPatterns("/*");
        registration.setName("xss_filter");
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }

}
