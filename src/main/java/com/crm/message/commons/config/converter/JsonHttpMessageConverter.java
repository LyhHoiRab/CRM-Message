package com.crm.message.commons.config.converter;

import com.google.gson.Gson;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.wah.doraemon.utils.GsonUtils;

public class JsonHttpMessageConverter extends GsonHttpMessageConverter{

    private Gson gson;

    public JsonHttpMessageConverter(){
        super.setGson(getGson());
    }

    public Gson getGson(){
        if(gson == null){
            gson = GsonUtils.getGson();
        }

        return gson;
    }
}
