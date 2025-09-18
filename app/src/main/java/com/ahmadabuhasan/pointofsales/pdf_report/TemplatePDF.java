package com.ahmadabuhasan.pointofsales.pdf_report;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.drive.DriveFile;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class TemplatePDF {

    PdfWriter pdfWriter;

    private final Context context;
    private Document document;
    private File pdfFile;
    private Paragraph paragraph;

    private final Font fTitle = new Font(Font.FontFamily.TIMES_ROMAN, 6.0f, Font.NORMAL, BaseColor.GRAY);
    private final Font fSubTitle = new Font(Font.FontFamily.TIMES_ROMAN, 4.0f, Font.ITALIC, BaseColor.GRAY);
    private final Font fText = new Font(Font.FontFamily.TIMES_ROMAN, 4.0f, Font.ITALIC, BaseColor.GRAY);
    private final Font fHighText = new Font(Font.FontFamily.TIMES_ROMAN, 4.0f, Font.ITALIC, BaseColor.GRAY);
    private final Font fRowText = new Font(Font.FontFamily.TIMES_ROMAN, 4.0f, Font.ITALIC, BaseColor.GRAY);

    public TemplatePDF(Context context1) {
        this.context = context1;
    }

    public void openDocument() {
        createFile();
        try {
            Rectangle pageSize = new Rectangle(164.41f, 500.41f);
            Document document = new Document(pageSize);
            this.document = document;
            this.pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(this.pdfFile));
            this.document.open();
        } catch (Exception e) {
            Log.e("createFile", e.toString());
        }
    }

    private void createFile() {
        File folder = new File(Environment.getExternalStorageDirectory().toString(), PdfObject.TEXT_PDFDOCENCODING);
        if (!folder.exists()) {
            folder.mkdir();
        }
        this.pdfFile = new File(folder, "order_receipt.pdf");
    }

    public void closeDocument() {
        this.document.close();
    }

    public void addMetaData(String title, String subject, String author) {
        this.document.addTitle(title);
        this.document.addSubject(subject);
        this.document.addAuthor(author);
    }

    public void addTitle(String title, String subTitle, String date) {
        try {
            this.paragraph = new Paragraph();
            addChildP(new Paragraph(title, this.fTitle));
            addChildP(new Paragraph(subTitle, this.fSubTitle));
            addChildP(new Paragraph("Order Date:" + date, this.fHighText));
            this.document.add(this.paragraph);
        } catch (Exception e) {
            Log.e("addTitle", e.toString());
        }
    }

    public void addImage(Bitmap bm) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte[] byteArray = stream.toByteArray();
            Image img = Image.getInstance(byteArray);
            img.setAlignment(Image.ALIGN_BOTTOM);
            img.setAlignment(Image.ALIGN_CENTER);
            img.scaleAbsolute(80.0f, 20.0f);
            this.document.add(img);
        } catch (Exception e) {
            Log.e("addParagraph", e.toString());
        }
    }

    public void addChildP(Paragraph childParagraph) {
        childParagraph.setAlignment(Element.ALIGN_CENTER);
        this.paragraph.add((Element) childParagraph);
    }

    public void addParagraph(String text) {
        try {
            this.paragraph = new Paragraph(text, this.fText);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            this.document.add(this.paragraph);
        } catch (Exception e) {
            Log.e("addParagraph", e.toString());
        }
    }

    public void addRightParagraph(String text) {
        try {
            Paragraph paragraph = new Paragraph(text, this.fText);
            this.paragraph = paragraph;
            paragraph.setSpacingAfter(5.0f);
            this.paragraph.setSpacingBefore(5.0f);
            this.paragraph.setAlignment(Element.ALIGN_CENTER);
            this.document.add(this.paragraph);
        } catch (Exception e) {
            Log.e("addParagraph", e.toString());
        }
    }

    public void createTable(String[] header, ArrayList<String[]> clients) {
        try {
            this.paragraph = new Paragraph();
            paragraph.setFont(this.fText);
            PdfPTable pdfPTable = new PdfPTable(header.length);
            pdfPTable.setWidthPercentage(100.0f);
            pdfPTable.setSpacingBefore(5.0f);
            for (String str : header) { //int indexC = 0; indexC < header.length; indexC++
                PdfPCell pdfPCell = new PdfPCell(new Phrase(str, this.fSubTitle)); //(new Phrase(header[indexC],
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setBorderColor(BaseColor.GRAY);
                pdfPTable.addCell(pdfPCell);
            }
            for (int indexR = 0; indexR < clients.size(); indexR++) {
                String[] row = clients.get(indexR);
                for (int indexC = 0; indexC < header.length; indexC++) {
                    PdfPCell pdfPCell2 = new PdfPCell(new Phrase(row[indexC], this.fRowText));
                    pdfPCell2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    pdfPCell2.setBorder(PdfPCell.ALIGN_LEFT);
                    pdfPTable.addCell(pdfPCell2);
                }
            }
            this.paragraph.add((Element) pdfPTable);
            this.document.add(this.paragraph);
        } catch (Exception e) {
            Log.e("createTable", e.toString());
        }
    }

    @SuppressLint("WrongConstant")
    public void viewPDF() {
        Intent intent = new Intent(this.context, ViewPDFActivity.class);
        intent.putExtra("path", this.pdfFile.getAbsolutePath());
        intent.addFlags(DriveFile.MODE_READ_ONLY);
        this.context.startActivity(intent);
    }
}