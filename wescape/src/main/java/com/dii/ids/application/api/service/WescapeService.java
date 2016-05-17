package com.dii.ids.application.api.service;

import com.dii.ids.application.entity.Node;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface WescapeService {
    @GET("nodes.json")
    Call<List<Node>> listNodes(@Header("Authorization") String authorization);
}
