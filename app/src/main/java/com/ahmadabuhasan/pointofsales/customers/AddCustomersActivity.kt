package com.ahmadabuhasan.pointofsales.customers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.ahmadabuhasan.pointofsales.DashboardActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.database.DatabaseOpenHelper
import com.ahmadabuhasan.pointofsales.databinding.ActivityAddCustomersBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import com.ahmadabuhasan.pointofsales.utils.LoadingDialog
import com.ajts.androidmads.library.ExcelToSQLite
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class AddCustomersActivity : BaseActivity() {

    private lateinit var binding: ActivityAddCustomersBinding
    private var loading: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.add_customer)
        }

        val databaseAccess = DatabaseAccess.getInstance(this)

        binding.tvAddCustomer.setOnClickListener {
            val customerName = binding.etCustomerName.text.toString().trim()
            val customerCell = binding.etCustomerCell.text.toString().trim()
            val customerEmail = binding.etCustomerEmail.text.toString().trim()
            val customerAddress = binding.etCustomerAddress.text.toString().trim()

            when {
                customerName.isEmpty() -> {
                    binding.etCustomerName.error = getString(R.string.enter_customer_name)
                    binding.etCustomerName.requestFocus()
                }
                customerCell.isEmpty() -> {
                    binding.etCustomerCell.error = getString(R.string.enter_customer_cell)
                    binding.etCustomerCell.requestFocus()
                }
                !customerEmail.contains("@") || !customerEmail.contains(".") -> {
                    binding.etCustomerEmail.error = getString(R.string.enter_valid_email)
                    binding.etCustomerEmail.requestFocus()
                }
                customerAddress.isEmpty() -> {
                    binding.etCustomerAddress.error = getString(R.string.enter_customer_address)
                    binding.etCustomerAddress.requestFocus()
                }
                else -> {
                    databaseAccess.open()
                    if (databaseAccess.addCustomer(customerName, customerCell, customerEmail, customerAddress)) {
                        Toasty.success(this, R.string.customer_successfully_added, Toasty.LENGTH_SHORT).show()
                        startActivity(Intent(this, CustomersActivity::class.java).apply {
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
        menuInflater.inflate(R.menu.add_product_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { finish(); true }
            R.id.menu_import -> { fileChooser(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val openFileLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            onImport(uri)
        }
    }

    fun fileChooser() {
        openFileLauncher.launch(arrayOf(MIME_TYPE))
    }

    fun onImport(uri: Uri) {
        val databaseAccess = DatabaseAccess.getInstance(this)
        databaseAccess.open()
        val stream = contentResolver.openInputStream(uri)
        if (stream == null) {
            Toast.makeText(this, R.string.no_file_found, Toast.LENGTH_SHORT).show()
            return
        }
        val excelToSQLite = ExcelToSQLite(applicationContext, DatabaseOpenHelper.DATABASE_NAME, false)
        excelToSQLite.importFromStream(stream, object : ExcelToSQLite.ImportListener {
            override fun onStart() {
                loading = LoadingDialog(this@AddCustomersActivity)
                loading?.show(getString(R.string.data_importing_please_wait))
            }

            override fun onCompleted(dbName: String) {
                Handler(Looper.getMainLooper()).postDelayed({
                    loading?.dismiss()
                    Toasty.success(this@AddCustomersActivity, R.string.data_successfully_imported, Toasty.LENGTH_SHORT).show()
                    startActivity(Intent(this@AddCustomersActivity, DashboardActivity::class.java))
                    finish()
                }, 5000L)
            }

            override fun onError(e: Exception) {
                loading?.dismiss()
                Toasty.error(this@AddCustomersActivity, R.string.data_import_fail, Toasty.LENGTH_SHORT).show()
                Log.d("Error : ", "${e.message}")
            }
        })
    }

    private companion object {
        const val MIME_TYPE = "application/vnd.ms-excel"
    }
}