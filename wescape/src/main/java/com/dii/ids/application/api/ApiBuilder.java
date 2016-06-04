package com.dii.ids.application.api;

import android.util.Log;

import com.dii.ids.application.api.service.WescapeService;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiBuilder {
    // @TODO Passare in produzione
    private static final boolean DEV_ENVIRONMENT = true;
    private static final long CONNECTION_TIMEOUT_STD = 3;
    private static final long READ_TIMEOUT = 5;
    private static final long WRITE_TIMEOUT = 5;

    public static WescapeService buildWescapeService(String ipAddress) {
        return ApiBuilder.buildWescapeService(ipAddress, DEV_ENVIRONMENT);
    }

    public static WescapeService buildWescapeService(String ipAddress, long connectionTimeout) {
        return ApiBuilder.buildWescapeService(ipAddress, DEV_ENVIRONMENT, connectionTimeout);
    }


    public static WescapeService buildWescapeService(String ipAddress, boolean development) {
        return ApiBuilder.buildWescapeService(ipAddress, DEV_ENVIRONMENT, CONNECTION_TIMEOUT_STD);
    }

    public static WescapeService buildWescapeService(String ipAddress, boolean development, long connectionTimeout) {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(connectionTimeout, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build();

        String endpoint = "";
        if (development) {
            endpoint = "app_dev.php/";
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ipAddress + "/" + endpoint)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(WescapeService.class);
    }
}
