package com.dii.ids.application.main.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dii.ids.application.R;
import com.dii.ids.application.animations.ShowProgressAnimation;
import com.dii.ids.application.api.auth.exception.LoginException;
import com.dii.ids.application.entity.Edge;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.entity.repository.EdgeRepository;
import com.dii.ids.application.entity.repository.NodeRepository;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.main.authentication.tasks.AutomaticLoginTask;
import com.dii.ids.application.main.authentication.tasks.LoadAssetsTask;
import com.dii.ids.application.main.authentication.tasks.UserLoginTask;
import com.dii.ids.application.main.authentication.utils.EmailAutocompleter;
import com.dii.ids.application.main.navigation.NavigationActivity;
import com.dii.ids.application.main.settings.SettingsActivity;
import com.dii.ids.application.validators.EmailValidator;
import com.dii.ids.application.validators.PasswordValidator;

import java.io.InputStream;
import java.util.List;

public class LoginFragment extends BaseFragment {
    public static final String TAG = LoginFragment.class.getName();
    public static final int SIGNUP_CREDENTIAL_REQUEST = 300;
    private static final int CLICK_TO_OPEN = 8, CLICK_TO_FEEDBACK = 4;
    private static final String AUTOMATIC_LOGIN = "automatic_login";

    public ViewHolder holder;
    private EmailAutocompleter emailAutocompleter;
    private Toast hiddenMenuFeedbackToast;
    private int logoClickTimes;
    private boolean doAutomaticLogin = true;

    public static LoginFragment newInstance(boolean doAutomaticLogin) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putBoolean(AUTOMATIC_LOGIN, doAutomaticLogin);
        fragment.setArguments(args);
        return fragment;
    }

    public LoginFragment enableAutomaticLogin(boolean enabled) {
        if (getArguments() == null) {
            setArguments(new Bundle());
        }
        getArguments().putBoolean(AUTOMATIC_LOGIN, enabled);
        return this;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGNUP_CREDENTIAL_REQUEST) {
            if (resultCode == SignupFragment.ACCOUNT_CREATED) {
                String email = data.getStringExtra(SignupFragment.INTENT_KEY_EMAIL);
                String passwod = data.getStringExtra(SignupFragment.INTENT_KEY_PASSWORD);
                triggerLoginTask(email, passwod);
                doAutomaticLogin = false;
            }
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

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailAutocompleter = new EmailAutocompleter(this, holder.emailField);
        holder.showProgressAnimation = new ShowProgressAnimation(holder.scrollView, holder.progressBar,
                getShortAnimTime());

        // Si nasconde la action bar
        ((AuthenticationActivity) getActivity()).hideActionBar();

        emailAutocompleter.populateAutoComplete();

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

        holder.useOfflineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Node> savedNodes = NodeRepository.findAll();
                List<Edge> savedEdges = EdgeRepository.findAll();
                if (savedEdges.size() == 0 && savedNodes.size() == 0) {
                    // @TODO aggiungere staticamente i dati
                    //Toast.makeText(getContext(), getString(R.string.error_no_data_cached), Toast.LENGTH_LONG).show();
                    holder.showProgressAnimation.showProgress(true);
                    InputStream nodeStream = getResources().openRawResource(R.raw.nodes);
                    InputStream edgeStream = getResources().openRawResource(R.raw.edges);
                    LoadAssetsTask task = new LoadAssetsTask(new LoadOfflineAssetsTaskListener(), nodeStream, edgeStream);
                    task.execute();
                } else {
                    openNavigationActivity(true);
                }
            }
        });

        if (getArguments() != null) {
            doAutomaticLogin = getArguments().getBoolean(AUTOMATIC_LOGIN);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Tenta di effettuare il login automatico
        if (doAutomaticLogin) {
            automaticLogin();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        logoClickTimes = 0;
    }

    /**
     * Attempts to sign in or register the account specified by the login form. If there are form
     * errors (invalid email, missing fields, etc.), the errors are presented and no actual login
     * attempt is made.
     */
    private void attemptLogin() {
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
            triggerLoginTask(email, password);
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
        signupFragment.setTargetFragment(this, SIGNUP_CREDENTIAL_REQUEST);

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
     * Login automatico in caso di presenza di authentication token valido
     */
    private void automaticLogin() {
        holder.showProgressAnimation.showProgress(true);
        AutomaticLoginTask task = new AutomaticLoginTask(getContext(), new AutomaticLoginTaskListener());
        task.execute();
    }

    /**
     * @param email
     * @param password
     */
    private void triggerLoginTask(String email, String password) {
        holder.showProgressAnimation.showProgress(true);

        UserLoginTask loginTask = new UserLoginTask(getContext(), new LoginTaskListener());
        loginTask.execute(email, password);
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

    private void openNavigationActivity(boolean offline) {
        Intent intent = new Intent(getActivity(), NavigationActivity.class);
        intent.putExtra(NavigationActivity.OFFLINE_USAGE, offline);
        startActivity(intent);

        if (!offline) {
            getActivity().finish();
        }
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
        public final Button useOfflineButton;
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
            useOfflineButton = find(view, R.id.use_offline_button);
            progressBar = find(view, R.id.login_progress);
            scrollView = find(view, R.id.login_scroll_view);
            signupTextView = find(view, R.id.sign_up_text);
            resetPasswdTextView = find(view, R.id.reset_passwd_text);
            logoImageView = find(view, R.id.wescape_logo_image_view);
        }
    }

    private class LoginTaskListener implements TaskListener<Void> {
        private final String TAG = LoginTaskListener.class.getName();

        @Override
        public void onTaskSuccess(Void v) {
            openNavigationActivity(false);
        }

        @Override
        public void onTaskError(Exception e) {
            Log.e(TAG, "Errore", e);
            handleGeneralErrors(e);
            holder.showProgressAnimation.showProgress(false);

            if (e instanceof LoginException) {
                holder.passwordFieldLayout.setError(getString(R.string.error_incorrect_password));
                holder.passwordField.requestFocus();
            }
        }

        @Override
        public void onTaskComplete() {
        }

        @Override
        public void onTaskCancelled() {
            holder.showProgressAnimation.showProgress(false);
        }
    }

    private class AutomaticLoginTaskListener implements TaskListener<Void> {
        @Override
        public void onTaskSuccess(Void aVoid) {
            openNavigationActivity(false);
        }

        @Override
        public void onTaskError(Exception e) {
            holder.showProgressAnimation.showProgress(false);
            handleGeneralErrors(e);
        }

        @Override
        public void onTaskComplete() {
            doAutomaticLogin = false;
        }

        @Override
        public void onTaskCancelled() {
            holder.showProgressAnimation.showProgress(false);
        }
    }

    private class LoadOfflineAssetsTaskListener implements TaskListener<Void> {
        @Override
        public void onTaskSuccess(Void aVoid) {
            holder.showProgressAnimation.showProgress(false);
            openNavigationActivity(true);
        }

        @Override
        public void onTaskError(Exception e) {
            holder.showProgressAnimation.showProgress(false);
            handleGeneralErrors(e);
        }

        @Override
        public void onTaskComplete() {

        }

        @Override
        public void onTaskCancelled() {
            holder.showProgressAnimation.showProgress(false);
        }
    }
}
