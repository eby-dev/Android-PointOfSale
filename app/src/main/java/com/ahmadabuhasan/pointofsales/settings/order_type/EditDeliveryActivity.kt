package com.ahmadabuhasan.pointofsales.settings.order_type

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
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

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.edit_delivery)
        }

        val deliveryId = intent.getStringExtra(Constant.ORDER_TYPE_ID)

        binding.etDeliveryName.setText(intent.getStringExtra(Constant.ORDER_TYPE_NAME))
        binding.etDeliveryName.isEnabled = false

        binding.tvUpdateDelivery.visibility = View.GONE
        binding.tvEditDelivery.setOnClickListener {
            binding.etDeliveryName.isEnabled = true
            binding.etDeliveryName.setTextColor(0xffff0000.toInt())
            binding.tvEditDelivery.visibility = View.GONE
            binding.tvUpdateDelivery.visibility = View.VISIBLE
        }

        binding.tvUpdateDelivery.setOnClickListener {
            val deliveryName = binding.etDeliveryName.text.toString().trim()
            if (deliveryName.isEmpty()) {
                binding.etDeliveryName.error = getString(R.string.enter_delivery_name)
                binding.etDeliveryName.requestFocus()
            } else {
                val databaseAccess = DatabaseAccess.getInstance(this)
                databaseAccess.open()
                if (databaseAccess.updateOrderType(deliveryId, deliveryName)) {
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
