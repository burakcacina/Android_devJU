package net.burak.androidproject;

import android.util.Base64;
import android.util.Log;
import com.google.gson.Gson;
import net.burak.androidproject.models.TokenModel;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static net.burak.androidproject.AppConstants.LOG_TAG;

public class AppUtils {
    private static Gson gson = new Gson();

    public static TokenModel decodeToken(String JWTEncoded) {
        try {
            String[] split = JWTEncoded.split("\\.");
            return gson.fromJson(getJson(split[1]), TokenModel.class);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return null;
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
