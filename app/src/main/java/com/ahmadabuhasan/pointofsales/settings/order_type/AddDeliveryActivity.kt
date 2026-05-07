package com.ahmadabuhasan.pointofsales.settings.order_type

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivityAddDeliveryBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class AddDeliveryActivity : BaseActivity() {

    private lateinit var binding: ActivityAddDeliveryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDeliveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("Add Delivery")

        binding.tvAddDelivery.setOnClickListener {
            val delivery_name = binding.etDeliveryName.text.toString().trim()
            if (delivery_name.isEmpty()) {
                binding.etDeliveryName.error = getString(R.string.enter_delivery_name)
                binding.etDeliveryName.requestFocus()
            } else {
                val databaseAccess = DatabaseAccess.getInstance(this)
                databaseAccess.open()
                if (databaseAccess.addOrderType(delivery_name)) {
                    Toasty.success(this, R.string.successfully_added, Toasty.LENGTH_SHORT).show()
                    val i = Intent(this, DeliveryActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                } else {
                    Toasty.error(this, R.string.failed, Toasty.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId != android.R.id.home) return super.onOptionsItemSelected(item)
        finish()
        return true
    }
}
