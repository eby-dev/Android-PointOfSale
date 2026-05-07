package com.ahmadabuhasan.pointofsales.orders

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivityOrderDetailsBinding
import com.ahmadabuhasan.pointofsales.pdf_report.BarCodeEncoder
import com.ahmadabuhasan.pointofsales.pdf_report.TemplatePDF
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import com.ahmadabuhasan.pointofsales.utils.IPrintToPrinter
import com.ahmadabuhasan.pointofsales.utils.PrefMng
import com.ahmadabuhasan.pointofsales.utils.Tools
import com.ahmadabuhasan.pointofsales.utils.WoosimPrnMng
import com.ahmadabuhasan.pointofsales.utils.printerFactory
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import es.dmoral.toasty.Toasty
import java.text.DecimalFormat
import java.util.ArrayList

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class OrderDetailsActivity : BaseActivity() {

    companion object {
        private const val REQUEST_CONNECT = 100
    }

    private lateinit var binding: ActivityOrderDetailsBinding

    lateinit var order_id: String
    lateinit var customer_name: String
    lateinit var order_date: String
    lateinit var order_time: String
    lateinit var tax: String
    lateinit var discount: String
    lateinit var shop_name: String
    lateinit var shop_contact: String
    lateinit var shop_email: String
    lateinit var shop_address: String
    lateinit var shop_currency: String
    lateinit var receiptCustomerName: String
    lateinit var receiptThanks: String
    var getTax: Double = 0.0
    var getDiscount: Double = 0.0
    var total_price: Double = 0.0
    var calculated_TotalPrice: Double = 0.0

    private var mPrnMng: WoosimPrnMng? = null
    var bm: Bitmap? = null
    val decimalFormat = DecimalFormat("#0.00")
    lateinit var databaseAccess: DatabaseAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.order_details)

        binding.ivNoOrder.visibility = View.GONE
        binding.tvNoOrder.visibility = View.GONE

        order_id = intent.extras!!.getString(Constant.ORDER_ID)!!
        customer_name = intent.extras!!.getString(Constant.CUSTOMER_NAME)!!
        order_date = intent.extras!!.getString(Constant.ORDER_DATE)!!
        order_time = intent.extras!!.getString(Constant.ORDER_TIME)!!
        tax = intent.extras!!.getString(Constant.TAX)!!
        discount = intent.extras!!.getString(Constant.DISCOUNT)!!

        binding.orderDetailsRecyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.orderDetailsRecyclerview.setHasFixedSize(true)

        databaseAccess = DatabaseAccess.getInstance(this)
        databaseAccess.open()
        val orderDetailsList = databaseAccess.getOrderDetailsList(order_id)
        if (orderDetailsList.isEmpty()) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show()
        } else {
            val adapter = OrderDetailsAdapter(this@OrderDetailsActivity, orderDetailsList)
            binding.orderDetailsRecyclerview.adapter = adapter
        }

        databaseAccess.open()
        val shopData = databaseAccess.shopInformation
        shop_name = shopData[0][Constant.SHOP_NAME]!!
        shop_contact = shopData[0][Constant.SHOP_CONTACT]!!
        shop_email = shopData[0][Constant.SHOP_EMAIL]!!
        shop_address = shopData[0][Constant.SHOP_ADDRESS]!!
        shop_currency = shopData[0][Constant.SHOP_CURRENCY]!!

        databaseAccess.open()
        total_price = databaseAccess.totalOrderPrice(order_id)
        getTax = tax.toDouble()
        getDiscount = discount.toDouble()
        calculated_TotalPrice = (total_price + getTax) - getDiscount

        binding.tvTotalPrice.text = String.format("%s%s%s", getString(R.string.sub_total), shop_currency, decimalFormat.format(total_price))
        binding.tvTax.text = String.format("%s : %s%s", getString(R.string.total_tax), shop_currency, decimalFormat.format(getTax))
        binding.tvDiscount.text = String.format("%s : %s%s", getString(R.string.discount), shop_currency, decimalFormat.format(getDiscount))
        binding.tvTotalCost.text = String.format("%s%s%s", getString(R.string.total_price), shop_currency, decimalFormat.format(calculated_TotalPrice))

        receiptCustomerName = "Customer Name: Mr/Mrs. $customer_name"
        receiptThanks = "Thanks for purchase. Visit again"
        val templatePDF = TemplatePDF(application)
        templatePDF.openDocument()
        templatePDF.addMetaData("Order Receipt", "Order Receipt", "Point Of Sale")
        templatePDF.addTitle(
            shop_name,
            shop_address
                    + "\n Email: " + shop_email
                    + "\n Contact: " + shop_contact
                    + "\n Invoice ID: " + order_id,
            "$order_time $order_date"
        )
        templatePDF.addParagraph(receiptCustomerName)

        val barCodeEncoder = BarCodeEncoder()
        try {
            bm = barCodeEncoder.encodeAsBitmap(order_id, BarcodeFormat.CODE_128, 600, 300)
        } catch (e: WriterException) {
            Log.d("Data", e.toString())
        }

        val header = arrayOf("Description", "Price")
        binding.btnPdfReceipt.setOnClickListener {
            templatePDF.createTable(header, ordersData)
            templatePDF.addRightParagraph(receiptThanks)
            templatePDF.addImage(bm)
            templatePDF.closeDocument()
            templatePDF.viewPDF()
        }

        binding.btnThermalPrinter.setOnClickListener {
            if (Tools.isBlueToothOn(this)) {
                PrefMng.saveActivePrinter(this, PrefMng.PRN_WOOSIM_SELECTED)
                val i = Intent(this@OrderDetailsActivity, DeviceListActivity::class.java)
                startActivityForResult(i, REQUEST_CONNECT)
            }
        }
    }

    val ordersData: ArrayList<Array<String>>
        get() {
            val rows = ArrayList<Array<String>>()
            databaseAccess.open()
            val orderDetailsList = databaseAccess.getOrderDetailsList(order_id)
            for (i in orderDetailsList.indices) {
                val name = orderDetailsList[i][Constant.PRODUCT_NAME]
                val weight = orderDetailsList[i][Constant.PRODUCT_WEIGHT]
                val price = orderDetailsList[i][Constant.PRODUCT_PRICE]
                val qty = orderDetailsList[i][Constant.PRODUCT_QTY]

                val cost_total = price!!.toDouble() * qty!!.toInt()

                rows.add(arrayOf(
                    name + "\n" +
                            weight + "\n(" +
                            qty + "x" +
                            shop_currency + price + ")",
                    shop_currency + decimalFormat.format(cost_total)
                ))
            }
            rows.add(arrayOf("..........................................", ".................................."))
            rows.add(arrayOf("Sub Total: ", shop_currency + decimalFormat.format(total_price)))
            rows.add(arrayOf("Total Tax: ", shop_currency + decimalFormat.format(getTax)))
            rows.add(arrayOf("Discount: ", shop_currency + decimalFormat.format(getDiscount)))
            rows.add(arrayOf("..........................................", ".................................."))
            rows.add(arrayOf("Total Price: ", shop_currency + decimalFormat.format(calculated_TotalPrice)))
            return rows
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CONNECT && resultCode == RESULT_OK) {
            try {
                val blutoothAddr = data!!.extras!!.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS)
                val testPrinter: IPrintToPrinter = TestPrinter(this, shop_name, shop_address, shop_email, shop_contact,
                    order_id, order_date, order_time, receiptCustomerName, receiptThanks,
                    total_price, calculated_TotalPrice, tax, discount, shop_currency)
                mPrnMng = printerFactory.createPrnMng(this, blutoothAddr, testPrinter)
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        if (mPrnMng != null) mPrnMng!!.releaseAllocatoins()
        super.onDestroy()
    }
}
