package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetUsersAddedMeResult {

    @SerializedName("users")
    public ListUser[] users;

}
