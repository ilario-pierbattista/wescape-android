package com.dii.ids.application.main.authentication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.dii.ids.application.R;
import com.dii.ids.application.main.authentication.utils.EmailAutocompleter;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment must implement the
 * {@link RequestResetFragment.OnFragmentInteractionListener} interface to handle interaction
 * events. Use the {@link RequestResetFragment#newInstance} factory method to create an instance of
 * this fragment.
 */
public class RequestResetFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "email";

    // TODO: Rename and change types of parameters
    private String email;
    private ViewHolder holder;
    private EmailAutocompleter autocompleter;

    private OnFragmentInteractionListener mListener;

    public RequestResetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param email Parameter 1.
     * @return A new instance of fragment RequestResetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestResetFragment newInstance(String email) {
        RequestResetFragment fragment = new RequestResetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, email);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_request_reset, container, false);
        ((AuthenticationActivity) getActivity())
                .showActionBar(getString(R.string.authentication_title_bar));
        holder = new ViewHolder(view);
        autocompleter = new EmailAutocompleter(this, holder.emailField);
        autocompleter.populateAutoComplete();

        if(email != null) {
            holder.emailField.setText(email);
        }

        return view;
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
