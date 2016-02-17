package com.dii.ids.application.main.authentication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
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
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.interfaces.AsyncTaskCallbacksInterface;
import com.dii.ids.application.main.authentication.tasks.UserSignupTask;
import com.dii.ids.application.main.authentication.utils.EmailAutocompleter;
import com.dii.ids.application.animations.ShowProgressAnimation;
import com.dii.ids.application.validators.EmailValidator;
import com.dii.ids.application.validators.PasswordValidator;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment must implement the
 * {@link SignupFragment.OnFragmentInteractionListener} interface to handle interaction events. Use
 * the {@link SignupFragment#newInstance} factory method to create an instance of this fragment.
 */
public class SignupFragment extends BaseFragment implements AsyncTaskCallbacksInterface<UserSignupTask> {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "email";

    private String email;

    private UserSignupTask signupTask;
    private ViewHolder holder;
    private EmailAutocompleter emailAutocompleter;
    private ShowProgressAnimation showProgressAnimation;

    private OnFragmentInteractionListener mListener;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param email Parameter 1.
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance(String email) {
        SignupFragment fragment = new SignupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, email);
        fragment.setArguments(args);
        return fragment;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString(ARG_PARAM1);
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
        showProgressAnimation = new ShowProgressAnimation(this, holder.scrollView, holder.progressBar);

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Attempts to sign in or register the account specified by the login form. If there are form
     * errors (invalid email, missing fields, etc.), the errors are presented and no actual login
     * attempt is made.
     */
    private void attempSignup() {
        if (signupTask != null) {
            return;
        }

        PasswordValidator passwordValidator = new PasswordValidator();
        EmailValidator emailValidator = new EmailValidator();

        // Reset errors.
        holder.emailFieldLayout.setError(null);
        holder.passwordFieldLayout.setError(null);
        holder.passwordConfirmFieldLayout.setError(null);

        // Store values at the time of the login attempt.
        String email = holder.emailField.getText().toString();
        String password = holder.passwordField.getText().toString();
        String passwordConfirm = holder.passwordConfirmField.getText().toString();

        boolean cancel = false;
        View focusView = null;

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
            showProgressAnimation.showProgress(true);
            signupTask = new UserSignupTask(email, password)
                    .inject(this);
            signupTask.execute((Void) null);
        }
    }

    @Override
    public void onTaskSuccess(UserSignupTask asyncTask) {
        wipeAsyncTask();
    }

    public void wipeAsyncTask() {
        showProgressAnimation.showProgress(false);
        signupTask = null;
    }

    @Override
    public void onTaskError(UserSignupTask asyncTask) {
        wipeAsyncTask();
        holder.passwordField.setError(getString(R.string.error_incorrect_password));
        holder.passwordField.requestFocus();
    }

    @Override
    public void onTaskCancelled(UserSignupTask asyncTask) {
        wipeAsyncTask();
    }

    /**
     * This interface must be implemented by activities that contain this fragment to allow an
     * interaction in this fragment to be communicated to the activity and potentially other
     * fragments contained in that activity.
     * <p/>
     * See the Android Training lesson <a href= "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static class ViewHolder {
        public final ProgressBar progressBar;
        public final ScrollView scrollView;
        public final AutoCompleteTextView emailField;
        public final TextInputLayout emailFieldLayout;
        public final EditText passwordField;
        public final TextInputLayout passwordFieldLayout;
        public final EditText passwordConfirmField;
        public final TextInputLayout passwordConfirmFieldLayout;
        public final Button signupButton;

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
        }
    }
}
