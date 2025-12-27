package com.ahmadabuhasan.pointofsales.database;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.ahmadabuhasan.pointofsales.R;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class DatabaseOpenHelper extends SQLiteAssetHelper {

    public static final String DATABASE_NAME = "POS.db";
    private static final int DATABASE_VERSION = 1;
    private final Context context;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // https://github.com/prof18/Database-Backup-Restore.git
    public void backupOld(String outFileName) {

        //database path
        final String inFileName = context.getDatabasePath(DATABASE_NAME).toString();

        try {
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            //
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toasty.success(context, R.string.backup_completed_successfully, Toasty.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toasty.error(context, R.string.unable_to_backup_database_retry, Toasty.LENGTH_SHORT).show();
            Log.e("backupDB", Objects.requireNonNull(e.getMessage()));
        }
    }

    public void backup(String outFileName) {

        final String inFileName =
                context.getDatabasePath(DATABASE_NAME).getAbsolutePath();

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, outFileName + ".db");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + "/" +
                            context.getString(R.string.app_name)
            );
        }

        Uri collection;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        } else {
            collection = MediaStore.Files.getContentUri("external");
        }

        Uri uri = context.getContentResolver().insert(collection, values);

        if (uri == null) {
            Toasty.error(context, R.string.unable_to_backup_database_retry).show();
            return;
        }

        try (
                InputStream fis = new FileInputStream(inFileName);
                OutputStream output =
                        context.getContentResolver().openOutputStream(uri)
        ) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            Toasty.success(context,
                    R.string.backup_completed_successfully,
                    Toasty.LENGTH_SHORT
            ).show();

        } catch (Exception e) {
            Toasty.error(context,
                    R.string.unable_to_backup_database_retry,
                    Toasty.LENGTH_SHORT
            ).show();
            Log.e("backupDB", e.toString());
        }
    }

    public void importDB(String inFileName) {

        final String outFileName = context.getDatabasePath(DATABASE_NAME).toString();

        try {
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toasty.success(context, R.string.database_Import_completed, Toasty.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toasty.error(context, R.string.unable_to_import_database_retry, Toasty.LENGTH_SHORT).show();
            Log.e("importDB", Objects.requireNonNull(e.getMessage()));
        }
    }
}