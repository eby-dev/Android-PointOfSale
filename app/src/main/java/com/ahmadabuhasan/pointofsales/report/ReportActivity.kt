package com.ahmadabuhasan.pointofsales.report

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.databinding.ActivityReportBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import com.ahmadabuhasan.pointofsales.utils.Utils

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class ReportActivity : BaseActivity() {

    private lateinit var binding: ActivityReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utils().interstitialAdsShow(this)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.report)
        }

        binding.cvSalesReport.setOnClickListener { startActivity(Intent(this@ReportActivity, SalesReportActivity::class.java)) }
        binding.cvGraphReport.setOnClickListener { startActivity(Intent(this@ReportActivity, GraphReportActivity::class.java)) }
        binding.cvExpenseReport.setOnClickListener { startActivity(Intent(this@ReportActivity, ExpenseReportActivity::class.java)) }
        binding.cvGraphExpense.setOnClickListener { startActivity(Intent(this@ReportActivity, ExpenseGraphActivity::class.java)) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
