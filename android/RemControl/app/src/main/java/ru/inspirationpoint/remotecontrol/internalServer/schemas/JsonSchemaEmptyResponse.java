package ru.inspirationpoint.remotecontrol.internalServer.schemas;

import com.google.gson.annotations.SerializedName;

public class JsonSchemaEmptyResponse extends JsonSchema {
    @SerializedName("status")
    public Status status;
}
