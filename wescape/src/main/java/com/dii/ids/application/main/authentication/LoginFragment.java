package com.dii.ids.application.main.authentication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dii.ids.application.R;
import com.dii.ids.application.main.authentication.tasks.UserLoginTask;
import com.dii.ids.application.main.authentication.utils.EmailAutocompleter;
import com.dii.ids.application.main.navigation.NavigationActivity;
import com.dii.ids.application.validators.EmailValidator;
import com.dii.ids.application.validators.PasswordValidator;

public class LoginFragment extends Fragment {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private final String LOG_TAG = AuthenticationActivity.class.getSimpleName();
    private ViewHolder holder;
    private EmailAutocompleter emailAutocompleter;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        holder = new ViewHolder(view);
        emailAutocompleter = new EmailAutocompleter(this, holder.emailField);

        // Si nasconde la action bar
        ((AuthenticationActivity) getActivity()).hideActionBar();

        emailAutocompleter.populateAutoComplete();

        holder.passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
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
                resetPasswdClicked(v);
            }
        });


        //@TODO: forza il passaggio alla home finche non viene implementato il login
        holder.homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NavigationActivity.class);
                startActivity(intent);
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
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password)
                    .inject(this, holder);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            holder.scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
            holder.scrollView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    holder.scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            holder.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            holder.progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    holder.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            holder.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            holder.scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        emailAutocompleter.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    /**
     * Sostituisce al fragment attuale quello di registrazione
     *
     * @param v Oggetto TextView clickato
     */
    private void openSignupFragment(View v) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new SignupFragment();
        fragmentTransaction.replace(R.id.authentication_content_pane, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Listener del TextView per il reset della password
     *
     * @param v Oggetto TextView clickato
     */
    public void resetPasswdClicked(View v) {
        Log.i(LOG_TAG, "ResetPasswdText clicked!");
    }

    public void wipeAsyncTask() {
        mAuthTask = null;
        showProgress(false);
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
        public final Button homeButton;
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

            homeButton = (Button) view.findViewById(R.id.login_home_button);
        }
    }
}
