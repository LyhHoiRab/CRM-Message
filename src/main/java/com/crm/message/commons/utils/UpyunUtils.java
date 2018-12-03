package com.crm.message.commons.utils;

import main.java.com.UpYun;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.wah.doraemon.security.exception.UtilsException;

import java.util.Map;

public class UpyunUtils{

    private static final int DEFAULT_TIME_OUT = 120;

    public static UpYun create(String opeator, String bucket, String password){
        if(StringUtils.isBlank(opeator)){
            throw new UtilsException("又拍云操作员账号不能为空");
        }
        if(StringUtils.isBlank(bucket)){
            throw new UtilsException("又拍云空间名不能为空");
        }
        if(StringUtils.isBlank(password)){
            throw new UtilsException("又拍云操作员密码不能为空");
        }

        UpYun upyun = new UpYun(bucket, opeator, password);
        upyun.setDebug(false);
        upyun.setTimeout(DEFAULT_TIME_OUT);

        return upyun;
    }

    public static boolean upload(UpYun upyun, String path, CommonsMultipartFile file){
        if(upyun == null){
            throw new UtilsException("又拍云客户端不能为空");
        }
        if(StringUtils.isBlank(path)){
            throw new UtilsException("又拍云上传路径不能为空");
        }
        if(file == null || file.getBytes().length == 0){
            throw new UtilsException("又拍云上传文件不能为空");
        }

        return upload(upyun, path, file.getBytes());
    }

    public static boolean upload(UpYun upyun, String path, byte[] bytes){
        if(upyun == null){
            throw new UtilsException("又拍云客户端不能为空");
        }
        if(StringUtils.isBlank(path)){
            throw new UtilsException("又拍云上传路径不能为空");
        }
        if(bytes == null || bytes.length == 0){
            throw new UtilsException("又拍云上传文件不能为空");
        }

        return upyun.writeFile(path, bytes);
    }

    public static  boolean delete(UpYun upyun, String path){
        if(upyun == null){
            throw new UtilsException("又拍云客户端不能为空");
        }
        if(StringUtils.isBlank(path)){
            throw new UtilsException("又拍云上传路径不能为空");
        }

        return upyun.deleteFile(path);
    }

    public static Map<String, String> getFileInfo(UpYun upyun, String path){
        if(upyun == null){
            throw new UtilsException("又拍云客户端不能为空");
        }
        if(StringUtils.isBlank(path)){
            throw new UtilsException("又拍云上传路径不能为空");
        }

        return upyun.getFileInfo(path);
    }
}
