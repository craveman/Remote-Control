package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class RegisterRequest extends JsonSchema {
    
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
    

    public RegisterRequest(String email, String name, String phone, String nick, String birthday, String category, String clubId, String weapon, String sexType) {
        this.email = email;this.name = name;this.phone = phone;this.birthday = birthday;this.category = category;this.clubId = clubId;this.weapon = weapon;this.sexType = sexType; this.nick = nick;
    }
}
