package com.atguigu.lease.common.exception;

import com.atguigu.lease.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ClassName: GlobalExceptionHandler
 * Description:
 *
 * @Author: Stu.XiaoDai
 * @Create: 2025/5/23 下午8:12
 * @Version 1.0
 */
@ControllerAdvice // 用于声明处理全局Controller方法异常的类
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class) // 用于声明处理异常的方法，`value`属性用于声明该方法处理的异常类型
    @ResponseBody // 表示将方法的返回值作为HTTP的响应体
    public Result handle(Exception e){
        e.printStackTrace();
        return Result.fail();
    }

    @ExceptionHandler(LeaseException.class) // 用于声明处理异常的方法，`value`属性用于声明该方法处理的异常类型
    @ResponseBody // 表示将方法的返回值作为HTTP的响应体
    public Result handle(LeaseException e){
        e.printStackTrace();
        return Result.fail(e.getCode(), e.getMessage());
    }
}
