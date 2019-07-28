package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class SendMessageResult {

    @SerializedName("message")
    public Message message;
}
