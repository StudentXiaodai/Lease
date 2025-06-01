package com.atguigu.lease.common.login;

/**
 * ClassName: LoginUserHolder
 * Description:
 *
 * @Author: Stu.XiaoDai
 * @Create: 2025/6/2 上午2:41
 * @Version 1.0
 */
public class LoginUserHolder {
    public static ThreadLocal<LoginUser> threadLocal = new ThreadLocal<>();

    public static void setLoginUser(LoginUser loginUser) {
        threadLocal.set(loginUser);
    }

    public static LoginUser getLoginUser() {
        return threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }
}
