package com.ahmadabuhasan.pointofsales.report

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.database.DatabaseOpenHelper
import com.ahmadabuhasan.pointofsales.databinding.ActivitySalesReportBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import com.ajts.androidmads.library.SQLiteToExcel
import com.obsez.android.lib.filechooser.ChooserDialog
import es.dmoral.toasty.Toasty
import java.io.File
import java.text.DecimalFormat

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class SalesReportActivity : BaseActivity() {

    private lateinit var binding: ActivitySalesReportBinding

    var loading: ProgressDialog? = null
    val decimalFormat = DecimalFormat("#0.00")
    lateinit var databaseAccess: DatabaseAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.all_sales)
        }

        binding.ivNoData.visibility = View.GONE
        binding.tvNoData.visibility = View.GONE

        binding.salesReportRecyclerview.layoutManager = LinearLayoutManager(applicationContext)
        binding.salesReportRecyclerview.setHasFixedSize(true)

        databaseAccess = DatabaseAccess.getInstance(this)
        databaseAccess.open()
        val allSalesItems = databaseAccess.allSalesItems
        if (allSalesItems.isEmpty()) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show()
            binding.salesReportRecyclerview.visibility = View.GONE
            binding.tvTotalPrice.visibility = View.GONE
            binding.tvNoData.visibility = View.VISIBLE
            binding.ivNoData.visibility = View.VISIBLE
            binding.ivNoData.setImageResource(R.drawable.not_found)
        } else {
            val adapter = SalesReportAdapter(this, allSalesItems)
            binding.salesReportRecyclerview.adapter = adapter
        }

        databaseAccess.open()
        val currency = databaseAccess.currency
        databaseAccess.open()
        val subTotal = databaseAccess.getTotalOrderPrice("all")
        binding.tvTotalPrice.text = String.format("%s%s%s", getString(R.string.total_sales), currency, decimalFormat.format(subTotal))

        databaseAccess.open()
        val getTax = databaseAccess.getTotalTax("all")
        binding.tvTotalTax.text = String.format("%s(+) : %s%s", getString(R.string.tax), currency, decimalFormat.format(getTax))

        databaseAccess.open()
        val getDiscount = databaseAccess.getTotalDiscount("all")
        binding.tvTotalDiscount.text = String.format("%s(-) : %s%s", getString(R.string.total_discount), currency, decimalFormat.format(getDiscount))

        val netSales = (subTotal + getTax) - getDiscount
        binding.tvNetSales.text = String.format("%s: %s%s", getString(R.string.net_sales), currency, decimalFormat.format(netSales))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.all_sales_menu, menu)
        return true
    }

    @SuppressLint("NonConstantResourceId")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        } else if (id == R.id.menu_all_sales) {
            getReport("all")
            return true
        } else if (id == R.id.menu_daily) {
            getReport(Constant.DAILY)
            return true
        } else if (id == R.id.menu_monthly) {
            getReport(Constant.MONTHLY)
            return true
        } else if (id == R.id.menu_yearly) {
            getReport(Constant.YEARLY)
            return true
        } else if (id == R.id.menu_export_data) {
            folderChooser()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun getReport(type: String) {
        databaseAccess.open()
        Log.d("TYPE", type)
        val salesReport = databaseAccess.getSalesReport(type)
        if (salesReport.isEmpty()) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show()
            binding.salesReportRecyclerview.visibility = View.GONE
            binding.tvTotalPrice.visibility = View.GONE
            binding.tvNoData.visibility = View.VISIBLE
            binding.ivNoData.visibility = View.VISIBLE
            binding.ivNoData.setImageResource(R.drawable.not_found)
        } else {
            binding.tvNoData.visibility = View.GONE
            binding.ivNoData.visibility = View.GONE
            binding.tvTotalPrice.visibility = View.VISIBLE
            binding.salesReportRecyclerview.visibility = View.VISIBLE

            val adapter1 = SalesReportAdapter(this, salesReport)
            binding.salesReportRecyclerview.adapter = adapter1
        }

        databaseAccess.open()
        val currency = databaseAccess.currency

        databaseAccess.open()
        val subTotal = databaseAccess.getTotalOrderPrice(type)
        binding.tvTotalPrice.text = String.format("%s%s%s", getString(R.string.total_sales), currency, decimalFormat.format(subTotal))

        databaseAccess.open()
        val getTax = databaseAccess.getTotalTax(type)
        binding.tvTotalTax.text = String.format("%s(+) : %s%s", getString(R.string.tax), currency, decimalFormat.format(getTax))

        databaseAccess.open()
        val getDiscount = databaseAccess.getTotalDiscount(type)
        binding.tvTotalDiscount.text = String.format("%s(-) : %s%s", getString(R.string.discount), currency, decimalFormat.format(getDiscount))

        val netSale = (subTotal + getTax) - getDiscount
        binding.tvNetSales.text = String.format("%s: %s%s", getString(R.string.net_sales), currency, decimalFormat.format(netSale))
    }

    fun folderChooser() {
        ChooserDialog(this as Activity)
            .displayPath(true)
            .withFilter(true, false)
            .withChosenListener { dir, _ ->
                onExport(dir)
                Log.d("PATH", dir)
            }.build().show()
    }

    fun onExport(path: String) {
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        val sqLiteToExcel = SQLiteToExcel(applicationContext, DatabaseOpenHelper.DATABASE_NAME, path)
        sqLiteToExcel.exportSingleTable(Constant.orderDetails, "order_details.xls", object : SQLiteToExcel.ExportListener {
            override fun onStart() {
                loading = ProgressDialog(this@SalesReportActivity)
                loading?.setMessage(getString(R.string.data_exporting_please_wait))
                loading?.setCancelable(false)
                loading?.show()
            }

            override fun onCompleted(filePath: String) {
                val mHand = Handler()
                mHand.postDelayed({
                    loading?.dismiss()
                    Toasty.success(this@SalesReportActivity, R.string.data_successfully_exported, Toasty.LENGTH_SHORT).show()
                }, 5000L)
            }

            override fun onError(e: Exception) {
                loading?.dismiss()
                Toasty.error(this@SalesReportActivity, R.string.data_export_fail, Toasty.LENGTH_SHORT).show()
            }
        })
    }
}
