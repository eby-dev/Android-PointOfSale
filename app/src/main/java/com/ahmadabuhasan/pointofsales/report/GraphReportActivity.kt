package com.ahmadabuhasan.pointofsales.report

import android.os.Bundle
import android.view.MenuItem
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivityGraphReportBinding
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

class GraphReportActivity : BaseActivity() {

    private lateinit var binding: ActivityGraphReportBinding

    var mYear: Int = 0
    val decimalFormat = DecimalFormat("#0.00")
    val databaseAccess: DatabaseAccess = DatabaseAccess.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.monthly_sales_graph)

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

        binding.layoutYear.setOnClickListener { chooseYearOnly() }
    }

    fun getGraphData(mYear: Int) {
        val monthList = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val monthNumber = arrayOf("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")

        val barEntries = ArrayList<BarEntry>()
        for (i in 1..12) {
            databaseAccess.open()
            val month = monthNumber[i]
            barEntries.add(BarEntry(i.toFloat(), databaseAccess.getMonthlySalesAmount(month, "" + mYear)))
        }

        val xAxis = binding.barChart.xAxis
        xAxis.setCenterAxisLabels(true)
        xAxis.granularity = 1.0f
        xAxis.isGranularityEnabled = true
        xAxis.labelCount = 12
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(monthList)

        val barDataSet = BarDataSet(barEntries, getString(R.string.monthly_sales_report))
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
        val sub_total = databaseAccess.getTotalOrderPriceForGraph(Constant.YEARLY, mYear)
        binding.tvTotalSales.text = String.format("%s%s%s", getString(R.string.total_sales), currency, decimalFormat.format(sub_total))

        databaseAccess.open()
        val get_tax = databaseAccess.getTotalTaxForGraph(Constant.YEARLY, mYear)
        binding.tvTotalTax.text = String.format("%s(+) : %s%s", getString(R.string.total_tax), currency, decimalFormat.format(get_tax))

        databaseAccess.open()
        val get_discount = databaseAccess.getTotalDiscountForGraph(Constant.YEARLY, mYear)
        binding.tvDiscount.text = String.format("%s(-) : %s%s", getString(R.string.total_discount), currency, decimalFormat.format(get_discount))

        val net_sales = (sub_total + get_tax) - get_discount
        binding.tvNetSales.text = String.format("%s: %s%s", getString(R.string.net_sales), currency, decimalFormat.format(net_sales))
    }

    private fun chooseYearOnly() {
        binding.tvSelectYear.setOnClickListener {
            val builder = MonthPickerDialog.Builder(this, { _, selectedYear ->
                binding.tvSelectYear.text = String.format("%s %s", getString(R.string.year), selectedYear)
                this.mYear = selectedYear
                getGraphData(this.mYear)
            }, mYear, 0)
            builder.showYearOnly()
                .setTitle(getString(R.string.select_year))
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
