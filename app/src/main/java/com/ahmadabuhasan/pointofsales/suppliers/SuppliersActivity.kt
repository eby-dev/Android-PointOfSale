package com.ahmadabuhasan.pointofsales.suppliers

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.database.DatabaseOpenHelper
import com.ahmadabuhasan.pointofsales.databinding.ActivitySuppliersBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import com.ajts.androidmads.library.SQLiteToExcel
import com.obsez.android.lib.filechooser.ChooserDialog
import es.dmoral.toasty.Toasty
import java.io.File

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class SuppliersActivity : BaseActivity() {

    private lateinit var binding: ActivitySuppliersBinding
    private var loading: ProgressDialog? = null
    private lateinit var databaseAccess: DatabaseAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuppliersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Utils().interstitialAdsShow(this)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.all_suppliers)
        }

        binding.supplierRecyclerview.layoutManager = LinearLayoutManager(applicationContext)
        binding.supplierRecyclerview.setHasFixedSize(true)

        databaseAccess = DatabaseAccess.getInstance(this)
        databaseAccess.open()
        val supplierData = databaseAccess.suppliers
        Log.d("data", "${supplierData.size}")

        if (supplierData.isEmpty()) {
            Toasty.info(this, R.string.no_suppliers_found, Toasty.LENGTH_SHORT).show()
            binding.ivNoSupplier.setImageResource(R.drawable.no_data)
        } else {
            binding.ivNoSupplier.visibility = View.GONE
            binding.supplierRecyclerview.adapter = SupplierAdapter(this, supplierData)
        }

        binding.etSupplierSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                databaseAccess.open()
                val searchSupplier = databaseAccess.searchSuppliers(s.toString())
                if (searchSupplier.isEmpty()) {
                    binding.supplierRecyclerview.visibility = View.GONE
                    binding.ivNoSupplier.visibility = View.VISIBLE
                    binding.ivNoSupplier.setImageResource(R.drawable.no_data)
                } else {
                    binding.ivNoSupplier.visibility = View.GONE
                    binding.supplierRecyclerview.visibility = View.VISIBLE
                    binding.supplierRecyclerview.adapter = SupplierAdapter(this@SuppliersActivity, searchSupplier)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddSuppliersActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.export_suppliers_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { finish(); true }
            R.id.menu_export_supplier -> { folderChooser(); true }
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
        sqLiteToExcel.exportSingleTable(Constant.suppliers, "suppliers.xls", object : SQLiteToExcel.ExportListener {
            override fun onStart() {
                loading = ProgressDialog(this@SuppliersActivity).apply {
                    setMessage(getString(R.string.data_exporting_please_wait))
                    setCancelable(false)
                    show()
                }
            }

            override fun onCompleted(filePath: String) {
                Handler(Looper.getMainLooper()).postDelayed({
                    loading?.dismiss()
                    Toasty.success(this@SuppliersActivity, R.string.data_successfully_exported, Toasty.LENGTH_SHORT).show()
                }, 5000L)
            }

            override fun onError(e: Exception) {
                loading?.dismiss()
                Toasty.error(this@SuppliersActivity, R.string.data_export_fail, Toasty.LENGTH_SHORT).show()
            }
        })
    }
}