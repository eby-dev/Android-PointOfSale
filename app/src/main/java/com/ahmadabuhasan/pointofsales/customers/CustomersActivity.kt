package com.ahmadabuhasan.pointofsales.customers

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.database.DatabaseOpenHelper
import com.ahmadabuhasan.pointofsales.databinding.ActivityCustomersBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import com.ajts.androidmads.library.SQLiteToExcel
import com.obsez.android.lib.filechooser.ChooserDialog
import es.dmoral.toasty.Toasty
import java.io.File

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class CustomersActivity : BaseActivity() {

    private lateinit var binding: ActivityCustomersBinding
    private var loading: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Utils().interstitialAdsShow(this)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.all_customer)
        }

        binding.customerRecyclerview.setLayoutManager(androidx.recyclerview.widget.LinearLayoutManager(applicationContext))
        binding.customerRecyclerview.setHasFixedSize(true)

        val databaseAccess = DatabaseAccess.getInstance(this)
        databaseAccess.open()
        val customerData = databaseAccess.customers
        Log.d("data", "${customerData.size}")

        if (customerData.size <= 0) {
            Toasty.info(this, R.string.no_customer_found, Toasty.LENGTH_SHORT).show()
            binding.ivNoCustomer.setImageResource(R.drawable.no_data)
        } else {
            binding.ivNoCustomer.visibility = View.GONE
            binding.customerRecyclerview.adapter = CustomerAdapter(this, customerData)
        }

        binding.etCustomerSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                databaseAccess.open()
                val searchCustomerList = databaseAccess.searchCustomers(s.toString())
                if (searchCustomerList.size <= 0) {
                    binding.customerRecyclerview.visibility = View.GONE
                    binding.ivNoCustomer.visibility = View.VISIBLE
                    binding.ivNoCustomer.setImageResource(R.drawable.no_data)
                } else {
                    binding.ivNoCustomer.visibility = View.GONE
                    binding.customerRecyclerview.visibility = View.VISIBLE
                    binding.customerRecyclerview.adapter = CustomerAdapter(this@CustomersActivity, searchCustomerList)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddCustomersActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_customer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { finish(); true }
            R.id.menu_export_customer -> { folderChooser(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun folderChooser() {
        ChooserDialog(this)
            .displayPath(true)
            .withFilter(true, false)
            .withChosenListener { dir, _ ->
                onExport(dir)
                Log.d("path", dir)
            }
            .build()
            .show()
    }

    fun onExport(path: String) {
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        val sqLiteToExcel = SQLiteToExcel(applicationContext, DatabaseOpenHelper.DATABASE_NAME, path)
        sqLiteToExcel.exportSingleTable(Constant.customers, "customers.xls", object : SQLiteToExcel.ExportListener {
            override fun onStart() {
                loading = ProgressDialog(this@CustomersActivity).apply {
                    setMessage(getString(R.string.data_exporting_please_wait))
                    setCancelable(false)
                    show()
                }
            }

            override fun onCompleted(filePath: String) {
                Handler(Looper.getMainLooper()).postDelayed({
                    loading?.dismiss()
                    Toasty.success(this@CustomersActivity, R.string.data_successfully_exported, Toasty.LENGTH_SHORT).show()
                }, 5000L)
            }

            override fun onError(e: Exception) {
                loading?.dismiss()
                Toasty.error(this@CustomersActivity, R.string.data_export_fail, Toasty.LENGTH_SHORT).show()
                Log.d("Error", e.toString())
            }
        })
    }
}