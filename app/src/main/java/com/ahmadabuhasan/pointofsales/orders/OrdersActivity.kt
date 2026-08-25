package com.ahmadabuhasan.pointofsales.orders

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmadabuhasan.pointofsales.DashboardActivity
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivityOrdersBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import com.google.android.gms.ads.AdRequest
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class OrdersActivity : BaseActivity() {

    private lateinit var binding: ActivityOrdersBinding
    lateinit var databaseAccess: DatabaseAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.adViewOrders.loadAd(AdRequest.Builder().build())

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.order_history)
        }

        binding.ivNoOrder.visibility = View.GONE
        binding.tvNoOrder.visibility = View.GONE

        binding.ordersRecyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.ordersRecyclerview.setHasFixedSize(true)

        databaseAccess = DatabaseAccess.getInstance(this)
        databaseAccess.open()
        val orderList = databaseAccess.orderList
        if (orderList.isEmpty()) {
            Toasty.info(this, R.string.no_order_found, Toasty.LENGTH_SHORT).show()
            binding.ordersRecyclerview.visibility = View.GONE
            binding.tvNoOrder.visibility = View.VISIBLE
            binding.ivNoOrder.visibility = View.VISIBLE
            binding.ivNoOrder.setImageResource(R.drawable.not_found)
        } else {
            val adapter = OrderAdapter(this@OrdersActivity, orderList)
            binding.ordersRecyclerview.adapter = adapter
        }

        binding.etSearchOrder.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                databaseAccess.open()
                val searchOrder = databaseAccess.searchOrderList(charSequence.toString())
                if (searchOrder.isEmpty()) {
                    binding.ordersRecyclerview.visibility = View.GONE
                    binding.ivNoOrder.visibility = View.VISIBLE
                    binding.ivNoOrder.setImageResource(R.drawable.no_data)
                    return
                }
                binding.ivNoOrder.visibility = View.GONE
                binding.ordersRecyclerview.visibility = View.VISIBLE

                val adapter1 = OrderAdapter(this@OrdersActivity, searchOrder)
                binding.ordersRecyclerview.adapter = adapter1
            }
            override fun afterTextChanged(editable: Editable) {}
        })
    }

    override fun onBackPressed() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // AdMob requires the banner to follow the activity lifecycle: pausing it
    // stops impressions being counted while the screen is not visible, and
    // destroying it releases the underlying WebView.
    override fun onResume() {
        super.onResume()
        binding.adViewOrders.resume()
    }

    override fun onPause() {
        binding.adViewOrders.pause()
        super.onPause()
    }

    override fun onDestroy() {
        binding.adViewOrders.destroy()
        super.onDestroy()
    }
}
