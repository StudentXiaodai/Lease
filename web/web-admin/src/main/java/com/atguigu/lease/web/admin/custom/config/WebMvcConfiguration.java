package com.atguigu.lease.web.admin.custom.config;

import com.atguigu.lease.web.admin.custom.converter.StringToBaseEnumConverterFactory;
import com.atguigu.lease.web.admin.custom.interceptor.AuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ClassName: WebMvcConfiguration
 * Description:
 *              注册上述的`StringToItemTypeConverter`类型转换器
 * @Author: Stu.XiaoDai
 * @Create: 2025/5/22 上午1:33
 * @Version 1.0
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private StringToBaseEnumConverterFactory stringToBaseEnumConverterFactory;

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.authenticationInterceptor).addPathPatterns("/admin/**").excludePathPatterns("/admin/login/**" );
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(this.stringToBaseEnumConverterFactory);
    }
}
