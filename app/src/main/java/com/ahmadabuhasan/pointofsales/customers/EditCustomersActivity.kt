package com.ahmadabuhasan.pointofsales.customers

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivityEditCustomersBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class EditCustomersActivity : BaseActivity() {

    private lateinit var binding: ActivityEditCustomersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCustomersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.edit_customer)
        }

        val databaseAccess = DatabaseAccess.getInstance(this)
        val customerId = intent.getStringExtra(Constant.CUSTOMER_ID)

        binding.etCustomerName.setText(intent.getStringExtra(Constant.CUSTOMER_NAME))
        binding.etCustomerCell.setText(intent.getStringExtra(Constant.CUSTOMER_CELL))
        binding.etCustomerEmail.setText(intent.getStringExtra(Constant.CUSTOMER_EMAIL))
        binding.etCustomerAddress.setText(intent.getStringExtra(Constant.CUSTOMER_ADDRESS))

        binding.etCustomerName.isEnabled = false
        binding.etCustomerCell.isEnabled = false
        binding.etCustomerEmail.isEnabled = false
        binding.etCustomerAddress.isEnabled = false

        binding.tvUpdateCustomer.visibility = View.GONE

        binding.tvEditCustomer.setOnClickListener {
            binding.etCustomerName.isEnabled = true
            binding.etCustomerCell.isEnabled = true
            binding.etCustomerEmail.isEnabled = true
            binding.etCustomerAddress.isEnabled = true

            binding.etCustomerName.setTextColor(Color.RED)
            binding.etCustomerCell.setTextColor(Color.RED)
            binding.etCustomerEmail.setTextColor(Color.RED)
            binding.etCustomerAddress.setTextColor(Color.RED)

            binding.tvEditCustomer.visibility = View.GONE
            binding.tvUpdateCustomer.visibility = View.VISIBLE
        }

        binding.tvUpdateCustomer.setOnClickListener {
            val customerName = binding.etCustomerName.text.toString().trim()
            val customerCell = binding.etCustomerCell.text.toString().trim()
            val customerEmail = binding.etCustomerEmail.text.toString().trim()
            val customerAddress = binding.etCustomerAddress.text.toString().trim()

            when {
                customerName.isEmpty() -> {
                    binding.etCustomerName.error = getString(R.string.enter_customer_name)
                    binding.etCustomerName.requestFocus()
                }
                customerCell.isEmpty() -> {
                    binding.etCustomerCell.error = getString(R.string.enter_customer_cell)
                    binding.etCustomerCell.requestFocus()
                }
                !customerEmail.contains("@") || !customerEmail.contains(".") -> {
                    binding.etCustomerEmail.error = getString(R.string.enter_valid_email)
                    binding.etCustomerEmail.requestFocus()
                }
                customerAddress.isEmpty() -> {
                    binding.etCustomerAddress.error = getString(R.string.enter_customer_address)
                    binding.etCustomerAddress.requestFocus()
                }
                else -> {
                    databaseAccess.open()
                    if (databaseAccess.updateCustomer(customerId, customerName, customerCell, customerEmail, customerAddress)) {
                        Toasty.success(this, R.string.update_successfully, Toasty.LENGTH_SHORT).show()
                        startActivity(Intent(this, CustomersActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        })
                    } else {
                        Toasty.error(this, R.string.failed, Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}