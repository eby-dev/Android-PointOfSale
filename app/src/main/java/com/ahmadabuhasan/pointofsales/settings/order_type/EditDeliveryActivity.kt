package com.ahmadabuhasan.pointofsales.settings.order_type

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.internal.view.SupportMenu
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivityEditDeliveryBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class EditDeliveryActivity : BaseActivity() {

    private lateinit var binding: ActivityEditDeliveryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDeliveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("Edit Delivery")

        val delivery_id = intent.extras!!.getString(Constant.ORDER_TYPE_ID)

        binding.etDeliveryName.setText(intent.extras!!.getString(Constant.ORDER_TYPE_NAME))
        binding.etDeliveryName.isEnabled = false

        binding.tvUpdateDelivery.visibility = View.GONE
        binding.tvEditDelivery.setOnClickListener {
            binding.etDeliveryName.isEnabled = true
            binding.etDeliveryName.setTextColor(SupportMenu.CATEGORY_MASK)
            binding.tvEditDelivery.visibility = View.GONE
            binding.tvUpdateDelivery.visibility = View.VISIBLE
        }

        binding.tvUpdateDelivery.setOnClickListener {
            val delivery_name = binding.etDeliveryName.text.toString().trim()
            if (delivery_name.isEmpty()) {
                binding.etDeliveryName.error = getString(R.string.enter_delivery_name)
                binding.etDeliveryName.requestFocus()
            } else {
                val databaseAccess = DatabaseAccess.getInstance(this)
                databaseAccess.open()
                if (databaseAccess.updateOrderType(delivery_id, delivery_name)) {
                    Toasty.success(this, R.string.update_successfully, Toasty.LENGTH_SHORT).show()
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
