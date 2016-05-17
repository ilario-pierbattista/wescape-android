package com.dii.ids.application.api.service;

import com.dii.ids.application.api.form.PasswordOAuth2Form;
import com.dii.ids.application.api.form.RefreshOAuthForm;
import com.dii.ids.application.api.response.AccessTokenBundle;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OAuth2Service {
    @POST("/oauth/v2/token")
    Call<AccessTokenBundle> getAccessToken(@Body PasswordOAuth2Form passwordOAuth2Form);

    @POST("/oauth/v2/token")
    Call<AccessTokenBundle> refreshAccessToken(@Body RefreshOAuthForm refreshOAuthForm);
}