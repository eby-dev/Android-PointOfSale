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

    lateinit var orderId: String
    lateinit var customerName: String
    lateinit var orderDate: String
    lateinit var orderTime: String
    lateinit var tax: String
    lateinit var discount: String
    lateinit var shopName: String
    lateinit var shopContact: String
    lateinit var shopEmail: String
    lateinit var shopAddress: String
    lateinit var shopCurrency: String
    lateinit var receiptCustomerName: String
    lateinit var receiptThanks: String
    var taxAmount: Double = 0.0
    var discountAmount: Double = 0.0
    var totalPrice: Double = 0.0
    var calculatedTotalPrice: Double = 0.0

    private var mPrnMng: WoosimPrnMng? = null
    var bm: Bitmap? = null
    val decimalFormat = DecimalFormat("#0.00")
    lateinit var databaseAccess: DatabaseAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.order_details)
        }

        binding.ivNoOrder.visibility = View.GONE
        binding.tvNoOrder.visibility = View.GONE

        orderId = intent.getStringExtra(Constant.ORDER_ID).orEmpty()
        customerName = intent.getStringExtra(Constant.CUSTOMER_NAME).orEmpty()
        orderDate = intent.getStringExtra(Constant.ORDER_DATE).orEmpty()
        orderTime = intent.getStringExtra(Constant.ORDER_TIME).orEmpty()
        tax = intent.getStringExtra(Constant.TAX).orEmpty()
        discount = intent.getStringExtra(Constant.DISCOUNT).orEmpty()

        binding.orderDetailsRecyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.orderDetailsRecyclerview.setHasFixedSize(true)

        databaseAccess = DatabaseAccess.getInstance(this)
        databaseAccess.open()
        val orderDetailsList = databaseAccess.getOrderDetailsList(orderId)
        if (orderDetailsList.isEmpty()) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show()
        } else {
            val adapter = OrderDetailsAdapter(this@OrderDetailsActivity, orderDetailsList)
            binding.orderDetailsRecyclerview.adapter = adapter
        }

        databaseAccess.open()
        val shopData = databaseAccess.shopInformation
        shopName = shopData[0][Constant.SHOP_NAME].orEmpty()
        shopContact = shopData[0][Constant.SHOP_CONTACT].orEmpty()
        shopEmail = shopData[0][Constant.SHOP_EMAIL].orEmpty()
        shopAddress = shopData[0][Constant.SHOP_ADDRESS].orEmpty()
        shopCurrency = shopData[0][Constant.SHOP_CURRENCY].orEmpty()

        databaseAccess.open()
        totalPrice = databaseAccess.totalOrderPrice(orderId)
        taxAmount = tax.toDouble()
        discountAmount = discount.toDouble()
        calculatedTotalPrice = (totalPrice + taxAmount) - discountAmount

        binding.tvTotalPrice.text = String.format("%s%s%s", getString(R.string.sub_total), shopCurrency, decimalFormat.format(totalPrice))
        binding.tvTax.text = String.format("%s : %s%s", getString(R.string.total_tax), shopCurrency, decimalFormat.format(taxAmount))
        binding.tvDiscount.text = String.format("%s : %s%s", getString(R.string.discount), shopCurrency, decimalFormat.format(discountAmount))
        binding.tvTotalCost.text = String.format("%s%s%s", getString(R.string.total_price), shopCurrency, decimalFormat.format(calculatedTotalPrice))

        receiptCustomerName = "Customer Name: Mr/Mrs. $customerName"
        receiptThanks = "Thanks for purchase. Visit again"
        val templatePDF = TemplatePDF(application)
        templatePDF.openDocument()
        templatePDF.addMetaData("Order Receipt", "Order Receipt", "Point Of Sale")
        templatePDF.addTitle(
            shopName,
            shopAddress
                    + "\n Email: " + shopEmail
                    + "\n Contact: " + shopContact
                    + "\n Invoice ID: " + orderId,
            "$orderTime $orderDate"
        )
        templatePDF.addParagraph(receiptCustomerName)

        val barCodeEncoder = BarCodeEncoder()
        try {
            bm = barCodeEncoder.encodeAsBitmap(orderId, BarcodeFormat.CODE_128, 600, 300)
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
            val orderDetailsList = databaseAccess.getOrderDetailsList(orderId)
            for (i in orderDetailsList.indices) {
                val name = orderDetailsList[i][Constant.PRODUCT_NAME]
                val weight = orderDetailsList[i][Constant.PRODUCT_WEIGHT]
                val price = orderDetailsList[i][Constant.PRODUCT_PRICE]
                val qty = orderDetailsList[i][Constant.PRODUCT_QTY]

                val costTotal = price.orEmpty().toDouble() * qty.orEmpty().toInt()

                rows.add(arrayOf(
                    name + "\n" +
                            weight + "\n(" +
                            qty + "x" +
                            shopCurrency + price + ")",
                    shopCurrency + decimalFormat.format(costTotal)
                ))
            }
            rows.add(arrayOf("..........................................", ".................................."))
            rows.add(arrayOf("Sub Total: ", shopCurrency + decimalFormat.format(totalPrice)))
            rows.add(arrayOf("Total Tax: ", shopCurrency + decimalFormat.format(taxAmount)))
            rows.add(arrayOf("Discount: ", shopCurrency + decimalFormat.format(discountAmount)))
            rows.add(arrayOf("..........................................", ".................................."))
            rows.add(arrayOf("Total Price: ", shopCurrency + decimalFormat.format(calculatedTotalPrice)))
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
                val blutoothAddr = data?.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS).orEmpty()
                val testPrinter: IPrintToPrinter = TestPrinter(this, shopName, shopAddress, shopEmail, shopContact,
                    orderId, orderDate, orderTime, receiptCustomerName, receiptThanks,
                    totalPrice, calculatedTotalPrice, tax, discount, shopCurrency)
                mPrnMng = printerFactory.createPrnMng(this, blutoothAddr, testPrinter)
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        mPrnMng?.releaseAllocatoins()
        super.onDestroy()
    }
}
