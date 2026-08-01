package com.ahmadabuhasan.pointofsales.settings

import android.os.Bundle
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivitySettingsBinding
import com.ahmadabuhasan.pointofsales.settings.backup.BackupActivity
import com.ahmadabuhasan.pointofsales.settings.categories.CategoriesActivity
import com.ahmadabuhasan.pointofsales.settings.order_type.DeliveryActivity
import com.ahmadabuhasan.pointofsales.settings.payment_method.PaymentMethodActivity
import com.ahmadabuhasan.pointofsales.settings.shop.ShopInformationActivity
import com.ahmadabuhasan.pointofsales.settings.weight.WeightActivity
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import com.ahmadabuhasan.pointofsales.utils.Utils
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.action_settings)
        }

        // AdMob suspended (see MultiLanguageApp). Skip interstitial.
        // Utils().interstitialAdsShow(this)

        binding.cvShopInfo.setOnClickListener { startActivity(Intent(this@SettingsActivity, ShopInformationActivity::class.java)) }
        binding.cvCategory.setOnClickListener { startActivity(Intent(this@SettingsActivity, CategoriesActivity::class.java)) }
        binding.cvWeight.setOnClickListener { startActivity(Intent(this@SettingsActivity, WeightActivity::class.java)) }
        binding.cvDelivery.setOnClickListener { startActivity(Intent(this@SettingsActivity, DeliveryActivity::class.java)) }
        binding.cvPaymentMethod.setOnClickListener { startActivity(Intent(this@SettingsActivity, PaymentMethodActivity::class.java)) }
        binding.cvBackup.setOnClickListener { startActivity(Intent(this@SettingsActivity, BackupActivity::class.java)) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        } else if (item.itemId == R.id.menu_reset) {
            val databaseAccess = DatabaseAccess.getInstance(this)
            databaseAccess.open()
            AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(getString(R.string.confirmation))
                .setMessage(getString(R.string.are_you_sure_you_want_to_delete_all_data))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    databaseAccess.clearAllData()
                    Toasty.success(this, R.string.all_data_deleted_successfully, Toasty.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    databaseAccess.close()
                    dialog.dismiss()
                }
                .show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.setting_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
