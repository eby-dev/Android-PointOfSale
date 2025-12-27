package com.ahmadabuhasan.pointofsales.settings.backup;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import androidx.documentfile.provider.DocumentFile;

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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
    private static final int REQUEST_CHOOSE_FOLDER = 3;

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
            BackupActivity.this.localBackup.performBackup(db, null);
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

    public void folderChooserOld() {
        new ChooserDialog((Activity) this)
                .displayPath(true)
                .withFilter(true, false, new String[0])
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String dir, File dirFile) {
                        BackupActivity.this.onExport(dir, null);
                        Log.d("path", dir);
                    }
                }).build().show();
    }

    public void folderChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CHOOSE_FOLDER);
    }

    public void onExport(String path, Uri folderUri) {
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
                        copyExportToChosenFolder(folderUri);
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
                break;

            case REQUEST_CHOOSE_FOLDER:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Uri folderUri = data.getData();
                        getContentResolver().takePersistableUriPermission(
                                folderUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        );
                        exportWithChosenFolder(folderUri);
                    }
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

    private void exportWithChosenFolder(Uri folderUri) {
        File tempDir = new File(getExternalFilesDir(null), getString(R.string.app_name));
        onExport(tempDir.getAbsolutePath(), folderUri);
    }

    private void copyExportToChosenFolder(Uri folderUri) {
        try {
            String fileName = "POS_AllData.xls";

            File sourceFile = new File(
                    getExternalFilesDir(null),
                    getString(R.string.app_name) + "/" + fileName
            );

            if (!sourceFile.exists()) return;

            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, folderUri);
            if (pickedDir == null || !pickedDir.isDirectory()) return;

            DocumentFile targetFile =
                    pickedDir.createFile(
                            "application/vnd.ms-excel",
                            fileName
                    );

            if (targetFile == null) return;

            InputStream in = new FileInputStream(sourceFile);
            OutputStream out = getContentResolver().openOutputStream(targetFile.getUri());

            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            in.close();
            out.close();
        } catch (Exception e) {
            Log.e("EXPORT", e.getMessage(), e);
        }
    }

}