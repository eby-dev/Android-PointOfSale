package com.ahmadabuhasan.pointofsales.settings.weight

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.internal.view.SupportMenu
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivityEditWeightBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class EditWeightActivity : BaseActivity() {

    private lateinit var binding: ActivityEditWeightBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditWeightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.update_weight)

        val weight_id = intent.extras!!.getString(Constant.WEIGHT_ID)

        binding.etWeUnNameName.setText(intent.extras!!.getString(Constant.WEIGHT_UNIT))
        binding.etWeUnNameName.isEnabled = false

        binding.tvUpdateWeight.visibility = View.GONE
        binding.tvEditWeight.setOnClickListener {
            binding.etWeUnNameName.isEnabled = true
            binding.etWeUnNameName.setTextColor(SupportMenu.CATEGORY_MASK)
            binding.tvEditWeight.visibility = View.GONE
            binding.tvUpdateWeight.visibility = View.VISIBLE
        }

        binding.tvUpdateWeight.setOnClickListener {
            val weight_name = binding.etWeUnNameName.text.toString().trim()
            if (weight_name.isEmpty()) {
                binding.etWeUnNameName.error = getString(R.string.enter_weight_name)
                binding.etWeUnNameName.requestFocus()
            } else {
                val databaseAccess = DatabaseAccess.getInstance(this@EditWeightActivity)
                databaseAccess.open()
                if (databaseAccess.updateWeight(weight_id, weight_name)) {
                    Toasty.success(this@EditWeightActivity, R.string.successfully_updated, Toasty.LENGTH_SHORT).show()
                    val i = Intent(this@EditWeightActivity, WeightActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                } else {
                    Toasty.error(this@EditWeightActivity, R.string.failed, Toasty.LENGTH_SHORT).show()
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
