package com.ahmadabuhasan.pointofsales.expense;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.ActivityAddExpenseBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class AddExpenseActivity extends BaseActivity {

    private ActivityAddExpenseBinding binding;
    int mYear, mMonth, mDay, mHour, mMinute;
    String time_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_expense);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date());

        this.binding.etExpenseDate.setText(currentDate);
        this.binding.etExpenseTime.setText(currentTime);

        this.binding.etExpenseDate.setOnClickListener(view -> this.datePicker());
        this.binding.etExpenseTime.setOnClickListener(view -> this.timePicker());

        this.binding.tvAddExpense.setOnClickListener(view -> {
            String expense_name = this.binding.etExpenseName.getText().toString();
            String expense_note = this.binding.etExpenseNote.getText().toString();
            String expense_amount = this.binding.etExpenseAmount.getText().toString();
            String expense_date = this.binding.etExpenseDate.getText().toString();
            String expense_time = this.binding.etExpenseTime.getText().toString();

            if (expense_name.isEmpty()) {
                this.binding.etExpenseName.setError(this.getString(R.string.expense_name_cannot_be_empty));
                this.binding.etExpenseName.requestFocus();
            } else if (expense_amount.isEmpty()) {
                this.binding.etExpenseAmount.setError(this.getString(R.string.expense_amount_cannot_be_empty));
                this.binding.etExpenseAmount.requestFocus();
            } else {
                databaseAccess.open();
                if (databaseAccess.addExpense(expense_name, expense_amount, expense_note, expense_date, expense_time)) {
                    Toasty.success(this, R.string.expense_successfully_added, Toasty.LENGTH_SHORT).show();
                    Intent i = new Intent(AddExpenseActivity.this, ExpenseActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    this.startActivity(i);
                } else {
                    Toasty.error(this, R.string.failed, Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void datePicker() {
        Calendar calendar = Calendar.getInstance();
        this.mYear = calendar.get(Calendar.YEAR);
        this.mMonth = calendar.get(Calendar.MONTH);
        this.mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, monthOfYear, dayOfMonth) -> {
            int month = monthOfYear + 1;
            String fm = "" + month;
            String fd = "" + dayOfMonth;
            if (monthOfYear < 9) {
                fm = "0" + month;
            }
            if (dayOfMonth < 10) {
                fd = "0" + dayOfMonth;
            }
            String date_time = year + "-" + fm + "-" + fd;
            System.out.println(date_time);
            AddExpenseActivity.this.binding.etExpenseDate.setText(date_time);
        }, this.mYear, this.mMonth, this.mDay);
        datePickerDialog.show();
    }

    private void timePicker() {
        Calendar calendar = Calendar.getInstance();
        this.mHour = calendar.get(Calendar.HOUR_OF_DAY);
        this.mMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timePicker, hourOfDay, minute) -> {
            String am_pm;
            AddExpenseActivity.this.mHour = hourOfDay;
            AddExpenseActivity.this.mMinute = minute;

            if (mHour == 0) {
                am_pm = "AM";
                if (mMinute < 10) {
                    time_date = "0" + mHour + ":0" + mMinute + " " + am_pm;
                } else {
                    time_date = "0" + mHour + ":" + mMinute + " " + am_pm;
                }
            }

            if (mHour < 12) {
                am_pm = "AM";
                if (mHour < 10) {
                    if (mMinute < 10) {
                        time_date = "0" + mHour + ":0" + mMinute + " " + am_pm;
                    } else {
                        time_date = "0" + mHour + ":" + mMinute + " " + am_pm;
                    }
                } else {
                    if (mMinute < 10) {
                        time_date = mHour + ":0" + mMinute + " " + am_pm;
                    } else {
                        time_date = mHour + ":" + mMinute + " " + am_pm;
                    }
                }
            }

            if (mHour == 12) {
                am_pm = "PM";
                mHour = 0;
                if (mMinute < 10) {
                    time_date = "0" + mHour + ":0" + mMinute + " " + am_pm;
                } else {
                    time_date = "0" + mHour + ":" + mMinute + " " + am_pm;
                }
            }

            if (mHour > 12) {
                am_pm = "PM";
                mHour = hourOfDay - 12;
                if (mHour < 10) {
                    if (mMinute < 10) {
                        time_date = "0" + mHour + ":0" + mMinute + " " + am_pm;
                    } else {
                        time_date = "0" + mHour + ":" + mMinute + " " + am_pm;
                    }
                } else {
                    if (mMinute < 10) {
                        time_date = mHour + ":0" + mMinute + " " + am_pm;
                    } else {
                        time_date = mHour + ":" + mMinute + " " + am_pm;
                    }
                }
            }

            AddExpenseActivity.this.binding.etExpenseTime.setText(time_date);
        }, this.mHour, this.mMinute, false);
        timePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() != android.R.id.home) {
            return super.onOptionsItemSelected(item);
        }
        finish();
        return true;
    }
}