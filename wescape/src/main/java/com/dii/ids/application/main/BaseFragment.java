package com.dii.ids.application.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public abstract class BaseFragment extends Fragment {

    public static String TOOLBAR_TITLE = "toolbar_title";

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
