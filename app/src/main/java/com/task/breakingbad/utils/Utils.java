package com.task.breakingbad.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

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

        if (error instanceof SocketTimeoutException) {
            return "Problem connecting to server. Please check your internet connection and try again.";

        } else if (error instanceof IOException) {
            return "Problem connecting to server. Please check your internet connection and try again.";

        } else if (error instanceof JSONException) {
            return "Server error. Please try again later.";

        } else if (error instanceof HttpException) {
            Log.i("info", "HttpException");

            String responseBody = null;
            try {
                responseBody = Objects.requireNonNull(((HttpException) error).response().errorBody()).string();
                Log.i("info", "responseBody: " + responseBody + " statusCode " + ((HttpException) error).code());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
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
     * retrieve messages from reponse body
     *
     * @param responseBody json response body received by server
     * @return string message/s present in json
     */
    private static String getErrors(String responseBody) {
//        try {
//            JSONObject jsonObject = new JSONObject(responseBody);
//            return jsonObject.getString("message");
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return "";
//        }

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
     * Validate email boolean.
     *
     * @param emailStr the email str
     * @return the boolean
     */
    public static boolean validateEmail(CharSequence emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);

        String x = "";
        if (x.matches("^([+]\\d{2}[ ])?\\d{10}$")) {
            System.out.println("OK");
        }


        return matcher.find();
    }

    /**
     * Validate password boolean.
     *
     * @param passwordStr the password str
     * @return the boolean
     */
    public static boolean validatePassword(CharSequence passwordStr) {
        Matcher matcher = VALID_PASSWORD_REGEX.matcher(passwordStr);
        return matcher.find();
    }

    /**
     * Validate password boolean.
     *
     * @param number the phone str
     * @return the boolean
     */
    public static boolean validatePhone(CharSequence number) {
        Matcher matcher = VALID_NUMBER_92.matcher(number);
        Matcher matcher1 = VALID_NUMBER_03.matcher(number);
        return matcher.find() || matcher1.find();
    }

    /**
     * noConnectionDialog.
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

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
