package com.ahmadabuhasan.pointofsales.database;

import android.content.Context;

import com.ahmadabuhasan.pointofsales.R;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

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
    public void backup(String outFileName) {

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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }
}