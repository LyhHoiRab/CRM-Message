package com.crm.message.commons.consts;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:beans.properties")
public class SilkConfig{

    public static String SILK_DECODER_PATH;

    public static String AMR_PATH;

    public static String MP3_PATH;

    @Value("${ffmpeg.silk.decoder.path}")
    public void setSilkDecoderPath(String silkDecoderPath){
        if(StringUtils.isBlank(SILK_DECODER_PATH)){
            SILK_DECODER_PATH = silkDecoderPath;
        }
    }

    @Value("${ffmpeg.silk.amr.path}")
    public void setAmrPath(String amrPath){
        if(StringUtils.isBlank(AMR_PATH)){
            AMR_PATH = amrPath;
        }
    }

    @Value("${ffmpeg.silk.mp3.path}")
    public void setMp3Path(String mp3Path){
        if(StringUtils.isBlank(MP3_PATH)){
            MP3_PATH = mp3Path;
        }
    }
}
