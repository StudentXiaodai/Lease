package com.atguigu.lease.common.exception;

import com.atguigu.lease.common.result.ResultCodeEnum;
import lombok.Data;

/**
 * ClassName: LeaseException
 * Description:
 *
 * @Author: Stu.XiaoDai
 * @Create: 2025/5/27 上午1:36
 * @Version 1.0
 */
@Data
public class LeaseException extends RuntimeException{
    private Integer code;

    public LeaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public LeaseException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }
}
