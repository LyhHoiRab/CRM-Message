package com.crm.message.commons.consts;

import org.apache.commons.codec.binary.Base64;

public class JPushConfig{

    public static final Base64 base64 = new Base64();

    public static final String PUSH_API = "https://bjapi.push.jiguang.cn/v3/push";

    // 监控App
    public static String APP_KEY = "024b39e181fa9a902f3bf2ff";
    public static String SECRET = "ce63bb0a3419815ff58c204c";
    // 桌面App
    public static String T_APP_KEY = "e96dc3e49eae0705b63a0e77";
    public static String T_SECRET = "21bb8bcc94febd7745cbbf61";
    //public static final String APP_KEY     = "447d3c125d1f667ace40d169";
    //public static final String SECRET      = "c69a4d2b91a73be8f48f2494";
    //public static final String AUTH_STRING = "Basic " + new String(base64.encode(new String(APP_KEY + ":" + SECRET).getBytes()));

    public static final String PLATFORM_ANDROID  = "android";
    public static final String PLATFORM_IOS      = "ios";
    public static final String PLATFORM_WINPHONE = "winphone";
}
