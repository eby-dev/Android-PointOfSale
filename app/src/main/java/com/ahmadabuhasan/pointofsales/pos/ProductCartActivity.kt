package com.ahmadabuhasan.pointofsales.pos

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivityProductCartBinding
import com.ahmadabuhasan.pointofsales.orders.OrdersActivity
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import com.github.mikephil.charting.utils.Utils
import es.dmoral.toasty.Toasty
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class ProductCartActivity : BaseActivity() {

    private lateinit var binding: ActivityProductCartBinding
    private val locale = Locale("in", "ID")
    private val formatIDR = NumberFormat.getInstance(locale)

    lateinit var customerAdapter: ArrayAdapter<String>
    lateinit var customerNames: MutableList<String>
    lateinit var orderTypeAdapter: ArrayAdapter<String>
    lateinit var orderTypeNames: MutableList<String>
    lateinit var paymentMethodAdapter: ArrayAdapter<String>
    lateinit var paymentMethodNames: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.product_cart)

        binding.cartRecyclerview.layoutManager = LinearLayoutManager(applicationContext)
        binding.cartRecyclerview.setHasFixedSize(true)

        val databaseAccess = DatabaseAccess.getInstance(this)
        databaseAccess.open()
        val cartProductList = databaseAccess.cartProduct as MutableList<HashMap<String, String>>
        if (cartProductList.isEmpty()) {
            binding.tvTotalPrice.visibility = View.GONE
            binding.btnSubmitOrder.visibility = View.GONE
            binding.cartRecyclerview.visibility = View.GONE
            binding.linearLayout.visibility = View.GONE

            binding.tvNoProduct.visibility = View.VISIBLE
            binding.ivNoProduct.visibility = View.VISIBLE
            binding.ivNoProduct.setImageResource(R.drawable.empty_cart)
        } else {
            binding.tvNoProduct.visibility = View.GONE
            binding.ivNoProduct.visibility = View.GONE

            val adapter = CartAdapter(this, cartProductList,
                binding.ivNoProduct,
                binding.tvNoProduct,
                binding.tvTotalPrice,
                binding.btnSubmitOrder)
            binding.cartRecyclerview.adapter = adapter
        }

        binding.btnSubmitOrder.setOnClickListener { dialog() }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun dialog() {
        val databaseAccess1 = DatabaseAccess.getInstance(this)
        databaseAccess1.open()
        val shopData = databaseAccess1.shopInformation
        val shop_currency = shopData[0][Constant.SHOP_CURRENCY]
        val tax = shopData[0][Constant.TAX]
        val getTax = tax!!.toDouble()

        val dialog = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_payment, null)
        dialog.setView(dialogView)
        dialog.setCancelable(false)

        val ibClose: ImageButton = dialogView.findViewById(R.id.ib_close)
        val btnSubmit: Button = dialogView.findViewById(R.id.btn_submit)
        val tvDialogCustomer: TextView = dialogView.findViewById(R.id.tv_dialog_customer)
        val ibCustomer: ImageButton = dialogView.findViewById(R.id.ib_dialog_customer)
        val tvDialogOrderType: TextView = dialogView.findViewById(R.id.tv_dialog_order_type)
        val ibOrderType: ImageButton = dialogView.findViewById(R.id.ib_dialog_order_type)
        val tvDialogPaymentMethod: TextView = dialogView.findViewById(R.id.tv_dialog_order_status)
        val ibPaymentMethod: ImageButton = dialogView.findViewById(R.id.ib_dialog_payment_method)
        val tvDialogTotal: TextView = dialogView.findViewById(R.id.tv_dialog_total)
        val tvDialogTax: TextView = dialogView.findViewById(R.id.tv_dialog_tax)
        val tvTotalTax: TextView = dialogView.findViewById(R.id.tv_dialog_total_tax)
        val etDialogDiscount: EditText = dialogView.findViewById(R.id.et_dialog_discount)
        val tvDialogTotalCost: TextView = dialogView.findViewById(R.id.tv_dialog_total_cost)

        val total_cost = CartAdapter.total_price!!
        val sb = shop_currency + formatIDR.format(total_cost)
        tvDialogTotal.text = sb
        tvDialogTax.text = String.format("%s( %s%%) : ", getString(R.string.tax), tax)

        val calculated_tax = (total_cost * getTax) / 100.0
        tvTotalTax.text = String.format("%s%s", shop_currency, formatIDR.format(calculated_tax))

        val calculated_totalCost = (total_cost + calculated_tax) - Utils.DOUBLE_EPSILON
        tvDialogTotalCost.text = String.format("%s%s", shop_currency, formatIDR.format(calculated_totalCost))

        etDialogDiscount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                val getDiscount = charSequence.toString()
                if (getDiscount.isEmpty() || getDiscount == ".") {
                    val calculated = (total_cost + calculated_tax) - Utils.DOUBLE_EPSILON
                    tvDialogTotalCost.text = String.format("%s%s", shop_currency, formatIDR.format(calculated))
                    return
                }

                val discount_bigger = total_cost + calculated_tax
                val discount = getDiscount.toDouble()
                if (discount > discount_bigger) {
                    etDialogDiscount.error = getString(R.string.discount_cant_be_greater_than_total_price)
                    etDialogDiscount.requestFocus()
                    btnSubmit.visibility = View.INVISIBLE
                    return
                }

                btnSubmit.visibility = View.VISIBLE
                val calculated_withDiscount = (total_cost + calculated_tax) - discount
                tvDialogTotalCost.text = String.format("%s%s", shop_currency, formatIDR.format(calculated_withDiscount))
            }
            override fun afterTextChanged(editable: Editable) {}
        })

        customerNames = ArrayList()
        databaseAccess1.open()
        val customer = databaseAccess1.customers
        for (i in customer.indices) {
            customerNames.add(customer[i][Constant.CUSTOMER_NAME]!!)
        }

        ibCustomer.setOnClickListener {
            customerAdapter = ArrayAdapter(this@ProductCartActivity, android.R.layout.simple_list_item_1)
            customerAdapter.addAll(customerNames)

            val dialog1 = AlertDialog.Builder(this@ProductCartActivity)
            val dialogView1 = layoutInflater.inflate(R.layout.dialog_list_search, null)
            dialog1.setView(dialogView1)
            dialog1.setCancelable(false)

            val title: TextView = dialogView1.findViewById(R.id.tv_dialog_title)
            val search: EditText = dialogView1.findViewById(R.id.et_dialog_search)
            val dialogListView: ListView = dialogView1.findViewById(R.id.dialog_listView)
            val btnCancel: Button = dialogView1.findViewById(R.id.btn_dialog_cancel)

            title.setText(R.string.select_customer)
            dialogListView.isVerticalScrollBarEnabled = true
            dialogListView.adapter = customerAdapter
            search.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                    customerAdapter.filter.filter(charSequence)
                }
                override fun afterTextChanged(editable: Editable) {}
            })
            val alertDialog = dialog1.create()
            btnCancel.setOnClickListener { alertDialog.dismiss() }
            alertDialog.show()
            dialogListView.setOnItemClickListener { _, _, position, _ ->
                alertDialog.dismiss()
                val selectedItem = customerAdapter.getItem(position)
                tvDialogCustomer.text = selectedItem
            }
        }

        orderTypeNames = ArrayList()
        databaseAccess1.open()
        val order_type = databaseAccess1.orderType
        for (i1 in order_type.indices) {
            orderTypeNames.add(order_type[i1][Constant.ORDER_TYPE_NAME]!!)
        }

        ibOrderType.setOnClickListener {
            orderTypeAdapter = ArrayAdapter(this@ProductCartActivity, android.R.layout.simple_list_item_1)
            orderTypeAdapter.addAll(orderTypeNames)

            val dialog2 = AlertDialog.Builder(this@ProductCartActivity)
            val dialogView2 = layoutInflater.inflate(R.layout.dialog_list_search, null)
            dialog2.setView(dialogView2)
            dialog2.setCancelable(false)

            val title: TextView = dialogView2.findViewById(R.id.tv_dialog_title)
            val search: EditText = dialogView2.findViewById(R.id.et_dialog_search)
            val dialogListView: ListView = dialogView2.findViewById(R.id.dialog_listView)
            val btnCancel: Button = dialogView2.findViewById(R.id.btn_dialog_cancel)

            title.setText(R.string.select_order_type)
            dialogListView.isVerticalScrollBarEnabled = true
            dialogListView.adapter = orderTypeAdapter
            search.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                    orderTypeAdapter.filter.filter(charSequence)
                }
                override fun afterTextChanged(editable: Editable) {}
            })
            val alertDialog = dialog2.create()
            btnCancel.setOnClickListener { alertDialog.dismiss() }
            alertDialog.show()
            dialogListView.setOnItemClickListener { _, _, position, _ ->
                alertDialog.dismiss()
                val selectedItem = orderTypeAdapter.getItem(position)
                tvDialogOrderType.text = selectedItem
            }
        }

        paymentMethodNames = ArrayList()
        databaseAccess1.open()
        val payment_method = databaseAccess1.paymentMethod
        for (i2 in payment_method.indices) {
            paymentMethodNames.add(payment_method[i2][Constant.PAYMENT_METHOD_NAME]!!)
        }

        ibPaymentMethod.setOnClickListener {
            paymentMethodAdapter = ArrayAdapter(this@ProductCartActivity, android.R.layout.simple_list_item_1)
            paymentMethodAdapter.addAll(paymentMethodNames)

            val dialog3 = AlertDialog.Builder(this@ProductCartActivity)
            val dialogView3 = layoutInflater.inflate(R.layout.dialog_list_search, null)
            dialog3.setView(dialogView3)
            dialog3.setCancelable(false)

            val title: TextView = dialogView3.findViewById(R.id.tv_dialog_title)
            val search: EditText = dialogView3.findViewById(R.id.et_dialog_search)
            val dialogListView: ListView = dialogView3.findViewById(R.id.dialog_listView)
            val btnCancel: Button = dialogView3.findViewById(R.id.btn_dialog_cancel)

            title.setText(R.string.select_payment_method)
            dialogListView.isVerticalScrollBarEnabled = true
            dialogListView.adapter = paymentMethodAdapter
            search.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                    paymentMethodAdapter.filter.filter(charSequence)
                }
                override fun afterTextChanged(editable: Editable) {}
            })
            val alertDialog = dialog3.create()
            btnCancel.setOnClickListener { alertDialog.dismiss() }
            alertDialog.show()
            dialogListView.setOnItemClickListener { _, _, position, _ ->
                alertDialog.dismiss()
                val selectedItem = paymentMethodAdapter.getItem(position)
                tvDialogPaymentMethod.text = selectedItem
            }
        }

        val closeSubmit = dialog.create()
        ibClose.setOnClickListener { closeSubmit.dismiss() }
        btnSubmit.setOnClickListener {
            val discount: String
            val customer_name = tvDialogCustomer.text.toString().trim()
            val submit_orderType = tvDialogOrderType.text.toString().trim()
            val submit_paymentMethod = tvDialogPaymentMethod.text.toString().trim()
            val disc = etDialogDiscount.text.toString().trim()
            discount = if (disc.isEmpty()) "0.00" else disc
            proceedOrder(customer_name, submit_orderType, submit_paymentMethod, calculated_tax, discount)
            closeSubmit.dismiss()
        }
        closeSubmit.show()
    }

    private fun proceedOrder(customerName: String, orderType: String, paymentMethod: String, tax: Double, discount: String) {
        val productID = Constant.PRODUCT_ID
        val databaseAccess2 = DatabaseAccess.getInstance(this)
        databaseAccess2.open()
        if (databaseAccess2.cartItemCount > 0) {
            databaseAccess2.open()
            val lines = databaseAccess2.cartProduct
            if (lines.isEmpty()) {
                Toasty.error(this, R.string.no_product_found, Toasty.LENGTH_SHORT).show()
            } else {
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
                val currentTime = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date())
                val timeStamp = (System.currentTimeMillis() / 1000).toString()
                Log.d("Time", timeStamp)

                val jsonObject = JSONObject()
                try {
                    jsonObject.put(Constant.ORDER_DATE, currentDate)
                    jsonObject.put(Constant.ORDER_TIME, currentTime)
                    jsonObject.put(Constant.ORDER_TYPE, orderType)
                    jsonObject.put(Constant.ORDER_PAYMENT_METHOD, paymentMethod)
                    jsonObject.put(Constant.CUSTOMER_NAME, customerName)
                    jsonObject.put(Constant.TAX, tax)
                    jsonObject.put(Constant.DISCOUNT, discount)

                    val array = JSONArray()
                    for (i in lines.indices) {
                        databaseAccess2.open()
                        val product_id = lines[i][productID]
                        val product_name = databaseAccess2.getProductName(product_id)

                        databaseAccess2.open()
                        val weight_unit_id = lines[i][Constant.PRODUCT_WEIGHT_UNIT]
                        val weight_unit = databaseAccess2.getWeightUnitName(weight_unit_id)

                        databaseAccess2.open()
                        val product_image = databaseAccess2.getProductImage(product_id)

                        val obj = JSONObject()
                        obj.put(productID, product_id)
                        obj.put(Constant.PRODUCT_NAME, product_name)
                        obj.put(Constant.PRODUCT_WEIGHT, lines[i][Constant.PRODUCT_WEIGHT] + " " + weight_unit)
                        obj.put(Constant.PRODUCT_QTY, lines[i][Constant.PRODUCT_QTY])
                        obj.put(Constant.STOCK, lines[i][Constant.STOCK])
                        obj.put(Constant.PRODUCT_PRICE, lines[i][Constant.PRODUCT_PRICE])
                        obj.put(Constant.PRODUCT_IMAGE, product_image)
                        obj.put(Constant.PRODUCT_ORDER_DATE, currentDate)
                        array.put(obj)
                    }
                    jsonObject.put("lines", array)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                saveOrderInOfflineDb(jsonObject)
            }
        } else {
            Toasty.error(this, R.string.no_product_in_cart, Toasty.LENGTH_SHORT).show()
        }
    }

    private fun saveOrderInOfflineDb(obj: JSONObject) {
        val timeStamp = (System.currentTimeMillis() / 1000).toString()
        val databaseAccess3 = DatabaseAccess.getInstance(this)
        databaseAccess3.open()
        databaseAccess3.insertOrder(timeStamp, obj)
        Toasty.success(this, R.string.order_done_successful, Toasty.LENGTH_SHORT).show()
        val intent = Intent(this, OrdersActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            startActivity(Intent(this, PosActivity::class.java))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
