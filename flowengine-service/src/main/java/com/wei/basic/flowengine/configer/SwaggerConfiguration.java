package com.wei.basic.flowengine.configer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.collect.Lists;
import com.wei.common.util.DateUtil;
import com.wei.service.configure.EnvironmentDefine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @version 1.0
 * @author: wangqiaobin
 * @date : 2018/5/30
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration implements WebMvcConfigurer {

    @Resource
    private EnvironmentDefine environmentDefine;

    @Bean
    public Docket createRestApi() {
        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = Lists.newArrayList();
        ticketPar.name("City").description("城市")
                .modelRef(new ModelRef("string")).parameterType("header").defaultValue("1")
                .required(false).build();
        pars.add(ticketPar.build());
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(environmentDefine.isTest() || environmentDefine.isDev())
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.wei.basic.flowengine.web"))
                .paths(PathSelectors.any())
                .build().globalOperationParameters(pars);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("后端技术文档[flowengine-server]")
                .description("给大家推荐一个技术博客")
                .termsOfServiceUrl("www.example.com")
                .version("1.0")
                .build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("PUT", "DELETE", "GET", "POST")
                .allowedHeaders("Origin", "X-Requested-With", "Content-Type", "Accept", "Accept-Encoding", "City")
                .allowCredentials(true).maxAge(3600);
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter longConverter = buildLongConverter();
        converters.add(longConverter);
    }

    private MappingJackson2HttpMessageConverter buildLongConverter() {
        MappingJackson2HttpMessageConverter longConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat(DateUtil.DEFAULT_DATE_FORMAT));
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        longConverter.setObjectMapper(objectMapper);
        return longConverter;
    }
}
