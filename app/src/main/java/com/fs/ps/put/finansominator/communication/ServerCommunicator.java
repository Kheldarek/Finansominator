package com.fs.ps.put.finansominator.communication;

import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Kheldar on 22-Apr-17.
 */

public class ServerCommunicator {

    public static final String REQUEST_METHOD = "POST";

    public static String sendAndWaitForResponse(String apiKey, Map<String,Object> parameters) throws Exception{
        String response;
        URL url = new URL(apiKey);
        Log.i("URL", url.toString());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(REQUEST_METHOD);
        urlConnection.setConnectTimeout(3000);

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : parameters.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
        urlConnection.getOutputStream().write(postDataBytes);


        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            response = stringBuilder.toString();

        } finally {
            urlConnection.disconnect();
        }
        return response;

    }

}
