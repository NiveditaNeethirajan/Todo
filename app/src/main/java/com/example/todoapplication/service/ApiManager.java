package com.example.todoapplication.service;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ApiManager {
    private static final String BASE_URL = "https://todos.flexhire.com/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, String token,
                           AsyncHttpResponseHandler responseHandler) {
        if(token != null) {
            client.addHeader("Authorization", "Bearer  "+token);
        }

        client.get(getAbsoluteUrl(url), params, responseHandler);
    }
    public static void get(String url, RequestParams params,
                           AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, String token,
                                 AsyncHttpResponseHandler responseHandler) {
        if(token != null) {
            client.addHeader("Authorization", "Bearer  "+token);
        }

        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params,
                            AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void delete(String url, RequestParams params, String token,
                            AsyncHttpResponseHandler responseHandler) {
        if(token != null) {
            client.addHeader("Authorization", "Bearer  "+token);
        }

        client.delete(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
