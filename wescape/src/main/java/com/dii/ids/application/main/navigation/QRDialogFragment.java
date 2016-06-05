package com.dii.ids.application.main.navigation;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dii.ids.application.R;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.entity.repository.NodeRepository;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import org.apache.commons.lang3.SerializationUtils;

public class QRDialogFragment extends DialogFragment implements QRCodeReaderView.OnQRCodeReadListener {

    public static final String FRAGMENT_TAG = "fragment_read_qr";
    public static final String INTENT_QR_DATA_TAG = "qr_code";
    private QRCodeReaderView mydecoderview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().setTitle(getString(R.string.action_qr));
        View view = inflater.inflate(R.layout.navigation_qr_dialog_fragment, container, false);
        mydecoderview = (QRCodeReaderView) view.findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mydecoderview.getCameraManager().startPreview();
    }

    @Override
    public void onPause() {
        super.onPause();
        mydecoderview.getCameraManager().stopPreview();
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        String[] parts = text.split("_");
        if(parts != null && parts.length >= 2) {
            int id = Integer.parseInt(parts[0]);
            Node node = NodeRepository.find(id);
            Intent data = new Intent().putExtra(HomeFragment.INTENT_KEY_POSITION, SerializationUtils.serialize(node));
            getTargetFragment().onActivityResult(getTargetRequestCode(), 0, data);
            dismiss();
            FragmentManager fm = getActivity().getSupportFragmentManager();
            fm.popBackStack();
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_reading_qr), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void cameraNotFound() {
        Toast.makeText(getActivity(), getString(R.string.error_no_camera_found), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void QRCodeNotFoundOnCamImage() {

    }
}
