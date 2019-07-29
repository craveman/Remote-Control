package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetFightUserListsResult {
    @SerializedName("lists")
    public String[] lists;
}
