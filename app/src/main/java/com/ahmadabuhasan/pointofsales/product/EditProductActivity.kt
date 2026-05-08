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

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.product_details)
        }

        etProductCode = findViewById(R.id.et_product_code)
        productID = intent.getStringExtra(Constant.PRODUCT_ID)

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

            binding.etProductName.setTextColor(0xffff0000.toInt())
            etProductCode.setTextColor(0xffff0000.toInt())
            binding.etProductCategory.setTextColor(0xffff0000.toInt())
            binding.etProductDescription.setTextColor(0xffff0000.toInt())
            binding.etProductBuyPrice.setTextColor(0xffff0000.toInt())
            binding.etProductSellPrice.setTextColor(0xffff0000.toInt())
            binding.etProductStock.setTextColor(0xffff0000.toInt())
            binding.etProductWeight.setTextColor(0xffff0000.toInt())
            binding.etProductWeightUnit.setTextColor(0xffff0000.toInt())
            binding.etSupplier.setTextColor(0xffff0000.toInt())

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
        val productCategoryId = productData[0][Constant.PRODUCT_CATEGORY]
        val productWeightUnitId = productData[0][Constant.PRODUCT_WEIGHT_UNIT_ID]
        val productSupplierId = productData[0][Constant.PRODUCT_SUPPLIER]

        binding.etProductName.setText(productData[0][Constant.PRODUCT_NAME])
        binding.etProductCode.setText(productData[0][Constant.PRODUCT_CODE])
        databaseAccess.open()
        binding.etProductCategory.setText(databaseAccess.getCategoryName(productCategoryId))
        binding.etProductDescription.setText(productData[0][Constant.PRODUCT_DESCRIPTION])
        binding.etProductBuyPrice.setText(productData[0][Constant.PRODUCT_BUY_PRICE])
        binding.etProductSellPrice.setText(productData[0][Constant.PRODUCT_SELL_PRICE])
        binding.etProductStock.setText(productData[0][Constant.PRODUCT_STOCK])
        binding.etProductWeight.setText(productData[0][Constant.PRODUCT_WEIGHT])
        databaseAccess.open()
        binding.etProductWeightUnit.setText(databaseAccess.getWeightUnitName(productWeightUnitId))
        databaseAccess.open()
        binding.etSupplier.setText(databaseAccess.getSupplierName(productSupplierId))

        val productImage = productData[0][Constant.PRODUCT_IMAGE]
        if (productImage != null) {
            if (productImage.length < 6) {
                binding.ivProduct.setImageResource(R.drawable.image_placeholder)
            } else {
                val bytes = Base64.decode(productImage, Base64.DEFAULT)
                binding.ivProduct.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
            }
        }
        encodedImage = productImage ?: "N/A"

        selectedCategoryID = productCategoryId
        selectedWeightUnitID = productWeightUnitId
        selectedSupplierID = productSupplierId

        databaseAccess.open()
        val productCategory = databaseAccess.productCategory
        for (i in productCategory.indices) {
            categoryNames.add(productCategory[i][Constant.CATEGORY_NAME].orEmpty())
        }

        databaseAccess.open()
        val weightUnit = databaseAccess.weightUnit
        for (i1 in weightUnit.indices) {
            weightUnitNames.add(weightUnit[i1][Constant.WEIGHT_UNIT].orEmpty())
        }

        databaseAccess.open()
        val productSupplier = databaseAccess.productSupplier
        for (i2 in productSupplier.indices) {
            supplierNames.add(productSupplier[i2][Constant.SUPPLIERS_NAME].orEmpty())
        }

        binding.etProductCategory.setOnClickListener {
            categoryAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
            categoryAdapter?.addAll(categoryNames)

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
                    categoryAdapter?.filter?.filter(charSequence)
                }
                override fun afterTextChanged(editable: Editable) {}
            })
            val alertDialog = dialog.create()
            btnCancel.setOnClickListener { alertDialog.dismiss() }
            alertDialog.show()
            dialogListView.setOnItemClickListener { _, _, i, _ ->
                alertDialog.dismiss()
                val selectedItem = categoryAdapter?.getItem(i)
                var categoryId = "0"
                binding.etProductCategory.setText(selectedItem)
                for (i3 in categoryNames.indices) {
                    if (categoryNames[i3].equals(selectedItem, ignoreCase = true)) {
                        categoryId = (productCategory[i3] as HashMap<*, *>)[Constant.CATEGORY_ID] as String
                    }
                }
                selectedCategoryID = categoryId
                Log.d(Constant.CATEGORY_ID, categoryId)
            }
        }

        binding.etProductWeightUnit.setOnClickListener {
            weightUnitAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
            weightUnitAdapter?.addAll(weightUnitNames)

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
                    weightUnitAdapter?.filter?.filter(charSequence)
                }
                override fun afterTextChanged(editable: Editable) {}
            })
            val alertDialog = dialog.create()
            btnCancel.setOnClickListener { alertDialog.dismiss() }
            alertDialog.show()
            dialogListView.setOnItemClickListener { _, _, i, _ ->
                alertDialog.dismiss()
                val selectedItem = weightUnitAdapter?.getItem(i)
                var weightUnitId = "0"
                binding.etProductWeightUnit.setText(selectedItem)
                for (i4 in weightUnitNames.indices) {
                    if (weightUnitNames[i4].equals(selectedItem, ignoreCase = true)) {
                        weightUnitId = (weightUnit[i4] as HashMap<*, *>)[Constant.WEIGHT_ID] as String
                    }
                }
                selectedWeightUnitID = weightUnitId
                Log.d(Constant.WEIGHT_UNIT, selectedWeightUnitID.orEmpty())
            }
        }

        binding.etSupplier.setOnClickListener {
            supplierAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
            supplierAdapter?.addAll(supplierNames)

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
                    supplierAdapter?.filter?.filter(charSequence)
                }
                override fun afterTextChanged(editable: Editable) {}
            })
            val alertDialog = dialog.create()
            btnCancel.setOnClickListener { alertDialog.dismiss() }
            alertDialog.show()
            dialogListView.setOnItemClickListener { _, _, i, _ ->
                alertDialog.dismiss()
                val selectedItem = supplierAdapter?.getItem(i)
                var supplierId = "0"
                binding.etSupplier.setText(selectedItem)
                for (i5 in supplierNames.indices) {
                    if (supplierNames[i5].equals(selectedItem, ignoreCase = true)) {
                        supplierId = (productSupplier[i5] as HashMap<*, *>)[Constant.SUPPLIERS_ID] as String
                    }
                }
                selectedSupplierID = supplierId
                Log.d(Constant.SUPPLIERS_ID, selectedSupplierID.orEmpty())
            }
        }

        binding.tvUpdateProduct.setOnClickListener {
            val productName = binding.etProductName.text.toString()
            val productCode = binding.etProductCode.text.toString()
            val productCategory = selectedCategoryID
            val productDescription = binding.etProductDescription.text.toString()
            val productBuyPrice = binding.etProductBuyPrice.text.toString()
            val productSellPrice = binding.etProductSellPrice.text.toString()
            val productStock = binding.etProductStock.text.toString()
            val productWeight = binding.etProductWeight.text.toString()
            val productWeightUnit = selectedWeightUnitID
            val productSupplier = selectedSupplierID

            if (productName.isEmpty()) {
                binding.etProductName.error = getString(R.string.product_name_cannot_be_empty)
                binding.etProductName.requestFocus()
            } else if (productCategory.isNullOrEmpty()) {
                binding.etProductCategory.error = getString(R.string.product_category_cannot_be_empty)
                binding.etProductCategory.requestFocus()
            } else if (productSellPrice.isEmpty()) {
                binding.etProductSellPrice.error = getString(R.string.product_sell_price_cannot_be_empty)
                binding.etProductSellPrice.requestFocus()
            } else if (productStock.isEmpty()) {
                binding.etProductStock.error = getString(R.string.product_stock_cannot_be_empty)
                binding.etProductStock.requestFocus()
            } else if (productWeight.isEmpty()) {
                binding.etProductWeight.error = getString(R.string.product_weight_cannot_be_empty)
                binding.etProductWeight.requestFocus()
            } else if (productSupplier.isNullOrEmpty()) {
                binding.etSupplier.error = getString(R.string.product_supplier_cannot_be_empty)
                binding.etSupplier.requestFocus()
            } else {
                databaseAccess.open()
                val check = databaseAccess.updateProduct(productName, productCode, productCategory, productDescription, productBuyPrice, productSellPrice, productStock, productSupplier, encodedImage, productWeightUnit, productWeight, productID)
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
