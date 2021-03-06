package com.dii.ids.application.main.navigation;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dii.ids.application.R;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.entity.repository.NodeRepository;
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.main.navigation.adapters.NodeAdapter;
import com.dii.ids.application.main.navigation.adapters.StaticListAdapter;

import org.apache.commons.lang3.SerializationUtils;

import java.util.List;

import static android.Manifest.permission.CAMERA;

public class SelectionFragment extends BaseFragment {
    public static final int POSITION_ACQUIRED = 1;
    private static final String LOG_TAG = SelectionFragment.class.getSimpleName();
    private static final String SELECTION_REQUEST_CODE = "selection_request_code";
    private static final String ALREADY_SELECTED_NODE = "already_selected_node";
    public static final String OFFLINE_USAGE = "offline_usage";
    private static final int REQUEST_CAMERA_PERMISSION_CODE = 100;
    private NavigationActivity mActivity;
    private ViewHolder holder;
    private StaticListAdapter staticListAdapter;
    private Node alreadySelectedNode;
    private boolean offline;

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @return A new instance of fragment ResetPasswordFragment.
     */
    public static SelectionFragment newInstance(int requestCode, Node alreadySelectedNode, boolean offline) {
        SelectionFragment fragment = new SelectionFragment();
        Bundle args = new Bundle();
        args.putInt(SELECTION_REQUEST_CODE, requestCode);
        args.putSerializable(ALREADY_SELECTED_NODE, alreadySelectedNode);
        args.putSerializable(OFFLINE_USAGE, offline);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(getActivity(), data.getExtras().getString(QRDialogFragment.INTENT_QR_DATA_TAG),
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.navigation_selection_fragment, container, false);
        holder = new ViewHolder(view);
        offline = getArguments().getBoolean(OFFLINE_USAGE);

        // Setup toolbar
        mActivity = (NavigationActivity) getActivity();
        mActivity.setSupportActionBar((Toolbar) view.findViewById(R.id.navigation_standard_toolbar));
        assert mActivity.getSupportActionBar() != null;
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        switch (getArguments().getInt(SELECTION_REQUEST_CODE)) {
            case ORIGIN_SELECTION_REQUEST_CODE:
                holder.toolbarTitle.setText(R.string.navigation_select_origin);
                break;
            case DESTINATION_SELECTION_REQUEST_CODE:
                holder.toolbarTitle.setText(R.string.navigation_select_destination);
                break;
        }
        alreadySelectedNode = (Node) getArguments().getSerializable(ALREADY_SELECTED_NODE);

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

        // Setup list of nodes
        List<Node> nodesList = NodeRepository.findSelectableNodes(alreadySelectedNode);
        NodeAdapter nodeAdapter = new NodeAdapter(getContext(), nodesList);
        holder.searchFieldTextView.addTextChangedListener(new SearchWatcher());
        holder.nodeListView.setAdapter(nodeAdapter);
        holder.nodeListView.setTextFilterEnabled(true);
        holder.nodeListView.setOnItemClickListener(new NodeListListener());
        // TODO: disattivando il metodo si apre la tastiera
        //changeSearchFieldHintBehaviour();

        return view;
    }

    /**
     * Listener per aprire la vista per la selezione della posizione su mappa
     *
     * @param v Oggetto view
     */
    private void openSelectionFromMap(View v) {
        SelectionFromMapFragment fragment;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // Il fragment di selezione dalla mappa dovrà tornare un risultato al fragment home
        HomeFragment homeFragment = (HomeFragment) fragmentManager
                .findFragmentByTag(HomeFragment.TAG);

        fragment = SelectionFromMapFragment.newInstance(offline);
        fragment.setTargetFragment(homeFragment, getArguments().getInt(SELECTION_REQUEST_CODE));

        fragmentManager.beginTransaction()
                .replace(R.id.navigation_content_pane, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Setto il listener per il bottone per la scansione. Creo l'oggetto IntentIntegrator a partire dal fragment. In
     * questo modo posso riprendere le informazioni direttamente dal fragment senza passare dall'activity. Riprendo le
     * informazioni tramite il metodo onActivityResult.
     *
     * @param v Oggetto View
     */
    private void qrScannerListener(View v) {
        // Request permission
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openQr();
        } else if (getActivity().checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openQr();
        } else {
            getActivity().requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openQr();
        }
    }

    private void openQr() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(QRDialogFragment.FRAGMENT_TAG);
        if (fragment != null) {
            fm.beginTransaction().remove(fragment).commit();
        }
        HomeFragment homeFragment = (HomeFragment) fm.findFragmentByTag(HomeFragment.TAG);
        QRDialogFragment dialogFragment = new QRDialogFragment();
        switch (getArguments().getInt(SELECTION_REQUEST_CODE)) {
            case ORIGIN_SELECTION_REQUEST_CODE:
                dialogFragment.setTargetFragment(homeFragment, QR_READER_ORIGIN_REQUEST_CODE);
                break;
            case DESTINATION_SELECTION_REQUEST_CODE:
                dialogFragment.setTargetFragment(homeFragment, QR_READER_DESTINATION_REQUEST_CODE);
                break;
        }
        dialogFragment.show(fm, QRDialogFragment.FRAGMENT_TAG);
    }

    private void changeSearchFieldHintBehaviour() {
        holder.searchFieldTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                holder.searchFieldTextView.setHint("");
                return false;
            }
        });
        holder.searchFieldTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    holder.searchFieldTextView.setHint(R.string.search_hint);
                }
            }
        });
    }

    /**
     * UI elements wrapper class
     */
    public static class ViewHolder {
        public final Toolbar toolbar;
        public final TextView toolbarTitle;
        public final ListView staticListview;
        public final View searchFieldView;
        public final TextView searchFieldTextView;
        public final ImageView searchFieldIcon;
        public final ListView nodeListView;

        public ViewHolder(View view) {
            toolbar = (Toolbar) view.findViewById(R.id.navigation_standard_toolbar);
            toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            staticListview = (ListView) view.findViewById((R.id.selection_static_listview));
            searchFieldView = view.findViewById(R.id.navigation_search_field);
            searchFieldIcon = (ImageView) searchFieldView.findViewById(R.id.search_icon);
            nodeListView = (ListView) view.findViewById(R.id.nodes_listview);
            searchFieldTextView = (EditText) searchFieldView.findViewById(R.id.search_text);

        }
    }

    /**
     * Responsible for handling changes in searchDoublePath edit text.
     */
    private class SearchWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String searchQuery = holder.searchFieldTextView.getText().toString();
            NodeAdapter adapter = (NodeAdapter) holder.nodeListView.getAdapter();
            adapter.getFilter().filter(s);
        }
    }

    /**
     * Responsible for handling click in nodes listView
     */
    private class NodeListListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Node node = (Node) holder.nodeListView.getItemAtPosition(position);
            Intent data = new Intent();
            data.putExtra(HomeFragment.INTENT_KEY_POSITION, SerializationUtils.serialize(node));
            getTargetFragment().onActivityResult(getTargetRequestCode(), POSITION_ACQUIRED, data);
            FragmentManager fm = getActivity().getSupportFragmentManager();
            fm.popBackStack();
        }
    }

}
