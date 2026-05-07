package com.ahmadabuhasan.pointofsales.settings.payment_method

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivityAddPaymentMethodBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class AddPaymentMethodActivity : BaseActivity() {

    private lateinit var binding: ActivityAddPaymentMethodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.add_payment_method)

        binding.tvAddPaymentMethod.setOnClickListener {
            val payment_method_name = binding.etPaymentMethodName.text.toString().trim()
            if (payment_method_name.isEmpty()) {
                binding.etPaymentMethodName.error = getString(R.string.enter_payment_method_name)
                binding.etPaymentMethodName.requestFocus()
            } else {
                val databaseAccess = DatabaseAccess.getInstance(this)
                databaseAccess.open()
                if (databaseAccess.addPaymentMethod(payment_method_name)) {
                    Toasty.success(this, R.string.successfully_added, Toasty.LENGTH_SHORT).show()
                    val i = Intent(this@AddPaymentMethodActivity, PaymentMethodActivity::class.java)
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
