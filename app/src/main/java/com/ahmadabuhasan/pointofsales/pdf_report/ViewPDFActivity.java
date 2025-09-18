package com.ahmadabuhasan.pointofsales.pdf_report;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.databinding.ActivityViewPdfBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;
import com.itextpdf.text.pdf.PdfObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class ViewPDFActivity extends BaseActivity {

    private ActivityViewPdfBinding binding;

    private File file;
    private Context primaryBaseActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.order_receipt);

        Bundle bundle = getIntent().getExtras();
        Log.d("location", bundle.toString());
        if (bundle != null) {
            this.file = new File(bundle.getString("path", ""));
        }
        this.binding.pdfView.fromFile(this.file)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_pdf, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_print) {
            printPDF();
            return true;
        } else if (item.getItemId() == R.id.action_share) {
            Toast.makeText(this, R.string.share, Toast.LENGTH_SHORT).show();
            sharedPdfFile();
            return true;
        } else if (item.getItemId() == R.id.action_open_pdf) {
            Toast.makeText(this, R.string.open_with_external_pdf_reader, Toast.LENGTH_SHORT).show();
            openWithExternalPdfApp();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        this.primaryBaseActivity = newBase;
        super.attachBaseContext(newBase);
    }

    @SuppressLint("ObsoleteSdkInt")
    public void printPDF() {
        if (Build.VERSION.SDK_INT >= 19) {
            PrintManager printManager = (PrintManager) this.primaryBaseActivity.getSystemService(PRINT_SERVICE);
            String jobName = getString(R.string.app_name) + "Order Receipt";
            PrintDocumentAdapter pda = new PrintDocumentAdapter() {
                @Override
                public void onLayout(PrintAttributes printAttributes, PrintAttributes printAttributes1, CancellationSignal cancellationSignal, LayoutResultCallback layoutResultCallback, Bundle bundle) {

                    if (cancellationSignal.isCanceled()) {
                        layoutResultCallback.onLayoutCancelled();
                        return;
                    }

                    PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("Name of File").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
                    layoutResultCallback.onLayoutFinished(pdi, true);
                }

                @Override
                public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, WriteResultCallback writeResultCallback) {

                    InputStream input = null;
                    OutputStream output = null;

                    try {
                        File folder = new File(Environment.getExternalStorageDirectory().toString(), PdfObject.TEXT_PDFDOCENCODING);
                        if (!folder.exists()) {
                            folder.mkdir();
                        }

                        File file1 = new File(folder, "order_receipt.pdf");
                        input = new FileInputStream(file1);
                        output = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
                        byte[] buf = new byte[1024];

                        int bytesRead;
                        while ((bytesRead = input.read(buf)) > 0) {
                            output.write(buf, 0, bytesRead);
                        }

                        writeResultCallback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (input != null) {
                                input.close();
                            }
                            if (output != null) {
                                output.close();
                            }
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                }
            };
            if (printManager != null) {
                PrintAttributes.Builder builder = new PrintAttributes.Builder();
                builder.setMediaSize(PrintAttributes.MediaSize.PRC_6);
                printManager.print(jobName, pda, builder.build());
            }
        }
    }

    public void sharedPdfFile() {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", this.file);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setDataAndType(uri, "application/pdf");
        i.putExtra(Intent.EXTRA_STREAM, uri);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(i);
    }

    public void openWithExternalPdfApp() {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", this.file);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(uri, "application/pdf");
        i.putExtra(Intent.EXTRA_STREAM, uri);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(i);
    }
}