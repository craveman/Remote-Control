package server.exceptions;import android.content.Context;public class NotFoundException extends NetworkException {    private static final long serialVersionUID = 1189L;    public NotFoundException(Context context) {        super(context, "not_found", "Item not found.");    }}