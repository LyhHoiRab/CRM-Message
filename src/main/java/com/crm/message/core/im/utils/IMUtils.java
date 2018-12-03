package com.crm.message.core.im.utils;

import com.crm.api.core.im.consts.IMMessageType;
import com.crm.api.core.im.entity.IMMessage;
import com.crm.api.core.im.entity.IMMsgBody;
import com.crm.api.core.im.entity.IMUser;
import com.crm.message.core.im.utils.response.IMResponse;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.wah.doraemon.security.exception.UtilsException;
import org.wah.doraemon.utils.DateUtils;
import org.wah.doraemon.utils.HttpClientUtils;

import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class IMUtils{

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private static final String  SUCCESS         = "OK";
    private static final String  FAIL            = "FAIL";

    //账号导入
    private static final String ACCOUNT_IMPORT = "https://console.tim.qq.com/v4/im_open_login_svc/account_import?usersig={0}&identifier={1}&sdkappid={2}&contenttype=json";
    //发送消息
    private static final String SEND_MSG = "https://console.tim.qq.com/v4/openim/sendmsg?usersig={0}&identifier={1}&sdkappid={2}&contenttype=json";
    //批量发送消息
    private static final String BATCH_SEND_MSG = "https://console.tim.qq.com/v4/openim/batchsendmsg?usersig={0}&identifier={1}&sdkappid={2}&contenttype=json";

    public static void accountImport(String sdkAppId, String administrator, String sig, IMUser user){
        if(StringUtils.isBlank(sig)){
            throw new UtilsException("IM用户签名不能为空");
        }
        if(StringUtils.isBlank(sdkAppId)){
            throw new UtilsException("IM应用ID不能为空");
        }
        if(StringUtils.isBlank(administrator)){
            throw new UtilsException("IM应用管理员不能为空");
        }
        if(user == null || StringUtils.isBlank(user.getRelationId())){
            throw new UtilsException("IM用户登录账号不能为空");
        }

        //API
        String url = MessageFormat.format(ACCOUNT_IMPORT, sig, administrator, sdkAppId);
        //参数
        Map<String, String> params = new HashMap<String, String>();
        params.put("Identifier", user.getRelationId());
        params.put("Nick", user.getName());
        params.put("FaceUrl", user.getHeadImgUrl());

        CloseableHttpClient client = HttpClientUtils.createHttpsClient();

        try{
            HttpPost     post     = HttpClientUtils.post(url, null, params);
            HttpResponse response = client.execute(post);

            if(!HttpClientUtils.isOk(response)){
                throw new UtilsException(EntityUtils.toString(response.getEntity()));
            }

            IMResponse imResponse = HttpClientUtils.parse(response, IMResponse.class);
            //验证请求结果
            if(imResponse.getStatus().equalsIgnoreCase(FAIL)){
                throw new UtilsException(imResponse.getErrorInfo());
            }
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }finally{
            HttpClientUtils.close(client);
        }
    }

    public static void sendMsg(String sdkAppId, String administrator, String sig, IMMessage message){
        if(StringUtils.isBlank(sig)){
            throw new UtilsException("IM用户签名不能为空");
        }
        if(StringUtils.isBlank(sdkAppId)){
            throw new UtilsException("IM应用ID不能为空");
        }
        if(StringUtils.isBlank(administrator)){
            throw new UtilsException("IM应用管理员不能为空");
        }
        if(message == null){
            throw new UtilsException("IM信息不能为空");
        }
        if(StringUtils.isBlank(message.getToAccount())){
            throw new UtilsException("IM信息接收人不能为空");
        }

        //API
        String url = MessageFormat.format(SEND_MSG, sig, administrator, sdkAppId);

        try(CloseableHttpClient client = HttpClientUtils.createHttpsClient()){
            HttpPost     post     = HttpClientUtils.post(url, null, message);
            HttpResponse response = client.execute(post);

            if(!HttpClientUtils.isOk(response)){
                throw new UtilsException(EntityUtils.toString(response.getEntity()));
            }

            IMResponse imResponse = HttpClientUtils.parse(response, IMResponse.class);
            //验证请求结果
            if(imResponse.getStatus().equalsIgnoreCase(FAIL)){
                throw new UtilsException(imResponse.getErrorInfo());
            }
        }catch(Exception e) {
            throw new UtilsException(e.getMessage(), e);
        }
    }

    public static void sendMsg(String sdkAppId, String administrator, String sig, String message, String fromAccount,
                               List<String> toAccounts){
        if(StringUtils.isBlank(sig)){
            throw new UtilsException("IM用户签名不能为空");
        }
        if(StringUtils.isBlank(sdkAppId)){
            throw new UtilsException("IM应用ID不能为空");
        }
        if(StringUtils.isBlank(administrator)){
            throw new UtilsException("IM应用管理员不能为空");
        }
        if(toAccounts == null || toAccounts.isEmpty()){
            throw new UtilsException("IM信息接收人不能为空");
        }

        //API
        String url = MessageFormat.format(BATCH_SEND_MSG, sig, administrator, sdkAppId);

        try(CloseableHttpClient client = HttpClientUtils.createHttpsClient()){
            //参数
            Map<String, Object> params = new HashMap<String, Object>();
            //body
            IMMsgBody body = new IMMsgBody();
            Map<String, Object> msgContent = new HashMap<String, Object>();
            msgContent.put("Text", message);
            body.setMsgType(IMMessageType.TIM_TEXT_ELEM);
            body.setMsgContent(msgContent);
            params.put("MsgBody", Arrays.asList(body));
            //消息同步
            params.put("SyncOtherMachine", 1);
            //时间戳
            Long timestamp = DateUtils.timestamp(true);
            //随机数
            params.put("MsgRandom", timestamp);
            //时间
            params.put("MsgTimeStamp", timestamp);
            //发送者
            if(StringUtils.isNotBlank(fromAccount)){
                params.put("From_Account", fromAccount);
            }
            //接收者
            params.put("To_Account", toAccounts);
            //类型
            params.put("MsgType", IMMessageType.TIM_TEXT_ELEM);

            HttpPost     post     = HttpClientUtils.post(url, null, (Object) params);
            HttpResponse response = client.execute(post);

            if(!HttpClientUtils.isOk(response)){
                throw new UtilsException(EntityUtils.toString(response.getEntity()));
            }

            IMResponse imResponse = HttpClientUtils.parse(response, IMResponse.class);
            //验证请求结果
            if(imResponse.getStatus().equalsIgnoreCase(FAIL)){
                throw new UtilsException(imResponse.getErrorInfo());
            }
        }catch(Exception e) {
            throw new UtilsException(e.getMessage(), e);
        }
    }

    public static void sendTextMsg(String sdkAppId, String administrator, String sig, String fromAccount,
                                   String toAccount, String... contents){
        if(StringUtils.isBlank(sig)){
            throw new UtilsException("IM用户签名不能为空");
        }
        if(StringUtils.isBlank(sdkAppId)){
            throw new UtilsException("IM应用ID不能为空");
        }
        if(StringUtils.isBlank(administrator)){
            throw new UtilsException("IM应用管理员不能为空");
        }
        if(StringUtils.isBlank(toAccount)){
            throw new UtilsException("IM信息接收人不能为空");
        }

        //创建消息体
        IMMessage message = IMMessageUtils.createTextMsg(fromAccount, toAccount, contents);
        //发送信息
        sendMsg(sdkAppId, administrator, sig, message);
    }
}
