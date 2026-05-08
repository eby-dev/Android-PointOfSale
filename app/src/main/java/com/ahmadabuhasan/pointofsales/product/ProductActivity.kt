package com.ahmadabuhasan.pointofsales.product

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
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
import com.ahmadabuhasan.pointofsales.databinding.ActivityProductBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import com.ajts.androidmads.library.SQLiteToExcel
import com.google.android.gms.ads.AdRequest
import com.obsez.android.lib.filechooser.ChooserDialog
import es.dmoral.toasty.Toasty
import java.io.File

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class ProductActivity : BaseActivity() {

    private lateinit var binding: ActivityProductBinding
    var dialog: ProgressDialog? = null
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
            folderChooser()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    fun folderChooser() {
        ChooserDialog(this as Activity)
            .displayPath(true)
            .withFilter(true, false)
            .withChosenListener { dir, _ ->
                onExport(dir)
                Log.d("path", dir)
            }.build().show()
    }

    fun onExport(path: String) {
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        val sqLiteToExcel = SQLiteToExcel(applicationContext, DatabaseOpenHelper.DATABASE_NAME, path)
        sqLiteToExcel.exportSingleTable(Constant.products, "products.xls", object : SQLiteToExcel.ExportListener {
            override fun onStart() {
                dialog = ProgressDialog(this@ProductActivity)
                dialog?.setMessage(getString(R.string.data_exporting_please_wait))
                dialog?.setCancelable(false)
                dialog?.show()
            }

            override fun onCompleted(filePath: String) {
                val mHand = Handler()
                mHand.postDelayed({
                    dialog?.dismiss()
                    Toasty.success(this@ProductActivity, R.string.data_successfully_exported, Toasty.LENGTH_SHORT).show()
                }, 5000L)
            }

            override fun onError(e: Exception) {
                dialog?.dismiss()
                Toasty.error(this@ProductActivity, R.string.data_export_fail, Toasty.LENGTH_SHORT).show()
            }
        })
    }
}
