package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DialogOutput implements Serializable {

    @SerializedName("_id")
    public String _id;

    @SerializedName("title")
    public String title;

    @SerializedName("lastMessages")
    public Message[] lastMessages;

    @SerializedName("clients")
    public ClientInfo[] clients;

    @SerializedName("unreadCount")
    public int unreadCount;

    @SerializedName("created")
    public String created;

    @SerializedName("modified")
    public String modified;

    public DialogOutput(String _id, String title, Message[] lastMessages, ClientInfo[] clients, int unreadCount, String created, String modified) {
        this._id = _id;
        this.title = title;
        this.lastMessages = lastMessages;
        this.clients = clients;
        this.unreadCount = unreadCount;
        this.created = created;
        this.modified = modified;
    }
}
