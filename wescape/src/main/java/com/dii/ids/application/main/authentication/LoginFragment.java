package com.dii.ids.application.main.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dii.ids.application.R;
import com.dii.ids.application.animations.ShowProgressAnimation;
import com.dii.ids.application.api.ApiBuilder;
import com.dii.ids.application.api.AuthenticationManager;
import com.dii.ids.application.api.form.PasswordOAuth2Form;
import com.dii.ids.application.api.response.AccessTokenBundle;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.main.authentication.tasks.UpdateAccessTokenTask;
import com.dii.ids.application.main.authentication.tasks.UserLoginTask;
import com.dii.ids.application.main.authentication.utils.EmailAutocompleter;
import com.dii.ids.application.main.navigation.NavigationActivity;
import com.dii.ids.application.main.settings.SettingsActivity;
import com.dii.ids.application.validators.EmailValidator;
import com.dii.ids.application.validators.PasswordValidator;

public class LoginFragment extends BaseFragment {
    private final String TAG = LoginFragment.class.getName();
    private final int CLICK_TO_OPEN = 8,
            CLICK_TO_FEEDBACK = 4;
    public ViewHolder holder;

    private UserLoginTask loginTask = null;
    private UpdateAccessTokenTask updateTokenTask;
    private EmailAutocompleter emailAutocompleter;
    private Toast hiddenMenuFeedbackToast;
    private ApiBuilder apiBuilder;
    private AuthenticationManager authenticationManager;
    private int logoClickTimes;

    private TaskListener<AccessTokenBundle> loginTaskListener =
            new TaskListener<AccessTokenBundle>() {
                @Override
                public void onTaskSuccess(AccessTokenBundle accessTokenBundle) {
                    Intent intent = new Intent(getActivity(), NavigationActivity.class);
                    startActivity(intent);
                    authenticationManager.saveAccessToken(accessTokenBundle);
                }

                @Override
                public void onTaskError() {
                    holder.passwordField.setError(getString(R.string.error_incorrect_password));
                    holder.passwordField.requestFocus();
                }

                @Override
                public void onTaskComplete() {
                    wipeAsyncTask();
                }

                @Override
                public void onTaskCancelled() {
                    wipeAsyncTask();
                }
            };

    private TaskListener<AccessTokenBundle> updateTaskListener =
            new TaskListener<AccessTokenBundle>() {
                @Override
                public void onTaskSuccess(AccessTokenBundle accessTokenBundle) {
                    Intent intent = new Intent(getActivity(), NavigationActivity.class);
                    startActivity(intent);
                    authenticationManager.saveAccessToken(accessTokenBundle);
                }

                @Override
                public void onTaskError() {

                }

                @Override
                public void onTaskComplete() {
                    holder.showProgressAnimation.showProgress(false);
                }

                @Override
                public void onTaskCancelled() {
                    holder.showProgressAnimation.showProgress(false);
                }
            };

    public LoginFragment() {
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
        final View view = inflater.inflate(R.layout.authentication_login_fragment, container, false);
        holder = new ViewHolder(view);
        logoClickTimes = 0;
        apiBuilder = new ApiBuilder(getContext());
        authenticationManager = new AuthenticationManager(getContext());
        emailAutocompleter = new EmailAutocompleter(this, holder.emailField);
        holder.showProgressAnimation = new ShowProgressAnimation(holder.scrollView, holder.progressBar,
                getShortAnimTime());

        // Si nasconde la action bar
        ((AuthenticationActivity) getActivity()).hideActionBar();

        emailAutocompleter.populateAutoComplete();

        holder.passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    hideKeyboard(view);
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        holder.logoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHiddenMenu();
            }
        });

        holder.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
                attemptLogin();
            }
        });

        holder.homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NavigationActivity.class);
                startActivity(intent);
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

        refreshAccessToken();

        return view;
    }

    /**
     * Attempts to sign in or register the account specified by the login form. If there are form
     * errors (invalid email, missing fields, etc.), the errors are presented and no actual login
     * attempt is made.
     */
    private void attemptLogin() {
        if (loginTask != null) {
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
        if (TextUtils.isEmpty(password)) {
            holder.passwordFieldLayout.setError(getString(R.string.error_field_required));
            focusView = holder.passwordField;
            cancel = true;
        } else if (!(new PasswordValidator().isValid(password))) {
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
            holder.showProgressAnimation.showProgress(true);

            PasswordOAuth2Form passwordOAuth2Form = new PasswordOAuth2Form();
            passwordOAuth2Form.setClient_id(authenticationManager.getClientId());
            passwordOAuth2Form.setClient_secret(authenticationManager.getClientSecret());
            passwordOAuth2Form.setUsername("admin");
            passwordOAuth2Form.setPassword("admin");

            loginTask = new UserLoginTask(apiBuilder, loginTaskListener, passwordOAuth2Form);
            loginTask.execute();
        }
    }

    /**
     * Sostituisce al fragment attuale quello di registrazione
     *
     * @param v Oggetto TextView clickato
     */
    private void openSignupFragment(View v) {
        SignupFragment signupFragment;

        hideKeyboard(v);
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
        ResetRequestFragment resetFragment;

        hideKeyboard(v);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        resetFragment = ResetRequestFragment.newInstance(getValidEmailFromView());
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

    /**
     * Apre l'activity delle preferenze
     */
    private void openHiddenMenu() {
        logoClickTimes++;

        if (logoClickTimes >= CLICK_TO_OPEN) {
            Intent intent = new Intent(this.getContext(), SettingsActivity.class);
            startActivity(intent);
        } else if (logoClickTimes >= CLICK_TO_FEEDBACK) {
            if (hiddenMenuFeedbackToast == null) {
                hiddenMenuFeedbackToast = Toast.makeText(this.getContext(),
                        getString(R.string.toast_hidden_menu_feedback),
                        Toast.LENGTH_SHORT);
            }
            hiddenMenuFeedbackToast.show();
        }
    }

    /**
     * Aggiorna l'access token
     */
    private void refreshAccessToken() {
        AccessTokenBundle accessTokenBundle = authenticationManager.retrieveAccessToken();
        if (accessTokenBundle != null) {
            updateTokenTask = new UpdateAccessTokenTask(
                    updateTaskListener,
                    apiBuilder,
                    authenticationManager);
            updateTokenTask.execute();
            holder.showProgressAnimation
                    .showProgress(true);
        }
    }

    private void wipeAsyncTask() {
        loginTask = null;
        holder.showProgressAnimation.showProgress(false);
    }

    /**
     * Classe wrapper degli elementi della vista
     */
    public static class ViewHolder extends BaseFragment.ViewHolder {
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
        public final ImageView logoImageView;
        public ShowProgressAnimation showProgressAnimation;

        public ViewHolder(View view) {
            emailField = find(view, R.id.login_email_text_input);
            emailFieldLayout = find(view, R.id.login_email_text_input_layout);
            passwordField = find(view, R.id.login_password_text_input);
            passwordFieldLayout = find(view, R.id.login_password_text_input_layout);
            loginButton = find(view, R.id.login_signin_button);
            progressBar = find(view, R.id.login_progress);
            scrollView = find(view, R.id.login_scroll_view);
            signupTextView = find(view, R.id.sign_up_text);
            resetPasswdTextView = find(view, R.id.reset_passwd_text);
            homeButton = find(view, R.id.login_home_button);
            logoImageView = find(view, R.id.wescape_logo_image_view);
        }
    }
}
