package com.crm.message.commons.security.handler;

import com.crm.message.commons.security.exception.QueueServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wah.doraemon.security.consts.ResponseCode;
import org.wah.doraemon.security.exception.*;
import org.wah.doraemon.security.response.Responsed;

@ControllerAdvice
@ResponseBody
public class ExceptionAdvice{

    private Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    /**
     * 其他异常
     */
    @ExceptionHandler(value = Exception.class)
    public Responsed exception(Exception e){
        logger.error(e.getMessage(), e);

        return new Responsed(e.getMessage(), ResponseCode.INTERNAL_SERVER_ERROR, false);
    }

    /**
     * 404异常
     */
    @ExceptionHandler(value = NotFoundException.class)
    public Responsed notFound(NotFoundException e){
        logger.error(e.getLocalizedMessage(), e);

        return new Responsed(e.getMessage(), ResponseCode.NOT_FOUND, false);
    }

    /**
     * 消息队列异常
     */
    @ExceptionHandler(value = QueueServiceException.class)
    public void queueService(QueueServiceException e){
        logger.error(e.getMessage(), e);
    }

    /**
     * 持久层异常
     */
    @ExceptionHandler(value = DataAccessException.class)
    public Responsed dataAccess(DataAccessException e){
        logger.error(e.getMessage(), e);

        return new Responsed("数据库出错", ResponseCode.INTERNAL_SERVER_ERROR, false);
    }

    /**
     * 账户认证异常
     */
    @ExceptionHandler(value = AuthenticationException.class)
    public Responsed authentication(AuthenticationException e){
        logger.error(e.getMessage(), e);

        return new Responsed(e.getMessage(), ResponseCode.AUTHENTICATE_FAIL, false);
    }

    /**
     * 账户状态异常
     */
    @ExceptionHandler(value = InvalidStateException.class)
    public Responsed invalidState(InvalidStateException e){
        logger.error(e.getMessage(), e);

        return new Responsed(e.getMessage(), ResponseCode.INVALID_STATE, false);
    }

    /**
     * 票据认证异常
     */
    @ExceptionHandler(value = TicketAuthenticationException.class)
    public Responsed ticketAuthentication(TicketAuthenticationException e){
        logger.error(e.getMessage(), e);

        return new Responsed(e.getMessage(), ResponseCode.TICKET_AUTHENTICATE_FAIL, false);
    }

    /**
     * 票据凭证刷新失败
     */
    @ExceptionHandler(value = TicketRefreshFailException.class)
    public Responsed ticketRefreshFail(TicketRefreshFailException e){
        logger.error(e.getMessage(), e);

        return new Responsed(e.getMessage(), ResponseCode.TICKET_REFRESH_FAIL, false);
    }

    /**
     * 登录失败异常
     */
    @ExceptionHandler(value = LoginFailException.class)
    public Responsed loginFail(LoginFailException e){
        logger.error(e.getMessage(), e);

        return new Responsed(e.getMessage(), ResponseCode.LOGIN_FAIL, false);
    }

    /**
     * 未知用户信息异常
     */
    @ExceptionHandler(value = AccountNotFoundException.class)
    public Responsed accountNotFound(AccountNotFoundException e){
        logger.error(e.getMessage(), e);

        return new Responsed(e.getMessage(), ResponseCode.ACCOUNT_NOT_FOUND, false);
    }

    /**
     * 记录重复异常
     */
    @ExceptionHandler(value = DuplicateException.class)
    public Responsed duplicate(DuplicateException e){
        logger.error(e.getMessage(), e);

        return new Responsed(e.getMessage(), ResponseCode.DUPLICATE_DATA, false);
    }

    /**
     * 不支持的客户端类型
     */
    @ExceptionHandler(value = BrowserNotSupportException.class)
    public Responsed browserNotSupport(BrowserNotSupportException e){
        logger.error(e.getMessage(), e);

        return new Responsed(e.getMessage(), ResponseCode.BROWSER_NOT_SUPPORT, false);
    }

    /**
     * 权限禁止异常
     */
    @ExceptionHandler(value = ForbiddenException.class)
    public Responsed forbidden(ForbiddenException e){
        logger.error(e.getMessage(), e);

        return new Responsed(e.getMessage(), ResponseCode.FORBIDDEN, false);
    }
}
