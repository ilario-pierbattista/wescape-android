package com.dii.ids.application.main.authentication.tasks;

import android.os.AsyncTask;

import com.dii.ids.application.api.auth.Authenticator;
import com.dii.ids.application.listener.TaskListener;

/**
 * Represents an asynchronous login/registration task used to authenticate the user.
 */
public class UserLoginTask extends AsyncTask<String, Void, Boolean> {
    private static final String TAG = UserLoginTask.class.getName();
    private TaskListener<Void> listener;
    private Authenticator authenticator;
    private Exception thrownException;

    public UserLoginTask(
            Authenticator authenticator,
            TaskListener<Void> listener) {
        this.authenticator = authenticator;
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