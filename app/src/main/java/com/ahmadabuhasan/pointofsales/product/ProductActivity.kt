package com.ahmadabuhasan.pointofsales.product

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.database.DatabaseOpenHelper
import com.ahmadabuhasan.pointofsales.databinding.ActivityProductBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import com.ahmadabuhasan.pointofsales.utils.LoadingDialog
import com.ajts.androidmads.library.SQLiteToExcel
import com.google.android.gms.ads.AdRequest
import es.dmoral.toasty.Toasty
import java.io.File

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class ProductActivity : BaseActivity() {

    private lateinit var binding: ActivityProductBinding
    var dialog: LoadingDialog? = null
    lateinit var databaseAccess: DatabaseAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.adViewProduct.loadAd(AdRequest.Builder().build())

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.all_product)
        }

        binding.productRecyclerview.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(applicationContext)
        binding.productRecyclerview.setHasFixedSize(true)

        databaseAccess = DatabaseAccess.getInstance(this)
        databaseAccess.open()
        val productData = databaseAccess.products
        Log.d("data", "" + productData.size)
        if (productData.isEmpty()) {
            Toasty.info(this, R.string.no_product_found, Toasty.LENGTH_SHORT).show()
            binding.ivNoProduct.setImageResource(R.drawable.no_data)
        } else {
            binding.ivNoProduct.visibility = View.GONE
            val adapter = ProductAdapter(this, productData)
            binding.productRecyclerview.adapter = adapter
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                databaseAccess.open()
                val searchProductList = databaseAccess.getSearchProducts(charSequence.toString())
                if (searchProductList.isEmpty()) {
                    binding.productRecyclerview.visibility = View.GONE
                    binding.ivNoProduct.visibility = View.VISIBLE
                    binding.ivNoProduct.setImageResource(R.drawable.no_data)
                } else {
                    binding.ivNoProduct.visibility = View.GONE
                    binding.productRecyclerview.visibility = View.VISIBLE
                    val adapter1 = ProductAdapter(this@ProductActivity, searchProductList)
                    binding.productRecyclerview.adapter = adapter1
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        binding.fabAdd.setOnClickListener {
            startActivity(android.content.Intent(this@ProductActivity, AddProductActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.all_product_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else if (item.itemId == R.id.menu_export) {
            confirmExport()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private val createFileLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument(MIME_TYPE)) { uri ->
        if (uri != null) {
            onExport(uri)
        }
    }

    private fun confirmExport() {
        AlertDialog.Builder(this)
            .setTitle(R.string.export_product_title)
            .setMessage(R.string.export_product_message)
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.export) { _, _ -> folderChooser() }
            .show()
    }

    fun folderChooser() {
        createFileLauncher.launch(FILE_NAME)
    }

    fun onExport(targetUri: Uri) {
        val tempDir = File(getExternalFilesDir(null), getString(R.string.app_name))
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        val sqLiteToExcel = SQLiteToExcel(applicationContext, DatabaseOpenHelper.DATABASE_NAME, tempDir.absolutePath)
        // Base64 images exceed the BIFF8 record limit and break re-import.
        sqLiteToExcel.setExcludeColumns(listOf(Constant.PRODUCT_IMAGE))
        sqLiteToExcel.exportSingleTable(Constant.products, FILE_NAME, object : SQLiteToExcel.ExportListener {
            override fun onStart() {
                dialog = LoadingDialog(this@ProductActivity)
                dialog?.show(getString(R.string.data_exporting_please_wait))
            }

            override fun onCompleted(filePath: String) {
                val mHand = Handler(Looper.getMainLooper())
                mHand.postDelayed({
                    dialog?.dismiss()
                    if (copyExportToChosenFile(tempDir, targetUri)) {
                        Toasty.success(this@ProductActivity, R.string.data_successfully_exported, Toasty.LENGTH_SHORT).show()
                    } else {
                        Toasty.error(this@ProductActivity, R.string.data_export_fail, Toasty.LENGTH_SHORT).show()
                    }
                }, 5000L)
            }

            override fun onError(e: Exception) {
                dialog?.dismiss()
                Toasty.error(this@ProductActivity, R.string.data_export_fail, Toasty.LENGTH_SHORT).show()
            }
        })
    }

    // AdMob requires the banner to follow the activity lifecycle: pausing it
    // stops impressions being counted while the screen is not visible, and
    // destroying it releases the underlying WebView.
    override fun onResume() {
        super.onResume()
        binding.adViewProduct.resume()
    }

    override fun onPause() {
        binding.adViewProduct.pause()
        super.onPause()
    }

    override fun onDestroy() {
        binding.adViewProduct.destroy()
        super.onDestroy()
    }

    private fun copyExportToChosenFile(tempDir: File, targetUri: Uri): Boolean {
        return try {
            val sourceFile = File(tempDir, FILE_NAME)
            if (!sourceFile.exists()) return false

            sourceFile.inputStream().use { input ->
                contentResolver.openOutputStream(targetUri)?.use { output ->
                    input.copyTo(output)
                } ?: return false
            }
            sourceFile.delete()
            true
        } catch (e: Exception) {
            Log.e("EXPORT", "${e.message}", e)
            false
        }
    }

    private companion object {
        const val FILE_NAME = "products.xls"
        const val MIME_TYPE = "application/vnd.ms-excel"
    }
}
