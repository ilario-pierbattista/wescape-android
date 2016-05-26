package com.dii.ids.application.main.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
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
import com.dii.ids.application.api.auth.exception.DuplicatedEmailException;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.main.authentication.tasks.UserSignupTask;
import com.dii.ids.application.main.authentication.utils.EmailAutocompleter;
import com.dii.ids.application.validators.EmailValidator;
import com.dii.ids.application.validators.PasswordValidator;


public class SignupFragment extends BaseFragment {
    public static final String TAG = SignupFragment.class.getName();
    public static final int ACCOUNT_CREATED = 1;
    public static final String INTENT_KEY_EMAIL = "email";
    public static final String INTENT_KEY_PASSWORD = "password";
    private static final String EMAIL = "email";

    private String email;
    private ViewHolder holder;
    private EmailAutocompleter emailAutocompleter;

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param email Parameter 1.
     * @return A new instance of fragment SignupFragment.
     */
    public static SignupFragment newInstance(String email) {
        SignupFragment fragment = new SignupFragment();
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
        emailAutocompleter.onRequestPermissionResult(requestCode, permissions, grantResults);
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
        final View view = inflater.inflate(R.layout.authentication_signup_fragment, container, false);

        ((AuthenticationActivity) getActivity())
                .showActionBar(getString(R.string.authentication_title_bar));
        holder = new ViewHolder(view);
        emailAutocompleter = new EmailAutocompleter(this, holder.emailField);
        holder.showProgressAnimation = new ShowProgressAnimation(
                holder.scrollView, holder.progressBar, getShortAnimTime());

        emailAutocompleter.populateAutoComplete();

        // Se in fase di login è stata immessa un'email valida, questa viene propagata
        // alla fase di signup
        if (email != null) {
            holder.emailField.setText(email);
        }

        holder.passwordConfirmField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.signup || id == EditorInfo.IME_NULL) {
                    hideKeyboard(view);
                    attempSignup();
                    return true;
                }
                return false;
            }
        });

        holder.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
                attempSignup();
            }
        });

        return view;
    }

    /**
     * Attempts to sign in or register the account specified by the login form. If there are form
     * errors (invalid email, missing fields, etc.), the errors are presented and no actual login
     * attempt is made.
     */
    private void attempSignup() {
        PasswordValidator passwordValidator = new PasswordValidator();
        EmailValidator emailValidator = new EmailValidator();

        // Reset errors.
        holder.emailFieldLayout.setError(null);
        holder.passwordFieldLayout.setError(null);
        holder.passwordConfirmFieldLayout.setError(null);
        holder.generalErrorTextView.setText(null);

        // Store values at the time of the login attempt.
        String email = holder.emailField.getText().toString();
        String password = holder.passwordField.getText().toString();
        String passwordConfirm = holder.passwordConfirmField.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (password.isEmpty()) {
            holder.passwordFieldLayout.setError(getString(R.string.error_field_required));
            focusView = holder.passwordField;
            cancel = true;
        } else if (!(passwordValidator.isValid(password))) {
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
        if (TextUtils.isEmpty(email)) {
            holder.emailFieldLayout.setError(getString(R.string.error_field_required));
            focusView = holder.emailField;
            cancel = true;
        } else if (!emailValidator.isValid(email)) {
            holder.emailFieldLayout.setError(getString(R.string.error_invalid_email));
            focusView = holder.emailField;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            holder.showProgressAnimation.showProgress(true);
            UserSignupTask signupTask = new UserSignupTask(getContext(), new SignupTaskListener());
            signupTask.execute(email, password);
        }
    }

    public static class ViewHolder extends BaseFragment.ViewHolder {
        public final ProgressBar progressBar;
        public final ScrollView scrollView;
        public final AutoCompleteTextView emailField;
        public final TextInputLayout emailFieldLayout;
        public final EditText passwordField;
        public final TextInputLayout passwordFieldLayout;
        public final EditText passwordConfirmField;
        public final TextInputLayout passwordConfirmFieldLayout;
        public final Button signupButton;
        public final TextView generalErrorTextView;
        private ShowProgressAnimation showProgressAnimation;

        public ViewHolder(View v) {
            progressBar = (ProgressBar) v.findViewById(R.id.signup_progress);
            scrollView = (ScrollView) v.findViewById(R.id.signup_scroll_view);
            emailField = (AutoCompleteTextView) v.findViewById(R.id.signup_email_text_input);
            emailFieldLayout = (TextInputLayout) v.findViewById(R.id.signup_email_text_input_layout);
            passwordField = (EditText) v.findViewById(R.id.signup_password_text_input);
            passwordFieldLayout = (TextInputLayout) v.findViewById(R.id.signup_password_text_input_layout);
            passwordConfirmField = (EditText) v.findViewById(R.id.signup_password_confirm_text_input);
            passwordConfirmFieldLayout = (TextInputLayout) v.findViewById(R.id.signup_password_confirm_text_input_layout);
            signupButton = (Button) v.findViewById(R.id.signup_button);
            generalErrorTextView = find(v, R.id.general_error_text_view);
        }
    }

    private class SignupTaskListener implements TaskListener<String[]> {
        @Override
        public void onTaskSuccess(String[] credentials) {
            String email = credentials[0];
            String password = credentials[1];

            Intent data = new Intent();
            data.putExtra(INTENT_KEY_EMAIL, email);
            data.putExtra(INTENT_KEY_PASSWORD, password);
            getTargetFragment().onActivityResult(getTargetRequestCode(), ACCOUNT_CREATED, data);

            FragmentManager fm = getActivity().getSupportFragmentManager();
            fm.popBackStack();
        }

        @Override
        public void onTaskError(Exception e) {
            handleGeneralErrors(e);
            if (e instanceof DuplicatedEmailException) {
                holder.emailFieldLayout.setError(getString(R.string.error_duplicated_email));
                holder.emailField.requestFocus();
            } else {
                holder.generalErrorTextView.setText(R.string.error_signup_general);
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
}
