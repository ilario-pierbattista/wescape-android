package com.dii.ids.application.main.navigation.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dii.ids.application.R;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.navigation.Checkpoint;

public class NearestExitTask extends AsyncTask<Node, Void, Boolean> {

    public static final String TAG = MinimumPathTask.class.getName();
    private MaterialDialog dialog;
    private Context context;
    private Exception thrownException;
    private TaskListener<Checkpoint> listener;
    private Checkpoint searchResult;

    public NearestExitTask(Context context, TaskListener<Checkpoint> listener) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Node... params) {
        try {

            return (searchResult != null);
        } catch (Exception e) {
            thrownException = e;
            return false;
        }
    }

    @Override
    protected void onPreExecute() {
        dialog = new MaterialDialog.Builder(context)
                .title("Trovo l'uscita più vicina")
                .content(context.getString(R.string.please_wait))
                .progress(true, 0)
                .widgetColorRes(R.color.regularBlue)
                .show();
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (success) {
            listener.onTaskSuccess(searchResult);
        } else {
            listener.onTaskError(thrownException);
        }
        listener.onTaskComplete();
    }
}
