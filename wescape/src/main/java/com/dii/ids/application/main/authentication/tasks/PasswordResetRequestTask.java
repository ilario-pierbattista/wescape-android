package com.dii.ids.application.main.authentication.tasks;

import android.os.AsyncTask;

import com.dii.ids.application.R;
import com.dii.ids.application.main.authentication.RequestResetFragment;
import com.dii.ids.application.main.authentication.SignupFragment;

/**
 * Represents an asynchronous login/registration task used to authenticate the user.
 */
public class PasswordResetRequestTask extends AsyncTask<Void, Void, Boolean> {
    /**
     * A dummy authentication store containing known user names and passwords. TODO: remove after
     * connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com", "bar@example.com"
    };

    private final String email;
    private RequestResetFragment fragment;
    private RequestResetFragment.ViewHolder holder;

    public PasswordResetRequestTask(String email) {
        this.email = email;
    }

    public PasswordResetRequestTask inject(RequestResetFragment fragment, RequestResetFragment.ViewHolder holder) {
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

        for(String e : DUMMY_CREDENTIALS) {
            if(email.equals(e)) {
                return true;
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
            // @TODO Aggiungere gestione dell'errore
            fragment.wipeAsyncTask();
        }
    }

    @Override
    protected void onCancelled() {
        fragment.wipeAsyncTask();
    }
}