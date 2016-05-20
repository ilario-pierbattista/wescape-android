package com.dii.ids.application.main.authentication.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.dii.ids.application.api.auth.SessionManager;
import com.dii.ids.application.api.auth.wescape.TokenStorage;
import com.dii.ids.application.api.auth.wescape.WescapeSessionManager;
import com.dii.ids.application.listener.TaskListener;

public class AutomaticLoginTask extends AsyncTask<Void, Void, Boolean> {
    private SessionManager sessionManager;
    private TokenStorage tokenStorage;
    private TaskListener<Void> taskListener;
    private Exception thrownException;

    public AutomaticLoginTask(Context context,
                              TaskListener<Void> listener) {
        sessionManager = new WescapeSessionManager(context);
        tokenStorage = new TokenStorage(context);
        taskListener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            // Se il token è presente e valido, non verrà lanciata alcuna eccezione
            // Se il token è presente man non valido, sarà refreshato. Se tutto va bene non verrà
            // lanciata alcuna eccezione e sarà disponibile
            sessionManager.getAccessToken();

        } catch (Exception e) {
            thrownException = e;
            tokenStorage.delete();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            taskListener.onTaskSuccess(null);
        } else {
            taskListener.onTaskError(thrownException);
        }
        taskListener.onTaskComplete();
    }

    @Override
    protected void onCancelled() {
        taskListener.onTaskCancelled();
    }
}
