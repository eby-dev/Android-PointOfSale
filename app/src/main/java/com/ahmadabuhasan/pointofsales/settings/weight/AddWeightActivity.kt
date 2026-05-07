package com.ahmadabuhasan.pointofsales.settings.weight

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivityAddWeightBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class AddWeightActivity : BaseActivity() {

    private lateinit var binding: ActivityAddWeightBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWeightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.add_weight)

        binding.tvAddWeight.setOnClickListener {
            val weight_name = binding.etWeUnNameName.text.toString().trim()
            if (weight_name.isEmpty()) {
                binding.etWeUnNameName.error = getString(R.string.enter_weight_name)
                binding.etWeUnNameName.requestFocus()
            } else {
                val databaseAccess = DatabaseAccess.getInstance(this@AddWeightActivity)
                databaseAccess.open()
                if (databaseAccess.addWeight(weight_name)) {
                    Toasty.success(this@AddWeightActivity, R.string.successfully_added, Toasty.LENGTH_SHORT).show()
                    val i = Intent(this@AddWeightActivity, WeightActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                } else {
                    Toasty.error(this@AddWeightActivity, R.string.failed, Toasty.LENGTH_SHORT).show()
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
