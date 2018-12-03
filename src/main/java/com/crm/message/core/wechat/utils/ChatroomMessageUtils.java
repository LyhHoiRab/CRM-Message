package com.crm.message.core.wechat.utils;

import com.crm.api.core.wechat.consts.WechatFriendType;
import com.crm.api.core.wechat.consts.WechatMessageStatus;
import com.crm.api.core.wechat.consts.WechatMessageType;
import com.crm.api.core.wechat.entity.ChatroomMessage;
import com.crm.api.core.wechat.entity.WechatFriend;
import com.crm.message.commons.consts.SilkConfig;
import com.crm.message.commons.utils.SilkUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.wah.doraemon.security.exception.UtilsException;
import org.wah.doraemon.utils.HttpClientUtils;
import org.wah.doraemon.utils.IDGenerator;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatroomMessageUtils{

    public static String extract(ChatroomMessage message){
        if(message == null){
            return "";
        }

        String content = message.getContent();

        if(WechatMessageStatus.RECEIVE.equals(message.getStatus())){
            content = content.substring(content.indexOf(":") + 1);
        }

        switch(message.getType()){
            case SHARE:
            case TEXT:
            case IMAGE:
            case VIDEO:
            case LUCKY_PACKAGE_TRANSFER_CONFIRM:
                return content;
            case VOICE:
                return voiceParse(content);
            case EMOTICONS:
                return emojiParse(content);
            case TRANSFER:
                return transferParse(content);
            case LUCKY_PACKAGE:
                return luckyPackageParse(content);
            case LUCKY_PACKAGE_SEND:
                return luckyPackageSendParse(content);
            case SYSTEM:
                return systemParse(content);
            case TRANSFER_CONFIRM:
                return transferConfirmParse(content);
            case TRANSFER_DRAW:
                return transferDrawParse(content);
            case TRANSFER_SEND:
                return transferSendParse(content);
            case PERSON_CARD:
                return personCardParse(content);
            case LOCATION:
                return locationParse(content);
            case ONLINE_TALK:
                return onlineTalkParse(content);
            default:
                return "";
        }
    }

    public static String emojiParse(String content){
        if(StringUtils.isBlank(content)){
            return "";
        }

        try{
            Pattern pattern = Pattern.compile("<msg>[\\s\\S]*</msg>");
            Matcher matcher = pattern.matcher(content);

            if(matcher.find()){
                //匹配需要的内容
                content = matcher.group();
                //解释xml
                Document document = DocumentHelper.parseText(content);
                //根节点
                Element root = document.getRootElement();
                //正文
                Element emoji = root.element("emoji");
                //图片路径
                String cdnurl = emoji.attributeValue("cdnurl");

                if(StringUtils.isNotBlank(cdnurl)){
                    cdnurl = cdnurl.replace("http*#*", "http:");
                }

                return cdnurl;
            }

            return "";
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }

    public static String transferParse(String content){
        if(StringUtils.isBlank(content)){
            return "";
        }

        try{
            //解释xml
            Document document = DocumentHelper.parseText(content);
            //根节点
            Element root   = document.getRootElement();
            Element appmsg = root.element("appmsg");
            //正文
            Element des = appmsg.element("des");
            //截取内容
            content = des.getTextTrim();

            //转账备注
            Element wcpayinfo = appmsg.element("wcpayinfo");
            //正文
            Element paymemo = wcpayinfo.element("pay_memo");
            //截取备注
            String memo = paymemo.getTextTrim();

            Pattern pattern = Pattern.compile("收到转账([1-9]\\d*|0)(\\.\\d{1,2})?元");
            Matcher matcher = pattern.matcher(content);

            if(matcher.find()){
                return MessageFormat.format("{0}[{1}]", matcher.group(), memo);
            }

            return content;
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }

    public static String transferSendParse(String content){
        if(StringUtils.isBlank(content)){
            return "";
        }

        try{
            //解释xml
            Document document = DocumentHelper.parseText(content);
            //根节点
            Element root   = document.getRootElement();
            Element appmsg = root.element("appmsg");
            //正文
            Element des = appmsg.element("des");
            //截取内容
            content = des.getTextTrim();

            //转账备注
            Element wcpayinfo = appmsg.element("wcpayinfo");
            //正文
            Element paymemo = wcpayinfo.element("pay_memo");
            //截取备注
            String memo = paymemo.getTextTrim();

            Pattern pattern = Pattern.compile("转账([1-9]\\d*|0)(\\.\\d{1,2})?元");
            Matcher matcher = pattern.matcher(content);

            if(matcher.find()){
                return MessageFormat.format("{0}{1}[{2}]", "发出", matcher.group(), memo);
            }

            return content;
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }

    public static String transferConfirmParse(String content){
        if(StringUtils.isBlank(content)){
            return "";
        }

        try{
            //解释xml
            Document document = DocumentHelper.parseText(content);
            //根节点
            Element root   = document.getRootElement();
            Element appmsg = root.element("appmsg");
            //正文
            Element des = appmsg.element("des");
            //截取内容
            content = des.getTextTrim();
            //转账备注
            Element wcpayinfo = appmsg.element("wcpayinfo");
            //正文
            Element paymemo = wcpayinfo.element("pay_memo");
            //截取备注
            String memo = paymemo.getTextTrim();

            Pattern pattern = Pattern.compile("收到转账([1-9]\\d*|0)(\\.\\d{1,2})?元");
            Matcher matcher = pattern.matcher(content);

            if(matcher.find()){
                return MessageFormat.format("{0}{1}[{2}]", "已确认", matcher.group(), memo);
            }

            return content;
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }

    public static String transferDrawParse(String content){
        if(StringUtils.isBlank(content)){
            return "";
        }

        try{
            //解释xml
            Document document = DocumentHelper.parseText(content);
            //根节点
            Element root   = document.getRootElement();
            Element appmsg = root.element("appmsg");
            //正文
            Element des = appmsg.element("des");
            //截取内容
            content = des.getTextTrim();
            //转账备注
            Element wcpayinfo = appmsg.element("wcpayinfo");
            //正文
            Element paymemo = wcpayinfo.element("pay_memo");
            //截取备注
            String memo = paymemo.getTextTrim();

            Pattern pattern = Pattern.compile("转账([1-9]\\d*|0)(\\.\\d{1,2})?元");
            Matcher matcher = pattern.matcher(content);

            if(matcher.find()){
                return MessageFormat.format("{0}{1}[{2}]", "被领取", matcher.group(), memo);
            }

            return content;
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }

    public static String getTransferType(String content){
        if(StringUtils.isBlank(content)){
            return "";
        }

        try{
            //解释xml
            Document document = DocumentHelper.parseText(content);
            //根节点
            Element root   = document.getRootElement();
            Element appmsg = root.element("appmsg");
            //正文
            Element des = appmsg.element("des");
            //截取内容
            content = des.getTextTrim();
            //转账备注
            Element wcpayinfo = appmsg.element("wcpayinfo");
            //正文
            Element paysubtype = wcpayinfo.element("paysubtype");

            return paysubtype.getTextTrim();
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }

    public static String luckyPackageParse(String content){
        if(StringUtils.isBlank(content)){
            return "";
        }

        try{
            //解释xml
            Document document = DocumentHelper.parseText(content);
            //根节点
            Element root   = document.getRootElement();
            Element appmsg = root.element("appmsg");
            //正文
            Element title       = appmsg.element("title");
            Element wcpayinfo   = appmsg.element("wcpayinfo");

            Element senderTitle = wcpayinfo.element("sendertitle");

            return MessageFormat.format("{0}[{1}]", title.getTextTrim(), senderTitle.getTextTrim());
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }

    public static String luckyPackageSendParse(String content){
        if(StringUtils.isBlank(content)){
            return "";
        }

        try{
            //解释xml
            Document document = DocumentHelper.parseText(content);
            //根节点
            Element root   = document.getRootElement();
            Element appmsg = root.element("appmsg");
            //正文
            Element title       = appmsg.element("title");
            Element wcpayinfo   = appmsg.element("wcpayinfo");

            Element senderTitle = wcpayinfo.element("sendertitle");

            return MessageFormat.format("你发送了一个红包[{0}]", senderTitle.getTextTrim());
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }

    public static String systemParse(String content){
        if(StringUtils.isBlank(content)){
            return "";
        }

        try{
            //非好友
            Pattern pattern = Pattern.compile("[\\s\\S]*开启了朋友验证，你还不是他（她）朋友。请先发送朋友验证请求，对方验证通过后，才能聊天。");
            Matcher matcher = pattern.matcher(content);
            if(matcher.find()){
                return matcher.group();
            }

            //好友添加
            pattern = Pattern.compile("你已添加了[\\s\\S]*，现在可以开始聊天了。");
            matcher = pattern.matcher(content);
            if(matcher.find()){
                return matcher.group();
            }

            //领取红包
            pattern = Pattern.compile("<img src=\"SystemMessages_HongbaoIcon.png\"/>  你领取了[\\s\\S]*的[\\s\\S]*红包</_wc_custom_link_>");
            matcher = pattern.matcher(content);
            if(matcher.matches()){
                pattern = Pattern.compile("你领取了[\\s\\S]*的");
                matcher = pattern.matcher(content);
                if(matcher.find()){
                    return matcher.group() + "红包";
                }
            }

            return content;
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }

    public static String personCardParse(String content){
        if(StringUtils.isBlank(content)){
            return "";
        }

        try{
            //解释xml
            Document document = DocumentHelper.parseText(content);
            //根节点
            Element root   = document.getRootElement();

            //头像
            String headImgUrl = root.attributeValue("bigheadimgurl");
            //昵称
            String nickename = root.attributeValue("nickname");
            //微信号
            String wxno = root.attributeValue("alias");

            return "headImgUrl=" + headImgUrl + "&nickname=" + nickename + "&wxno=" + wxno;
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }

    public static String voiceParse(String content){
        if(StringUtils.isBlank(content)){
            return "";
        }

        try(CloseableHttpClient client = HttpClientUtils.createHttpClient()){
            HttpGet get = HttpClientUtils.get(content, null);
            HttpResponse response = client.execute(get);

            if(HttpClientUtils.isOk(response)){
                //文件名
                String fileName = IDGenerator.uuid32();
                //下载路径
                String path = MessageFormat.format(SilkConfig.AMR_PATH, fileName);

                //下载文件
                InputStream inputStream = HttpClientUtils.getStream(response);
                OutputStream outputStream = new FileOutputStream(path);
                byte[] buffer = new byte[4096];
                int length = 0;

                while((length = inputStream.read(buffer)) > 0){
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();

                return SilkUtils.changeToMp3AndUpload(path);
            }

            throw new UtilsException(EntityUtils.toString(response.getEntity()));
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }

    public static String locationParse(String content){
        if(StringUtils.isBlank(content)){
            return "";
        }

        try{
            //解释xml
            Document document = DocumentHelper.parseText(content);
            //根节点
            Element root = document.getRootElement();
            //物理地址
            Element location = root.element("location");

            //x
            String x       = location.attributeValue("x");
            //y
            String y       = location.attributeValue("y");
            //位置标签
            String label   = location.attributeValue("label");
            //位置名称
            String poiname = location.attributeValue("poiname");

            return poiname + label + "[" + x + "," + y + "]";
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }

    public static String onlineTalkParse(String content){
        if(StringUtils.isBlank(content)){
            return "";
        }

        if(content.contains("video")){
            return "video";
        }else if(content.contains("voice")){
            return "voice";
        }

        return null;
    }

    public static Boolean isFriend(String content){
        Pattern pattern = Pattern.compile("[\\s\\S]*开启了朋友验证，你还不是他（她）朋友。请先发送朋友验证请求，对方验证通过后，才能聊天。");
        Matcher matcher = pattern.matcher(content);
        return matcher.find();
    }

    public static WechatFriend friendAdviceParse(String content){
        if(StringUtils.isBlank(content)){
            return null;
        }

        try{
            Pattern pattern = Pattern.compile("<msg[\\s\\S]*</msg>");
            Matcher matcher = pattern.matcher(content);

            if(matcher.find()){
                //匹配需要的内容
                content = matcher.group();
                //解释xml
                Document document = DocumentHelper.parseText(content);
                //根节点
                Element root = document.getRootElement();

                //昵称
                String nickname = root.attributeValue("fromnickname");
                //微信wxid
                String wxid = root.attributeValue("fromusername");

                WechatFriend friend = new WechatFriend();
                friend.setNickname(nickname);
                friend.setWxid(wxid);
                friend.setWxno(wxid);
                friend.setType(WechatFriendType.APPLY);

                return friend;
            }

            return null;
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }

    public static Double getAmount(String content, WechatMessageType type){
        if(StringUtils.isBlank(content)){
            return null;
        }

        try{
            Pattern pattern = null;
            Matcher matcher = null;

            switch(type){
                case LUCKY_PACKAGE://红包
                case LUCKY_PACKAGE_SEND://发红包
                    return null;
                case LUCKY_PACKAGE_TRANSFER_CONFIRM://红包确认
                    pattern = Pattern.compile("([1-9]\\d*|0)(\\.\\d{1,2})?");
                    matcher = pattern.matcher(content);
                    if(matcher.find()){
                        return new Double(matcher.group());
                    }
                case TRANSFER://转账
                case TRANSFER_CONFIRM://转账确认
                case TRANSFER_SEND://发转账
                case TRANSFER_DRAW://转账被领取
                    //解释xml
                    Document document = DocumentHelper.parseText(content);
                    //根节点
                    Element root   = document.getRootElement();
                    Element appmsg = root.element("appmsg");
                    //转账信息
                    Element wcpayinfo = appmsg.element("wcpayinfo");
                    Element feedesc   = wcpayinfo.element("feedesc");
                    String amountString = feedesc.getTextTrim();

                    pattern = Pattern.compile("([1-9]\\d*|0)(\\.\\d{1,2})?");
                    matcher = pattern.matcher(amountString);
                    if(matcher.find()){
                        return new Double(matcher.group());
                    }
                default:
                    return null;
            }
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }
}
