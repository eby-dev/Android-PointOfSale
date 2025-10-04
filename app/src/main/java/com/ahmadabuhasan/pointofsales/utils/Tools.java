package com.ahmadabuhasan.pointofsales.utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ahmadabuhasan.pointofsales.R;

public class Tools {

    public static boolean isBlueToothOn(Context c) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(c, c.getString(R.string.bluetooth_not_available), Toast.LENGTH_LONG).show();
            return false;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(c, c.getString(R.string.turnon_bluetooth), Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(c, Manifest.permission.BLUETOOTH_CONNECT)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    c.startActivity(enableIntent);
                } else {
                    ActivityCompat.requestPermissions(
                            (Activity) c,
                            new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                            1001
                    );
                }
            } else {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            c.startActivity(enableIntent);
            }
            return false;
        }
        return true;
    }
}