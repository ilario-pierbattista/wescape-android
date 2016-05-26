package com.dii.ids.application.main.authentication;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dii.ids.application.R;
import com.dii.ids.application.animations.ShowProgressAnimation;
import com.dii.ids.application.api.auth.exception.reset.ExpiredSecretException;
import com.dii.ids.application.api.auth.exception.reset.WrongEmailException;
import com.dii.ids.application.api.auth.exception.reset.WrongSecretCodeException;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.main.authentication.tasks.PasswordResetTask;
import com.dii.ids.application.validators.EmailValidator;
import com.dii.ids.application.validators.PasswordValidator;
import com.dii.ids.application.validators.SecretCodeValidator;

public class ResetPasswordFragment extends BaseFragment {
    private static final String EMAIL = "email";
    private String email;
    private ViewHolder holder;

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param email Parameter 1.
     * @return A new instance of fragment ResetPasswordFragment.
     */
    public static ResetPasswordFragment newInstance(String email) {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString(EMAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.authentication_reset_password_fragment, container, false);
        ((AuthenticationActivity) getActivity())
                .showActionBar(getString(R.string.action_reset_request));
        holder = new ViewHolder(view);
        holder.showProgressAnimation = new ShowProgressAnimation(
                holder.scrollView, holder.progress, getShortAnimTime());

        holder.emailField.setText(email);

        holder.passwordConfirmField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.reset || id == EditorInfo.IME_NULL) {
                    hideKeyboard(view);

                    resetPassword();
                    return true;
                }
                return false;
            }
        });

        holder.resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
                resetPassword();
            }
        });

        return view;
    }

    /**
     * Attempts to sign in or register the account specified by the login form. If there are form
     * errors (invalid email, missing fields, etc.), the errors are presented and no actual login
     * attempt is made.
     */
    private void resetPassword() {
        EmailValidator emailValidator = new EmailValidator();
        PasswordValidator passwordValidator = new PasswordValidator();
        SecretCodeValidator secretCodeValidator = new SecretCodeValidator();

        // Reset errors.
        holder.emailFieldLayout.setError(null);
        holder.secretCodeFieldLayout.setError(null);
        holder.passwordFieldLayout.setError(null);
        holder.passwordConfirmFieldLayout.setError(null);

        // Store values at the time of the login attempt.
        String email = holder.emailField.getText().toString();
        String secretCode = holder.secretCodeField.getText().toString();
        String password = holder.passwordField.getText().toString();
        String passwordConfirm = holder.passwordConfirmField.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            holder.emailFieldLayout.setError(getString(R.string.error_field_required));
            focusView = holder.emailField;
            cancel = true;
        } else if (!emailValidator.isValid(email)) {
            holder.emailFieldLayout.setError(getString(R.string.error_invalid_email));
            focusView = holder.emailField;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!(passwordValidator.isValid(password))) {
            holder.passwordFieldLayout.setError(getString(R.string.error_invalid_password));
            focusView = holder.passwordField;
            cancel = true;
        }

        // Check for confirm password correct
        if (!password.equals(passwordConfirm)) {
            holder.passwordConfirmFieldLayout.setError(getString(R.string.error_mismatching_password_confirm));
            focusView = holder.passwordConfirmField;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(secretCode)) {
            holder.secretCodeFieldLayout.setError(getString(R.string.error_field_required));
            focusView = holder.secretCodeField;
            cancel = true;
        } else if (!secretCodeValidator.isValid(secretCode)) {
            holder.secretCodeFieldLayout.setError(getString(R.string.error_invalid_secretcode));
            focusView = holder.secretCodeField;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            holder.showProgressAnimation.showProgress(true);
            PasswordResetTask task = new PasswordResetTask(getContext(), new ResetPasswordTaskListener());
            task.execute(email, secretCode, password);
        }
    }

    private class ResetPasswordTaskListener implements TaskListener<Void> {
        @Override
        public void onTaskSuccess(Void aVoid) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            fm.popBackStack();
            fm.popBackStack();
        }

        @Override
        public void onTaskError(Exception e) {
            handleGeneralErrors(e);
            if (e instanceof WrongEmailException) {
                holder.emailFieldLayout.setError(getString(R.string.error_email_not_found));
                holder.emailField.requestFocus();
            } else if (e instanceof WrongSecretCodeException) {
                holder.secretCodeFieldLayout.setError(getString(R.string.error_wrong_secret_code));
                holder.secretCodeField.requestFocus();
            } else if (e instanceof ExpiredSecretException) {
                holder.secretCodeFieldLayout.setError(getString(R.string.error_secret_code_expired));
                holder.secretCodeField.requestFocus();
            } else {
                Log.e(TAG, "Eccezione non gestita", e);
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
        public final ProgressBar progress;
        public final ScrollView scrollView;
        public final EditText secretCodeField;
        public final TextInputLayout secretCodeFieldLayout;
        public final EditText passwordField;
        public final TextInputLayout passwordFieldLayout;
        public final EditText passwordConfirmField;
        public final TextInputLayout passwordConfirmFieldLayout;
        public final Button resetPasswordButton;
        private final AutoCompleteTextView emailField;
        private final TextInputLayout emailFieldLayout;
        private ShowProgressAnimation showProgressAnimation;

        public ViewHolder(View v) {
            progress = (ProgressBar) v.findViewById(R.id.reset_passwd_progress);
            scrollView = (ScrollView) v.findViewById(R.id.reset_passwd_scroll_view);
            emailField = (AutoCompleteTextView) v.findViewById(R.id.reset_passwd_email_text_input);
            emailFieldLayout = (TextInputLayout) v.findViewById(R.id.reset_passwd_email_text_input_layout);
            secretCodeField = (EditText) v.findViewById(R.id.reset_passwd_secretcode_text_input);
            secretCodeFieldLayout = (TextInputLayout) v.findViewById(R.id.reset_passwd_secretcode_text_input_layout);
            passwordField = (EditText) v.findViewById(R.id.reset_passwd_password_text_input);
            passwordFieldLayout = (TextInputLayout) v.findViewById(R.id.reset_passwd_password_text_input_layout);
            passwordConfirmField = (EditText) v.findViewById(R.id.reset_passwd_password_confirm_text_input);
            passwordConfirmFieldLayout = (TextInputLayout) v.findViewById(R.id.reset_passwd_password_confirm_text_input_layout);
            resetPasswordButton = (Button) v.findViewById(R.id.reset_passwd_button);
        }
    }
}
