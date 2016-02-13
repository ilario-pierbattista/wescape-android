package com.dii.ids.application.main.authentication.tasks;

import android.os.AsyncTask;

import com.dii.ids.application.R;
import com.dii.ids.application.main.authentication.LoginFragment;

/**
 * Represents an asynchronous login/registration task used to authenticate the user.
 */
public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
    /**
     * A dummy authentication store containing known user names and passwords. TODO: remove after
     * connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    private final String mEmail;
    private final String mPassword;
    private LoginFragment fragment;
    private LoginFragment.ViewHolder holder;

    public UserLoginTask(String email, String password) {
        mEmail = email;
        mPassword = password;
    }

    public UserLoginTask inject(LoginFragment fragment, LoginFragment.ViewHolder holder) {
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
            if (pieces[0].equals(mEmail)) {
                // Account exists, return true if the password matches.
                return pieces[1].equals(mPassword);
            }
        }

        // TODO: register the new account here.
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        fragment.wipeAsyncTask();

        if (success) {
            fragment.getActivity().finish();
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