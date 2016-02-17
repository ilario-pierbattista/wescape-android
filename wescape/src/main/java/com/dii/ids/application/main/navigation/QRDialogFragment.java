package com.dii.ids.application.main.navigation;

import android.app.Dialog;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dii.ids.application.R;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

public class QRDialogFragment extends DialogFragment implements QRCodeReaderView.OnQRCodeReadListener {

    private QRCodeReaderView mydecoderview;
    public static final String FRAGMENT_TAG = "fragment_read_qr";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().setTitle(getString(R.string.action_qr));
        View view = inflater.inflate(R.layout.fragment_qrdialog, container, false);
        mydecoderview = (QRCodeReaderView) view.findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cameraNotFound() {

    }

    @Override
    public void QRCodeNotFoundOnCamImage() {

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
}
