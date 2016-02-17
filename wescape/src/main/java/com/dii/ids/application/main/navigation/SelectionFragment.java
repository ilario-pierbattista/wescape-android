package com.dii.ids.application.main.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dii.ids.application.R;
import com.dii.ids.application.main.BaseFragment;

public class SelectionFragment extends BaseFragment {
    private static final String LOG_TAG = SelectionFragment.class.getSimpleName();
    private static final int DIALOG_REQUEST_CODE = 100;
    private NavigationActivity mActivity;
    private ViewHolder holder;
    private StaticListAdapter staticListAdapter;

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param selection Parameter 1.
     * @return A new instance of fragment ResetPasswordFragment.
     */
    public static SelectionFragment newInstance(String selection) {
        SelectionFragment fragment = new SelectionFragment();
        Bundle args = new Bundle();
        args.putString(TOOLBAR_TITLE, selection);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(getActivity(), data.getExtras().getString(QRDialogFragment.INTENT_QR_DATA_TAG), Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.navigation_selection_fragment, container, false);
        holder = new ViewHolder(view);

        // Setup toolbar
        mActivity = (NavigationActivity) getActivity();
        mActivity.setSupportActionBar((Toolbar) view.findViewById(R.id.navigation_standard_toolbar));
        assert mActivity.getSupportActionBar() != null;
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        holder.toolbarTitle.setText(getArguments().getString(TOOLBAR_TITLE));

        // Setup static actions table
        String[] staticActionsText = {
                getString(R.string.navigation_select_from_map),
                getString(R.string.navigation_select_from_qr)
        };

        int[] staticActionImages = {
                R.drawable.ic_map,
                R.drawable.ic_camera_alt
        };
        staticListAdapter = new StaticListAdapter(getContext(), staticActionsText, staticActionImages);
        holder.staticListview.setAdapter(staticListAdapter);
        holder.staticListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int posizione = (int) staticListAdapter.getItem(position);
                if (posizione == 0) {
                    openSelectionFromMap(view);
                } else {
                    qrScannerListener(view);
                }
            }
        });
        return view;
    }

    /**
     * Listener per aprire la vista per la selezione della posizione su mappa
     *
     * @param v Oggetto view
     */
    private void openSelectionFromMap(View v) {
        SelectionFromMapFragment fragment;

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragment = SelectionFromMapFragment.newInstance(getArguments().getString(TOOLBAR_TITLE));
        transaction.replace(R.id.navigation_content_pane, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Setto il listener per il bottone per la scansione. Creo l'oggetto IntentIntegrator a partire
     * dal fragment. In questo modo posso riprendere le informazioni direttamente dal fragment senza
     * passare dall'activity. Riprendo le informazioni tramite il metodo onActivityResult.
     *
     * @param v Oggetto View
     */
    private void qrScannerListener(View v) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(QRDialogFragment.FRAGMENT_TAG);
        if (fragment != null) {
            fm.beginTransaction().remove(fragment).commit();
        }
        QRDialogFragment dialogFragment = new QRDialogFragment();
        dialogFragment.setTargetFragment(this, DIALOG_REQUEST_CODE);
        dialogFragment.show(fm, QRDialogFragment.FRAGMENT_TAG);
    }

    /**
     * UI elements wrapper class
     */
    public static class ViewHolder {
        public final Toolbar toolbar;
        public final TextView toolbarTitle;
        public final ListView staticListview;

        public ViewHolder(View view) {
            toolbar = (Toolbar) view.findViewById(R.id.navigation_standard_toolbar);
            toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            staticListview = (ListView) view.findViewById((R.id.selection_static_listview));

        }
    }

}
