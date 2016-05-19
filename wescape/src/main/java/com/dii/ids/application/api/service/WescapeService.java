package com.dii.ids.application.api.service;

import com.dii.ids.application.api.form.PasswordOAuth2Form;
import com.dii.ids.application.api.form.RefreshOAuthForm;
import com.dii.ids.application.api.response.TokenResponse;
import com.dii.ids.application.entity.Edge;
import com.dii.ids.application.entity.Node;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface WescapeService {
    @POST("/oauth/v2/token")
    Call<TokenResponse> getAccessToken(@Body PasswordOAuth2Form passwordOAuth2Form);

    @POST("/oauth/v2/token")
    Call<TokenResponse> refreshAccessToken(@Body RefreshOAuthForm refreshOAuthForm);

    @GET("/api/v1/nodes.json")
    Call<List<Node>> listNodes(@Header("Authorization") String authorization);

    @GET("/api/v1/edges.json")
    Call<List<Edge>> listEdges(@Header("Authorization") String authorization);
}
