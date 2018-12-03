package com.crm.message.commons.consts;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UpyunConfig{

    private static final SimpleDateFormat fomatter = new SimpleDateFormat("yyyyMMdd");

    //账号相关
    public static final String OPEATOR  = "unesmall";
    public static final String BUCKET   = "cloned";
    public static final String PASSWORD = "unesmall123456";
    public static final String URL      = "http://upyun.ijucaimao.cn";

    //目录
    public static final String getVoiceDir(){
        return "/voice/wechat/" + fomatter.format(new Date()) + "/{0}.mp3";
    }

    //音频目录
    public static final String getAmrDir(){
        return "／amr/wechat/" + fomatter.format(new Date()) + "/{0}.amr";
    }
}
