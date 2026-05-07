package com.ahmadabuhasan.pointofsales.settings.categories

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
import com.ahmadabuhasan.pointofsales.databinding.ActivityCategoriesBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class CategoriesActivity : BaseActivity() {

    private lateinit var binding: ActivityCategoriesBinding
    lateinit var databaseAccess: DatabaseAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.categories)

        binding.categoryRecyclerview.layoutManager = LinearLayoutManager(applicationContext)
        binding.categoryRecyclerview.setHasFixedSize(true)

        databaseAccess = DatabaseAccess.getInstance(this)
        databaseAccess.open()
        val categoryData = databaseAccess.productCategory
        Log.d("data", "" + categoryData.size)
        if (categoryData.size <= 0) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show()
            binding.ivNoCategory.setImageResource(R.drawable.no_data)
        } else {
            binding.ivNoCategory.visibility = View.GONE
            binding.categoryRecyclerview.adapter = CategoryAdapter(this, categoryData)
        }

        binding.etCategorySearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                databaseAccess.open()
                val searchCategoryList = databaseAccess.searchProductCategory(charSequence.toString())
                if (searchCategoryList.size <= 0) {
                    binding.categoryRecyclerview.visibility = View.GONE
                    binding.ivNoCategory.visibility = View.VISIBLE
                    binding.ivNoCategory.setImageResource(R.drawable.no_data)
                } else {
                    binding.ivNoCategory.visibility = View.GONE
                    binding.categoryRecyclerview.visibility = View.VISIBLE
                    binding.categoryRecyclerview.adapter = CategoryAdapter(this@CategoriesActivity, searchCategoryList)
                }
            }
            override fun afterTextChanged(editable: Editable) {}
        })

        binding.fabAdd.setOnClickListener { startActivity(Intent(this@CategoriesActivity, AddCategoryActivity::class.java)) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
