package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class SaveMyProfileRequest extends JsonSchema {
    
    @SerializedName("session")
    public String session;
    
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
    

    public SaveMyProfileRequest(String session, String name, String nick, String phone, String birthday, String category, String clubId, String weapon, String sexType) {
        this.nick = nick;this.session = session;this.name = name;this.phone = phone;this.birthday = birthday;this.category = category;this.clubId = clubId;this.weapon = weapon;this.sexType = sexType;
    }
}
