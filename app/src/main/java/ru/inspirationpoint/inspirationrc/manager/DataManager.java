package ru.inspirationpoint.inspirationrc.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import ru.inspirationpoint.inspirationrc.InspirationDayApplication;
import ru.inspirationpoint.inspirationrc.manager.dataEntities.FightData;
import server.Server;
import server.exceptions.NetworkException;
import server.schemas.requests.FightInput;
import server.schemas.responses.GetAddressByLocationResult;
import server.schemas.responses.GetPossibleUsersResult;
import server.schemas.responses.GetUsersAddedMeResult;
import server.schemas.responses.ListUser;
import server.schemas.responses.LoginResult;
import server.schemas.responses.LogoutResult;
import server.schemas.responses.RegisterResult;
import server.schemas.responses.ResetPasswordResult;
import server.schemas.responses.SaveFightResult;

public class DataManager {

    private static DataManager sDataManager;
    private FightData mCurrentFightData;

    private DataManager() {
    }

    public static DataManager instance() {
        if (sDataManager == null) {
            sDataManager = new DataManager();
        }

        return sDataManager;
    }

    public FightData getCurrentFight() {
        return mCurrentFightData;
    }

    public void setCurrentFight(FightData fightData) {
        mCurrentFightData = fightData;
    }

    public void register(final String email, final String name, final String weapon, final String nick, RequestListener<RegisterResult> listener) {
        if (listener != null) {
            ServerRequest<?> serverRequest = new ServerRequest<RegisterResult>(listener) {
                @Override
                RegisterResult doServerRequest(Server server) throws NetworkException {
                    return server.register(InspirationDayApplication.getApplication(), email, name,
                            "", nick, "", "", "", weapon, "male");
                }
            };
            serverRequest.execute();
        }
    }

    public void login(final String email, final String password, RequestListener<LoginResult> listener) {
        if (listener != null) {
            ServerRequest<?> serverRequest = new ServerRequest<LoginResult>(listener) {
                @Override
                LoginResult doServerRequest(Server server) throws NetworkException {
                    LoginResult result = server.login(InspirationDayApplication.getApplication(), email, password);
                    setSessionId(result.session);
                    return result;
                }
            };
            serverRequest.execute();
        }
    }

    public void logout(RequestListener<LogoutResult> listener) {
        ServerRequest<?> serverRequest = new ServerRequest<LogoutResult>(listener) {
            @Override
            LogoutResult doServerRequest(Server server) throws NetworkException {
                LogoutResult result = server.logout(InspirationDayApplication.getApplication(), getSessionId());
                return result;
            }
        };
        serverRequest.execute();
        setSessionId("");
    }

    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(getSessionId());
    }

    public void resetPassword(final String email, RequestListener<ResetPasswordResult> listener) {
        if (listener != null) {
            ServerRequest<?> serverRequest = new ServerRequest<ResetPasswordResult>(listener) {
                @Override
                ResetPasswordResult doServerRequest(Server server) throws NetworkException {
                    return server.resetPassword(InspirationDayApplication.getApplication(), email);
                }
            };
            serverRequest.execute();
        }
    }

    public void saveFight(final FightInput fight, RequestListener<SaveFightResult> listener) {
        if (listener != null) {
            ServerRequest<?> serverRequest = new ServerRequest<SaveFightResult>(listener) {
                @Override
                SaveFightResult doServerRequest(Server server) throws NetworkException {
                    return server.saveFight(InspirationDayApplication.getApplication(), getSessionId(), fight);
                }
            };
            serverRequest.execute();
        }
    }

    public void getAddressByLocation(final Number latitude, final Number longitude, RequestListener<GetAddressByLocationResult> listener) {
        if (listener != null) {
            ServerRequest<?> serverRequest = new ServerRequest<GetAddressByLocationResult>(listener) {
                @Override
                GetAddressByLocationResult doServerRequest(Server server) throws NetworkException {
                    return server.getAddressByLocation(InspirationDayApplication.getApplication(), getSessionId(), latitude, longitude);
                }
            };
            serverRequest.execute();
        }
    }

    public ListUser[] getPossibleUsers(Context context, String searchString, Boolean includeMe) throws NetworkException {
        GetPossibleUsersResult result = Server.instance().getPossibleUsers(context, getSessionId(), searchString, includeMe);
        return result.users;
    }

    public  ListUser[] getUsersAddedMeList(Context context) throws NetworkException {
        GetUsersAddedMeResult result = Server.instance().getUsersAddeMe(context, getSessionId());
        return result.users;
    }

    public String getSessionId() {
        return SettingsManager.getValue(Constants.SESSION_ID_FIELD, "");
    }

    public void setSessionId(String sessionId) {
        SettingsManager.setValue(Constants.SESSION_ID_FIELD, sessionId);
    }


    public interface RequestListener<ResultType> {
        void onSuccess(ResultType result);

        void onFailed(String error, String message);

        void onStateChanged(boolean inProgress);
    }

    private static abstract class ServerRequest<ResultType> extends AsyncTask<Void, Void, ResultType> {

        private RequestListener<ResultType> mListener;
        private NetworkException mException;

        ServerRequest(RequestListener<ResultType> listener) {
            mListener = listener;
        }

        abstract ResultType doServerRequest(Server server) throws NetworkException;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mListener != null)
                mListener.onStateChanged(true);
        }

        @Override
        protected ResultType doInBackground(Void... val) {
            Server server = Server.instance();
            try {
                return doServerRequest(server);
            } catch (NetworkException e) {
                mException = e;
            }

            return null;
        }

        @Override
        protected void onPostExecute(ResultType result) {
            if (mListener != null) {
                if (result == null) {
                    mListener.onFailed(mException.getErrorId(), mException.getMessage());
                } else {
                    mListener.onSuccess(result);
                }

                mListener.onStateChanged(false);
            }
        }
    }
}
