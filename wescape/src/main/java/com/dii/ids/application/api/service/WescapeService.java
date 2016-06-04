package com.dii.ids.application.api.service;

import com.dii.ids.application.api.form.CreateUserForm;
import com.dii.ids.application.api.form.LoginForm;
import com.dii.ids.application.api.form.RefreshTokenForm;
import com.dii.ids.application.api.form.RequestPasswordForm;
import com.dii.ids.application.api.form.ResetPasswordForm;
import com.dii.ids.application.api.form.UserForm;
import com.dii.ids.application.api.response.StatusResponse;
import com.dii.ids.application.api.response.TokenResponse;
import com.dii.ids.application.api.response.UserResponse;
import com.dii.ids.application.entity.Edge;
import com.dii.ids.application.entity.Node;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface WescapeService {
    @POST("oauth/v2/token")
    Call<TokenResponse> getAccessToken(@Body LoginForm loginForm);

    @POST("/oauth/v2/token")
    Call<TokenResponse> refreshAccessToken(@Body RefreshTokenForm refreshTokenForm);

    @POST("/api/v1/users")
    Call<UserResponse> createUser(@Body CreateUserForm userForm);

    @PUT("/api/v1/users/{user}")
    Call<UserResponse> updateUser(@Header("Authorization") String authorization, @Path("user") int id, @Body UserForm userForm);

    @GET("/api/v1/user/whoami")
    Call<UserResponse> getCurrentUser(@Header("Authorization") String authorization);

    @POST("/api/v1/users/password/request")
    Call<StatusResponse> requestPasswordReset(@Body RequestPasswordForm requestPasswordForm);

    @POST("/api/v1/users/password/reset")
    Call<StatusResponse> resetPassword(@Body ResetPasswordForm resetPasswordForm);

    @GET("/api/v1/nodes.json")
    Call<List<Node>> listNodes(@Header("Authorization") String authorization);

    @GET("/api/v1/edges.json")
    Call<List<Edge>> listEdges(@Header("Authorization") String authorization);

    @GET("/static/maps/{floor}")
    @Streaming
    Call<ResponseBody> downloadFloorMap(@Path("floor") int floor);
}
