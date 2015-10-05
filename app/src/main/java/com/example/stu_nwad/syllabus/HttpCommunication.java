package com.example.stu_nwad.syllabus;
import android.util.Log;

import com.example.stu_nwad.activities.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 *  与服务器进行通信
 */
public class HttpCommunication {

    public static int timeout = 3000; // 3s

    public static String  performPostCall(String requestURL,
                                   HashMap<String, String> postDataParams) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(timeout);
            conn.setConnectTimeout(timeout);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            Log.d(MainActivity.TAG, "start writing data");
            writer.write(getPostDataString(postDataParams));
            Log.d(MainActivity.TAG, "writer.write()");
            writer.flush();
            Log.d(MainActivity.TAG, "writer.flush()");
            writer.close();
            Log.d(MainActivity.TAG, "writer.close()");
            os.close();
            Log.d(MainActivity.TAG, "outputstream has closed!");
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
                Log.d(MainActivity.TAG, "POST CALL OK");
            }
            else {
                response="";
                Log.d(MainActivity.TAG, "POST CALL BAD");

            }
        } catch (Exception e) {
            Log.d(MainActivity.TAG, e.toString());
        }

        return response;
    }

    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        Log.d(MainActivity.TAG, "getPostDataString");
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
