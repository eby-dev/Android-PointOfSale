package com.ahmadabuhasan.pointofsales.settings.categories

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivityEditCategoryBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class EditCategoryActivity : BaseActivity() {

    private lateinit var binding: ActivityEditCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.edit_category)
        }

        val categoryId = intent.getStringExtra(Constant.CATEGORY_ID)

        binding.etCategoryName.setText(intent.getStringExtra(Constant.CATEGORY_NAME))
        binding.etCategoryName.isEnabled = false

        binding.tvUpdateCategory.visibility = View.GONE
        binding.tvEditCategory.setOnClickListener {
            binding.etCategoryName.isEnabled = true
            binding.etCategoryName.setTextColor(0xffff0000.toInt())
            binding.tvEditCategory.visibility = View.GONE
            binding.tvUpdateCategory.visibility = View.VISIBLE
        }

        binding.tvUpdateCategory.setOnClickListener {
            val categoryName = binding.etCategoryName.text.toString().trim()
            if (categoryName.isEmpty()) {
                binding.etCategoryName.error = getString(R.string.enter_category_name)
                binding.etCategoryName.requestFocus()
            } else {
                val databaseAccess = DatabaseAccess.getInstance(this)
                databaseAccess.open()
                if (databaseAccess.updateCategory(categoryId, categoryName)) {
                    Toasty.success(this, R.string.category_updated, Toasty.LENGTH_SHORT).show()
                    val i = Intent(this@EditCategoryActivity, CategoriesActivity::class.java)
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
