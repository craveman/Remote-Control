package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetDialogsResult {

    @SerializedName("dialogs")
    public DialogOutput[] dialogs;
}
