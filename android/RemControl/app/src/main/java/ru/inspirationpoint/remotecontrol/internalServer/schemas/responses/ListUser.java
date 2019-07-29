package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ListUser implements Serializable {
    
    @SerializedName("_id")
    public String _id = "";
    
    @SerializedName("email")
    public String email = "";
    
    @SerializedName("name")
    public String name = "";
    
    @SerializedName("phone")
    public String phone;
    
    @SerializedName("birthday")
    public String birthday = "";
    
    @SerializedName("category")
    public String category;
    
    @SerializedName("clubId")
    public String clubId;
    
    @SerializedName("weapon")
    public String weapon;
    
    @SerializedName("sexType")
    public String sexType;

    @SerializedName("nick")
    public String nick = "";
    
}
