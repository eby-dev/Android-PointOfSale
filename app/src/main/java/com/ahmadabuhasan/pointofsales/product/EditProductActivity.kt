package com.ahmadabuhasan.pointofsales.product

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.internal.view.SupportMenu
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ActivityEditProductBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import es.dmoral.toasty.Toasty
import `in`.mayanknagwanshi.imagepicker.ImageSelectActivity
import java.io.ByteArrayOutputStream

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class EditProductActivity : BaseActivity() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var etProductCode: EditText
    }

    private lateinit var binding: ActivityEditProductBinding

    var categoryAdapter: ArrayAdapter<String>? = null
    lateinit var categoryNames: MutableList<String>
    var selectedCategoryID: String? = null

    var weightUnitAdapter: ArrayAdapter<String>? = null
    lateinit var weightUnitNames: MutableList<String>
    var selectedWeightUnitID: String? = null

    var supplierAdapter: ArrayAdapter<String>? = null
    lateinit var supplierNames: MutableList<String>
    var selectedSupplierID: String? = null

    var productID: String? = null
    var mediaPath: String? = null
    var encodedImage: String = "N/A"
    lateinit var databaseAccess: DatabaseAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.product_details)

        etProductCode = findViewById(R.id.et_product_code)
        productID = intent.extras!!.getString(Constant.PRODUCT_ID)

        binding.etProductName.isEnabled = false
        etProductCode.isEnabled = false
        binding.ivScanCode.isEnabled = false
        binding.etProductCategory.isEnabled = false
        binding.etProductDescription.isEnabled = false
        binding.etProductBuyPrice.isEnabled = false
        binding.etProductSellPrice.isEnabled = false
        binding.etProductStock.isEnabled = false
        binding.etProductWeight.isEnabled = false
        binding.etProductWeightUnit.isEnabled = false
        binding.etSupplier.isEnabled = false
        binding.tvChooseImage.isEnabled = false
        binding.ivProduct.isEnabled = false

        binding.tvUpdateProduct.visibility = View.GONE
        binding.tvEditProduct.setOnClickListener {
            binding.etProductName.isEnabled = true
            etProductCode.isEnabled = true
            binding.ivScanCode.isEnabled = true
            binding.etProductCategory.isEnabled = true
            binding.etProductDescription.isEnabled = true
            binding.etProductBuyPrice.isEnabled = true
            binding.etProductSellPrice.isEnabled = true
            binding.etProductStock.isEnabled = true
            binding.etProductWeight.isEnabled = true
            binding.etProductWeightUnit.isEnabled = true
            binding.etSupplier.isEnabled = true
            binding.tvChooseImage.isEnabled = true
            binding.ivProduct.isEnabled = true

            binding.etProductName.setTextColor(SupportMenu.CATEGORY_MASK)
            etProductCode.setTextColor(SupportMenu.CATEGORY_MASK)
            binding.etProductCategory.setTextColor(SupportMenu.CATEGORY_MASK)
            binding.etProductDescription.setTextColor(SupportMenu.CATEGORY_MASK)
            binding.etProductBuyPrice.setTextColor(SupportMenu.CATEGORY_MASK)
            binding.etProductSellPrice.setTextColor(SupportMenu.CATEGORY_MASK)
            binding.etProductStock.setTextColor(SupportMenu.CATEGORY_MASK)
            binding.etProductWeight.setTextColor(SupportMenu.CATEGORY_MASK)
            binding.etProductWeightUnit.setTextColor(SupportMenu.CATEGORY_MASK)
            binding.etSupplier.setTextColor(SupportMenu.CATEGORY_MASK)

            binding.tvEditProduct.visibility = View.GONE
            binding.tvUpdateProduct.visibility = View.VISIBLE
        }

        binding.ivScanCode.setOnClickListener {
            startActivity(Intent(this@EditProductActivity, EditProductScannerViewActivity::class.java))
        }

        binding.tvChooseImage.setOnClickListener {
            val i = Intent(this@EditProductActivity, ImageSelectActivity::class.java)
            i.putExtra(ImageSelectActivity.FLAG_COMPRESS, true)
            i.putExtra(ImageSelectActivity.FLAG_CAMERA, true)
            i.putExtra(ImageSelectActivity.FLAG_GALLERY, true)
            startActivityForResult(i, 1213)
        }

        binding.ivProduct.setOnClickListener {
            val i = Intent(this@EditProductActivity, ImageSelectActivity::class.java)
            i.putExtra(ImageSelectActivity.FLAG_COMPRESS, true)
            i.putExtra(ImageSelectActivity.FLAG_CAMERA, true)
            i.putExtra(ImageSelectActivity.FLAG_GALLERY, true)
            startActivityForResult(i, 1213)
        }

        categoryNames = ArrayList()
        weightUnitNames = ArrayList()
        supplierNames = ArrayList()

        databaseAccess = DatabaseAccess.getInstance(this)
        databaseAccess.open()
        val productData = databaseAccess.getProductsInfo(productID)
        val product_categoryID = productData[0][Constant.PRODUCT_CATEGORY]
        val product_weightUnitID = productData[0][Constant.PRODUCT_WEIGHT_UNIT_ID]
        val product_supplierID = productData[0][Constant.PRODUCT_SUPPLIER]

        binding.etProductName.setText(productData[0][Constant.PRODUCT_NAME])
        binding.etProductCode.setText(productData[0][Constant.PRODUCT_CODE])
        databaseAccess.open()
        binding.etProductCategory.setText(databaseAccess.getCategoryName(product_categoryID))
        binding.etProductDescription.setText(productData[0][Constant.PRODUCT_DESCRIPTION])
        binding.etProductBuyPrice.setText(productData[0][Constant.PRODUCT_BUY_PRICE])
        binding.etProductSellPrice.setText(productData[0][Constant.PRODUCT_SELL_PRICE])
        binding.etProductStock.setText(productData[0][Constant.PRODUCT_STOCK])
        binding.etProductWeight.setText(productData[0][Constant.PRODUCT_WEIGHT])
        databaseAccess.open()
        binding.etProductWeightUnit.setText(databaseAccess.getWeightUnitName(product_weightUnitID))
        databaseAccess.open()
        binding.etSupplier.setText(databaseAccess.getSupplierName(product_supplierID))

        val product_image = productData[0][Constant.PRODUCT_IMAGE]
        if (product_image != null) {
            if (product_image.length < 6) {
                binding.ivProduct.setImageResource(R.drawable.image_placeholder)
            } else {
                val bytes = Base64.decode(product_image, Base64.DEFAULT)
                binding.ivProduct.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
            }
        }
        encodedImage = product_image ?: "N/A"

        selectedCategoryID = product_categoryID
        selectedWeightUnitID = product_weightUnitID
        selectedSupplierID = product_supplierID

        databaseAccess.open()
        val productCategory = databaseAccess.productCategory
        for (i in productCategory.indices) {
            categoryNames.add(productCategory[i][Constant.CATEGORY_NAME]!!)
        }

        databaseAccess.open()
        val weightUnit = databaseAccess.weightUnit
        for (i1 in weightUnit.indices) {
            weightUnitNames.add(weightUnit[i1][Constant.WEIGHT_UNIT]!!)
        }

        databaseAccess.open()
        val productSupplier = databaseAccess.productSupplier
        for (i2 in productSupplier.indices) {
            supplierNames.add(productSupplier[i2][Constant.SUPPLIERS_NAME]!!)
        }

        binding.etProductCategory.setOnClickListener {
            categoryAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
            categoryAdapter!!.addAll(categoryNames)

            val dialog = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.dialog_list_search, null)
            dialog.setView(dialogView)
            dialog.setCancelable(false)

            val title = dialogView.findViewById<TextView>(R.id.tv_dialog_title)
            val search = dialogView.findViewById<EditText>(R.id.et_dialog_search)
            val dialogListView = dialogView.findViewById<ListView>(R.id.dialog_listView)
            val btnCancel = dialogView.findViewById<Button>(R.id.btn_dialog_cancel)

            title.setText(R.string.product_category)
            dialogListView.isVerticalScrollBarEnabled = true
            dialogListView.adapter = categoryAdapter
            search.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                    categoryAdapter!!.filter.filter(charSequence)
                }
                override fun afterTextChanged(editable: Editable) {}
            })
            val alertDialog = dialog.create()
            btnCancel.setOnClickListener { alertDialog.dismiss() }
            alertDialog.show()
            dialogListView.setOnItemClickListener { _, _, i, _ ->
                alertDialog.dismiss()
                val selectedItem = categoryAdapter!!.getItem(i)
                var category_id = "0"
                binding.etProductCategory.setText(selectedItem)
                for (i3 in categoryNames.indices) {
                    if (categoryNames[i3].equals(selectedItem, ignoreCase = true)) {
                        category_id = (productCategory[i3] as HashMap<*, *>)[Constant.CATEGORY_ID] as String
                    }
                }
                selectedCategoryID = category_id
                Log.d(Constant.CATEGORY_ID, category_id)
            }
        }

        binding.etProductWeightUnit.setOnClickListener {
            weightUnitAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
            weightUnitAdapter!!.addAll(weightUnitNames)

            val dialog = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.dialog_list_search, null)
            dialog.setView(dialogView)
            dialog.setCancelable(false)

            val title = dialogView.findViewById<TextView>(R.id.tv_dialog_title)
            val search = dialogView.findViewById<EditText>(R.id.et_dialog_search)
            val dialogListView = dialogView.findViewById<ListView>(R.id.dialog_listView)
            val btnCancel = dialogView.findViewById<Button>(R.id.btn_dialog_cancel)

            title.setText(R.string.product_weight_unit)
            dialogListView.isVerticalScrollBarEnabled = true
            dialogListView.adapter = weightUnitAdapter
            search.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                    weightUnitAdapter!!.filter.filter(charSequence)
                }
                override fun afterTextChanged(editable: Editable) {}
            })
            val alertDialog = dialog.create()
            btnCancel.setOnClickListener { alertDialog.dismiss() }
            alertDialog.show()
            dialogListView.setOnItemClickListener { _, _, i, _ ->
                alertDialog.dismiss()
                val selectedItem = weightUnitAdapter!!.getItem(i)
                var weight_unit_id = "0"
                binding.etProductWeightUnit.setText(selectedItem)
                for (i4 in weightUnitNames.indices) {
                    if (weightUnitNames[i4].equals(selectedItem, ignoreCase = true)) {
                        weight_unit_id = (weightUnit[i4] as HashMap<*, *>)[Constant.WEIGHT_ID] as String
                    }
                }
                selectedWeightUnitID = weight_unit_id
                Log.d(Constant.WEIGHT_UNIT, selectedWeightUnitID!!)
            }
        }

        binding.etSupplier.setOnClickListener {
            supplierAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
            supplierAdapter!!.addAll(supplierNames)

            val dialog = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.dialog_list_search, null)
            dialog.setView(dialogView)
            dialog.setCancelable(false)

            val title = dialogView.findViewById<TextView>(R.id.tv_dialog_title)
            val search = dialogView.findViewById<EditText>(R.id.et_dialog_search)
            val dialogListView = dialogView.findViewById<ListView>(R.id.dialog_listView)
            val btnCancel = dialogView.findViewById<Button>(R.id.btn_dialog_cancel)

            title.setText(R.string.suppliers)
            dialogListView.isVerticalScrollBarEnabled = true
            dialogListView.adapter = supplierAdapter
            search.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                    supplierAdapter!!.filter.filter(charSequence)
                }
                override fun afterTextChanged(editable: Editable) {}
            })
            val alertDialog = dialog.create()
            btnCancel.setOnClickListener { alertDialog.dismiss() }
            alertDialog.show()
            dialogListView.setOnItemClickListener { _, _, i, _ ->
                alertDialog.dismiss()
                val selectedItem = supplierAdapter!!.getItem(i)
                var supplier_id = "0"
                binding.etSupplier.setText(selectedItem)
                for (i5 in supplierNames.indices) {
                    if (supplierNames[i5].equals(selectedItem, ignoreCase = true)) {
                        supplier_id = (productSupplier[i5] as HashMap<*, *>)[Constant.SUPPLIERS_ID] as String
                    }
                }
                selectedSupplierID = supplier_id
                Log.d(Constant.SUPPLIERS_ID, selectedSupplierID!!)
            }
        }

        binding.tvUpdateProduct.setOnClickListener {
            val product_name = binding.etProductName.text.toString()
            val product_code = binding.etProductCode.text.toString()
            val product_category = selectedCategoryID
            val product_description = binding.etProductDescription.text.toString()
            val product_buyPrice = binding.etProductBuyPrice.text.toString()
            val product_sellPrice = binding.etProductSellPrice.text.toString()
            val product_stock = binding.etProductStock.text.toString()
            val product_weight = binding.etProductWeight.text.toString()
            val product_weightUnit = selectedWeightUnitID
            val product_supplier = selectedSupplierID

            if (product_name.isEmpty()) {
                binding.etProductName.error = getString(R.string.product_name_cannot_be_empty)
                binding.etProductName.requestFocus()
            } else if (product_category.isNullOrEmpty()) {
                binding.etProductCategory.error = getString(R.string.product_category_cannot_be_empty)
                binding.etProductCategory.requestFocus()
            } else if (product_sellPrice.isEmpty()) {
                binding.etProductSellPrice.error = getString(R.string.product_sell_price_cannot_be_empty)
                binding.etProductSellPrice.requestFocus()
            } else if (product_stock.isEmpty()) {
                binding.etProductStock.error = getString(R.string.product_stock_cannot_be_empty)
                binding.etProductStock.requestFocus()
            } else if (product_weight.isEmpty()) {
                binding.etProductWeight.error = getString(R.string.product_weight_cannot_be_empty)
                binding.etProductWeight.requestFocus()
            } else if (product_supplier.isNullOrEmpty()) {
                binding.etSupplier.error = getString(R.string.product_supplier_cannot_be_empty)
                binding.etSupplier.requestFocus()
            } else {
                databaseAccess.open()
                val check = databaseAccess.updateProduct(product_name, product_code, product_category, product_description, product_buyPrice, product_sellPrice, product_stock, product_supplier, encodedImage, product_weightUnit, product_weight, productID)
                if (check) {
                    Toasty.success(this, R.string.update_successfully, Toasty.LENGTH_SHORT).show()
                    val i = Intent(this@EditProductActivity, ProductActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                    return@setOnClickListener
                }
                Toasty.error(this, R.string.failed, Toasty.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1213 && resultCode == Activity.RESULT_OK && data != null) {
            try {
                mediaPath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH)
                val selectedImage = BitmapFactory.decodeFile(mediaPath)
                binding.ivProduct.setImageBitmap(selectedImage)
                encodedImage = encodeImage(selectedImage)
            } catch (e: Exception) {
                Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun encodeImage(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId != android.R.id.home) {
            return super.onOptionsItemSelected(item)
        }
        finish()
        return true
    }
}
