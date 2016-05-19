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
    private TaskListener<Void> listener;
    private Exception thrownException;

    public UserSignupTask(Context context,
                          TaskListener<Void> listener) {
        userManager = new WescapeUserManager(context);
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            String email = params[0];
            String password = params[1];

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