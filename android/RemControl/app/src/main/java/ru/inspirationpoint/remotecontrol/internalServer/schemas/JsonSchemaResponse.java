package ru.inspirationpoint.remotecontrol.internalServer.schemas;

import com.google.gson.annotations.SerializedName;

public abstract class JsonSchemaResponse<TResponse> extends JsonSchemaEmptyResponse {
    @SerializedName("result")
    public TResponse result;
}
