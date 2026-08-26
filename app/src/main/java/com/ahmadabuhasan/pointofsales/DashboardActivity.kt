package com.ahmadabuhasan.pointofsales

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ahmadabuhasan.pointofsales.customers.CustomersActivity
import com.ahmadabuhasan.pointofsales.databinding.ActivityDashboardBinding
import com.ahmadabuhasan.pointofsales.expense.ExpenseActivity
import com.ahmadabuhasan.pointofsales.orders.OrdersActivity
import com.ahmadabuhasan.pointofsales.pos.PosActivity
import com.ahmadabuhasan.pointofsales.product.ProductActivity
import com.ahmadabuhasan.pointofsales.report.ReportActivity
import com.ahmadabuhasan.pointofsales.settings.SettingsActivity
import com.ahmadabuhasan.pointofsales.suppliers.SuppliersActivity
import com.ahmadabuhasan.pointofsales.utils.BaseActivity
import com.ahmadabuhasan.pointofsales.utils.LocaleManager
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class DashboardActivity : BaseActivity() {

    companion object {
        private const val FLEXIBLE_APP_UPDATE_REQ_CODE = 123
        private var backPressed: Long = 0
    }

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var installStateUpdatedListener: InstallStateUpdatedListener

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = Html.fromHtml("<font color='#000000'>${getString(R.string.app_name)}</font>")
            setBackgroundDrawable(ContextCompat.getDrawable(this@DashboardActivity, R.drawable.actionbar_gradient))
            elevation = 0f
        }

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermission()
        }

        appUpdate()

        // The banner is loaded in onResume(), which always runs after
        // onCreate() — loading here too would fire two requests per launch.
        binding.cardCustomers.setOnClickListener { startActivity(Intent(this, CustomersActivity::class.java)) }
        binding.cardSuppliers.setOnClickListener { startActivity(Intent(this, SuppliersActivity::class.java)) }
        binding.cardProducts.setOnClickListener { startActivity(Intent(this, ProductActivity::class.java)) }
        binding.cardPos.setOnClickListener { startActivity(Intent(this, PosActivity::class.java)) }
        binding.cardExpense.setOnClickListener { startActivity(Intent(this, ExpenseActivity::class.java)) }
        binding.cardOrderList.setOnClickListener { startActivity(Intent(this, OrdersActivity::class.java)) }
        binding.cardReport.setOnClickListener { startActivity(Intent(this, ReportActivity::class.java)) }
        binding.cardSettings.setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        binding.adView.resume()
        binding.adView.loadAd(AdRequest.Builder().build())
    }

    // AdMob requires the banner to follow the activity lifecycle: pausing it
    // stops impressions being counted while the screen is not visible, and
    // destroying it releases the underlying WebView.
    override fun onPause() {
        binding.adView.pause()
        super.onPause()
    }

    override fun onDestroy() {
        binding.adView.destroy()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.language_menu, menu)
        return true
    }

    @SuppressLint("NonConstantResourceId")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.local_english -> {
                setNewLocale(this, LocaleManager.ENGLISH)
                Toast.makeText(applicationContext, "English", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.local_indonesian -> {
                setNewLocale(this, LocaleManager.INDONESIAN)
                Toast.makeText(applicationContext, "Indonesian", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // https://androidwave.com/android-multi-language-support-best-practices/
    private fun setNewLocale(appCompatActivity: AppCompatActivity, @LocaleManager.LocaleDef language: String) {
        LocaleManager.setNewLocale(this, language)
        val intent = appCompatActivity.intent
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis()) {
            finishAffinity()
        } else {
            Toasty.info(this, R.string.press_once_again_to_exit, Toasty.LENGTH_SHORT).show()
        }
        backPressed = System.currentTimeMillis()
    }

    private fun requestPermission() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.POST_NOTIFICATIONS
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    report.areAllPermissionsGranted()
                }

                override fun onPermissionRationaleShouldBeShown(list: List<PermissionRequest>, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            })
            .withErrorListener { Toast.makeText(applicationContext, R.string.error, Toast.LENGTH_SHORT).show() }
            .onSameThread()
            .check()
    }

    private fun appUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkUpdate()
        installStateUpdatedListener = InstallStateUpdatedListener { state ->
            when (state.installStatus()) {
                InstallStatus.DOWNLOADED -> popupSnackBarForCompleteUpdate()
                InstallStatus.INSTALLED -> removeInstallStateUpdateListener()
                else -> Toast.makeText(
                    applicationContext,
                    "InstallStateUpdatedListener: state: ${state.installStatus()}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun checkUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            when {
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> startUpdateFlow(appUpdateInfo)
                appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED -> popupSnackBarForCompleteUpdate()
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun startUpdateFlow(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, FLEXIBLE_APP_UPDATE_REQ_CODE)
        } catch (e: IntentSender.SendIntentException) {
            Log.e("Update Error", e.message ?: "Unknown error")
        }
    }

    private fun popupSnackBarForCompleteUpdate() {
        Snackbar.make(
            findViewById(R.id.activity_dashboard),
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("RESTART") { appUpdateManager.completeUpdate() }
            setActionTextColor(ContextCompat.getColor(this@DashboardActivity, R.color.red))
            show()
        }
    }

    private fun removeInstallStateUpdateListener() {
        appUpdateManager.unregisterListener(installStateUpdatedListener)
    }
}