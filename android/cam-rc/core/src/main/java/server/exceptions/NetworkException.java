package server.exceptions;

import android.content.Context;
import android.content.res.Resources;

public class NetworkException extends Exception {

    private static final long serialVersionUID = -4276026795305272738L;
    private static final String errorPostfix = "_server_error";
    private String mErrorId;

    public NetworkException(Context context, String errorId, String message) {
        super(getLocalErrorMessage(context, errorId, message));
        mErrorId = errorId;
    }

    private static String getLocalErrorMessage(Context context, String errorId, String defaultErrorMessage) {
        Resources res = context.getResources();
        String localErrorMessage = defaultErrorMessage;
        try {
            localErrorMessage = res.getString(res.getIdentifier(errorId + errorPostfix, "string", context.getPackageName()));
        } catch (Resources.NotFoundException e) {
        }
        return localErrorMessage;
    }

    public String getErrorId() {
        return mErrorId;
    }
}
