package com.dii.ids.application.main.authentication.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.dii.ids.application.api.auth.UserManager;
import com.dii.ids.application.api.auth.wescape.WescapeUserManager;
import com.dii.ids.application.listener.TaskListener;

/**
 * Represents an asynchronous login/registration task used to authenticate the user.
 */
public class PasswordResetTask extends AsyncTask<String, Void, Boolean> {
    private UserManager userManager;
    private Exception thrownException;
    private TaskListener<Void> listener;

    public PasswordResetTask(Context context, TaskListener<Void> listener) {
        userManager = new WescapeUserManager(context);
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            String email, secretCode, password;
            email = params[0];
            secretCode = params[1];
            password = params[2];

            userManager.resetPassword(email, secretCode, password);
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