package com.water.alkaline.kengen.data.network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.MyApplication;
import com.water.alkaline.kengen.utils.Constant;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {

    private static RetroClient retrofitCLient;

    private Retrofit retroMain;
    private Retrofit retrofit2;

    public boolean isConnected() {
        String iface = "";
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp())
                    iface = networkInterface.getName();

                if (iface.contains("tun") || iface.contains("ppp") || iface.contains("pptp")) {
                    return true;
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }

        return false;
    }

    public RetroClient(Context context) {

        String BASE_URL = PowerPreference.getDefaultFile().getString(Constant.apiKey, MyApplication.getSub(context)) + MyApplication.getMain(context);
        String YOU_URL = MyApplication.getSub(context);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(1);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        chain -> {
                            Request original = chain.request();
                            if (isConnected() && !original.url().toString().startsWith(MyApplication.getSub(MyApplication.getContext()))) {
                                return new Response.Builder()
                                        .code(404)
                                        .body(ResponseBody.create(null, ""))
                                        .protocol(Protocol.HTTP_2)
                                        .message("not-verified").request(chain.request())
                                        .build();
                            } else {

                                Request.Builder requestBuilder = original.newBuilder()
                                        .method(original.method(), original.body());

                                Request request = requestBuilder.build();
                                return chain.proceed(request);
                            }
                        }
                ).dispatcher(dispatcher).build();

        retroMain = new Retrofit.Builder()
                .baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        retrofit2 = new Retrofit.Builder()
                .baseUrl(YOU_URL).addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
    }

    public static synchronized RetroClient getInstance(Context context) {
        if (retrofitCLient == null) {
            retrofitCLient = new RetroClient(context);
        }
        return retrofitCLient;
    }

    public RetroInterface getApi() {
        return retroMain.create(RetroInterface.class);
    }

    public RetroInterface getYouApi() {
        return retrofit2.create(RetroInterface.class);
    }

}
