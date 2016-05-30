package com.dii.ids.application.main.authentication.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.dii.ids.application.api.auth.Authenticator;
import com.dii.ids.application.api.auth.wescape.WescapeAuthenticator;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.main.settings.SettingsActivity;

/**
 * Represents an asynchronous login/registration task used to authenticate the user.
 */
public class UserLoginTask extends AsyncTask<String, Void, Boolean> {
    private static final String TAG = UserLoginTask.class.getName();
    private TaskListener<Void> listener;
    private Exception thrownException;
    private Authenticator authenticator;

    public UserLoginTask(
            Context context,
            TaskListener<Void> listener) {

        String ipAddress = (PreferenceManager.getDefaultSharedPreferences(context))
                .getString(SettingsActivity.WESCAPE_HOSTNAME,
                        SettingsActivity.WESCAPE_DEFAULT_HOSTNAME);
        this.authenticator = new WescapeAuthenticator(context, ipAddress);
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            String email = params[0];
            String password = params[1];

            authenticator.login(email, password);

            return true;
        } catch (Exception e) {
            thrownException = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            listener.onTaskSuccess(null);
        } else {
            listener.onTaskError(thrownException);
        }
        listener.onTaskComplete();
    }

    @Override
    protected void onCancelled() {
        listener.onTaskCancelled();
    }
}