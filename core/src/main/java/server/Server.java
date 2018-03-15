package server;

import android.content.Context;

import server.exceptions.NetworkException;
import server.schemas.requests.FightInput;
import server.schemas.requests.GetAddressByLocationRequest;
import server.schemas.requests.GetPossibleUsersRequest;
import server.schemas.requests.GetUsersAddedMeRequest;
import server.schemas.requests.LoginRequest;
import server.schemas.requests.LogoutRequest;
import server.schemas.requests.RegisterRequest;
import server.schemas.requests.ResetPasswordRequest;
import server.schemas.requests.SaveFightRequest;
import server.schemas.responses.GetAddressByLocationResponse;
import server.schemas.responses.GetAddressByLocationResult;
import server.schemas.responses.GetPossibleUsersResponse;
import server.schemas.responses.GetPossibleUsersResult;
import server.schemas.responses.GetUsersAddedMeResponse;
import server.schemas.responses.GetUsersAddedMeResult;
import server.schemas.responses.LoginResponse;
import server.schemas.responses.LoginResult;
import server.schemas.responses.LogoutResponse;
import server.schemas.responses.LogoutResult;
import server.schemas.responses.RegisterResponse;
import server.schemas.responses.RegisterResult;
import server.schemas.responses.ResetPasswordResponse;
import server.schemas.responses.ResetPasswordResult;
import server.schemas.responses.SaveFightResponse;
import server.schemas.responses.SaveFightResult;

public final class Server {

    private String serverBaseUrl = "";
    private static Server s_instance;

    public static Server instance() {
        if (s_instance == null) {
            s_instance = new Server();
        }
        return s_instance;
    }

    private Server() {
    }

    public void init(String serverBaseUrl) {
        this.serverBaseUrl = serverBaseUrl;
    }

    private String buildMethodUrl(String methodName) {
        return serverBaseUrl + "/" + methodName;
    }

    public LoginResult login(Context context, String email, String password) throws NetworkException {
        LoginRequest request = new LoginRequest(email, password);

        ApiCallPerformer<LoginRequest, LoginResponse> caller = new ApiCallPerformer<LoginRequest, LoginResponse>(
                buildMethodUrl("login"), LoginResponse.class);
        return caller.PerformCall(context, request).result;
    }

    public LogoutResult logout(Context context, String session) throws NetworkException {
        LogoutRequest request = new LogoutRequest(session);

        ApiCallPerformer<LogoutRequest, LogoutResponse> caller = new ApiCallPerformer<LogoutRequest, LogoutResponse>(
                buildMethodUrl("logout"), LogoutResponse.class);
        return caller.PerformCall(context, request).result;
    }

    public RegisterResult register(Context context, String email, String name, String phone, String nick, String birthday, String category, String clubId, String weapon, String sexType) throws NetworkException {
        RegisterRequest request = new RegisterRequest(email, name, phone, nick, birthday, category, clubId, weapon, sexType);

        ApiCallPerformer<RegisterRequest, RegisterResponse> caller = new ApiCallPerformer<RegisterRequest, RegisterResponse>(
                buildMethodUrl("register"), RegisterResponse.class);
        return caller.PerformCall(context, request).result;
    }

    public ResetPasswordResult resetPassword(Context context, String email) throws NetworkException {
        ResetPasswordRequest request = new ResetPasswordRequest(email);

        ApiCallPerformer<ResetPasswordRequest, ResetPasswordResponse> caller = new ApiCallPerformer<ResetPasswordRequest, ResetPasswordResponse>(
                buildMethodUrl("resetPassword"), ResetPasswordResponse.class);
        return caller.PerformCall(context, request).result;
    }

    public GetAddressByLocationResult getAddressByLocation(Context context, String session, Number latitude, Number longitude) throws NetworkException {
        GetAddressByLocationRequest request = new GetAddressByLocationRequest(session, latitude, longitude);

        ApiCallPerformer<GetAddressByLocationRequest, GetAddressByLocationResponse> caller = new ApiCallPerformer<GetAddressByLocationRequest, GetAddressByLocationResponse>(
                buildMethodUrl("getAddressByLocation"), GetAddressByLocationResponse.class);
        return caller.PerformCall(context, request).result;
    }

    public GetUsersAddedMeResult getUsersAddeMe(Context context, String session) throws NetworkException {
        GetUsersAddedMeRequest request = new GetUsersAddedMeRequest(session);

        ApiCallPerformer<GetUsersAddedMeRequest, GetUsersAddedMeResponse> caller = new ApiCallPerformer<GetUsersAddedMeRequest, GetUsersAddedMeResponse>(
                buildMethodUrl("getUsersAddedMeAsFriend"), GetUsersAddedMeResponse.class);
        return caller.PerformCall(context, request).result;
    }

    public SaveFightResult saveFight(Context context, String session, FightInput fight) throws NetworkException {
        SaveFightRequest request = new SaveFightRequest(session, fight);

        ApiCallPerformer<SaveFightRequest, SaveFightResponse> caller = new ApiCallPerformer<SaveFightRequest, SaveFightResponse>(
                buildMethodUrl("saveFight"), SaveFightResponse.class);
        return caller.PerformCall(context, request).result;
    }

    public GetPossibleUsersResult getPossibleUsers(Context context, String session, String searchString, Boolean includeMe) throws NetworkException {
        GetPossibleUsersRequest request = new GetPossibleUsersRequest(session, searchString, includeMe);

        ApiCallPerformer<GetPossibleUsersRequest, GetPossibleUsersResponse> caller = new ApiCallPerformer<GetPossibleUsersRequest, GetPossibleUsersResponse>(
                buildMethodUrl("getPossibleUsers"), GetPossibleUsersResponse.class);
        return caller.PerformCall(context, request).result;
    }

}