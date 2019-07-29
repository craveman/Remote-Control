package ru.inspirationpoint.remotecontrol.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;

public class QRScanDialog extends DialogFragment {

    private AlertDialog qrDialog;
    private QRListener mListener;
    private CodeScanner mCodeScanner;

    public static QRScanDialog newInstance() {

        Bundle args = new Bundle();

        QRScanDialog fragment = new QRScanDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                new AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                new AlertDialog.Builder(getActivity());
        View contentView = inflater.inflate(R.layout.qr_dlg, null);
        CodeScannerView scannerView = contentView.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(getActivity(), scannerView);
        mCodeScanner.setDecodeCallback(result -> {
            if (mListener != null) {
                mListener.onCodeDetected(result.getText());
                qrDialog.dismiss();
            }
        });
        qrDialog = builder.setView(contentView).show();
        qrDialog.setCanceledOnTouchOutside(false);
        return qrDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof QRListener) {
            mListener = (QRListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    public interface QRListener{
        void onCodeDetected(String text);
    }
}
