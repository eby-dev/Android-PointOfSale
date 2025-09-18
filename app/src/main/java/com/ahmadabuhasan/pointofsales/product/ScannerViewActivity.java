package com.ahmadabuhasan.pointofsales.product;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Objects;

import es.dmoral.toasty.Toasty;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class ScannerViewActivity extends BaseActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_view);

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.qr_barcode_scanner);

        if (Build.VERSION.SDK_INT >= 23) {
            requestCameraPermission();
        }

        this.scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        this.scannerView.startCamera();
        this.scannerView.setResultHandler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.scannerView.setResultHandler(this);
        this.scannerView.startCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        AddProductActivity.etProductCode.setText(rawResult.getText());
        Toasty.info(this, rawResult.toString(), Toasty.LENGTH_SHORT).show();
        Log.d("QRCodeScanner", rawResult.getText());
        Log.d("QRCodeScanner", rawResult.getBarcodeFormat().toString());
        onBackPressed();
    }

    private void requestCameraPermission() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {

                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        ScannerViewActivity.this.scannerView = new ZXingScannerView(ScannerViewActivity.this);
                        ScannerViewActivity activity = ScannerViewActivity.this;

                        activity.setContentView(activity.scannerView);

                        ScannerViewActivity.this.scannerView.startCamera();
                        ScannerViewActivity.this.scannerView.setResultHandler(ScannerViewActivity.this);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        if (permissionDeniedResponse.isPermanentlyDenied()) {
                            Toasty.info(ScannerViewActivity.this, R.string.camera_permission, Toasty.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() != android.R.id.home) {
            return super.onOptionsItemSelected(item);
        }
        finish();
        return true;
    }
}