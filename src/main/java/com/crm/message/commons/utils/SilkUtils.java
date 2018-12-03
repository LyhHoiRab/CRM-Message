package com.crm.message.commons.utils;

import com.crm.message.commons.consts.SilkConfig;
import com.crm.message.commons.consts.UpyunConfig;
import main.java.com.UpYun;
import org.apache.commons.lang3.StringUtils;
import org.wah.doraemon.security.exception.UtilsException;
import org.wah.doraemon.utils.FileUtils;
import org.wah.doraemon.utils.IDGenerator;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;

public class SilkUtils{

    public static String changeToMp3AndUpload(String amr){
        if(StringUtils.isBlank(amr)){
            throw new UtilsException("音频文件路径不能为空");
        }

        try{
            String command = SilkConfig.SILK_DECODER_PATH + " " + amr + " mp3";

            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(command);
            process.waitFor();

            //转换后的mp3
            String mp3 = FileUtils.getName(amr) + ".mp3";
            FileInputStream inputStream = new FileInputStream(mp3);

            int count = 0;
            while(count == 0){
                count = inputStream.available();
            }

            byte[] file = new byte[count];
            int read = 0;
            while(read < count){
                read += inputStream.read(file, read, count - read);
            }
            inputStream.close();

            //上传
            String fileName = IDGenerator.uuid32();
            //上传文件路径
            String path = MessageFormat.format(UpyunConfig.getVoiceDir(), fileName);

            UpYun upyun = UpyunUtils.create(UpyunConfig.OPEATOR, UpyunConfig.BUCKET, UpyunConfig.PASSWORD);
            if(UpyunUtils.upload(upyun, path, file)){
                Files.deleteIfExists(Paths.get(mp3));
                Files.deleteIfExists(Paths.get(amr));

                return UpyunConfig.URL + path;
            }

            throw new UtilsException("音频上传出错");
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }

    public static String changeToAmrAndUpload(String mp3){
        if(StringUtils.isBlank(mp3)){
            throw new UtilsException("音频文件路径不能为空");
        }

        try{
            String amr = FileUtils.getName(mp3) + ".amr";

            //转换
            String command = MessageFormat.format("ffmpeg -i {0} -ac 1 -ar 8000 {1}", mp3, amr);

            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(command);
            process.waitFor();

            //转换后的amr
            FileInputStream inputStream = new FileInputStream(amr);
            int count = 0;
            while(count == 0){
                count = inputStream.available();
            }

            byte[] file = new byte[count];
            int read = 0;
            while(read < count){
                read += inputStream.read(file, read, count - read);
            }
            inputStream.close();

            //上传
            String fileName = IDGenerator.uuid32();
            //上传文件路径
            String path = MessageFormat.format(UpyunConfig.getAmrDir(), fileName);

            UpYun upyun = UpyunUtils.create(UpyunConfig.OPEATOR, UpyunConfig.BUCKET, UpyunConfig.PASSWORD);
            if(UpyunUtils.upload(upyun, path, file)){
                Files.deleteIfExists(Paths.get(mp3));
                Files.deleteIfExists(Paths.get(amr));

                return UpyunConfig.URL + path;
            }

            throw new UtilsException("音频上传出错");
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }
}
