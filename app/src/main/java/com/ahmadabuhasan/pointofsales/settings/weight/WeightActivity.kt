package com.ahmadabuhasan.pointofsales.settings.weight

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
import com.ahmadabuhasan.pointofsales.databinding.ActivityWeightBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class WeightActivity : BaseActivity() {

    private lateinit var binding: ActivityWeightBinding
    lateinit var databaseAccess: DatabaseAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.weight)

        binding.weightRecyclerview.layoutManager = LinearLayoutManager(applicationContext)
        binding.weightRecyclerview.setHasFixedSize(true)

        databaseAccess = DatabaseAccess.getInstance(this)
        databaseAccess.open()
        val weightData = databaseAccess.productWeight
        Log.d("data", "" + weightData.size)
        if (weightData.size <= 0) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show()
            binding.ivNoWeight.setImageResource(R.drawable.no_data)
        } else {
            binding.ivNoWeight.visibility = View.GONE
            binding.weightRecyclerview.adapter = WeightAdapter(this, weightData)
        }

        binding.etWeUnNameSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                databaseAccess.open()
                val searchWeightList = databaseAccess.searchProductWeight(charSequence.toString())
                if (searchWeightList.size <= 0) {
                    binding.weightRecyclerview.visibility = View.GONE
                    binding.ivNoWeight.visibility = View.VISIBLE
                    binding.ivNoWeight.setImageResource(R.drawable.no_data)
                } else {
                    binding.ivNoWeight.visibility = View.GONE
                    binding.weightRecyclerview.visibility = View.VISIBLE
                    binding.weightRecyclerview.adapter = WeightAdapter(this@WeightActivity, searchWeightList)
                }
            }
            override fun afterTextChanged(editable: Editable) {}
        })

        binding.fabAdd.setOnClickListener { startActivity(Intent(this@WeightActivity, AddWeightActivity::class.java)) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
