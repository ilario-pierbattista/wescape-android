package com.dii.ids.application.main.authentication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dii.ids.application.R;
import com.dii.ids.application.main.authentication.interfaces.AsyncTaskCallbacksInterface;
import com.dii.ids.application.main.authentication.tasks.UserLoginTask;
import com.dii.ids.application.main.authentication.utils.EmailAutocompleter;
import com.dii.ids.application.main.authentication.utils.ShowProgressAnimation;
import com.dii.ids.application.validators.EmailValidator;
import com.dii.ids.application.validators.PasswordValidator;

public class LoginFragment extends Fragment implements AsyncTaskCallbacksInterface<UserLoginTask> {
    private final String LOG_TAG = AuthenticationActivity.class.getSimpleName();
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private ViewHolder holder;
    private EmailAutocompleter emailAutocompleter;
    private ShowProgressAnimation showProgressAnimation;

    public LoginFragment() {
        // Required empty public constructor
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        holder = new ViewHolder(view);
        emailAutocompleter = new EmailAutocompleter(this, holder.emailField);
        showProgressAnimation = new ShowProgressAnimation(this, holder.scrollView, holder.progressBar);

        // Si nasconde la action bar
        ((AuthenticationActivity) getActivity()).hideActionBar();

        emailAutocompleter.populateAutoComplete();

        holder.passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    // Nasconde la tastiera
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        holder.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        holder.signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignupFragment(v);
            }
        });

        holder.resetPasswdTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openResetRequestFragment(v);
            }
        });

        return view;
    }

    /**
     * Attempts to sign in or register the account specified by the login form. If there are form
     * errors (invalid email, missing fields, etc.), the errors are presented and no actual login
     * attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        holder.emailFieldLayout.setError(null);
        holder.passwordFieldLayout.setError(null);

        // Store values at the time of the login attempt.
        String email = holder.emailField.getText().toString();
        String password = holder.passwordField.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!(new PasswordValidator().isValid(password))) {
            holder.passwordFieldLayout.setError(getString(R.string.error_invalid_password));
            focusView = holder.passwordField;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            holder.emailFieldLayout.setError(getString(R.string.error_field_required));
            focusView = holder.emailField;
            cancel = true;
        } else if (!(new EmailValidator().isValid(email))) {
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
            showProgressAnimation.showProgress(true);
            mAuthTask = new UserLoginTask(email, password)
                    .inject(this);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Sostituisce al fragment attuale quello di registrazione
     *
     * @param v Oggetto TextView clickato
     */
    private void openSignupFragment(View v) {
        SignupFragment signupFragment;

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        signupFragment = SignupFragment.newInstance(getValidEmailFromView());

        fragmentTransaction.replace(R.id.authentication_content_pane, signupFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Sostituisce il fragment attuale con quello di richiesta di reset della password
     *
     * @param v Oggetto TextView clickato
     */
    private void openResetRequestFragment(View v) {
        RequestResetFragment resetFragment;
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        resetFragment = RequestResetFragment.newInstance(getValidEmailFromView());
        fragmentTransaction.replace(R.id.authentication_content_pane, resetFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Estrae dalla vista un eventuale indirizzo email valido immesso
     *
     * @return Stringa con l'indirizzo, se presente e valido, null altrimenti
     */
    @Nullable
    private String getValidEmailFromView() {
        String email = holder.emailField.getText().toString();
        if (new EmailValidator().isValid(email)) {
            return email;
        } else {
            return null;
        }
    }

    @Override
    public void onTaskSuccess(UserLoginTask asyncTask) {
        wipeAsyncTask();
        this.getActivity().finish();
    }

    private void wipeAsyncTask() {
        mAuthTask = null;
        showProgressAnimation.showProgress(false);
    }

    @Override
    public void onTaskError(UserLoginTask userLoginTask) {
        wipeAsyncTask();
        holder.passwordField.setError(getString(R.string.error_incorrect_password));
        holder.passwordField.requestFocus();
    }

    @Override
    public void onTaskCancelled(UserLoginTask userLoginTask) {
        wipeAsyncTask();
    }

    /**
     * Classe wrapper degli elementi della vista
     */
    public static class ViewHolder {
        public final AutoCompleteTextView emailField;
        public final TextInputLayout emailFieldLayout;
        public final TextInputLayout passwordFieldLayout;
        public final EditText passwordField;
        public final Button loginButton;
        public final ProgressBar progressBar;
        public final ScrollView scrollView;
        public final TextView signupTextView;
        public final TextView resetPasswdTextView;

        public ViewHolder(View view) {
            emailField = (AutoCompleteTextView) view.findViewById(R.id.login_email_text_input);
            emailFieldLayout = (TextInputLayout) view.findViewById(R.id.login_email_text_input_layout);
            passwordField = (EditText) view.findViewById(R.id.login_password_text_input);
            passwordFieldLayout = (TextInputLayout) view.findViewById(R.id.login_password_text_input_layout);
            loginButton = (Button) view.findViewById(R.id.login_signin_button);
            progressBar = (ProgressBar) view.findViewById(R.id.login_progress);
            scrollView = (ScrollView) view.findViewById(R.id.login_scroll_view);
            signupTextView = (TextView) view.findViewById(R.id.sign_up_text);
            resetPasswdTextView = (TextView) view.findViewById(R.id.reset_passwd_text);
        }
    }
}
