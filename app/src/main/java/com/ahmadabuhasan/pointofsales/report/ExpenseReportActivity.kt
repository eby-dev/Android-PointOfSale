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
import com.ahmadabuhasan.pointofsales.databinding.ActivityExpenseReportBinding
import com.ahmadabuhasan.pointofsales.expense.ExpenseAdapter
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import com.ajts.androidmads.library.SQLiteToExcel
import com.obsez.android.lib.filechooser.ChooserDialog
import es.dmoral.toasty.Toasty
import java.io.File
import java.text.DecimalFormat

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class ExpenseReportActivity : BaseActivity() {

    private lateinit var binding: ActivityExpenseReportBinding

    var loading: ProgressDialog? = null
    val decimalFormat = DecimalFormat("#0.00")
    lateinit var databaseAccess: DatabaseAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.all_expense)

        binding.ivNoData.visibility = View.GONE
        binding.tvNoData.visibility = View.GONE

        binding.expenseReportRecyclerview.layoutManager = LinearLayoutManager(applicationContext)
        binding.expenseReportRecyclerview.setHasFixedSize(true)

        databaseAccess = DatabaseAccess.getInstance(this)
        databaseAccess.open()
        val allExpense = databaseAccess.allExpense
        if (allExpense.size <= 0) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show()
            binding.expenseReportRecyclerview.visibility = View.GONE
            binding.tvTotalPrice.visibility = View.GONE
            binding.tvNoData.visibility = View.VISIBLE
            binding.ivNoData.visibility = View.VISIBLE
            binding.ivNoData.setImageResource(R.drawable.not_found)
        } else {
            val adapter = ExpenseAdapter(this, allExpense)
            binding.expenseReportRecyclerview.adapter = adapter
        }

        databaseAccess.open()
        val currency = databaseAccess.currency

        databaseAccess.open()
        val total_price = databaseAccess.getTotalExpense("all")
        binding.tvTotalPrice.text = String.format("%s%s%s", getString(R.string.total_expense), currency, decimalFormat.format(total_price))
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
        val expenseReport = databaseAccess.getExpenseReport(type)
        if (expenseReport.size <= 0) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show()
            binding.expenseReportRecyclerview.visibility = View.GONE
            binding.tvTotalPrice.visibility = View.GONE
            binding.tvNoData.visibility = View.VISIBLE
            binding.ivNoData.visibility = View.VISIBLE
            binding.ivNoData.setImageResource(R.drawable.not_found)
        } else {
            binding.tvNoData.visibility = View.GONE
            binding.ivNoData.visibility = View.GONE
            binding.tvTotalPrice.visibility = View.VISIBLE
            binding.expenseReportRecyclerview.visibility = View.VISIBLE

            val adapter1 = ExpenseAdapter(this, expenseReport)
            binding.expenseReportRecyclerview.adapter = adapter1
        }

        databaseAccess.open()
        val currency = databaseAccess.currency

        databaseAccess.open()
        val total_price = databaseAccess.getTotalExpense(type)
        binding.tvTotalPrice.text = String.format("%s%s%s", getString(R.string.total_expense), currency, decimalFormat.format(total_price))
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
        sqLiteToExcel.exportSingleTable(Constant.expense, "expense.xls", object : SQLiteToExcel.ExportListener {
            override fun onStart() {
                loading = ProgressDialog(this@ExpenseReportActivity)
                loading!!.setMessage(getString(R.string.data_exporting_please_wait))
                loading!!.setCancelable(false)
                loading!!.show()
            }

            override fun onCompleted(filePath: String) {
                val mHand = Handler()
                mHand.postDelayed({
                    loading!!.dismiss()
                    Toasty.success(this@ExpenseReportActivity, R.string.data_successfully_exported, Toasty.LENGTH_SHORT).show()
                }, 5000L)
            }

            override fun onError(e: Exception) {
                loading!!.dismiss()
                Toasty.error(this@ExpenseReportActivity, R.string.data_export_fail, Toasty.LENGTH_SHORT).show()
            }
        })
    }
}
