package com.ahmadabuhasan.pointofsales.product

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import com.google.zxing.Result
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import es.dmoral.toasty.Toasty
import me.dm7.barcodescanner.zxing.ZXingScannerView

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class EditProductScannerViewActivity : BaseActivity(), ZXingScannerView.ResultHandler {

    private lateinit var scannerView: ZXingScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product_scanner_view)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.qr_barcode_scanner)

        if (Build.VERSION.SDK_INT >= 23) {
            requestCameraPermission()
        }

        scannerView = ZXingScannerView(this)
        setContentView(scannerView)
        scannerView.startCamera()
        scannerView.setResultHandler(this)
    }

    override fun onResume() {
        super.onResume()
        scannerView.setResultHandler(this)
        scannerView.startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        scannerView.stopCamera()
    }

    override fun handleResult(rawResult: Result) {
        EditProductActivity.etProductCode.setText(rawResult.text)
        Toasty.info(this, rawResult.toString(), Toasty.LENGTH_SHORT).show()
        Log.d("QRCodeScanner", rawResult.text)
        Log.d("QRCodeScanner", rawResult.barcodeFormat.toString())
        onBackPressed()
    }

    private fun requestCameraPermission() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                    scannerView = ZXingScannerView(this@EditProductScannerViewActivity)
                    val activity = this@EditProductScannerViewActivity
                    activity.setContentView(activity.scannerView)
                    scannerView.startCamera()
                    scannerView.setResultHandler(this@EditProductScannerViewActivity)
                }

                override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {
                    if (permissionDeniedResponse.isPermanentlyDenied) {
                        Toasty.info(this@EditProductScannerViewActivity, R.string.camera_permission, Toasty.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissionRequest: PermissionRequest, permissionToken: PermissionToken) {
                    permissionToken.continuePermissionRequest()
                }
            }).check()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId != android.R.id.home) {
            return super.onOptionsItemSelected(item)
        }
        finish()
        return true
    }
}
