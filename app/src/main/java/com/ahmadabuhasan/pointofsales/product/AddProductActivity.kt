package com.ahmadabuhasan.pointofsales.product

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.DashboardActivity
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.database.DatabaseOpenHelper
import com.ahmadabuhasan.pointofsales.databinding.ActivityAddProductBinding
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import com.ajts.androidmads.library.ExcelToSQLite
import com.obsez.android.lib.filechooser.ChooserDialog
import es.dmoral.toasty.Toasty
import `in`.mayanknagwanshi.imagepicker.ImageSelectActivity
import java.io.ByteArrayOutputStream
import java.io.File

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class AddProductActivity : BaseActivity() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var etProductCode: EditText
    }

    private lateinit var binding: ActivityAddProductBinding

    var categoryAdapter: ArrayAdapter<String>? = null
    lateinit var categoryNames: MutableList<String>
    var selectedCategoryID: String? = null

    var weightUnitAdapter: ArrayAdapter<String>? = null
    lateinit var weightUnitNames: MutableList<String>
    var selectedWeightUnitID: String? = null

    var supplierAdapter: ArrayAdapter<String>? = null
    lateinit var supplierNames: MutableList<String>
    var selectedSupplierID: String? = null

    var loading: ProgressDialog? = null
    var mediaPath: String? = null
    var encodedImage: String = "N/A"
    lateinit var databaseAccess: DatabaseAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.add_product)
        }

        etProductCode = findViewById(R.id.et_product_code)

        binding.ivScanCode.setOnClickListener {
            startActivity(Intent(this@AddProductActivity, ScannerViewActivity::class.java))
        }

        binding.tvChooseImage.setOnClickListener {
            val i = Intent(this@AddProductActivity, ImageSelectActivity::class.java)
            i.putExtra(ImageSelectActivity.FLAG_COMPRESS, true)
            i.putExtra(ImageSelectActivity.FLAG_CAMERA, true)
            i.putExtra(ImageSelectActivity.FLAG_GALLERY, true)
            startActivityForResult(i, 1213)
        }

        binding.ivProduct.setOnClickListener {
            val i = Intent(this@AddProductActivity, ImageSelectActivity::class.java)
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
                        categoryId = productCategory[i3][Constant.CATEGORY_ID].orEmpty()
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
                override fun onTextChanged(charSequence: CharSequence, start: Int, after: Int, count: Int) {
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
                        weightUnitId = weightUnit[i4][Constant.WEIGHT_ID].orEmpty()
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
                        supplierId = productSupplier[i5][Constant.SUPPLIERS_ID].orEmpty()
                    }
                }
                selectedSupplierID = supplierId
                Log.d(Constant.SUPPLIERS_ID, selectedSupplierID.orEmpty())
            }
        }

        binding.tvAddProduct.setOnClickListener {
            val productName = binding.etProductName.text.toString()
            val productCode = binding.etProductCode.text.toString()
            val productCategoryName = binding.etProductCategory.text.toString()
            val productCategoryId = selectedCategoryID
            val productDescription = binding.etProductDescription.text.toString()
            val productBuyPrice = binding.etProductBuyPrice.text.toString()
            val productSellPrice = binding.etProductSellPrice.text.toString()
            val productStock = binding.etProductStock.text.toString()
            val productWeight = binding.etProductWeight.text.toString()
            val productWeightUnitName = binding.etProductWeightUnit.text.toString()
            val productWeightUnitId = selectedWeightUnitID
            val productSupplierName = binding.etSupplier.text.toString()
            val productSupplierId = selectedSupplierID

            if (productName.isEmpty()) {
                binding.etProductName.error = getString(R.string.product_name_cannot_be_empty)
                binding.etProductName.requestFocus()
            } else if (productCategoryName.isEmpty() && productCategoryId.isNullOrEmpty()) {
                binding.etProductCategory.error = getString(R.string.product_category_cannot_be_empty)
                binding.etProductCategory.requestFocus()
            } else if (productSellPrice.isEmpty()) {
                binding.etProductSellPrice.error = getString(R.string.product_sell_price_cannot_be_empty)
                binding.etProductSellPrice.requestFocus()
            } else if (productWeightUnitName.isEmpty() && productWeight.isEmpty()) {
                binding.etProductWeight.error = getString(R.string.product_weight_cannot_be_empty)
                binding.etProductWeight.requestFocus()
            } else if (productStock.isEmpty()) {
                binding.etProductStock.error = getString(R.string.product_stock_cannot_be_empty)
                binding.etProductStock.requestFocus()
            } else if (productSupplierName.isEmpty() && productSupplierId.isNullOrEmpty()) {
                binding.etSupplier.error = getString(R.string.product_supplier_cannot_be_empty)
                binding.etSupplier.requestFocus()
            } else {
                databaseAccess.open()
                val check = databaseAccess.addProduct(productName, productCode, productCategoryId, productDescription, productBuyPrice, productSellPrice, productStock, productSupplierId, encodedImage, productWeightUnitId, productWeight)
                if (check) {
                    Toasty.success(this, R.string.product_successfully_added, Toasty.LENGTH_SHORT).show()
                    val i = Intent(this@AddProductActivity, ProductActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                } else {
                    Toasty.error(this, R.string.failed, Toasty.LENGTH_SHORT).show()
                }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_product_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else if (item.itemId == R.id.menu_import) {
            fileChooser()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    fun fileChooser() {
        ChooserDialog(this as Activity)
            .displayPath(true)
            .withFilter(false, false, "xls")
            .withChosenListener { dir, _ ->
                onImport(dir)
            }.withOnCancelListener { dialogInterface ->
                dialogInterface.cancel()
                Log.d("CANCEL", "CANCEL")
            }.build().show()
    }

    fun onImport(path: String) {
        databaseAccess.open()
        val file = File(path)
        if (!file.exists()) {
            Toast.makeText(this, R.string.no_file_found, Toast.LENGTH_SHORT).show()
            return
        }
        val excelToSQLite = ExcelToSQLite(applicationContext, DatabaseOpenHelper.DATABASE_NAME, false)
        excelToSQLite.importFromFile(path, object : ExcelToSQLite.ImportListener {
            override fun onStart() {
                loading = ProgressDialog(this@AddProductActivity)
                loading?.setMessage(getString(R.string.data_importing_please_wait))
                loading?.setCancelable(false)
                loading?.show()
            }

            override fun onCompleted(dbName: String) {
                val mHand = Handler()
                mHand.postDelayed({
                    loading?.dismiss()
                    Toasty.success(this@AddProductActivity, R.string.data_successfully_imported, Toasty.LENGTH_SHORT).show()
                    val i = Intent(this@AddProductActivity, DashboardActivity::class.java)
                    startActivity(i)
                    finish()
                }, 5000L)
            }

            override fun onError(e: Exception) {
                loading?.dismiss()
                Toasty.error(this@AddProductActivity, R.string.data_import_fail, Toasty.LENGTH_SHORT).show()
                Log.d("Error : ", "" + e.message)
            }
        })
    }
}
