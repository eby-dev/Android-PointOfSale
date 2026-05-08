package com.ahmadabuhasan.pointofsales.settings.payment_method

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivityEditPaymentMethodBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class EditPaymentMethodActivity : BaseActivity() {

    private lateinit var binding: ActivityEditPaymentMethodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.update_payment_method)
        }

        val paymentMethodId = intent.getStringExtra(Constant.PAYMENT_METHOD_ID)

        binding.etPaymentMethodName.setText(intent.getStringExtra(Constant.PAYMENT_METHOD_NAME))
        binding.etPaymentMethodName.isEnabled = false

        binding.tvUpdatePaymentMethod.visibility = View.GONE
        binding.tvEditPaymentMethod.setOnClickListener {
            binding.etPaymentMethodName.isEnabled = true
            binding.etPaymentMethodName.setTextColor(0xffff0000.toInt())
            binding.tvEditPaymentMethod.visibility = View.GONE
            binding.tvUpdatePaymentMethod.visibility = View.VISIBLE
        }

        binding.tvUpdatePaymentMethod.setOnClickListener {
            val paymentMethodName = binding.etPaymentMethodName.text.toString().trim()
            if (paymentMethodName.isEmpty()) {
                binding.etPaymentMethodName.error = getString(R.string.payment_method_name)
                binding.etPaymentMethodName.requestFocus()
            } else {
                val databaseAccess = DatabaseAccess.getInstance(this)
                databaseAccess.open()
                if (databaseAccess.updatePaymentMethod(paymentMethodId, paymentMethodName)) {
                    Toasty.success(this, R.string.successfully_added, Toasty.LENGTH_SHORT).show()
                    val i = Intent(this@EditPaymentMethodActivity, PaymentMethodActivity::class.java)
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
