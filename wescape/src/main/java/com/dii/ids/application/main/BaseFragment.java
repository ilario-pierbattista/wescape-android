package com.dii.ids.application.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.dii.ids.application.R;
import com.dii.ids.application.entity.db.WescapeDatabase;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.net.ConnectException;

public abstract class BaseFragment extends Fragment {

    public static final String TAG = BaseFragment.class.getName();
    public static final String META_CLIENT_ID_KEY = "com.dii.ids.application.WESCAPE_CLIENT_ID";
    public static final String META_CLIENT_SECRET_KEY = "com.dii.ids.application.WESCAPE_CLIENT_SECRET";

    public static final int STARTING_FLOOR = 155;
    public static final int QR_READER_DIALOG_REQUEST_CODE = 100;
    public static final int ORIGIN_SELECTION_REQUEST_CODE = 200;
    public static final int DESTINATION_SELECTION_REQUEST_CODE = 201;
    public static String TOOLBAR_TITLE = "toolbar_title";
    private Bundle metaData = null;
    protected DatabaseDefinition database;

    public BaseFragment() {
        database = FlowManager.getDatabase(WescapeDatabase.class);
    }

    /**
     * Wrap di ContextCompact.getColor()
     *
     * @param id Id del colore
     * @return Codice del color
     */
    public int color(int id) {
        return ContextCompat.getColor(getContext(), id);
    }

    /**
     * Nasconde forzatamente la tastiera
     *
     * @param view Oggetto vista interessato
     */
    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public int getShortAnimTime() {
        return getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    protected void handleGeneralErrors(Exception e) {
        if (e instanceof ConnectException) {
            Toast.makeText(getContext(), getString(R.string.error_connection_failed), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public static abstract class ViewHolder {

        @SuppressWarnings("unchecked")
        public <T extends View> T find(View view, int id) {
            View resultView = view.findViewById(id);
            try {
                return (T) resultView;
            } catch (ClassCastException e) {
                return null;
            }
        }
    }
}
