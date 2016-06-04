package com.dii.ids.application.notifications;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dii.ids.application.api.ApiBuilder;
import com.dii.ids.application.api.auth.wescape.WescapeSessionManager;
import com.dii.ids.application.api.response.UserResponse;
import com.dii.ids.application.api.service.WescapeService;
import com.dii.ids.application.main.settings.SettingsActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Response;

public class WescapeInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = WescapeInstanceIdService.class.getName();

    @Override
    public void onTokenRefresh() {
        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);
    }

    public static void sendRegistrationToServer(Context context, String accessToken) {
        //You can implement this method to store the token on your server

        String ipAddress = (PreferenceManager.getDefaultSharedPreferences(context))
                .getString(SettingsActivity.WESCAPE_HOSTNAME,
                           SettingsActivity.WESCAPE_DEFAULT_HOSTNAME);
        WescapeService service = ApiBuilder.buildWescapeService(ipAddress);

        try {
            Call<UserResponse> call = service.getCurrentUser(accessToken);
            Response<UserResponse> response = call.execute();

            switch (response.code()) {
                case HttpURLConnection.HTTP_OK: {
                    UserResponse userResponse = response.body();
                    Log.d(TAG, userResponse.toString());
                    break;
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Errore salvataggio device token: ", e);
        }

    }

}