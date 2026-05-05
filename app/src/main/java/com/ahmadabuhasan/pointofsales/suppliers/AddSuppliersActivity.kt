package com.ahmadabuhasan.pointofsales.suppliers

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.ahmadabuhasan.pointofsales.DashboardActivity
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.database.DatabaseOpenHelper
import com.ahmadabuhasan.pointofsales.databinding.ActivityAddSuppliersBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import com.ajts.androidmads.library.ExcelToSQLite
import com.obsez.android.lib.filechooser.ChooserDialog
import es.dmoral.toasty.Toasty
import java.io.File

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class AddSuppliersActivity : BaseActivity() {

    private lateinit var binding: ActivityAddSuppliersBinding
    private var loading: ProgressDialog? = null
    private lateinit var databaseAccess: DatabaseAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSuppliersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.add_supplier)
        }

        databaseAccess = DatabaseAccess.getInstance(this)

        binding.tvAddSupplier.setOnClickListener {
            val supplierName = binding.etSupplierName.text.toString().trim()
            val supplierContactName = binding.etSupplierContactName.text.toString().trim()
            val supplierCell = binding.etSupplierCell.text.toString().trim()
            val supplierEmail = binding.etSupplierEmail.text.toString().trim()
            val supplierAddress = binding.etSupplierAddress.text.toString().trim()

            when {
                supplierName.isEmpty() -> {
                    binding.etSupplierName.error = getString(R.string.enter_suppliers_name)
                    binding.etSupplierName.requestFocus()
                }
                supplierContactName.isEmpty() -> {
                    binding.etSupplierContactName.error = getString(R.string.enter_suppliers_contact_person_name)
                    binding.etSupplierContactName.requestFocus()
                }
                supplierCell.isEmpty() -> {
                    binding.etSupplierCell.error = getString(R.string.enter_suppliers_cell)
                    binding.etSupplierCell.requestFocus()
                }
                supplierEmail.isEmpty() || !supplierEmail.contains("@") || !supplierEmail.contains(".") -> {
                    binding.etSupplierEmail.error = getString(R.string.enter_valid_email)
                    binding.etSupplierEmail.requestFocus()
                }
                supplierAddress.isEmpty() -> {
                    binding.etSupplierAddress.error = getString(R.string.enter_suppliers_address)
                    binding.etSupplierAddress.requestFocus()
                }
                else -> {
                    databaseAccess.open()
                    if (databaseAccess.addSuppliers(supplierName, supplierContactName, supplierCell, supplierEmail, supplierAddress)) {
                        Toasty.success(this, R.string.suppliers_successfully_added, Toasty.LENGTH_SHORT).show()
                        startActivity(Intent(this, SuppliersActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        })
                    } else {
                        Toasty.error(this, R.string.failed, Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_supplier_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { finish(); true }
            R.id.menu_import_supplier -> { fileChooser(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun fileChooser() {
        ChooserDialog(this)
            .displayPath(true)
            .withFilter(false, false, "xls")
            .withChosenListener { dir, _ -> onImport(dir) }
            .withOnCancelListener { dialog ->
                Log.d("CANCEL", "CANCEL")
                dialog.cancel()
            }
            .build()
            .show()
    }

    fun onImport(path: String) {
        databaseAccess.open()
        val file = File(path)
        if (!file.exists()) {
            Toast.makeText(this, R.string.no_file_found, Toast.LENGTH_SHORT).show()
            return
        }
        val excelToSQLite = ExcelToSQLite(applicationContext, DatabaseOpenHelper.DATABASE_NAME, false)
        excelToSQLite.importFromFile(path, object : ExcelToSQLite.ImportListener {
            override fun onStart() {
                loading = ProgressDialog(this@AddSuppliersActivity).apply {
                    setMessage(getString(R.string.data_importing_please_wait))
                    setCancelable(false)
                    show()
                }
            }

            override fun onCompleted(dbName: String) {
                Handler(Looper.getMainLooper()).postDelayed({
                    loading?.dismiss()
                    Toasty.success(this@AddSuppliersActivity, R.string.data_successfully_imported, Toasty.LENGTH_SHORT).show()
                    startActivity(Intent(this@AddSuppliersActivity, DashboardActivity::class.java))
                    finish()
                }, 5000L)
            }

            override fun onError(e: Exception) {
                loading?.dismiss()
                Toasty.error(this@AddSuppliersActivity, R.string.data_import_fail, Toasty.LENGTH_SHORT).show()
                Log.d("Error : ", "${e.message}")
            }
        })
    }
}