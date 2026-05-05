package com.ahmadabuhasan.pointofsales.expense

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivityAddExpenseBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class AddExpenseActivity : BaseActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var mHour = 0
    private var mMinute = 0
    private var timeDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.add_expense)
        }

        val databaseAccess = DatabaseAccess.getInstance(this)

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
        val currentTime = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date())

        binding.etExpenseDate.setText(currentDate)
        binding.etExpenseTime.setText(currentTime)

        binding.etExpenseDate.setOnClickListener { datePicker() }
        binding.etExpenseTime.setOnClickListener { timePicker() }

        binding.tvAddExpense.setOnClickListener {
            val expenseName = binding.etExpenseName.text.toString()
            val expenseNote = binding.etExpenseNote.text.toString()
            val expenseAmount = binding.etExpenseAmount.text.toString()
            val expenseDate = binding.etExpenseDate.text.toString()
            val expenseTime = binding.etExpenseTime.text.toString()

            when {
                expenseName.isEmpty() -> {
                    binding.etExpenseName.error = getString(R.string.expense_name_cannot_be_empty)
                    binding.etExpenseName.requestFocus()
                }
                expenseAmount.isEmpty() -> {
                    binding.etExpenseAmount.error = getString(R.string.expense_amount_cannot_be_empty)
                    binding.etExpenseAmount.requestFocus()
                }
                else -> {
                    databaseAccess.open()
                    if (databaseAccess.addExpense(expenseName, expenseAmount, expenseNote, expenseDate, expenseTime)) {
                        Toasty.success(this, R.string.expense_successfully_added, Toasty.LENGTH_SHORT).show()
                        startActivity(Intent(this, ExpenseActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        })
                    } else {
                        Toasty.error(this, R.string.failed, Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun datePicker() {
        val calendar = Calendar.getInstance()
        mYear = calendar.get(Calendar.YEAR)
        mMonth = calendar.get(Calendar.MONTH)
        mDay = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            val month = monthOfYear + 1
            val fm = if (monthOfYear < 9) "0$month" else "$month"
            val fd = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
            val dateTime = "$year-$fm-$fd"
            println(dateTime)
            binding.etExpenseDate.setText(dateTime)
        }, mYear, mMonth, mDay).show()
    }

    private fun timePicker() {
        val calendar = Calendar.getInstance()
        mHour = calendar.get(Calendar.HOUR_OF_DAY)
        mMinute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, hourOfDay, minute ->
            var amPm: String
            mHour = hourOfDay
            mMinute = minute

            if (mHour == 0) {
                amPm = "AM"
                timeDate = if (mMinute < 10) "0$mHour:0$mMinute $amPm" else "0$mHour:$mMinute $amPm"
            }

            if (mHour < 12) {
                amPm = "AM"
                timeDate = if (mHour < 10) {
                    if (mMinute < 10) "0$mHour:0$mMinute $amPm" else "0$mHour:$mMinute $amPm"
                } else {
                    if (mMinute < 10) "$mHour:0$mMinute $amPm" else "$mHour:$mMinute $amPm"
                }
            }

            if (mHour == 12) {
                amPm = "PM"
                mHour = 0
                timeDate = if (mMinute < 10) "0$mHour:0$mMinute $amPm" else "0$mHour:$mMinute $amPm"
            }

            if (mHour > 12) {
                amPm = "PM"
                mHour = hourOfDay - 12
                timeDate = if (mHour < 10) {
                    if (mMinute < 10) "0$mHour:0$mMinute $amPm" else "0$mHour:$mMinute $amPm"
                } else {
                    if (mMinute < 10) "$mHour:0$mMinute $amPm" else "$mHour:$mMinute $amPm"
                }
            }

            binding.etExpenseTime.setText(timeDate)
        }, mHour, mMinute, false).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId != android.R.id.home) {
            return super.onOptionsItemSelected(item)
        }
        finish()
        return true
    }
}