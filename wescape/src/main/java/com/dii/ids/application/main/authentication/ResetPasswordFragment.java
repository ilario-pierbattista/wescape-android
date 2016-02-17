package com.dii.ids.application.main.authentication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dii.ids.application.R;
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.interfaces.AsyncTaskCallbacksInterface;
import com.dii.ids.application.main.authentication.tasks.PasswordResetTask;
import com.dii.ids.application.animations.ShowProgressAnimation;
import com.dii.ids.application.validators.PasswordValidator;
import com.dii.ids.application.validators.SecretCodeValidator;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment must implement the
 * {@link ResetPasswordFragment.OnFragmentInteractionListener} interface to handle interaction
 * events. Use the {@link ResetPasswordFragment#newInstance} factory method to create an instance of
 * this fragment.
 */
public class ResetPasswordFragment extends BaseFragment implements AsyncTaskCallbacksInterface<PasswordResetTask> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EMAIL = "email";

    // TODO: Rename and change types of parameters
    private String email;
    private ViewHolder holder;
    private ShowProgressAnimation animation;
    private PasswordResetTask asyncTask;

    private OnFragmentInteractionListener mListener;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param email Parameter 1.
     * @return A new instance of fragment ResetPasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResetPasswordFragment newInstance(String email) {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        animation = new ShowProgressAnimation(this, holder.scrollView, holder.progress);

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
        if (asyncTask != null) {
            return;
        }

        PasswordValidator passwordValidator = new PasswordValidator();
        SecretCodeValidator secretCodeValidator = new SecretCodeValidator();

        // Reset errors.
        holder.secretCodeFieldLayout.setError(null);
        holder.passwordFieldLayout.setError(null);
        holder.passwordConfirmFieldLayout.setError(null);

        // Store values at the time of the login attempt.
        String secretCode = holder.secretCodeField.getText().toString();
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
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            animation.showProgress(true);
            asyncTask = new PasswordResetTask(this.email, secretCode, password, passwordConfirm)
                    .inject(this);
            asyncTask.execute((Void) null);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onTaskSuccess(PasswordResetTask asyncTask) {
        wipeAsyncTask();
        // Torna indietro al form di login
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack();
        fm.popBackStack();
    }

    @Override
    public void onTaskError(PasswordResetTask asyncTask) {
        wipeAsyncTask();
    }

    @Override
    public void onTaskCancelled(PasswordResetTask asyncTask) {
        wipeAsyncTask();
    }

    private void wipeAsyncTask() {
        asyncTask = null;
        animation.showProgress(false);
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

    public class ViewHolder {
        public final ProgressBar progress;
        public final ScrollView scrollView;
        public final EditText secretCodeField;
        public final TextInputLayout secretCodeFieldLayout;
        public final EditText passwordField;
        public final TextInputLayout passwordFieldLayout;
        public final EditText passwordConfirmField;
        public final TextInputLayout passwordConfirmFieldLayout;
        public final Button resetPasswordButton;

        public ViewHolder(View v) {
            progress = (ProgressBar) v.findViewById(R.id.reset_passwd_progress);
            scrollView = (ScrollView) v.findViewById(R.id.reset_passwd_scroll_view);
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
