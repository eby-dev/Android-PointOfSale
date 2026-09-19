package com.ahmadabuhasan.pointofsales.report

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.activity.result.contract.ActivityResultContracts
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.database.DatabaseOpenHelper
import com.ahmadabuhasan.pointofsales.databinding.ActivityExpenseReportBinding
import com.ahmadabuhasan.pointofsales.expense.ExpenseAdapter
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import com.ahmadabuhasan.pointofsales.utils.LoadingDialog
import com.ajts.androidmads.library.SQLiteToExcel
import es.dmoral.toasty.Toasty
import java.io.File
import java.text.DecimalFormat

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class ExpenseReportActivity : BaseActivity() {

    private lateinit var binding: ActivityExpenseReportBinding

    var loading: LoadingDialog? = null
    val decimalFormat = DecimalFormat("#0.00")
    lateinit var databaseAccess: DatabaseAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.all_expense)
        }

        binding.ivNoData.visibility = View.GONE
        binding.tvNoData.visibility = View.GONE

        binding.expenseReportRecyclerview.layoutManager = LinearLayoutManager(applicationContext)
        binding.expenseReportRecyclerview.setHasFixedSize(true)

        databaseAccess = DatabaseAccess.getInstance(this)
        databaseAccess.open()
        val allExpense = databaseAccess.allExpense
        if (allExpense.isEmpty()) {
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
        val totalPrice = databaseAccess.getTotalExpense("all")
        binding.tvTotalPrice.text = String.format("%s%s%s", getString(R.string.total_expense), currency, decimalFormat.format(totalPrice))
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
        if (expenseReport.isEmpty()) {
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
        val totalPrice = databaseAccess.getTotalExpense(type)
        binding.tvTotalPrice.text = String.format("%s%s%s", getString(R.string.total_expense), currency, decimalFormat.format(totalPrice))
    }

    private val createFileLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument(MIME_TYPE)) { uri ->
        if (uri != null) {
            onExport(uri)
        }
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
        sqLiteToExcel.exportSingleTable(Constant.expense, FILE_NAME, object : SQLiteToExcel.ExportListener {
            override fun onStart() {
                loading = LoadingDialog(this@ExpenseReportActivity)
                loading?.show(getString(R.string.data_exporting_please_wait))
            }

            override fun onCompleted(filePath: String) {
                val mHand = Handler(Looper.getMainLooper())
                mHand.postDelayed({
                    loading?.dismiss()
                    if (copyExportToChosenFile(tempDir, targetUri)) {
                        Toasty.success(this@ExpenseReportActivity, R.string.data_successfully_exported, Toasty.LENGTH_SHORT).show()
                    } else {
                        Toasty.error(this@ExpenseReportActivity, R.string.data_export_fail, Toasty.LENGTH_SHORT).show()
                    }
                }, 5000L)
            }

            override fun onError(e: Exception) {
                loading?.dismiss()
                Toasty.error(this@ExpenseReportActivity, R.string.data_export_fail, Toasty.LENGTH_SHORT).show()
            }
        })
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
        const val FILE_NAME = "expense.xls"
        const val MIME_TYPE = "application/vnd.ms-excel"
    }
}
