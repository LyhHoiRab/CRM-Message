package com.crm.message.core.im.utils.response;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IMResponse{

    @SerializedName("ActionStatus")
    private String status;

    @SerializedName("ErrorCode")
    private Integer errorCode;

    @SerializedName("ErrorInfo")
    private String errorInfo;
}
