package com.atguigu.hellomp.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: MPConfiguration
 * Description:
 *              当执行分页查询时(如使用 Page 对象)，拦截器会自动将查询改写成带有 LIMIT 的分页查询
 * @Author: Stu.XiaoDai
 * @Create: 2025/5/25 下午10:31
 * @Version 1.0
 */
@Configuration
public class MPConfiguration {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
