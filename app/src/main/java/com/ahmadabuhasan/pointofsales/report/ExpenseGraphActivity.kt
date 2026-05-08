package com.ahmadabuhasan.pointofsales.report

import android.os.Bundle
import android.view.MenuItem
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivityExpenseGraphBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.whiteelephant.monthpicker.MonthPickerDialog
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class ExpenseGraphActivity : BaseActivity() {

    private lateinit var binding: ActivityExpenseGraphBinding

    var mYear: Int = 0
    val decimalFormat = DecimalFormat("#0.00")
    val databaseAccess: DatabaseAccess = DatabaseAccess.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.monthly_expense_in_graph)
        }

        binding.barChart.setDrawBarShadow(false)
        binding.barChart.setDrawValueAboveBar(true)
        binding.barChart.setDrawGridBackground(true)
        binding.barChart.setMaxVisibleValueCount(50)
        binding.barChart.setPinchZoom(false)

        val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        binding.tvSelectYear.text = String.format("%s %s", getString(R.string.year), currentYear)
        val parseInt = Integer.parseInt(currentYear)
        mYear = parseInt
        getGraphData(parseInt)
    }

    fun getGraphData(mYear: Int) {
        val monthList = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val monthNumber = arrayOf("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")

        val barEntries = ArrayList<BarEntry>()
        for (i in 1..12) {
            databaseAccess.open()
            val month = monthNumber[i]
            barEntries.add(BarEntry(i.toFloat(), databaseAccess.getMonthlyExpenseAmount(month, "" + mYear)))
        }

        val xAxis = binding.barChart.xAxis
        xAxis.setCenterAxisLabels(true)
        xAxis.granularity = 1.0f
        xAxis.isGranularityEnabled = true
        xAxis.labelCount = 12
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(monthList)

        val barDataSet = BarDataSet(barEntries, getString(R.string.monthly_expense_report))
        barDataSet.setColors(*ColorTemplate.LIBERTY_COLORS)
        val barData = BarData(barDataSet)
        barData.barWidth = 0.9f

        binding.barChart.data = barData
        binding.barChart.setScaleEnabled(false)
        binding.barChart.invalidate()
        binding.barChart.notifyDataSetChanged()

        databaseAccess.open()
        val currency = databaseAccess.currency
        databaseAccess.open()
        val totalExpense = databaseAccess.getTotalExpenseForGraph(Constant.YEARLY, mYear)
        binding.tvTotalSales.text = String.format("%s%s%s", getString(R.string.total_expense), currency, decimalFormat.format(totalExpense))

        binding.layoutYear.setOnClickListener { chooseYearOnly() }
    }

    private fun chooseYearOnly() {
        binding.tvSelectYear.setOnClickListener {
            val builder = MonthPickerDialog.Builder(this, { _, selectedYear ->
                binding.tvSelectYear.text = String.format("%s %s", getString(R.string.year), selectedYear)
                this.mYear = selectedYear
                getGraphData(this.mYear)
            }, mYear, 0)
            builder.showYearOnly()
                .setTitle(this@ExpenseGraphActivity.getString(R.string.select_year))
                .setYearRange(1990, 2099)
                .build().show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId != android.R.id.home) {
            return super.onOptionsItemSelected(item)
        }
        finish()
        return true
    }
}
