package com.dii.ids.application.main.authentication.tasks;

import android.os.AsyncTask;

import com.dii.ids.application.main.authentication.ResetPasswordFragment;

/**
 * Represents an asynchronous login/registration task used to authenticate the user.
 */
public class PasswordResetTask extends AsyncTask<Void, Void, Boolean> {
    /**
     * A dummy authentication store containing known user names and passwords. TODO: remove after
     * connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com", "bar@example.com"
    };

    private final String email;
    private final String secretCode;
    private final String password;
    private final String passwordConfirm;
    private ResetPasswordFragment fragment;

    public PasswordResetTask(String email, String secretCode, String password, String passwordConfirm) {
        this.email = email;
        this.secretCode = secretCode;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }

    public PasswordResetTask inject(ResetPasswordFragment fragment) {
        this.fragment = fragment;
        return this;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.

        try {
            // Simulate network access.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return false;
        }

        for (String e : DUMMY_CREDENTIALS) {
            if (email.equals(e)) {
                return true;
            }
        }

        // TODO: register the new account here.
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            fragment.onTaskSuccess(this);
        } else {
            fragment.onTaskError(this);
        }
    }

    @Override
    protected void onCancelled() {
        fragment.onTaskCancelled(this);
    }
}