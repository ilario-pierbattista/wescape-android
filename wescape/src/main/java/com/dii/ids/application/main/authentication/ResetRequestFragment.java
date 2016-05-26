package com.dii.ids.application.main.authentication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dii.ids.application.R;
import com.dii.ids.application.animations.ShowProgressAnimation;
import com.dii.ids.application.api.auth.exception.reset.WrongEmailException;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.main.authentication.tasks.ResetRequestTask;
import com.dii.ids.application.main.authentication.utils.EmailAutocompleter;
import com.dii.ids.application.validators.EmailValidator;


public class ResetRequestFragment extends BaseFragment {
    public static final String TAG = ResetRequestTask.class.getName();
    private static final String EMAIL = "email";

    private String inputEmail;
    private ViewHolder holder;
    private EmailAutocompleter autocompleter;

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param email Parameter 1.
     * @return A new instance of fragment ResetRequestFragment.
     */
    public static ResetRequestFragment newInstance(String email) {
        ResetRequestFragment fragment = new ResetRequestFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        autocompleter.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            inputEmail = getArguments().getString(EMAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.authentication_request_reset_fragment, container, false);
        ((AuthenticationActivity) getActivity())
                .showActionBar(getString(R.string.authentication_title_bar));
        holder = new ViewHolder(view);
        holder.showProgressAnimation = new ShowProgressAnimation(
                holder.scrollView, holder.progressBar, getShortAnimTime());
        autocompleter = new EmailAutocompleter(this, holder.emailField);
        autocompleter.populateAutoComplete();

        if (inputEmail != null) {
            holder.emailField.setText(inputEmail);
        }

        holder.emailField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.request || id == EditorInfo.IME_NULL) {
                    hideKeyboard(view);

                    requestReset();
                    return true;
                }
                return false;
            }
        });

        holder.resetRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
                requestReset();
            }
        });

        holder.resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(view);
                resetPassword();
            }
        });

        return view;
    }

    /**
     * Attempts to sign in or register the account specified by the login form. If there are form
     * errors (invalid inputEmail, missing fields, etc.), the errors are presented and no actual
     * login attempt is made.
     */
    private void requestReset() {
        inputEmail = getValidEmailAddress();

        if (inputEmail != null) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            holder.showProgressAnimation.showProgress(true);
            ResetRequestTask resetRequestTask = new ResetRequestTask(
                    getContext(), new ResetRequestTaskListener());
            resetRequestTask.execute(inputEmail);
        }
    }

    private void resetPassword() {
        inputEmail = getValidEmailAddress();

        if (inputEmail != null) {
            ResetPasswordFragment fragment = ResetPasswordFragment.newInstance(inputEmail);

            FragmentManager fm = getActivity().getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.authentication_content_pane, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Nullable
    private String getValidEmailAddress() {
        EmailValidator emailValidator = new EmailValidator();
        String email = holder.emailField.getText().toString();
        View focusView = null;
        boolean error = false;

        if (TextUtils.isEmpty(email)) {
            holder.emailFieldLayout.setError(getString(R.string.error_field_required));
            focusView = holder.emailField;
            error = true;
        } else if (!emailValidator.isValid(email)) {
            holder.emailFieldLayout.setError(getString(R.string.error_invalid_email));
            focusView = holder.emailField;
            error = true;
        }

        if (error) {
            focusView.requestFocus();
            Log.i(TAG, "Invalid");
            return null;
        } else {
            Log.i(TAG, "Valid");
            return email;
        }
    }

    private class ResetRequestTaskListener implements TaskListener<Void> {
        @Override
        public void onTaskSuccess(Void aVoid) {
            ResetPasswordFragment fragment;

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragment = ResetPasswordFragment.newInstance(getValidEmailAddress());

            transaction.replace(R.id.authentication_content_pane, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit();
        }

        @Override
        public void onTaskError(Exception e) {
            handleGeneralErrors(e);
            if (e instanceof WrongEmailException) {
                holder.emailFieldLayout.setError(getString(R.string.error_email_not_found));
                holder.emailField.requestFocus();
            } else {
                Log.e(TAG, "Errore non gestito", e);
            }
        }

        @Override
        public void onTaskComplete() {
            holder.showProgressAnimation.showProgress(false);
        }

        @Override
        public void onTaskCancelled() {
            holder.showProgressAnimation.showProgress(false);
        }
    }

    public class ViewHolder extends BaseFragment.ViewHolder {
        private final ProgressBar progressBar;
        private final ScrollView scrollView;
        private final AutoCompleteTextView emailField;
        private final TextInputLayout emailFieldLayout;
        private final Button resetRequestButton;
        private final Button resetPasswordButton;
        private ShowProgressAnimation showProgressAnimation;

        public ViewHolder(View v) {
            progressBar = (ProgressBar) v.findViewById(R.id.reset_request_progress);
            scrollView = (ScrollView) v.findViewById(R.id.reset_request_scroll_view);
            emailField = (AutoCompleteTextView) v.findViewById(R.id.reset_request_email_text_input);
            emailFieldLayout = (TextInputLayout) v.findViewById(R.id.reset_request_email_text_input_layout);
            resetRequestButton = (Button) v.findViewById(R.id.request_reset_button);
            resetPasswordButton = find(v, R.id.go_to_reset_fragment_button);
        }
    }
}
