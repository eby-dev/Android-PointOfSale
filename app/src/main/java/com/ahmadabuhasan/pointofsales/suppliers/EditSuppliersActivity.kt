package com.ahmadabuhasan.pointofsales.suppliers

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivityEditSuppliersBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class EditSuppliersActivity : BaseActivity() {

    private lateinit var binding: ActivityEditSuppliersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditSuppliersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.edit_suppliers)
        }

        val supplierId = intent.getStringExtra(Constant.SUPPLIERS_ID)

        binding.etSupplierName.setText(intent.getStringExtra(Constant.SUPPLIERS_NAME))
        binding.etSupplierContactName.setText(intent.getStringExtra(Constant.SUPPLIERS_CONTACT_PERSON))
        binding.etSupplierCell.setText(intent.getStringExtra(Constant.SUPPLIERS_CELL))
        binding.etSupplierEmail.setText(intent.getStringExtra(Constant.SUPPLIERS_EMAIL))
        binding.etSupplierAddress.setText(intent.getStringExtra(Constant.SUPPLIERS_ADDRESS))

        binding.etSupplierName.isEnabled = false
        binding.etSupplierContactName.isEnabled = false
        binding.etSupplierCell.isEnabled = false
        binding.etSupplierEmail.isEnabled = false
        binding.etSupplierAddress.isEnabled = false

        binding.tvUpdateSupplier.visibility = View.GONE

        binding.tvEditSupplier.setOnClickListener {
            binding.etSupplierName.isEnabled = true
            binding.etSupplierContactName.isEnabled = true
            binding.etSupplierCell.isEnabled = true
            binding.etSupplierEmail.isEnabled = true
            binding.etSupplierAddress.isEnabled = true

            binding.etSupplierName.setTextColor(Color.RED)
            binding.etSupplierContactName.setTextColor(Color.RED)
            binding.etSupplierCell.setTextColor(Color.RED)
            binding.etSupplierEmail.setTextColor(Color.RED)
            binding.etSupplierAddress.setTextColor(Color.RED)

            binding.tvEditSupplier.visibility = View.GONE
            binding.tvUpdateSupplier.visibility = View.VISIBLE
        }

        binding.tvUpdateSupplier.setOnClickListener {
            val supplierName = binding.etSupplierName.text.toString().trim()
            val supplierContactName = binding.etSupplierContactName.text.toString().trim()
            val supplierCell = binding.etSupplierCell.text.toString().trim()
            val supplierEmail = binding.etSupplierEmail.text.toString().trim()
            val supplierAddress = binding.etSupplierAddress.text.toString().trim()

            when {
                supplierName.isEmpty() -> {
                    binding.etSupplierName.error = getString(R.string.enter_suppliers_name)
                    binding.etSupplierName.requestFocus()
                }
                supplierContactName.isEmpty() -> {
                    binding.etSupplierContactName.error = getString(R.string.enter_suppliers_contact_person_name)
                    binding.etSupplierContactName.requestFocus()
                }
                supplierCell.isEmpty() -> {
                    binding.etSupplierCell.error = getString(R.string.enter_suppliers_cell)
                    binding.etSupplierCell.requestFocus()
                }
                supplierEmail.isEmpty() || !supplierEmail.contains("@") || !supplierEmail.contains(".") -> {
                    binding.etSupplierEmail.error = getString(R.string.enter_valid_email)
                    binding.etSupplierEmail.requestFocus()
                }
                supplierAddress.isEmpty() -> {
                    binding.etSupplierAddress.error = getString(R.string.enter_suppliers_address)
                    binding.etSupplierAddress.requestFocus()
                }
                else -> {
                    val databaseAccess = DatabaseAccess.getInstance(this)
                    databaseAccess.open()
                    if (databaseAccess.updateSuppliers(supplierId, supplierName, supplierContactName, supplierCell, supplierEmail, supplierAddress)) {
                        Toasty.success(this, R.string.update_successfully, Toasty.LENGTH_SHORT).show()
                        startActivity(Intent(this, SuppliersActivity::class.java).apply {
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