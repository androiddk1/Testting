package com.example.sockettest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.sockettest.R.id.img;

/**
 * Created by yingpu on 2017/4/24.
 */

public class Constans {
    public static final String SOCKET_CONNECT_KEY = "C6149F66CEF5598C572FE2BA08EA4BF1";

    public static String parseImgJson(String json) {
        String img = "";
        try {
            JSONObject jsonObject = new JSONObject(json);
            String obj = jsonObject.getString("data");
            JSONObject jsonObject1 = new JSONObject(obj);
            String result = jsonObject1.getString("image");
//            result = result.substring(result.indexOf("base64,") + 7, result.length());
            return result;

        } catch (JSONException e) {
            e.printStackTrace();
            return img;
        }

    }
}
