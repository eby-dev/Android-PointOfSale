package com.ahmadabuhasan.pointofsales;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ahmadabuhasan.pointofsales.customers.CustomersActivity;
import com.ahmadabuhasan.pointofsales.databinding.ActivityDashboardBinding;
import com.ahmadabuhasan.pointofsales.expense.ExpenseActivity;
import com.ahmadabuhasan.pointofsales.orders.OrdersActivity;
import com.ahmadabuhasan.pointofsales.pos.PosActivity;
import com.ahmadabuhasan.pointofsales.product.ProductActivity;
import com.ahmadabuhasan.pointofsales.report.ReportActivity;
import com.ahmadabuhasan.pointofsales.settings.SettingsActivity;
import com.ahmadabuhasan.pointofsales.suppliers.SuppliersActivity;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;
import com.ahmadabuhasan.pointofsales.utils.LocaleManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class DashboardActivity extends BaseActivity {

    private static final int FLEXIBLE_APP_UPDATE_REQ_CODE = 123;

    private ActivityDashboardBinding binding;
    private static long backPressed;

    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#000000'>" + getString(R.string.app_name) + "</font>"));
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_gradient));
        getSupportActionBar().setElevation(0.0f);

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermission();
        }

        appUpdate();

        this.binding.adView.loadAd(new AdRequest.Builder().build());

        this.binding.cardCustomers.setOnClickListener(view -> this.startActivity(new Intent(DashboardActivity.this, CustomersActivity.class)));
        this.binding.cardSuppliers.setOnClickListener(view -> this.startActivity(new Intent(DashboardActivity.this, SuppliersActivity.class)));
        this.binding.cardProducts.setOnClickListener(view -> this.startActivity(new Intent(DashboardActivity.this, ProductActivity.class)));
        this.binding.cardPos.setOnClickListener(view -> this.startActivity(new Intent(DashboardActivity.this, PosActivity.class)));
        this.binding.cardExpense.setOnClickListener(view -> this.startActivity(new Intent(DashboardActivity.this, ExpenseActivity.class)));
        this.binding.cardOrderList.setOnClickListener(view -> this.startActivity(new Intent(DashboardActivity.this, OrdersActivity.class)));
        this.binding.cardReport.setOnClickListener(view -> this.startActivity(new Intent(DashboardActivity.this, ReportActivity.class)));
        this.binding.cardSettings.setOnClickListener(view -> this.startActivity(new Intent(DashboardActivity.this, SettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.adView.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.language_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        /*switch (item.getItemId()) {
            case R.id.local_english:
                setNewLocale(this, LocaleManager.ENGLISH);
                Toast.makeText(getApplicationContext(), "English", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.local_indonesian:
                setNewLocale(this, LocaleManager.INDONESIAN);
                Toast.makeText(getApplicationContext(), "Indonesian", Toast.LENGTH_SHORT).show();
                return true;
        }*/

        int id = item.getItemId();
        if (id == R.id.local_english) {
            setNewLocale(this, LocaleManager.ENGLISH);
            Toast.makeText(getApplicationContext(), "English", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.local_indonesian) {
            setNewLocale(this, LocaleManager.INDONESIAN);
            Toast.makeText(getApplicationContext(), "Indonesian", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // https://androidwave.com/android-multi-language-support-best-practices/
    private void setNewLocale(AppCompatActivity appCompatActivity, @LocaleManager.LocaleDef String language) {
        LocaleManager.setNewLocale(this, language);
        Intent intent = appCompatActivity.getIntent();
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }
    // En

    @Override
    public void onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis()) {
            finishAffinity();
        } else {
            Toasty.info(this, R.string.press_once_again_to_exit, Toasty.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }

    private void requestPermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.POST_NOTIFICATIONS
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        multiplePermissionsReport.areAllPermissionsGranted();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(dexterError -> Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show()).onSameThread().check();
    }

    private void appUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        checkUpdate();
        installStateUpdatedListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                removeInstallStateUpdateListener();
            } else {
                Toast.makeText(getApplicationContext(), "InstallStateUpdatedListener: state: " + state.installStatus(), Toast.LENGTH_LONG).show();
            }
        };
    }

    private void checkUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                startUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            }

        });
    }

    @SuppressWarnings("deprecation")
    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, FLEXIBLE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            Log.e("Update Error", Objects.requireNonNull(e.getMessage()));
        }
    }

    private void popupSnackBarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make(
                        findViewById(R.id.activity_dashboard),
                        "An update has just been downloaded.",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(
                ContextCompat.getColor(this, R.color.red));
        snackbar.show();
    }

    private void removeInstallStateUpdateListener() {
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }
}