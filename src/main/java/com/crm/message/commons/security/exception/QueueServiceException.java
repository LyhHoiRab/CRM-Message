package com.crm.message.commons.security.exception;

import java.text.MessageFormat;

public class QueueServiceException extends RuntimeException{

    public QueueServiceException(){

    }

    public QueueServiceException(Throwable cause){
        super(cause);
    }

    public QueueServiceException(String message, Throwable cause){
        super(message, cause);
    }

    public QueueServiceException(String message, Object... args){
        this(MessageFormat.format(message, args));
    }

    public QueueServiceException(Enum clazz, Throwable cause, Object... args){
        this(MessageFormat.format(clazz.toString(), args), cause);
    }

    public QueueServiceException(String message){
        super(message);
    }

    public QueueServiceException(Enum clazz, Object... args){
        super(MessageFormat.format(clazz.toString(), args));
    }
}
