package com.moon.lib.request.exception;

/**
 * Created by Moon on 2018/7/25.
 * 与服务器约定的异常
 */

public class ErrorCode {
    /**
     * 成功
     */
    public static final int _200 = 200;
    /**
     * 失败
     */
    public static final int _300 = 300;
    /**
     * 业务异常
     */
    public static final int _400 = 400;
    /**
     * 未登录
     */
    public static final int _403 = 403;
    /**
     * 用户信息不存在
     */
    public static final int _404 = 404;
    /**
     * 未开户
     */
    public static final int _406 = 406;
    /**
     * 未绑卡
     */
    public static final int _407 = 407;
    /**
     * 未激活 用户未实名认证
     */
    public static final int _408 = 408;
    /**
     * 系统维护中
     */
    public static final int _409 = 409;
    /**
     * 账户已锁定
     */
    public static final int _410 = 410;
    /**
     * 账户无效
     */
    public static final int _411 = 411;
    /**
     * 余额不足
     */
    public static final int _412 = 412;
    /**
     * token过期
     */
    public static final int _402 = 402;
}
