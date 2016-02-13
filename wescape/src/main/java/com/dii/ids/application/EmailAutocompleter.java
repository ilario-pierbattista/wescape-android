package com.dii.ids.application;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.view.View;
import android.widget.AutoCompleteTextView;

import static android.Manifest.permission.READ_CONTACTS;

public class EmailAutocompleter {
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    public static final int REQUEST_READ_CONTACTS = 0;
    private Fragment fragment;
    private AutoCompleteTextView autoCompleteTextView;

    public EmailAutocompleter(Fragment fragment,
                              AutoCompleteTextView autoCompleteTextView) {
        this.fragment = fragment;
        this.autoCompleteTextView = autoCompleteTextView;
    }

    public void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        fragment.getLoaderManager().initLoader(0, null, (LoaderManager.LoaderCallbacks)fragment);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (fragment.getActivity().checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (fragment.shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(autoCompleteTextView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            fragment.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            fragment.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions,
                                     @NonNull int[] grantResults) {
        if (requestCode == EmailAutocompleter.REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    public interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
}
