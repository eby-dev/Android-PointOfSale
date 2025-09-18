package com.ahmadabuhasan.pointofsales.settings.backup;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseOpenHelper;
import com.ahmadabuhasan.pointofsales.databinding.ActivityBackupBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;
import com.ajts.androidmads.library.SQLiteToExcel;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class BackupActivity extends BaseActivity {

    private static final String TAG = "Google Drive Activity";

    public static final int REQUEST_CODE_SIGN_IN = 0;
    public static final int REQUEST_CODE_OPENING = 1;
    public static final int REQUEST_CODE_CREATION = 2;
    public static final int REQUEST_CODE_PERMISSIONS = 2;

    private ActivityBackupBinding binding;
    ProgressDialog loading;

    // https://github.com/prof18/Database-Backup-Restore.git
    //variable for decide if i need to do a backup or a restore.
    //True stands for backup, False for restore
    private boolean isBackup = true;

    private BackupActivity activity;

    private LocalBackup localBackup;
    private RemoteBackup remoteBackup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.activity = this;
        super.onCreate(savedInstanceState);
        binding = ActivityBackupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.data_backup);

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermission();
        }

        this.localBackup = new LocalBackup(this);
        this.remoteBackup = new RemoteBackup(this);

        final DatabaseOpenHelper db = new DatabaseOpenHelper(getApplicationContext());

        this.binding.cvLocalBackup.setOnClickListener(view -> {
            String outFileName = Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name) + File.separator;
            BackupActivity.this.localBackup.performBackup(db, outFileName);
        });

        this.binding.cvLocalDbImport.setOnClickListener(view -> BackupActivity.this.localBackup.performRestore(db));

        this.binding.cvExportToExcel.setOnClickListener(view -> BackupActivity.this.folderChooser());

        this.binding.cvBackupToDrive.setOnClickListener(view -> {
            isBackup = true;
            BackupActivity.this.remoteBackup.connectToDrive(isBackup);
        });

        this.binding.cvImportFromDrive.setOnClickListener(view -> {
            isBackup = false;
            BackupActivity.this.remoteBackup.connectToDrive(isBackup);
        });

        binding.cvBackupToDrive.setVisibility(View.GONE);
        binding.cvImportFromDrive.setVisibility(View.GONE);
    }

    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        multiplePermissionsReport.areAllPermissionsGranted();
                        multiplePermissionsReport.isAnyPermissionPermanentlyDenied();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(dexterError -> Toast.makeText(BackupActivity.this.getApplicationContext(), "Error Occurred! ", Toast.LENGTH_SHORT).show()).onSameThread();
    }

    public void folderChooser() {
        new ChooserDialog((Activity) this)
                .displayPath(true)
                .withFilter(true, false, new String[0])
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String dir, File dirFile) {
                        BackupActivity.this.onExport(dir);
                        Log.d("path", dir);
                    }
                }).build().show();
    }

    public void onExport(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        SQLiteToExcel sqLiteToExcel = new SQLiteToExcel(getApplicationContext(), DatabaseOpenHelper.DATABASE_NAME, path);
        sqLiteToExcel.exportAllTables("POS_AllData.xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {
                BackupActivity.this.loading = new ProgressDialog(BackupActivity.this);
                BackupActivity.this.loading.setCancelable(false);
                BackupActivity.this.loading.setMessage(BackupActivity.this.getString(R.string.data_exporting_please_wait));
                BackupActivity.this.loading.show();
            }

            @Override
            public void onCompleted(String filePath) {
                Handler mHand = new Handler();
                mHand.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BackupActivity.this.loading.dismiss();
                        Toasty.success(BackupActivity.this, R.string.data_successfully_exported, Toasty.LENGTH_SHORT).show();
                    }
                }, 5000L);
            }

            @Override
            public void onError(Exception e) {
                BackupActivity.this.loading.dismiss();
                Toasty.error(BackupActivity.this, R.string.data_export_fail, Toasty.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case REQUEST_CODE_SIGN_IN:
                Log.i(TAG, "Sign in request code");
                // Called after user is signed in.
                if (resultCode == RESULT_OK) {
                    remoteBackup.connectToDrive(isBackup);
                }
                break;

            case REQUEST_CODE_CREATION:
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Backup successfully saved.");
                    Toasty.success(this, R.string.backup_successfully_loaded, Toasty.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_CODE_OPENING:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                    remoteBackup.mOpenItemTaskSource.setResult(driveId);
                } else {
                    remoteBackup.mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
                }

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}