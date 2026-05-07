package com.ahmadabuhasan.pointofsales.settings.payment_method

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
import com.ahmadabuhasan.pointofsales.databinding.ActivityPaymentMethodBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class PaymentMethodActivity : BaseActivity() {

    private lateinit var binding: ActivityPaymentMethodBinding
    lateinit var databaseAccess: DatabaseAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.all_payment_method)

        binding.paymentMethodRecyclerview.layoutManager = LinearLayoutManager(applicationContext)
        binding.paymentMethodRecyclerview.setHasFixedSize(true)

        databaseAccess = DatabaseAccess.getInstance(this)
        databaseAccess.open()
        val paymentMethodData = databaseAccess.paymentMethod
        Log.d("data", "" + paymentMethodData.size)
        if (paymentMethodData.size <= 0) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show()
            binding.ivNoPaymentMethod.setImageResource(R.drawable.no_data)
        } else {
            binding.ivNoPaymentMethod.visibility = View.GONE
            val adapter = PaymentMethodAdapter(this, paymentMethodData)
            binding.paymentMethodRecyclerview.adapter = adapter
        }

        binding.etPaymentMethodSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                databaseAccess.open()
                val searchPaymentMethodList = databaseAccess.searchPaymentMethod(charSequence.toString())
                if (searchPaymentMethodList.size <= 0) {
                    binding.paymentMethodRecyclerview.visibility = View.GONE
                    binding.ivNoPaymentMethod.visibility = View.VISIBLE
                    binding.ivNoPaymentMethod.setImageResource(R.drawable.no_data)
                } else {
                    binding.paymentMethodRecyclerview.visibility = View.VISIBLE
                    binding.ivNoPaymentMethod.visibility = View.GONE
                    val adapter = PaymentMethodAdapter(this@PaymentMethodActivity, searchPaymentMethodList)
                    binding.paymentMethodRecyclerview.adapter = adapter
                }
            }
            override fun afterTextChanged(editable: Editable) {}
        })

        binding.fabAdd.setOnClickListener { startActivity(Intent(this@PaymentMethodActivity, AddPaymentMethodActivity::class.java)) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
