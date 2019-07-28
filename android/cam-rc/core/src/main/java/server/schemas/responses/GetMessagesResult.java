package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetMessagesResult {

    @SerializedName("messages")
    public Message[] messages;
}
