package com.ahmadabuhasan.pointofsales.settings.categories

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivityAddCategoryBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class AddCategoryActivity : BaseActivity() {

    private lateinit var binding: ActivityAddCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.add_category)
        }

        binding.tvAddCategory.setOnClickListener {
            val categoryName = binding.etCategoryName.text.toString().trim()
            if (categoryName.isEmpty()) {
                binding.etCategoryName.error = getString(R.string.enter_category_name)
                binding.etCategoryName.requestFocus()
            } else {
                val databaseAccess = DatabaseAccess.getInstance(this)
                databaseAccess.open()
                if (databaseAccess.addCategory(categoryName)) {
                    Toasty.success(this, R.string.category_added_successfully, Toasty.LENGTH_SHORT).show()
                    val i = Intent(this@AddCategoryActivity, CategoriesActivity::class.java)
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
