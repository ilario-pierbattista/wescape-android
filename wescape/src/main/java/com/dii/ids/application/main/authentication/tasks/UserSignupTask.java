package com.dii.ids.application.main.authentication.tasks;

import android.os.AsyncTask;

import com.dii.ids.application.R;
import com.dii.ids.application.main.authentication.SignupFragment;

/**
 * Represents an asynchronous login/registration task used to authenticate the user.
 */
public class UserSignupTask extends AsyncTask<Void, Void, Boolean> {
    /**
     * A dummy authentication store containing known user names and passwords. TODO: remove after
     * connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    private final String email;
    private final String password;
    private SignupFragment fragment;
    private SignupFragment.ViewHolder holder;

    public UserSignupTask(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserSignupTask inject(SignupFragment fragment, SignupFragment.ViewHolder holder) {
        this.fragment = fragment;
        this.holder = holder;
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

        for (String credential : DUMMY_CREDENTIALS) {
            String[] pieces = credential.split(":");
            if (pieces[0].equals(email)) {
                // Account exists, return true if the password matches.
                return pieces[1].equals(password);
            }
        }

        // TODO: register the new account here.
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        fragment.wipeAsyncTask();

        if (success) {
            // @TODO Sostituire con altra roba
            fragment.wipeAsyncTask();
        } else {
            holder.passwordField.setError(fragment.getString(R.string.error_incorrect_password));
            holder.passwordField.requestFocus();
        }
    }

    @Override
    protected void onCancelled() {
        fragment.wipeAsyncTask();
    }
}