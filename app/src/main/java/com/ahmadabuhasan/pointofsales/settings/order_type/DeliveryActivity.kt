package com.ahmadabuhasan.pointofsales.settings.order_type

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivityDeliveryBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class DeliveryActivity : BaseActivity() {

    private lateinit var binding: ActivityDeliveryBinding
    lateinit var databaseAccess: DatabaseAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("All Delivery")

        binding.deliveryRecyclerview.layoutManager = LinearLayoutManager(applicationContext)
        binding.deliveryRecyclerview.setHasFixedSize(true)

        databaseAccess = DatabaseAccess.getInstance(this)
        databaseAccess.open()
        val deliveryData = databaseAccess.orderType
        Log.d("data", "" + deliveryData.size)
        if (deliveryData.size <= 0) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show()
            binding.ivNoDelivery.setImageResource(R.drawable.no_data)
        } else {
            binding.ivNoDelivery.visibility = View.GONE
            binding.deliveryRecyclerview.adapter = DeliveryAdapter(this, deliveryData)
        }

        binding.etDeliverySearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                databaseAccess.open()
                val searchDeliveryList = databaseAccess.searchOrderType(charSequence.toString())
                if (searchDeliveryList.size <= 0) {
                    binding.deliveryRecyclerview.visibility = View.GONE
                    binding.ivNoDelivery.visibility = View.VISIBLE
                    binding.ivNoDelivery.setImageResource(R.drawable.no_data)
                } else {
                    binding.ivNoDelivery.visibility = View.GONE
                    binding.deliveryRecyclerview.visibility = View.VISIBLE
                    binding.deliveryRecyclerview.adapter = DeliveryAdapter(this@DeliveryActivity, searchDeliveryList)
                }
            }
            override fun afterTextChanged(editable: Editable) {}
        })

        binding.fabAdd.setOnClickListener { startActivity(Intent(this@DeliveryActivity, AddDeliveryActivity::class.java)) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
