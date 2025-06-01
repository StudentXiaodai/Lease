package com.atguigu.lease.common.login;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ClassName: LoginUser
 * Description:
 *
 * @Author: Stu.XiaoDai
 * @Create: 2025/6/2 上午2:42
 * @Version 1.0
 */
@Data
@AllArgsConstructor
public class LoginUser {

    private Long userId;
    private String username;
}
