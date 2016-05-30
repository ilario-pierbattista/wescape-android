package com.dii.ids.application.api;

import com.dii.ids.application.api.service.WescapeService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiBuilder {
    // @TODO Passare in produzione
    private static final boolean DEV_ENVIRONMENT = true;

    public static WescapeService buildWescapeService(String ipAddress) {
        return ApiBuilder.buildWescapeService(ipAddress, DEV_ENVIRONMENT);
    }

    public static WescapeService buildWescapeService(String ipAddress, boolean development) {
        String endpoint = "";
        if (development) {
            endpoint = "app_dev.php/";
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ipAddress + "/" + endpoint)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(WescapeService.class);
    }
}
