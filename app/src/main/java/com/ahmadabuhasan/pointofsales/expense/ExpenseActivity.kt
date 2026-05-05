package com.ahmadabuhasan.pointofsales.expense

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
import com.ahmadabuhasan.pointofsales.databinding.ActivityExpenseBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class ExpenseActivity : BaseActivity() {

    private lateinit var binding: ActivityExpenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Utils().interstitialAdsShow(this)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.all_expense)
        }

        binding.expenseRecyclerview.layoutManager = LinearLayoutManager(applicationContext)
        binding.expenseRecyclerview.setHasFixedSize(true)

        val databaseAccess = DatabaseAccess.getInstance(this)
        databaseAccess.open()
        val expenseData = databaseAccess.allExpense
        Log.d("data", "${expenseData.size}")

        if (expenseData.size <= 0) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show()
            binding.ivNoExpense.setImageResource(R.drawable.no_data)
        } else {
            binding.ivNoExpense.visibility = View.GONE
            binding.expenseRecyclerview.adapter = ExpenseAdapter(this, expenseData)
        }

        binding.etExpenseSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                databaseAccess.open()
                val searchExpenseList = databaseAccess.searchExpense(s.toString())
                if (searchExpenseList.size <= 0) {
                    binding.expenseRecyclerview.visibility = View.GONE
                    binding.ivNoExpense.visibility = View.VISIBLE
                    binding.ivNoExpense.setImageResource(R.drawable.no_data)
                } else {
                    binding.ivNoExpense.visibility = View.GONE
                    binding.expenseRecyclerview.visibility = View.VISIBLE
                    binding.expenseRecyclerview.adapter = ExpenseAdapter(this@ExpenseActivity, searchExpenseList)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
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