package com.dii.ids.application.main.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dii.ids.application.R;
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.main.authentication.tasks.UserLoginTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class SelectionFragment extends BaseFragment {

    private NavigationActivity mActivity;
    private ViewHolder holder;
    private StaticListAdapter staticListAdapter;
    private static final String LOG_TAG = SelectionFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_selection, container, false);
        holder = new ViewHolder(view);

        // Setup toolbar
        mActivity = (NavigationActivity) getActivity();
        mActivity.setSupportActionBar((Toolbar) view.findViewById(R.id.navigation_standard_toolbar));
        assert mActivity.getSupportActionBar() != null;
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        holder.toolbarTitle.setText(getArguments().getString(BaseFragment.TOOLBAR_TITLE));

        // Setup static actions table
        String[] staticActionsText = {
                getString(R.string.navigation_select_from_map),
                getString(R.string.navigation_select_from_qr)
        };

        int[] staticActionImages = {
                android.R.drawable.ic_dialog_map,
                R.drawable.ws_camera
        };
        staticListAdapter = new StaticListAdapter(getContext(), staticActionsText, staticActionImages);
        holder.staticListview.setAdapter(staticListAdapter);
        holder.staticListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int posizione = (int) staticListAdapter.getItem(position);
                if(posizione == 0) {
                    // TODO: 16/02/16 Implementare l'activity che deve essere richiamata
                }else {
                    qrScannerListener(view);
                }
            }
        });
        return view;
    }

    /**
     * Setto il listener per il bottone per la scansione. Creo l'oggetto IntentIntegrator a partire
     * dal fragment. In questo modo posso riprendere le informazioni direttamente dal fragment
     * senza passare dall'activity. Riprendo le informazioni tramite il metodo onActivityResult.
     *
     * @param v Oggetto View
     */
    private void qrScannerListener(View v) {
        IntentIntegrator intent = IntentIntegrator.forSupportFragment(this);
        intent.initiateScan();
    }

    /**
     * Metodo che viene richiamato quando si chiude l'intent della fotocamera. Avendo creato
     * l'oggetto IntentResult a partire da un fragment possiamo riprendere le informazioni senza
     * ripassare dalla rispettiva activity.
     *
     * @param requestCode Request code dell'intent
     * @param resultCode Result code dell'intent
     * @param intent Oggetto intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String re = scanResult.getContents();
            if (re != null) {
                Toast.makeText(getActivity(), re, Toast.LENGTH_LONG).show();
            }
        }
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
