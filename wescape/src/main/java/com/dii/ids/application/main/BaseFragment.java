package com.dii.ids.application.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public abstract class BaseFragment extends Fragment {

    public static String TOOLBAR_TITLE = "toolbar_title";

    protected void hideKeyboard(View view) {
        // Nasconde la tastiera
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
