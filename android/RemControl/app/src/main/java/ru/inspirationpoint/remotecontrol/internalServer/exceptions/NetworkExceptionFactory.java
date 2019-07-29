package ru.inspirationpoint.remotecontrol.internalServer.exceptions;

import android.content.Context;

import server.schemas.Status;

public abstract class NetworkExceptionFactory {

    public static NetworkException GetExceptionByStatus(Context context, Status status) {

        NetworkException exception;
        
        if (status.Error.equals("internal")) {
            exception = new InternalException(context);
        } else if (status.Error.equals("invalid_parameter")) {
            exception = new InvalidParameterException(context);
        } else if (status.Error.equals("ssl_required")) {
            exception = new SslRequiredException(context);
        } else if (status.Error.equals("not_found")) {
            exception = new NotFoundException(context);
        } else if (status.Error.equals("already_exists")) {
            exception = new AlreadyExistsException(context);
        } else if (status.Error.equals("disabled_user")) {
            exception = new DisabledUserException(context);
        } else if (status.Error.equals("object_in_use")) {
            exception = new ObjectInUseException(context);
        } else if (status.Error.equals("invalid_user")) {
            exception = new InvalidUserException(context);
        } else if (status.Error.equals("invalid_session")) {
            exception = new InvalidSessionException(context);
        } else if (status.Error.equals("weak_password")) {
            exception = new WeakPasswordException(context);
        } else {
            String message = String.format("Error while making call to API. Error: %s. Error message: %s",
                status.Error, status.ErrorMessage);
            exception = new NetworkException(context, "unknown", message);
        }

        return exception;
    }
}
