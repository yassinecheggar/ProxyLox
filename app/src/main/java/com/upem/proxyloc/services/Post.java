package com.upem.proxyloc.services;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Post extends AsyncTask<Object, Integer, String> {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    @Override
    protected String doInBackground(Object... objects) {
        OkHttpClient client = new OkHttpClient();
        String url = objects[0].toString();

      //  Log.e("post data ", "doInBackground: "+ objects[1].toString() );
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),objects[1].toString());

       // Log.e("json POst ", jsonObject.toString() );
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0")
                .addHeader("Content-Type", "application/json")
                .post(body).build();

        try {
            Response response = client.newCall(request).execute();
                Log.e("resp" , response.code()+"");
            if (response.code() == 200) {
                //Log.e("post", "success");
                final String myResponse = response.body().string();

                Log.e("lola", myResponse);
                return "succes";
            }
        }catch (Exception E){
            Log.e("Post data ", "yes");
        }

        return "error";
    }


    public String sendshit(Object... objects){

        OkHttpClient client = new OkHttpClient();
        String url = objects[0].toString();

        //  Log.e("post data ", "doInBackground: "+ objects[1].toString() );
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),objects[1].toString());

        // Log.e("json POst ", jsonObject.toString() );
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0")
                .addHeader("Content-Type", "application/json")
                .post(body).build();

        try {
            Response response = client.newCall(request).execute();
            Log.e("resp" , response.code()+"");
            if (response.code() == 200) {
                //Log.e("post", "success");
                final String myResponse = response.body().string();

                Log.e("lola", myResponse);
                return "succes";
            }
        }catch (Exception E){
            Log.e("Post data ", "yes");
        }

        return "error";

    }


}
