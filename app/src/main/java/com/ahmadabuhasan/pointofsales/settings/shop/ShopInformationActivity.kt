package com.ahmadabuhasan.pointofsales.settings.shop

import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivityShopInformationBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class ShopInformationActivity : BaseActivity() {

    private lateinit var binding: ActivityShopInformationBinding
    lateinit var databaseAccess: DatabaseAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.shop_information)
        }

        databaseAccess = DatabaseAccess.getInstance(this)
        databaseAccess.open()
        val shopData = databaseAccess.shopInformation

        binding.etShopName.setText(shopData[0][Constant.SHOP_NAME])
        binding.etShopContact.setText(shopData[0][Constant.SHOP_CONTACT])
        binding.etShopEmail.setText(shopData[0][Constant.SHOP_EMAIL])
        binding.etShopAddress.setText(shopData[0][Constant.SHOP_ADDRESS])
        binding.etShopCurrency.setText(shopData[0][Constant.SHOP_CURRENCY])
        binding.etTax.setText(shopData[0][Constant.TAX])

        binding.etShopName.isEnabled = false
        binding.etShopContact.isEnabled = false
        binding.etShopEmail.isEnabled = false
        binding.etShopAddress.isEnabled = false
        binding.etShopCurrency.isEnabled = false
        binding.etTax.isEnabled = false

        binding.tvShopUpdate.visibility = View.GONE
        binding.tvShopEdit.setOnClickListener {
            binding.etShopName.isEnabled = true
            binding.etShopContact.isEnabled = true
            binding.etShopEmail.isEnabled = true
            binding.etShopAddress.isEnabled = true
            binding.etShopCurrency.isEnabled = true
            binding.etTax.isEnabled = true

            binding.etShopName.setTextColor(0xffff0000.toInt())
            binding.etShopContact.setTextColor(0xffff0000.toInt())
            binding.etShopEmail.setTextColor(0xffff0000.toInt())
            binding.etShopAddress.setTextColor(0xffff0000.toInt())
            binding.etShopCurrency.setTextColor(0xffff0000.toInt())
            binding.etTax.setTextColor(0xffff0000.toInt())

            binding.tvShopUpdate.visibility = View.VISIBLE
            binding.tvShopEdit.visibility = View.GONE
        }

        binding.tvShopUpdate.setOnClickListener {
            val shopName = binding.etShopName.text.toString().trim()
            val shopContact = binding.etShopContact.text.toString().trim()
            val shopEmail = binding.etShopEmail.text.toString().trim()
            val shopAddress = binding.etShopAddress.text.toString().trim()
            val shopCurrency = binding.etShopCurrency.text.toString().trim()
            val tax = binding.etTax.text.toString().trim()

            if (shopName.isEmpty()) {
                binding.etShopName.error = getString(R.string.shop_name_cannot_be_empty)
                binding.etShopName.requestFocus()
            } else if (shopContact.isEmpty()) {
                binding.etShopContact.error = getString(R.string.shop_contact_cannot_be_empty)
                binding.etShopContact.requestFocus()
            } else if (shopEmail.isEmpty() || !shopEmail.contains("@") || !shopEmail.contains(".")) {
                binding.etShopEmail.error = getString(R.string.enter_valid_email)
                binding.etShopEmail.requestFocus()
            } else if (shopAddress.isEmpty()) {
                binding.etShopAddress.error = getString(R.string.shop_address_cannot_be_empty)
                binding.etShopAddress.requestFocus()
            } else if (shopCurrency.isEmpty()) {
                binding.etShopCurrency.error = getString(R.string.shop_currency_cannot_be_empty)
                binding.etShopCurrency.requestFocus()
            } else if (tax.isEmpty()) {
                binding.etTax.error = getString(R.string.tax_in_percentage)
                binding.etTax.requestFocus()
            } else {
                Toasty.warning(this, Html.fromHtml(getString(R.string.purchase_pro_message)), Toasty.LENGTH_LONG).show()

                /*databaseAccess.open()
                val check = databaseAccess.updateShopInformation(shop_name, shop_contact, shop_email, shop_address, shop_currency, tax)
                if (check) {
                    Toasty.success(this, R.string.shop_information_updated_successfully, Toasty.LENGTH_SHORT).show()
                    val i = Intent(this@ShopInformationActivity, SettingsActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                } else {
                    Toasty.error(this, R.string.failed, Toasty.LENGTH_SHORT).show()
                }*/
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
