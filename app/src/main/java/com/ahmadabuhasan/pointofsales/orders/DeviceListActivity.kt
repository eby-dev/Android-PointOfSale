package com.ahmadabuhasan.pointofsales.orders

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.databinding.ActivityDeviceListBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */

@SuppressLint("MissingPermission")
class DeviceListActivity : BaseActivity() {

    private lateinit var binding: ActivityDeviceListBinding

    companion object {
        private const val TAG = "DeviceListActivity"
        private const val D = true
        var EXTRA_DEVICE_ADDRESS = "device_address"
        private const val REQUEST_BLUETOOTH_PERMISSIONS = 1001
    }

    private lateinit var mBtAdapter: BluetoothAdapter
    private lateinit var mPairedDevicesArrayAdapter: ArrayAdapter<String>
    private lateinit var mNewDevicesArrayAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceListBinding.inflate(layoutInflater)

        setContentView(binding.root)
        val br = supportActionBar
        if (br != null) {
            br.setDisplayHomeAsUpEnabled(true)
            br.setHomeAsUpIndicator(R.drawable.back)
        }

        if (!bluetoothPermissions()) {
            Toast.makeText(this, R.string.permission_bluetooth_denied, Toast.LENGTH_LONG).show()
            return
        }

        initializeBluetoothUI()
    }

    private fun initializeBluetoothUI() {
        setResult(Activity.RESULT_CANCELED)

        binding.buttonScan.setOnClickListener { v ->
            doDiscovery()
            v.visibility = View.GONE
        }

        mPairedDevicesArrayAdapter = ArrayAdapter(this, R.layout.device_name)
        mNewDevicesArrayAdapter = ArrayAdapter(this, R.layout.device_name)

        binding.pairedDevices.adapter = mPairedDevicesArrayAdapter
        binding.pairedDevices.onItemClickListener = mDeviceClickListener

        binding.newDevices.adapter = mNewDevicesArrayAdapter
        binding.newDevices.onItemClickListener = mDeviceClickListener

        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        ContextCompat.registerReceiver(this, mReceiver, filter, ContextCompat.RECEIVER_EXPORTED)

        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        ContextCompat.registerReceiver(this, mReceiver, filter, ContextCompat.RECEIVER_EXPORTED)

        mBtAdapter = BluetoothAdapter.getDefaultAdapter()
        val pairedDevices = mBtAdapter.bondedDevices

        if (pairedDevices.isNotEmpty()) {
            findViewById<View>(R.id.title_paired_devices).visibility = View.VISIBLE
            for (device in pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.name + "\n" + device.address)
            }
        } else {
            val noDevices = resources.getText(R.string.none_paired).toString()
            mPairedDevicesArrayAdapter.add(noDevices)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mBtAdapter.isInitialized) {
            mBtAdapter.cancelDiscovery()
        }
        this.unregisterReceiver(mReceiver)
    }

    private fun doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()")

        binding.buttonScan.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        setTitle(R.string.scanning)

        binding.titleNewDevices.visibility = View.VISIBLE

        if (mBtAdapter.isDiscovering) {
            mBtAdapter.cancelDiscovery()
        }

        mBtAdapter.startDiscovery()
    }

    private val mDeviceClickListener = AdapterView.OnItemClickListener { _, v, _, _ ->
        mBtAdapter.cancelDiscovery()

        val info = (v as TextView).text.toString()
        val address = info.substring(info.length - 17)

        val intent = Intent()
        intent.putExtra(EXTRA_DEVICE_ADDRESS, address)

        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) ?: return
                if (device.bondState != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.name + "\n" + device.address)
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                binding.titleNewDevices.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                setTitle(R.string.select_device)
                if (mNewDevicesArrayAdapter.count == 0) {
                    val noDevices = resources.getText(R.string.none_found).toString()
                    mNewDevicesArrayAdapter.add(noDevices)
                }
            }
        }
    }

    private fun bluetoothPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val hasScanPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
            val hasConnectPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED

            if (!hasScanPermission || !hasConnectPermission) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ),
                    REQUEST_BLUETOOTH_PERMISSIONS)
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permission_bluetooth_granted, Toast.LENGTH_SHORT).show()
                initializeBluetoothUI()
            } else {
                Toast.makeText(this, R.string.permission_bluetooth_denied, Toast.LENGTH_LONG).show()
            }
        }
    }
}
