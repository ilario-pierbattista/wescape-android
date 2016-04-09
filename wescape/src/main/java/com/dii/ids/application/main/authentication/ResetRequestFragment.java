package com.dii.ids.application.main.authentication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
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
import com.dii.ids.application.interfaces.AsyncTaskCallbacksInterface;
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.main.authentication.tasks.ResetRequestTask;
import com.dii.ids.application.main.authentication.utils.EmailAutocompleter;
import com.dii.ids.application.validators.EmailValidator;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment must implement the
 * {@link ResetRequestFragment.OnFragmentInteractionListener} interface to handle interaction
 * events. Use the {@link ResetRequestFragment#newInstance} factory method to create an instance of
 * this fragment.
 */
public class ResetRequestFragment extends BaseFragment
        implements AsyncTaskCallbacksInterface<ResetRequestTask> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "email";

    // TODO: Rename and change types of parameters
    private String email;
    private ViewHolder holder;
    private EmailAutocompleter autocompleter;
    private ResetRequestTask asyncTask;
    private ShowProgressAnimation showProgressAnimation;

    private OnFragmentInteractionListener mListener;

    public ResetRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param email Parameter 1.
     * @return A new instance of fragment ResetRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResetRequestFragment newInstance(String email) {
        ResetRequestFragment fragment = new ResetRequestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, email);
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
        final View view = inflater.inflate(R.layout.authentication_request_reset_fragment, container, false);
        ((AuthenticationActivity) getActivity())
                .showActionBar(getString(R.string.authentication_title_bar));
        holder = new ViewHolder(view);
        showProgressAnimation = new ShowProgressAnimation(holder.scrollView, holder.progressBar, getShortAnimTime());
        autocompleter = new EmailAutocompleter(this, holder.emailField);
        autocompleter.populateAutoComplete();

        if (email != null) {
            holder.emailField.setText(email);
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

        return view;
    }

    /**
     * Attempts to sign in or register the account specified by the login form. If there are form
     * errors (invalid email, missing fields, etc.), the errors are presented and no actual login
     * attempt is made.
     */
    private void requestReset() {
        if (asyncTask != null) {
            return;
        }

        EmailValidator emailValidator = new EmailValidator();

        // Reset errors.
        holder.emailFieldLayout.setError(null);

        // Store values at the time of the login attempt.
        String email = holder.emailField.getText().toString();

        boolean cancel = false;
        View focusView = null;

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
            asyncTask = new ResetRequestTask(email)
                    .inject(this, holder);
            asyncTask.execute((Void) null);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onTaskSuccess(ResetRequestTask asyncTask) {
        wipeAsyncTask();
        ResetPasswordFragment fragment;

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragment = ResetPasswordFragment.newInstance(getValidEmailAddress());

        transaction.replace(R.id.authentication_content_pane, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onTaskError(ResetRequestTask asyncTask) {
        wipeAsyncTask();
    }

    @Override
    public void onTaskCancelled(ResetRequestTask asyncTask) {
        wipeAsyncTask();
    }

    private void wipeAsyncTask() {
        showProgressAnimation.showProgress(false);
        asyncTask = null;
    }

    @Nullable
    private String getValidEmailAddress() {
        String email = holder.emailField.getText().toString();
        if (new EmailValidator().isValid(email)) {
            return email;
        } else {
            return null;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        private final ProgressBar progressBar;
        private final ScrollView scrollView;
        private final AutoCompleteTextView emailField;
        private final TextInputLayout emailFieldLayout;
        private final Button resetRequestButton;

        public ViewHolder(View v) {
            progressBar = (ProgressBar) v.findViewById(R.id.reset_request_progress);
            scrollView = (ScrollView) v.findViewById(R.id.reset_request_scroll_view);
            emailField = (AutoCompleteTextView) v.findViewById(R.id.reset_request_email_text_input);
            emailFieldLayout = (TextInputLayout) v.findViewById(R.id.reset_request_email_text_input_layout);
            resetRequestButton = (Button) v.findViewById(R.id.request_reset_button);
        }
    }
}
