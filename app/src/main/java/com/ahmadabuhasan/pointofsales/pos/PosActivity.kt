package com.ahmadabuhasan.pointofsales.pos

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmadabuhasan.pointofsales.DashboardActivity
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivityPosBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import com.google.android.gms.ads.AdRequest
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class PosActivity : BaseActivity() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var tvCount: TextView
        @SuppressLint("StaticFieldLeak")
        lateinit var etSearch: EditText
    }

    private lateinit var binding: ActivityPosBinding
    var spanCount: Int = 0
    var posProductAdapter: PosProductAdapter? = null
    var productCategoryAdapter: ProductCategoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // AdMob suspended (see MultiLanguageApp). Hide banner + skip loadAd.
        binding.adViewPos.visibility = View.GONE
        // binding.adViewPos.loadAd(AdRequest.Builder().build())

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.all_product)
            hide()
        }

        binding.ivNoProduct.visibility = View.GONE
        binding.tvNoProduct.visibility = View.GONE

        spanCount = when {
            (resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE -> Configuration.SCREENLAYOUT_SIZE_XLARGE
            (resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL -> Configuration.SCREENLAYOUT_SIZE_NORMAL
            (resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL -> Configuration.SCREENLAYOUT_SIZE_NORMAL
            else -> Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        tvCount = binding.tvCount
        val databaseAccess = DatabaseAccess.getInstance(this)
        databaseAccess.open()
        val count = databaseAccess.cartItemCount
        if (count == 0) {
            tvCount.visibility = View.INVISIBLE
        } else {
            tvCount.visibility = View.VISIBLE
            tvCount.text = count.toString()
        }

        binding.ivBack.setOnClickListener {
            startActivity(Intent(this@PosActivity, DashboardActivity::class.java))
            finish()
        }
        binding.ivCart.setOnClickListener { startActivity(Intent(this@PosActivity, ProductCartActivity::class.java)) }
        binding.ivScanner.setOnClickListener { startActivity(Intent(this@PosActivity, ScannerActivity::class.java)) }

        binding.tvReset.setOnClickListener {
            etSearch.text.clear()
            databaseAccess.open()
            val productList = databaseAccess.products
            if (productList.isEmpty()) {
                binding.posRecyclerview.visibility = View.GONE
                binding.tvNoProduct.visibility = View.VISIBLE
                binding.ivNoProduct.visibility = View.VISIBLE
                binding.ivNoProduct.setImageResource(R.drawable.not_found)
            } else {
                binding.tvNoProduct.visibility = View.GONE
                binding.ivNoProduct.visibility = View.GONE
                binding.posRecyclerview.visibility = View.VISIBLE

                posProductAdapter = PosProductAdapter(this@PosActivity, productList)
                binding.posRecyclerview.adapter = posProductAdapter
            }
        }

        binding.categoryRecyclerview.layoutManager = LinearLayoutManager(this@PosActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.categoryRecyclerview.setHasFixedSize(true)
        databaseAccess.open()
        val categoryData = databaseAccess.productCategory
        Log.d("data", "" + categoryData.size)
        if (categoryData.isEmpty()) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show()
        } else {
            productCategoryAdapter = ProductCategoryAdapter(this@PosActivity, categoryData, binding.posRecyclerview, binding.ivNoProduct, binding.tvNoProduct)
            binding.categoryRecyclerview.adapter = productCategoryAdapter
        }

        val gridLayoutManager = GridLayoutManager(applicationContext, 2)
        binding.posRecyclerview.layoutManager = gridLayoutManager
        binding.posRecyclerview.setHasFixedSize(true)

        etSearch = binding.etSearch
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                databaseAccess.open()
                val searchProductList = databaseAccess.getSearchProducts(charSequence.toString())
                if (searchProductList.size <= 0) {
                    binding.posRecyclerview.visibility = View.GONE
                    binding.tvNoProduct.visibility = View.VISIBLE
                    binding.ivNoProduct.visibility = View.VISIBLE
                    binding.ivNoProduct.setImageResource(R.drawable.not_found)
                } else {
                    binding.tvNoProduct.visibility = View.GONE
                    binding.ivNoProduct.visibility = View.GONE
                    binding.posRecyclerview.visibility = View.VISIBLE

                    posProductAdapter = PosProductAdapter(this@PosActivity, searchProductList)
                    binding.posRecyclerview.adapter = posProductAdapter
                }
            }
            override fun afterTextChanged(editable: Editable) {}
        })

        databaseAccess.open()
        val productList = databaseAccess.products
        if (productList.size <= 0) {
            binding.posRecyclerview.visibility = View.GONE
            binding.tvNoProduct.visibility = View.VISIBLE
            binding.ivNoProduct.visibility = View.VISIBLE
            binding.ivNoProduct.setImageResource(R.drawable.not_found)
        } else {
            binding.tvNoProduct.visibility = View.GONE
            binding.ivNoProduct.visibility = View.GONE
            binding.posRecyclerview.visibility = View.VISIBLE

            posProductAdapter = PosProductAdapter(this, productList)
            binding.posRecyclerview.adapter = posProductAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_cart, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.menu_cart_button -> {
                startActivity(Intent(this, ProductCartActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
