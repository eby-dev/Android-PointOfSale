package com.ahmadabuhasan.pointofsales.orders;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.ahmadabuhasan.pointofsales.Constant;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.pdf_report.BarCodeEncoder;
import com.ahmadabuhasan.pointofsales.utils.IPrintToPrinter;
import com.ahmadabuhasan.pointofsales.utils.WoosimPrnMng;
import com.ahmadabuhasan.pointofsales.utils.printerFactory;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.woosim.printer.WoosimCmd;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class TestPrinter implements IPrintToPrinter {

    private final Context context;
    String shop_name, shop_address, shop_email, shop_contact,
            invoiceId, orderDate, orderTime, customerName,
            footer, tax, discount, shop_currency;
    double subTotal, totalPrice;

    String productName, price, qty, weight;
    double cost_total;
    Bitmap bm;
    List<HashMap<String, String>> orderDetailList;
    DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    public TestPrinter(Context context1, String shop_name1, String shop_address1, String shop_email1, String shop_contact1,
                       String invoiceId1, String orderDate1, String orderTime1, String customerName1, String footer1,
                       double subTotal1, double totalPrice1, String tax1, String discount1, String shop_currency1) {
        this.context = context1;
        this.shop_name = shop_name1;
        this.shop_address = shop_address1;
        this.shop_email = shop_email1;
        this.shop_contact = shop_contact1;
        this.invoiceId = invoiceId1;
        this.orderDate = orderDate1;
        this.orderTime = orderTime1;
        this.customerName = customerName1;
        this.footer = footer1;
        this.subTotal = subTotal1;
        this.totalPrice = totalPrice1;
        this.tax = tax1;
        this.discount = discount1;
        this.shop_currency = shop_currency1;

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        this.orderDetailList = databaseAccess.getOrderDetailsList(invoiceId1);
    }

    @Override
    public void printContent(WoosimPrnMng prnMng) {
        double getTax = Double.parseDouble(this.tax);
        double getDiscount = Double.parseDouble(this.discount);

        BarCodeEncoder barCodeEncoder = new BarCodeEncoder();
        try {
            bm = barCodeEncoder.encodeAsBitmap(this.invoiceId, BarcodeFormat.CODE_128, 400, 48);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        printerFactory.createPaperMng(this.context);
        prnMng.printStr(this.shop_name, 2, WoosimCmd.ALIGN_CENTER);
        prnMng.printStr(this.shop_address, 1, WoosimCmd.ALIGN_CENTER);
        prnMng.printStr("Email: " + this.shop_email, 1, WoosimCmd.ALIGN_CENTER);
        prnMng.printStr("Contact: " + this.shop_contact, 1, WoosimCmd.ALIGN_CENTER);
        prnMng.printStr("Invoice ID: " + this.invoiceId, 1, WoosimCmd.ALIGN_CENTER);
        prnMng.printStr("Order Time: " + this.orderTime + " " + this.orderDate, 1, WoosimCmd.ALIGN_CENTER);
        prnMng.printStr(this.customerName, 1, WoosimCmd.ALIGN_CENTER);
        String sb = "Email: " +
                this.shop_email;
        prnMng.printStr(sb, 1, WoosimCmd.ALIGN_CENTER);
        prnMng.printStr("--------------------------------");
        prnMng.printStr("  Items        Price  Qty  Total", 1, WoosimCmd.ALIGN_CENTER);
        prnMng.printStr("--------------------------------");

        for (int i = 0; i < orderDetailList.size(); i++) {
            this.productName = this.orderDetailList.get(i).get(Constant.PRODUCT_NAME);
            this.weight = this.orderDetailList.get(i).get(Constant.PRODUCT_WEIGHT);
            this.qty = this.orderDetailList.get(i).get(Constant.PRODUCT_QTY);
            this.price = this.orderDetailList.get(i).get(Constant.PRODUCT_PRICE);
            double getPrice = Double.parseDouble(Objects.requireNonNull(price));
            double getQty = Integer.parseInt(qty);
            Double.isNaN(getQty);
            this.cost_total = getQty * getPrice;
            prnMng.leftRightAlign(this.productName.trim(), " " +
                    this.price + " x" + this.qty + " " +
                    this.decimalFormat.format(this.cost_total));
        }

        prnMng.printStr("--------------------------------");
        prnMng.printStr("Sub Total: " + this.shop_currency + this.decimalFormat.format(this.subTotal), 1, WoosimCmd.ALIGN_RIGHT);
        prnMng.printStr("Total Tax (+): " + this.shop_currency + this.decimalFormat.format(getTax), 1, WoosimCmd.ALIGN_RIGHT);
        prnMng.printStr("Discount (-): " + this.shop_currency + this.decimalFormat.format(getDiscount), 1, WoosimCmd.ALIGN_RIGHT);
        prnMng.printStr("--------------------------------");
        prnMng.printStr("Total Price: " + this.shop_currency + this.decimalFormat.format(this.totalPrice), 1, WoosimCmd.ALIGN_RIGHT);
        prnMng.printStr(this.footer, 1, WoosimCmd.ALIGN_CENTER);
        prnMng.printNewLine();
        prnMng.printPhoto(bm);
        prnMng.printNewLine();
        prnMng.printNewLine();
        printEnded(prnMng);
    }

    @Override
    public void printEnded(WoosimPrnMng prnMng) {
        //Do any finalization you like after print ended.
        if (prnMng.printSucc()) {
            Toast.makeText(context, R.string.print_succ, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, R.string.print_error, Toast.LENGTH_LONG).show();
        }
    }
}