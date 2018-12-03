package com.crm.message.core.queue.facade;

import com.crm.api.core.im.entity.IMCustomMessage;
import com.crm.api.core.im.entity.IMMessage;
import com.crm.api.core.log.entity.TransferLogger;
import com.crm.api.core.log.entity.WechatFriendLogger;
import com.crm.api.core.wechat.consts.WechatFriendType;
import com.crm.api.core.wechat.consts.WechatMessageStatus;
import com.crm.api.core.wechat.consts.WechatMessageType;
import com.crm.api.core.wechat.entity.ChatroomMessage;
import com.crm.api.core.wechat.entity.Wechat;
import com.crm.api.core.wechat.entity.WechatFriend;
import com.crm.api.core.wechat.entity.WechatMessage;
import com.crm.api.core.words.entity.Word;
import com.crm.api.core.words.entity.WordRecord;
import com.crm.message.commons.consts.IMConfig;
import com.crm.message.commons.security.exception.QueueServiceException;
import com.crm.message.core.im.utils.IMMessageUtils;
import com.crm.message.core.im.utils.IMUtils;
import com.crm.message.core.log.dao.TransferLoggerDao;
import com.crm.message.core.log.dao.WechatFriendLoggerDao;
import com.crm.message.core.wechat.dao.ChatroomMessageDao;
import com.crm.message.core.wechat.dao.WechatDao;
import com.crm.message.core.wechat.dao.WechatFriendDao;
import com.crm.message.core.wechat.dao.WechatMessageDao;
import com.crm.message.core.wechat.utils.ChatroomMessageUtils;
import com.crm.message.core.wechat.utils.WechatMessageUtils;
import com.crm.message.core.words.dao.WordDao;
import com.crm.message.core.words.dao.WordRecordDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.wah.doraemon.utils.GsonUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class QueueFacade{

    @Autowired
    private WechatDao wechatDao;

    @Autowired
    private WechatFriendDao wechatFriendDao;

    @Autowired
    private WechatMessageDao wechatMessageDao;

    @Autowired
    private WordDao wordDao;

    @Autowired
    private WordRecordDao wordRecordDao;

    @Autowired
    private WechatFriendLoggerDao wechatFriendLoggerDao;

    @Autowired
    private TransferLoggerDao transferLoggerDao;

    @Autowired
    private ChatroomMessageDao chatroomMessageDao;

    public void saveWechatMessage(WechatMessage message) throws QueueServiceException{
        Assert.notNull(message, "微信消息不能为空");
        Assert.hasText(message.getWechatId(), "所属微信ID不能为空");
        Assert.notNull(message.getConversationTime(), "微信发送时间不能为空");

        //消息处理
        WechatMessageType type = message.getType();

        if(type != null){
            switch(type){
                case FRIEND_ADVICE://好友申请
                    friendAdvice(message);
                    break;
                case SYSTEM://系统消息
                    system(message);
                    break;
                case TEXT://文本消息
                    text(message);
                    break;
                case LUCKY_PACKAGE://红包消息
                case TRANSFER://转账消息
                case TRANSFER_CONFIRM://转账确认消息
                case LUCKY_PACKAGE_TRANSFER_CONFIRM://红包确认消息
                    transfer(message);
                    break;
                case VIDEO://视频消息
                    video(message);
                    break;
                case IMAGE://图片消息
                case VOICE://语音消息
                case EMOTICONS://表情消息
                case SHARE://文件消息
                case PERSON_CARD://名片消息
                case LOCATION://地理位置消息
                    other(message);
                    break;
                default://其他
                    other(message);
                    break;
            }
        }
    }

    public void system(WechatMessage message) throws QueueServiceException{
        Assert.notNull(message, "微信消息不能为空");
        Assert.hasText(message.getWechatId(), "所属微信ID不能为空");
        Assert.notNull(message.getConversationTime(), "微信发送时间不能为空");

        //查询微信好友
        WechatFriend friend = wechatFriendDao.getByWechatIdAndWxid(message.getWechatId(), message.getWxid());

        if(friend != null){
            //消息处理
            if(WechatMessageUtils.isFriend(message.getContent())){
                //处理僵尸粉
                friend.setType(WechatFriendType.ZOMBIE_FAN);
                wechatFriendDao.update(friend);
            }

            //保存客户ID
            if(StringUtils.isNotBlank(friend.getSellerId())){
                message.setSellerId(friend.getSellerId());
            }
            //标识为已加载完成
            message.setLoaded(true);
            //保存消息
            wechatMessageDao.saveOrUpdate(message);

            //发送到IM
            if(WechatMessageStatus.RECEIVE.equals(message.getStatus())
                    && StringUtils.isNotBlank(friend.getSellerId())){

                //创建自定义消息
                IMCustomMessage custom = new IMCustomMessage();
                custom.setId(friend.getId());
                custom.setConversationTime(message.getConversationTime());
                custom.setContent(message.getExtract());
                custom.setWxno(friend.getWxno());
                custom.setWxid(friend.getWxid());
                custom.setFromAccount(message.getWechatId());
                custom.setToAccount(friend.getSellerId());
                custom.setType(message.getType());
                custom.setLoaded(message.getLoaded());
                custom.setMsgId(message.getId());
                //发送到IM
                IMMessage imMessage = IMMessageUtils.createTextMsg(custom.getFromAccount(), custom.getToAccount(), GsonUtils.serialize(custom));
                IMUtils.sendMsg(IMConfig.SDK_APPID, IMConfig.ADMINISTRATOR, IMConfig.ADMINISTRATOR_SIG, imMessage);
            }
        }
    }

    public void text(WechatMessage message) throws QueueServiceException{
        Assert.notNull(message, "微信消息不能为空");
        Assert.hasText(message.getWechatId(), "所属微信ID不能为空");
        Assert.notNull(message.getConversationTime(), "微信发送时间不能为空");

        //查询微信
        Wechat wechat = wechatDao.getById(message.getWechatId());
        //查询微信好友
        WechatFriend friend = wechatFriendDao.getByWechatIdAndWxid(message.getWechatId(), message.getWxid());

        if(friend != null){
            //保存客户ID
            if(StringUtils.isNotBlank(friend.getSellerId())){
                message.setSellerId(friend.getSellerId());
            }
            //标识为已加载完成
            message.setLoaded(true);
            //保存消息
            wechatMessageDao.saveOrUpdate(message);

            //发送
            if(WechatMessageStatus.SEND_SUCCESS.equals(message.getStatus())
                    || WechatMessageStatus.SENDING.equals(message.getStatus())){

                //敏感词
                List<Word> words = wordDao.find(wechat.getCompanyId(), null, wechat.getId(), null, null);
                words.addAll(wordDao.find(wechat.getCompanyId(), null, null, null, true));

                if(words != null && !words.isEmpty()){
                    StringBuffer sb = new StringBuffer();

                    for(int i = 0; i < words.size(); i++){
                        if (i > 0) {
                            sb.append("|");
                        }

                        sb.append(words.get(i).getContent());
                    }

                    Pattern pattern = Pattern.compile(sb.toString());
                    Matcher matcher = pattern.matcher(message.getContent());

                    Set<String> word = new HashSet<String>();
                    while(matcher.find()){
                        word.add(matcher.group());
                    }

                    if(word != null && !word.isEmpty()){
                        String sensitive = StringUtils.join(word, ",");

                        WordRecord record = new WordRecord();
                        record.setMessageId(message.getId());
                        record.setWords(sensitive);
                        wordRecordDao.save(record);
                    }
                }
            }

            //发送到IM
            if(WechatMessageStatus.RECEIVE.equals(message.getStatus())
                    && StringUtils.isNotBlank(friend.getSellerId())){

                //创建自定义消息
                IMCustomMessage custom = new IMCustomMessage();
                custom.setId(friend.getId());
                custom.setConversationTime(message.getConversationTime());
                custom.setContent(message.getExtract());
                custom.setWxno(friend.getWxno());
                custom.setWxid(friend.getWxid());
                custom.setFromAccount(message.getWechatId());
                custom.setToAccount(friend.getSellerId());
                custom.setType(message.getType());
                custom.setLoaded(message.getLoaded());
                custom.setMsgId(message.getId());
                //发送到IM
                IMMessage imMessage = IMMessageUtils.createTextMsg(custom.getFromAccount(), custom.getToAccount(), GsonUtils.serialize(custom));
                IMUtils.sendMsg(IMConfig.SDK_APPID, IMConfig.ADMINISTRATOR, IMConfig.ADMINISTRATOR_SIG, imMessage);
            }
        }
    }

    public void transfer(WechatMessage message) throws QueueServiceException{
        Assert.notNull(message, "微信消息不能为空");
        Assert.hasText(message.getWechatId(), "所属微信ID不能为空");
        Assert.notNull(message.getConversationTime(), "微信发送时间不能为空");

        //查询微信好友
        WechatFriend friend = wechatFriendDao.getByWechatIdAndWxid(message.getWechatId(), message.getWxid());

        if(friend != null){
            //保存转账记录
            String paySubType = WechatMessageUtils.getTransferType(message.getContent());

            switch(message.getType()){
                case TRANSFER:
                    if("3".equals(paySubType)){
                        //转账被领取
                        message.setType(WechatMessageType.TRANSFER_DRAW);
                    }
                    break;
                case TRANSFER_CONFIRM:
                    if("1".equals(paySubType)){
                        //发起转账
                        message.setType(WechatMessageType.TRANSFER_SEND);
                    }
                    break;
                case LUCKY_PACKAGE:
                    if(WechatMessageStatus.SEND_SUCCESS.equals(message.getStatus())
                            || WechatMessageStatus.SENDING.equals(message.getStatus())){
                        //发起红包
                        message.setType(WechatMessageType.LUCKY_PACKAGE_SEND);
                    }
                    break;
                default:
                    break;
            }

            //保存客户ID
            if(StringUtils.isNotBlank(friend.getSellerId())){
                message.setSellerId(friend.getSellerId());
            }
            //标识为已加载完成
            message.setLoaded(true);
            //保存消息
            wechatMessageDao.saveOrUpdate(message);

            //保存转账记录
            TransferLogger logger = new TransferLogger();
            logger.setWechatId(message.getWechatId());
            logger.setWxid(message.getWxid());
            logger.setType(message.getType());
            logger.setConversationTime(message.getConversationTime());
            logger.setMessageId(message.getId());
            logger.setAmount(WechatMessageUtils.getAmount(message.getContent(), message.getType()));
            transferLoggerDao.save(logger);

            //发送到IM
            if(WechatMessageStatus.RECEIVE.equals(message.getStatus())
                    && StringUtils.isNotBlank(friend.getSellerId())){

                //创建自定义消息
                IMCustomMessage custom = new IMCustomMessage();
                custom.setId(friend.getId());
                custom.setConversationTime(message.getConversationTime());
                custom.setContent(message.getExtract());
                custom.setWxno(friend.getWxno());
                custom.setWxid(friend.getWxid());
                custom.setFromAccount(message.getWechatId());
                custom.setToAccount(friend.getSellerId());
                custom.setType(message.getType());
                custom.setLoaded(message.getLoaded());
                custom.setMsgId(message.getId());
                //发送到IM
                IMMessage imMessage = IMMessageUtils.createTextMsg(custom.getFromAccount(), custom.getToAccount(), GsonUtils.serialize(custom));
                IMUtils.sendMsg(IMConfig.SDK_APPID, IMConfig.ADMINISTRATOR, IMConfig.ADMINISTRATOR_SIG, imMessage);
            }
        }
    }

    public void video(WechatMessage message) throws QueueServiceException{
        Assert.notNull(message, "微信消息不能为空");
        Assert.hasText(message.getWechatId(), "所属微信ID不能为空");
        Assert.notNull(message.getConversationTime(), "微信发送时间不能为空");

        //查询微信好友
        WechatFriend friend = wechatFriendDao.getByWechatIdAndWxid(message.getWechatId(), message.getWxid());

        if(friend != null){
            //文件未上传
            if(StringUtils.isBlank(message.getContent())){
                message.setLoaded(false);

            }else{
                List<WechatMessage> list = wechatMessageDao.findByWechatIdAndMsgId(message.getWechatId(), message.getMsgId());
                if(list != null && !list.isEmpty()){
                    WechatMessage target = list.get(0);

                    message.setId(target.getId());
                    message.setLoaded(true);
                    message.setExtract(WechatMessageUtils.extract(message));
                }
            }

            //保存客户ID
            if(StringUtils.isNotBlank(friend.getSellerId())){
                message.setSellerId(friend.getSellerId());
            }
            //保存消息
            wechatMessageDao.saveOrUpdate(message);

            //发送到IM
            if(WechatMessageStatus.RECEIVE.equals(message.getStatus())
                    && StringUtils.isNotBlank(friend.getSellerId())
                    && !message.getLoaded()){

                //创建自定义消息
                IMCustomMessage custom = new IMCustomMessage();
                custom.setId(friend.getId());
                custom.setConversationTime(message.getConversationTime());
                custom.setContent(message.getExtract());
                custom.setWxno(friend.getWxno());
                custom.setWxid(friend.getWxid());
                custom.setFromAccount(message.getWechatId());
                custom.setToAccount(friend.getSellerId());
                custom.setType(message.getType());
                custom.setLoaded(message.getLoaded());
                custom.setMsgId(message.getId());
                //发送到IM
                IMMessage imMessage = IMMessageUtils.createTextMsg(custom.getFromAccount(), custom.getToAccount(), GsonUtils.serialize(custom));
                IMUtils.sendMsg(IMConfig.SDK_APPID, IMConfig.ADMINISTRATOR, IMConfig.ADMINISTRATOR_SIG, imMessage);
            }
        }
    }

    public void friendAdvice(WechatMessage message) throws QueueServiceException{
        Assert.notNull(message, "微信消息不能为空");
        Assert.hasText(message.getWechatId(), "所属微信ID不能为空");
        Assert.notNull(message.getConversationTime(), "微信发送时间不能为空");

        //好友申请
        WechatFriend apply = WechatMessageUtils.friendAdviceParse(message.getContent());
        if(apply != null){
            //保存记录
            WechatFriendLogger logger = new WechatFriendLogger();
            logger.setWechatId(message.getWechatId());
            logger.setWxid(apply.getWxid());
            logger.setType(message.getType());
            wechatFriendLoggerDao.save(logger);
        }
    }

    public void other(WechatMessage message) throws QueueServiceException{
        Assert.notNull(message, "微信消息不能为空");
        Assert.hasText(message.getWechatId(), "所属微信ID不能为空");
        Assert.notNull(message.getConversationTime(), "微信发送时间不能为空");

        //查询微信好友
        WechatFriend friend = wechatFriendDao.getByWechatIdAndWxid(message.getWechatId(), message.getWxid());

        if(friend != null){
            //保存客户ID
            if(StringUtils.isNotBlank(friend.getSellerId())){
                message.setSellerId(friend.getSellerId());
            }
            //标识为已加载完成
            message.setLoaded(true);
            //保存消息
            wechatMessageDao.saveOrUpdate(message);

            //发送到IM
            if(WechatMessageStatus.RECEIVE.equals(message.getStatus())
                    && StringUtils.isNotBlank(friend.getSellerId())){

                //创建自定义消息
                IMCustomMessage custom = new IMCustomMessage();
                custom.setId(friend.getId());
                custom.setConversationTime(message.getConversationTime());
                custom.setContent(message.getExtract());
                custom.setWxno(friend.getWxno());
                custom.setWxid(friend.getWxid());
                custom.setFromAccount(message.getWechatId());
                custom.setToAccount(friend.getSellerId());
                custom.setType(message.getType());
                custom.setLoaded(message.getLoaded());
                custom.setMsgId(message.getId());
                //发送到IM
                IMMessage imMessage = IMMessageUtils.createTextMsg(custom.getFromAccount(), custom.getToAccount(), GsonUtils.serialize(custom));
                IMUtils.sendMsg(IMConfig.SDK_APPID, IMConfig.ADMINISTRATOR, IMConfig.ADMINISTRATOR_SIG, imMessage);
            }
        }
    }

    public void saveChatroomMessage(WechatMessage message) throws QueueServiceException{
        Assert.notNull(message, "微信消息不能为空");
        Assert.hasText(message.getWechatId(), "所属微信ID不能为空");
        Assert.notNull(message.getConversationTime(), "微信发送时间不能为空");

        //转换
        ChatroomMessage chatroomMessage = new ChatroomMessage();
        chatroomMessage.setWechatId(message.getWechatId());
        chatroomMessage.setRoomid(message.getWxid());
        chatroomMessage.setType(message.getType());
        chatroomMessage.setSeller(message.getSeller());
        chatroomMessage.setContent(message.getContent());
        chatroomMessage.setMsgId(message.getMsgId());
        chatroomMessage.setStatus(message.getStatus());
        chatroomMessage.setContent(message.getContent());
        chatroomMessage.setConversationTime(message.getConversationTime());
        chatroomMessage.setLoaded(true);

        WechatMessageType type = chatroomMessage.getType();

        if(WechatMessageStatus.RECEIVE.equals(chatroomMessage.getStatus())
                && !WechatMessageType.LUCKY_PACKAGE_TRANSFER_CONFIRM.equals(type)
                && !WechatMessageType.SYSTEM.equals(type)){
            //接收
            String content = chatroomMessage.getContent();

            if(StringUtils.isNotBlank(content)){
                chatroomMessage.setSender(content.substring(0, content.indexOf(":")));
            }

        }else if(WechatMessageStatus.SENDING.equals(chatroomMessage.getStatus())
                || WechatMessageStatus.SEND_SUCCESS.equals(chatroomMessage.getStatus())){
            //发送
            Wechat wechat = wechatDao.getById(message.getWechatId());
            chatroomMessage.setSender(wechat.getWxid());
        }

        if(WechatMessageType.TEXT.equals(type)
                || WechatMessageType.IMAGE.equals(type)
                || WechatMessageType.VOICE.equals(type)
                || WechatMessageType.EMOTICONS.equals(type)
                || WechatMessageType.SYSTEM.equals(type)
                || WechatMessageType.UNKNOWN.equals(type)
                || WechatMessageType.LUCKY_PACKAGE.equals(type)
                || WechatMessageType.TRANSFER.equals(type)
                || WechatMessageType.TRANSFER_CONFIRM.equals(type)
                || WechatMessageType.LUCKY_PACKAGE_TRANSFER_CONFIRM.equals(type)
                || WechatMessageType.SHARE.equals(type)
                || WechatMessageType.PERSON_CARD.equals(type)
                || WechatMessageType.LOCATION.equals(type)){

            chatroomMessageDao.saveOrUpdate(chatroomMessage);

        }else if(WechatMessageType.VIDEO.equals(type)){
            //文件未上传
            if(StringUtils.isBlank(chatroomMessage.getContent())){
                chatroomMessage.setLoaded(false);

            }else{
                List<ChatroomMessage> list = chatroomMessageDao.findByWechatIdAndRoomidAndMsgId(chatroomMessage.getWechatId(), chatroomMessage.getRoomid(), message.getMsgId());
                if(list != null && !list.isEmpty()){
                    ChatroomMessage target = list.get(0);

                    chatroomMessage.setId(target.getId());
                    chatroomMessage.setLoaded(true);
                    chatroomMessage.setExtract(ChatroomMessageUtils.extract(chatroomMessage));
                }
            }

            chatroomMessageDao.saveOrUpdate(chatroomMessage);
        }
    }
}
