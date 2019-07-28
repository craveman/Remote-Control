package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class Profile {

    @SerializedName("_id")
    public String _id;

    @SerializedName("email")
    public String email;

    @SerializedName("name")
    public String name;

    @SerializedName("phone")
    public String phone;

    @SerializedName("birthday")
    public String birthday;

    @SerializedName("category")
    public String category;

    @SerializedName("clubId")
    public String clubId;

    @SerializedName("weapon")
    public String weapon;

    @SerializedName("sexType")
    public String sexType;

    @SerializedName("nick")
    public String nick;

}
