package com.water.alkaline.kengen.data.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.water.alkaline.kengen.utils.SecUtils;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {

    public static final String BASE_URL = SecUtils.getMain();
    public static final String YOU_URL = SecUtils.getSub();
    private static RetroClient mInstance;
    private final Retrofit retrofit;
    private final Retrofit retrofit2;

    private RetroClient() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(1);

        OkHttpClient client = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        chain -> {
                            Request original = chain.request();

                            Request.Builder requestBuilder = original.newBuilder()
                                    .method(original.method(), original.body());

                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                ).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        retrofit2 = new Retrofit.Builder()
                .baseUrl(YOU_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
    }

    public static synchronized RetroClient getInstance() {
        if (mInstance == null) {
            mInstance = new RetroClient();
        }
        return mInstance;
    }

    public RetroInterface getApi() {
        return retrofit.create(RetroInterface.class);
    }

    public RetroInterface getYouApi() {
        return retrofit2.create(RetroInterface.class);
    }

}