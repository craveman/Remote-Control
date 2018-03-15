package server.schemas;

import com.google.gson.annotations.SerializedName;

public class Status {
    @SerializedName("error")
    public String Error;

    @SerializedName("errorMessage")
    public String ErrorMessage;
}
