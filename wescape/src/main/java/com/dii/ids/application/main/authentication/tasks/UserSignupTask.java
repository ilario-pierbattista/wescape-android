package com.dii.ids.application.main.authentication.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.dii.ids.application.api.auth.UserManager;
import com.dii.ids.application.api.auth.wescape.WescapeUserManager;
import com.dii.ids.application.listener.TaskListener;

/**
 * Represents an asynchronous login/registration task used to authenticate the user.
 */
public class UserSignupTask extends AsyncTask<String, Void, Boolean> {
    private UserManager userManager;
    private TaskListener<String[]> listener;
    private Exception thrownException;
    private String email, password;

    public UserSignupTask(Context context,
                          TaskListener<String[]> listener) {
        userManager = new WescapeUserManager(context);
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            email = params[0];
            password = params[1];

            userManager.signup(email, password);
        } catch (Exception e) {
            thrownException = e;
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            String[] credentials = new String[2];
            credentials[0] = email;
            credentials[1] = password;
            listener.onTaskSuccess(credentials);
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