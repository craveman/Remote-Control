package server;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import server.exceptions.NetworkException;
import server.exceptions.NetworkExceptionFactory;
import server.schemas.JsonSchema;
import server.schemas.JsonSchemaEmptyResponse;
import server.schemas.Status;

final class ApiCallPerformer<TRequestSchema extends JsonSchema, TResponseSchema extends JsonSchemaEmptyResponse> {

    private String _uri;
    private Class<TResponseSchema> _responseSchemaType;

    public ApiCallPerformer(String uri, Class<TResponseSchema> responseSchemaType) {
        _uri = uri;
        this._responseSchemaType = responseSchemaType;
    }

    public TResponseSchema PerformCall(Context context, TRequestSchema schema) throws NetworkException {
        String json = _getJson(schema);
        byte[] data = new byte[0];
        try {
            data = json.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        URL url = null;
        try {
            url = new URL(_uri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setUseCaches(false);
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Content-type", "application/json");

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                out.write(data);
                out.flush();

                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                if (inputStream != null) {
                    TResponseSchema response = _getSchema(convertInputStreamToString(inputStream));
                    _throwIfErrorOccurred(context, response.status);
                    return response;
                } else {
                    throw new NetworkException(context, "null_input_stream", "Input stream is null or empty");
                }
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new NetworkException(context, "not_responding", "Server is not responding");
        }
    }

    private String _getJson(TRequestSchema schema) {
        return new Gson().toJson(schema);
    }

    private TResponseSchema _getSchema(String json) {
        return new Gson().fromJson(json, _responseSchemaType);
    }

    private void _throwIfErrorOccurred(Context context, Status status) throws NetworkException {
        if (!status.Error.equals("ok")) {
            throw NetworkExceptionFactory.GetExceptionByStatus(context, status);
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";

        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        inputStream.close();
        return result;
    }
}
