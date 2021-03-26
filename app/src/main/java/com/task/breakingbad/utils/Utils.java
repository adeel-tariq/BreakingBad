package com.task.breakingbad.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

// utility class for doing some across the applications tasks (personally created)
public class Utils {

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern VALID_PASSWORD_REGEX =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z]).{6,}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern VALID_NUMBER_92 =
            Pattern.compile("^([+]\\d{2})\\d{10}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern VALID_NUMBER_03 =
            Pattern.compile("^([03]\\d{2})\\d{8}$", Pattern.CASE_INSENSITIVE);

    /**
     * checks for error type
     *
     * @param error throwable exception upon communication with server in presenter
     * @return string message or resource ID of message to be alerted to user
     */
    public static String errorType(Throwable error) {

        if (error instanceof SocketTimeoutException) { // if api error was timeout
            return "Problem connecting to server. Please check your internet connection and try again.";

        } else if (error instanceof IOException) { // if api error was IOException
            return "Problem connecting to server. Please check your internet connection and try again.";

        } else if (error instanceof JSONException) { // if api error was json exception
            return "Server error. Please try again later.";

        } else if (error instanceof HttpException) { // if api error was http exception
            Log.i("info", "HttpException");

            String responseBody = null;
            try {
                responseBody = Objects.requireNonNull(((HttpException) error).response().errorBody()).string();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            // based on different status code error message is extracted out from response this needs to be changed depending upon different ways different api pass error response data
            int statusCode = ((HttpException) error).code();
            if (statusCode >= 400 && statusCode < 500) {
                if (statusCode == 400 || statusCode == 403) {
                    String message = getMessage(responseBody);
                    if (message == null
                            || message.equalsIgnoreCase("Token is not active.")
                            || message.equalsIgnoreCase("Token invalid")
                            || message.equalsIgnoreCase("Token not provided")
                            || message.equalsIgnoreCase("User session not found")
                            || message.equalsIgnoreCase("null")
                            || message.equalsIgnoreCase("Unauthorised")
                            || message.equalsIgnoreCase("Your request is not authorized as token is invalid!")) {
                        return "-0";
                    } else {
                        return getMessage(responseBody);
                    }
                } else if (statusCode == 422) {
                    return getErrors(responseBody);
                } else if (statusCode == 401) {
                    String message = getMessage(responseBody);
                    if (message.contains("Token is not active.") || message.contains("Token invalid")
                            || message.contains("Token not provided") || message.contains("User session not found")
                            || message.equals("null") || message.equals("Your request is not authorized as token is invalid!")) {
                        return "-0";
                    } else {
                        return getMessage(responseBody);
                    }
                } else if (statusCode == 405) {
                    return "Problem connecting to server. Please try again later.";
                } else if (statusCode == 404) {
                    return "Problem connecting to server. Please try again later.";
                }
            } else {
                return "Problem connecting to server. Please try again later.";
            }
        }
        return "";
    }

    /**
     * retrieve message from response body
     *
     * @param responseBody json response body received by server
     * @return string message present in json
     */
    private static String getMessage(String responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            if (jsonObject.has("error_description")) {
                return jsonObject.getString("error_description").trim();
            } else {
                return jsonObject.getString("message").trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    /**
     * retrieve messages from response body
     *
     * @param responseBody json response body received by server
     * @return string message/s present in json
     */
    private static String getErrors(String responseBody) {
        try {
            StringBuilder errors = new StringBuilder();
            JSONObject jsonObject = new JSONObject(responseBody);

            if (jsonObject.has("message")) {
                JSONArray errorObj = jsonObject.getJSONArray("message");
                for (int i = 0; i < errorObj.length(); i++) {
                    errors.append("- ").append(errorObj.get(i)).append("\n");
                }
            } else {
                Log.e("info", "getErrors: 422 error message array problem");
                errors.append("Something went wrong, please try again later.");
            }
            return errors.toString().trim();
        } catch (JSONException e) {
            e.printStackTrace();

            try {
                JSONObject jsonObject = new JSONObject(responseBody);
                if (jsonObject.has("message")) {
                    return jsonObject.getString("message").trim();
                }
            } catch (Exception e1) {
                Log.e("info", "getErrors: 422 error message string problem");
                e1.printStackTrace();
            }
            return "Something went wrong, please try again later.";
        }

    }

    /**
     * check if internet connection is present or not and show a toast if not
     *
     * @param context the context
     */
    public static boolean connectionStatusOk(Context context) {

        boolean isConnected = false;
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    isConnected = true;
                }
            }
        }
        if (!isConnected) {
            info(context, "No Internet Connection", 4);
        }
        return isConnected;
    }

    /**
     * Info.
     *
     * @param context the context
     * @param message the message
     * @param type    message type. 1 = success, 2 = info, 3 = warning, 4 = error
     */
    public static void info(Context context, String message, int type) {
        if (type == 1)
            Toasty.success(context, message, Toasty.LENGTH_SHORT, true).show();
        else if (type == 2)
            Toasty.info(context, message, Toasty.LENGTH_SHORT, true).show();
        else if (type == 3)
            Toasty.warning(context, message, Toasty.LENGTH_SHORT, true).show();
        else if (type == 4)
            Toasty.error(context, message, Toasty.LENGTH_SHORT, true).show();
    }
}
