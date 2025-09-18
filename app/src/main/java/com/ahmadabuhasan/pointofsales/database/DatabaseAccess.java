package com.ahmadabuhasan.pointofsales.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ahmadabuhasan.pointofsales.Constant;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class DatabaseAccess {

    private static DatabaseAccess INSTANCE;
    private SQLiteDatabase database;
    private final SQLiteOpenHelper openHelper;

    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DatabaseAccess(context);
        }
        return INSTANCE;
    }

    public void open() {
        this.database = this.openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public boolean addCustomer(String customer_name, String customer_cell, String customer_email, String customer_address) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.CUSTOMER_NAME, customer_name);
        contentValues.put(Constant.CUSTOMER_CELL, customer_cell);
        contentValues.put(Constant.CUSTOMER_EMAIL, customer_email);
        contentValues.put(Constant.CUSTOMER_ADDRESS, customer_address);
        long check = this.database.insert(Constant.customers, null, contentValues);
        close();
        return check != -1;
    }

    public boolean addCategory(String category_name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.CATEGORY_NAME, category_name);
        long check = this.database.insert(Constant.PRODUCT_CATEGORY, null, contentValues);
        close();
        return check != -1;
    }

    public boolean addOrderType(String delivery_name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.ORDER_TYPE_NAME, delivery_name);
        long check = this.database.insert(Constant.ORDER_TYPE, null, contentValues);
        close();
        return check != -1;
    }

    public boolean addPaymentMethod(String payment_method_name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.PAYMENT_METHOD_NAME, payment_method_name);
        long check = this.database.insert(Constant.paymentMethod, null, contentValues);
        close();
        return check != -1;
    }

    public boolean addWeight(String weight_name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.WEIGHT_UNIT, weight_name);
        long check = this.database.insert(Constant.PRODUCT_WEIGHT, null, contentValues);
        close();
        return check != -1;
    }

    public boolean updateCategory(String category_id, String category_name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.CATEGORY_NAME, category_name);
        long check = this.database.update(Constant.PRODUCT_CATEGORY, contentValues, "category_id=? ", new String[]{category_id});
        close();
        return check != -1;
    }

    public boolean updateOrderType(String delivery_id, String delivery_name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.ORDER_TYPE_NAME, delivery_name);
        long check = this.database.update(Constant.ORDER_TYPE, contentValues, "order_type_id=? ", new String[]{delivery_id});
        close();
        return check != -1;
    }

    public boolean updatePaymentMethod(String payment_method_id, String payment_method_name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.PAYMENT_METHOD_NAME, payment_method_name);
        long check = this.database.update(Constant.paymentMethod, contentValues, "payment_method_id=? ", new String[]{payment_method_id});
        close();
        return check != -1;
    }

    public boolean updateWeight(String weight_id, String weight_name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.WEIGHT_UNIT, weight_name);
        long check = this.database.update(Constant.PRODUCT_WEIGHT, contentValues, "weight_id=? ", new String[]{weight_id});
        close();
        return check != -1;
    }

    public boolean updateCustomer(String customer_id, String customer_name, String customer_cell, String customer_email, String customer_address) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.CUSTOMER_NAME, customer_name);
        contentValues.put(Constant.CUSTOMER_CELL, customer_cell);
        contentValues.put(Constant.CUSTOMER_EMAIL, customer_email);
        contentValues.put(Constant.CUSTOMER_ADDRESS, customer_address);
        long check = this.database.update(Constant.customers, contentValues, " customer_id=? ", new String[]{customer_id});
        close();
        return check != -1;
    }

    public boolean addProduct(String product_name, String product_code, String product_category, String product_description, String product_buy_price, String product_sell_price, String product_stock, String product_supplier, String product_image, String weight_unit_id, String product_weight) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.PRODUCT_NAME, product_name);
        contentValues.put(Constant.PRODUCT_CODE, product_code);
        contentValues.put(Constant.PRODUCT_CATEGORY, product_category);
        contentValues.put(Constant.PRODUCT_DESCRIPTION, product_description);
        contentValues.put(Constant.PRODUCT_BUY_PRICE, product_buy_price);
        contentValues.put(Constant.PRODUCT_SELL_PRICE, product_sell_price);
        contentValues.put(Constant.PRODUCT_SUPPLIER, product_supplier);
        contentValues.put(Constant.PRODUCT_IMAGE, product_image);
        contentValues.put(Constant.PRODUCT_STOCK, product_stock);
        contentValues.put(Constant.PRODUCT_WEIGHT_UNIT_ID, weight_unit_id);
        contentValues.put(Constant.PRODUCT_WEIGHT, product_weight);
        long check = this.database.insert(Constant.products, null, contentValues);
        close();
        return check != -1;
    }

    public boolean updateProduct(String product_name, String product_code, String product_category, String product_description, String product_buy_price, String product_sell_price, String product_stock, String product_supplier, String product_image, String weight_unit_id, String product_weight, String product_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.PRODUCT_NAME, product_name);
        contentValues.put(Constant.PRODUCT_CODE, product_code);
        contentValues.put(Constant.PRODUCT_CATEGORY, product_category);
        contentValues.put(Constant.PRODUCT_DESCRIPTION, product_description);
        contentValues.put(Constant.PRODUCT_BUY_PRICE, product_buy_price);
        contentValues.put(Constant.PRODUCT_SELL_PRICE, product_sell_price);
        contentValues.put(Constant.PRODUCT_SUPPLIER, product_supplier);
        contentValues.put(Constant.PRODUCT_IMAGE, product_image);
        contentValues.put(Constant.PRODUCT_STOCK, product_stock);
        contentValues.put(Constant.PRODUCT_WEIGHT_UNIT_ID, weight_unit_id);
        contentValues.put(Constant.PRODUCT_WEIGHT, product_weight);
        long check = this.database.update(Constant.products, contentValues, "product_id=?", new String[]{product_id});
        close();
        return check != -1;
    }

    public boolean addExpense(String expense_name, String expense_amount, String expense_note, String date, String time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.EXPENSE_NAME, expense_name);
        contentValues.put(Constant.EXPENSE_AMOUNT, expense_amount);
        contentValues.put(Constant.EXPENSE_NOTE, expense_note);
        contentValues.put(Constant.EXPENSE_DATE, date);
        contentValues.put(Constant.EXPENSE_TIME, time);
        long check = this.database.insert(Constant.expense, null, contentValues);
        close();
        return check != -1;
    }

    public boolean updateExpense(String expense_id, String expense_name, String expense_amount, String expense_note, String date, String time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.EXPENSE_NAME, expense_name);
        contentValues.put(Constant.EXPENSE_AMOUNT, expense_amount);
        contentValues.put(Constant.EXPENSE_NOTE, expense_note);
        contentValues.put(Constant.EXPENSE_DATE, date);
        contentValues.put(Constant.EXPENSE_TIME, time);
        long check = this.database.update(Constant.expense, contentValues, "expense_id=?", new String[]{expense_id});
        close();
        return check != -1;
    }

    public boolean addSuppliers(String suppliers_name, String suppliers_contact_person, String suppliers_cell, String suppliers_email, String suppliers_address) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.SUPPLIERS_NAME, suppliers_name);
        contentValues.put(Constant.SUPPLIERS_CONTACT_PERSON, suppliers_contact_person);
        contentValues.put(Constant.SUPPLIERS_CELL, suppliers_cell);
        contentValues.put(Constant.SUPPLIERS_EMAIL, suppliers_email);
        contentValues.put(Constant.SUPPLIERS_ADDRESS, suppliers_address);
        long check = this.database.insert(Constant.suppliers, null, contentValues);
        close();
        return check != -1;
    }

    public boolean updateSuppliers(String suppliers_id, String suppliers_name, String suppliers_contact_person, String suppliers_cell, String suppliers_email, String suppliers_address) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.SUPPLIERS_NAME, suppliers_name);
        contentValues.put(Constant.SUPPLIERS_CONTACT_PERSON, suppliers_contact_person);
        contentValues.put(Constant.SUPPLIERS_CELL, suppliers_cell);
        contentValues.put(Constant.SUPPLIERS_EMAIL, suppliers_email);
        contentValues.put(Constant.SUPPLIERS_ADDRESS, suppliers_address);
        long check = this.database.update(Constant.suppliers, contentValues, "suppliers_id=?", new String[]{suppliers_id});
        close();
        return check != -1;
    }

    public String getProductImage(String product_id) {
        String image = "N/A";
        Cursor cursor = this.database.rawQuery("SELECT * FROM products WHERE product_id='" + product_id + "'", null);
        if (cursor.moveToFirst()) {
            do {
                image = cursor.getString(8);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return image;
    }

    public String getWeightUnitName(String weight_unit_id) {
        String weight_unit_name = "N/A";
        Cursor cursor = this.database.rawQuery("SELECT * FROM product_weight WHERE weight_id=" + weight_unit_id + "", null);
        if (cursor.moveToFirst()) {
            do {
                weight_unit_name = cursor.getString(1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return weight_unit_name;
    }

    public String getSupplierName(String supplier_id) {
        String supplier_name = "N/A";
        Cursor cursor = database.rawQuery("SELECT * FROM suppliers WHERE suppliers_id=" + supplier_id + "", null);
        if (cursor.moveToFirst()) {
            do {
                supplier_name = cursor.getString(1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return supplier_name;
    }

    public String getCategoryName(String category_id) {
        String product_category = "N/A";
        Cursor cursor = this.database.rawQuery("SELECT * FROM product_category WHERE category_id=" + category_id + "", null);
        if (cursor.moveToFirst()) {
            do {
                product_category = cursor.getString(1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return product_category;
    }

    public int addToCart(String product_id, String weight, String weight_unit, String price, int qty, String stock) {
        Cursor cursor = this.database.rawQuery("SELECT * FROM product_cart WHERE product_id='" + product_id + "'", null);
        if (cursor.getCount() >= 1) {
            return 2;
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Constant.PRODUCT_ID, product_id);
            contentValues.put(Constant.PRODUCT_WEIGHT, weight);
            contentValues.put(Constant.PRODUCT_WEIGHT_UNIT, weight_unit);
            contentValues.put(Constant.PRODUCT_PRICE, price);
            contentValues.put(Constant.PRODUCT_QTY, qty);
            contentValues.put(Constant.STOCK, stock);
            long check = this.database.insert(Constant.productCart, null, contentValues);
            cursor.close();
            close();
            if (check == -1) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public ArrayList<HashMap<String, String>> getCartProduct() {
        ArrayList<HashMap<String, String>> productCart = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM product_cart", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.CART_ID, cursor.getString(0));
                map.put(Constant.PRODUCT_ID, cursor.getString(1));
                map.put(Constant.PRODUCT_WEIGHT, cursor.getString(2));
                map.put(Constant.PRODUCT_WEIGHT_UNIT, cursor.getString(3));
                map.put(Constant.PRODUCT_PRICE, cursor.getString(4));
                map.put(Constant.PRODUCT_QTY, cursor.getString(5));
                map.put(Constant.STOCK, cursor.getString(6));

                productCart.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return productCart;
    }

    public void insertOrder(String order_id, JSONObject jsonObject) {

        String order_status = Constant.ORDER_STATUS;
        String Pending = Constant.PENDING;
        try {
            String order_date = jsonObject.getString(Constant.ORDER_DATE);
            String order_time = jsonObject.getString(Constant.ORDER_TIME);
            String order_type = jsonObject.getString(Constant.ORDER_TYPE);
            String order_paymentMethod = jsonObject.getString(Constant.ORDER_PAYMENT_METHOD);
            String customer_name = jsonObject.getString(Constant.CUSTOMER_NAME);
            String tax = jsonObject.getString(Constant.TAX);
            String discount = jsonObject.getString(Constant.DISCOUNT);

            ContentValues contentValues = new ContentValues();
            contentValues.put(Constant.INVOICE_ID, order_id);
            contentValues.put(Constant.ORDER_DATE, order_date);
            contentValues.put(Constant.ORDER_TIME, order_time);
            contentValues.put(Constant.ORDER_TYPE, order_type);
            contentValues.put(Constant.ORDER_PAYMENT_METHOD, order_paymentMethod);
            contentValues.put(Constant.CUSTOMER_NAME, customer_name);
            contentValues.put(Constant.TAX, tax);
            contentValues.put(Constant.DISCOUNT, discount);
            contentValues.put(order_status, Pending);

            this.database.insert(Constant.orderList, null, contentValues);
            this.database.delete(Constant.productCart, null, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("lines");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);

                String product_name = jo.getString(Constant.PRODUCT_NAME);
                String product_weight = jo.getString(Constant.PRODUCT_WEIGHT);
                String product_qty = jo.getString(Constant.PRODUCT_QTY);
                String product_price = jo.getString(Constant.PRODUCT_PRICE);
                String product_image = jo.getString(Constant.PRODUCT_IMAGE);
                String product_orderDate = jo.getString(Constant.PRODUCT_ORDER_DATE);

                String product_id = jo.getString(Constant.PRODUCT_ID);
                String stock = jo.getString(Constant.STOCK);
                int updated_stock = Integer.parseInt(stock) - Integer.parseInt(product_qty);

                ContentValues contentValues = new ContentValues();
                contentValues.put(Constant.INVOICE_ID, order_id);
                contentValues.put(Constant.PRODUCT_NAME, product_name);
                contentValues.put(Constant.PRODUCT_WEIGHT, product_weight);
                contentValues.put(Constant.PRODUCT_QTY, product_qty);
                contentValues.put(Constant.PRODUCT_PRICE, product_price);
                contentValues.put(Constant.PRODUCT_IMAGE, product_image);
                contentValues.put(Constant.PRODUCT_ORDER_DATE, product_orderDate);
                contentValues.put(Constant.ORDER_STATUS, Pending);

                ContentValues values = new ContentValues();
                values.put(Constant.PRODUCT_STOCK, updated_stock);

                this.database.insert(Constant.orderDetails, null, contentValues);
                this.database.update(Constant.products, values, "product_id=?", new String[]{product_id});
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        close();
    }

    public ArrayList<HashMap<String, String>> getOrderList() {
        ArrayList<HashMap<String, String>> orderList = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM order_list ORDER BY order_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.INVOICE_ID, cursor.getString(1));
                map.put(Constant.ORDER_DATE, cursor.getString(2));
                map.put(Constant.ORDER_TIME, cursor.getString(3));
                map.put(Constant.ORDER_TYPE, cursor.getString(4));
                map.put(Constant.ORDER_PAYMENT_METHOD, cursor.getString(5));
                map.put(Constant.CUSTOMER_NAME, cursor.getString(6));
                map.put(Constant.TAX, cursor.getString(7));
                map.put(Constant.DISCOUNT, cursor.getString(8));
                map.put(Constant.ORDER_STATUS, cursor.getString(9));

                orderList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return orderList;
    }

    public ArrayList<HashMap<String, String>> searchOrderList(String search) {
        ArrayList<HashMap<String, String>> orderList = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM order_list WHERE customer_name LIKE '%" + search + "%' OR invoice_id LIKE '%" + search + "%'  OR order_status LIKE '%" + search + "%' ORDER BY order_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.INVOICE_ID, cursor.getString(1));
                map.put(Constant.ORDER_DATE, cursor.getString(2));
                map.put(Constant.ORDER_TIME, cursor.getString(3));
                map.put(Constant.ORDER_TYPE, cursor.getString(4));
                map.put(Constant.ORDER_PAYMENT_METHOD, cursor.getString(5));
                map.put(Constant.CUSTOMER_NAME, cursor.getString(6));
                map.put(Constant.TAX, cursor.getString(7));
                map.put(Constant.DISCOUNT, cursor.getString(8));
                map.put(Constant.ORDER_STATUS, cursor.getString(9));

                orderList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return orderList;
    }

    public ArrayList<HashMap<String, String>> getOrderDetailsList(String order_id) {
        ArrayList<HashMap<String, String>> orderDetailsList = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM order_details WHERE invoice_id='" + order_id + "' ORDER BY order_details_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.PRODUCT_NAME, cursor.getString(2));
                map.put(Constant.PRODUCT_WEIGHT, cursor.getString(3));
                map.put(Constant.PRODUCT_QTY, cursor.getString(4));
                map.put(Constant.PRODUCT_PRICE, cursor.getString(5));
                map.put(Constant.PRODUCT_IMAGE, cursor.getString(6));

                orderDetailsList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return orderDetailsList;
    }

    public ArrayList<HashMap<String, String>> getAllSalesItems() {
        ArrayList<HashMap<String, String>> orderDetailsList = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM order_details  WHERE order_status='Completed' ORDER BY order_details_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.PRODUCT_NAME, cursor.getString(2));
                map.put(Constant.PRODUCT_WEIGHT, cursor.getString(3));
                map.put(Constant.PRODUCT_QTY, cursor.getString(4));
                map.put(Constant.PRODUCT_PRICE, cursor.getString(5));
                map.put(Constant.PRODUCT_IMAGE, cursor.getString(6));
                map.put(Constant.PRODUCT_ORDER_DATE, cursor.getString(7));

                orderDetailsList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return orderDetailsList;
    }

    public ArrayList<HashMap<String, String>> getSalesReport(String type) {
        ArrayList<HashMap<String, String>> orderDetailsList = new ArrayList<>();
        Cursor cursor = null;
        switch (type) {
            case "all":
                cursor = this.database.rawQuery("SELECT * FROM order_details WHERE order_status='Completed'   ORDER BY order_details_id DESC", null);
                break;
            case Constant.DAILY:
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM order_details  WHERE order_status='Completed' AND product_order_date='" + currentDate + "' ORDER BY order_Details_id DESC", null);
                break;
            case Constant.MONTHLY:
                String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM order_details  WHERE order_status='Completed' AND strftime('%m', product_order_date) = '" + currentMonth + "' ", null);
                break;
            case Constant.YEARLY:
                String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
                Log.d("YEAR", currentYear);
                cursor = this.database.rawQuery("SELECT * FROM order_details WHERE order_status='Completed' AND strftime('%Y', product_order_date) = '" + currentYear + "' ", null);
                break;
        }
        if (Objects.requireNonNull(cursor).moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.PRODUCT_NAME, cursor.getString(2));
                map.put(Constant.PRODUCT_WEIGHT, cursor.getString(3));
                map.put(Constant.PRODUCT_QTY, cursor.getString(4));
                map.put(Constant.PRODUCT_PRICE, cursor.getString(5));
                map.put(Constant.PRODUCT_IMAGE, cursor.getString(6));
                map.put(Constant.PRODUCT_ORDER_DATE, cursor.getString(7));

                orderDetailsList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return orderDetailsList;
    }

    public ArrayList<HashMap<String, String>> getExpenseReport(String type) {
        ArrayList<HashMap<String, String>> expenseList = new ArrayList<>();
        Cursor cursor = null;
        switch (type) {
            case "all":
                cursor = this.database.rawQuery("SELECT * FROM expense  ORDER BY expense_id DESC", null);
                break;
            case Constant.DAILY:
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                SQLiteDatabase sQLiteDatabase = this.database;
                cursor = sQLiteDatabase.rawQuery("SELECT * FROM expense WHERE   expense_date='" + currentDate + "' ORDER BY expense_id DESC", null);
                break;
            case Constant.MONTHLY:
                String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM expense WHERE strftime('%m', expense_date) = '" + currentMonth + "' ", null);
                break;
            case Constant.YEARLY:
                String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM expense WHERE strftime('%Y', expense_date) = '" + currentYear + "' ", null);
                break;
        }
        if (Objects.requireNonNull(cursor).moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.EXPENSE_ID, cursor.getString(0));
                map.put(Constant.EXPENSE_NAME, cursor.getString(1));
                map.put(Constant.EXPENSE_NOTE, cursor.getString(2));
                map.put(Constant.EXPENSE_AMOUNT, cursor.getString(3));
                map.put(Constant.EXPENSE_DATE, cursor.getString(4));
                map.put(Constant.EXPENSE_TIME, cursor.getString(5));

                expenseList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return expenseList;
    }

    public float getMonthlySalesAmount(String month, String getYear) {
        float total_price = 0.0f;
        Cursor cursor = this.database.rawQuery("SELECT * FROM order_details WHERE order_status='Completed'  AND  strftime('%m', product_order_date) = '" + month + "' AND strftime('%Y', product_order_date) = '" + getYear + "'  ", null);
        if (cursor.moveToFirst()) {
            do {
                total_price += ((float) Integer.parseInt(cursor.getString(4))) * Float.parseFloat(cursor.getString(5));
            } while (cursor.moveToNext());
        } else {
            total_price = 0.0f;
        }
        cursor.close();
        close();
        Log.d("total_price", "" + total_price);
        return total_price;
    }

    public float getMonthlyExpenseAmount(String month, String getYear) {
        float total_cost = 0.0f;
        Cursor cursor = this.database.rawQuery("SELECT * FROM expense WHERE strftime('%m', expense_date) = '" + month + "' AND strftime('%Y', expense_date) = '" + getYear + "'  ", null);
        if (cursor.moveToFirst()) {
            do {
                total_cost += Float.parseFloat(cursor.getString(3));
            } while (cursor.moveToNext());
        } else {
            total_cost = 0.0f;
        }
        cursor.close();
        close();
        Log.d("total_price", "" + total_cost);
        return total_cost;
    }

    public boolean deleteProductFromCart(String id) {
        long check = this.database.delete("product_cart", "cart_id=?", new String[]{id});
        this.database.close();
        return check == 1;
    }

    public ArrayList<HashMap<String, String>> getTabProducts(String category_id) {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM products WHERE product_category = '" + category_id + "' ORDER BY product_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.PRODUCT_ID, cursor.getString(0));
                map.put(Constant.PRODUCT_NAME, cursor.getString(1));
                map.put(Constant.PRODUCT_CODE, cursor.getString(2));
                map.put(Constant.PRODUCT_CATEGORY, cursor.getString(3));
                map.put(Constant.PRODUCT_DESCRIPTION, cursor.getString(4));
                map.put(Constant.PRODUCT_BUY_PRICE, cursor.getString(5));
                map.put(Constant.PRODUCT_SELL_PRICE, cursor.getString(6));
                map.put(Constant.PRODUCT_SUPPLIER, cursor.getString(7));
                map.put(Constant.PRODUCT_IMAGE, cursor.getString(8));
                map.put(Constant.PRODUCT_STOCK, cursor.getString(9));
                map.put(Constant.PRODUCT_WEIGHT_UNIT_ID, cursor.getString(10));
                map.put(Constant.PRODUCT_WEIGHT, cursor.getString(11));

                product.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return product;
    }

    public int getCartItemCount() {
        Cursor cursor = this.database.rawQuery("SELECT * FROM product_cart", null);
        int itemCount = cursor.getCount();
        cursor.close();
        close();
        return itemCount;
    }

    public void updateProductQty(String id, String qty) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.PRODUCT_QTY, qty);
        this.database.update("product_cart", contentValues, "cart_id=?", new String[]{id});
    }

    public String getProductName(String product_id) {
        String product_name = "N/A";
        Cursor cursor = this.database.rawQuery("SELECT * FROM products WHERE product_id='" + product_id + "'", null);
        if (cursor.moveToFirst()) {
            do {
                product_name = cursor.getString(1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return product_name;
    }

    public String getCurrency() {
        String currency = "N/A";
        Cursor cursor = this.database.rawQuery("SELECT * FROM shop", null);
        if (cursor.moveToFirst()) {
            do {
                currency = cursor.getString(5);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return currency;
    }

    public double getTotalPrice() {
        double total_price = Utils.DOUBLE_EPSILON;
        Cursor cursor = this.database.rawQuery("SELECT * FROM product_cart", null);
        if (cursor.moveToFirst()) {
            do {
                double price = Double.parseDouble(cursor.getString(4));
                int qty = Integer.parseInt(cursor.getString(5));
                total_price += price * qty;
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return total_price;
    }

    public double getTotalDiscount(String type) {
        Cursor cursor;
        double total_discount = Utils.DOUBLE_EPSILON;
        switch (type) {
            case Constant.MONTHLY:
                String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM order_list WHERE order_status='Completed'  AND strftime('%m', order_date) = '" + currentMonth + "' ", null);
                break;
            case Constant.YEARLY:
                String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM order_list WHERE order_status='Completed'  AND strftime('%Y', order_date) = '" + currentYear + "' ", null);
                break;
            case Constant.DAILY:
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM order_list WHERE order_status='Completed'  AND   order_date='" + currentDate + "' ORDER BY order_id DESC", null);
                break;
            default:
                cursor = this.database.rawQuery("SELECT * FROM order_list WHERE order_status='Completed'", null);
                break;
        }
        if (cursor.moveToFirst()) {
            do {
                total_discount += Double.parseDouble(cursor.getString(8));
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return total_discount;
    }

    public double getTotalDiscountForGraph(String type, int currentYear) {
        Cursor cursor;
        double total_discount = Utils.DOUBLE_EPSILON;
        switch (type) {
            case Constant.MONTHLY:
                String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM order_list WHERE order_status='Completed'  AND strftime('%m', order_date) = '" + currentMonth + "' ", null);
                break;
            case Constant.YEARLY:
                cursor = this.database.rawQuery("SELECT * FROM order_list WHERE order_status='Completed'  AND strftime('%Y', order_date) = '" + currentYear + "' ", null);
                break;
            case Constant.DAILY:
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM order_list WHERE order_status='Completed'  AND   order_date='" + currentDate + "' ORDER BY order_id DESC", null);
                break;
            default:
                cursor = this.database.rawQuery("SELECT * FROM order_list WHERE order_status='Completed'", null);
                break;
        }
        if (cursor.moveToFirst()) {
            do {
                total_discount += Double.parseDouble(cursor.getString(8));
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return total_discount;
    }

    public double getTotalTax(String type) {
        Cursor cursor;
        double total_tax = Utils.DOUBLE_EPSILON;
        switch (type) {
            case Constant.MONTHLY:
                String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM order_list WHERE order_status='Completed'  AND  strftime('%m', order_date) = '" + currentMonth + "' ", null);
                break;
            case Constant.YEARLY:
                String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM order_list WHERE order_status='Completed'  AND strftime('%Y', order_date) = '" + currentYear + "' ", null);
                break;
            case Constant.DAILY:
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM order_list WHERE order_status='Completed'  AND   order_date='" + currentDate + "' ORDER BY order_id DESC", null);
                break;
            default:
                cursor = this.database.rawQuery("SELECT * FROM order_list WHERE order_status='Completed' ", null);
                break;
        }
        if (cursor.moveToFirst()) {
            do {
                total_tax += Double.parseDouble(cursor.getString(7));
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return total_tax;
    }

    public double getTotalTaxForGraph(String type, int currentYear) {
        Cursor cursor;
        double total_tax = Utils.DOUBLE_EPSILON;
        switch (type) {
            case Constant.MONTHLY:
                String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM order_list WHERE order_status='Completed'  AND  strftime('%m', order_date) = '" + currentMonth + "' ", null);
                break;
            case Constant.YEARLY:
                cursor = this.database.rawQuery("SELECT * FROM order_list WHERE order_status='Completed'  AND strftime('%Y', order_date) = '" + currentYear + "' ", null);
                break;
            case Constant.DAILY:
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM order_list WHERE order_status='Completed'  AND   order_date='" + currentDate + "' ORDER BY order_id DESC", null);
                break;
            default:
                cursor = this.database.rawQuery("SELECT * FROM order_list WHERE order_status='Completed' ", null);
                break;
        }
        if (cursor.moveToFirst()) {
            do {
                total_tax += Double.parseDouble(cursor.getString(7));
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return total_tax;
    }

    public double getTotalOrderPrice(String type) {
        Cursor cursor;
        double total_price = Utils.DOUBLE_EPSILON;
        switch (type) {
            case Constant.MONTHLY:
                String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM order_details WHERE order_status='Completed'  AND strftime('%m', product_order_date) = '" + currentMonth + "' ", null);
                break;
            case Constant.YEARLY:
                String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM order_details WHERE order_status='Completed'  AND  strftime('%Y', product_order_date) = '" + currentYear + "' ", null);
                break;
            case Constant.DAILY:
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM order_details WHERE order_status='Completed'  AND   product_order_date='" + currentDate + "' ORDER BY order_Details_id DESC", null);
                break;
            default:
                cursor = this.database.rawQuery("SELECT * FROM order_details WHERE order_status='Completed' ", null);
                break;
        }
        if (cursor.moveToFirst()) {
            do {
                int qty = Integer.parseInt(cursor.getString(4));
                double price = Double.parseDouble(cursor.getString(5));
                total_price += price * qty;
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return total_price;
    }

    public double getTotalOrderPriceForGraph(String type, int currentYear) {
        Cursor cursor;
        double total_price = Utils.DOUBLE_EPSILON;
        switch (type) {
            case Constant.MONTHLY:
                String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM order_details WHERE order_status='Completed'  AND strftime('%m', product_order_date) = '" + currentMonth + "' ", null);
                break;
            case Constant.YEARLY:
                cursor = this.database.rawQuery("SELECT * FROM order_details WHERE order_status='Completed'  AND  strftime('%Y', product_order_date) = '" + currentYear + "' ", null);
                break;
            case Constant.DAILY:
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM order_details WHERE order_status='Completed'  AND   product_order_date='" + currentDate + "' ORDER BY order_Details_id DESC", null);
                break;
            default:
                cursor = this.database.rawQuery("SELECT * FROM order_details WHERE order_status='Completed' ", null);
                break;
        }
        if (cursor.moveToFirst()) {
            do {
                double qty = Integer.parseInt(cursor.getString(4));
                double price = Double.parseDouble(cursor.getString(5));
                total_price += price * qty;
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return total_price;
    }

    public double totalOrderPrice(String invoice_id) {
        double total_price = Utils.DOUBLE_EPSILON;
        Cursor cursor = this.database.rawQuery("SELECT * FROM order_details WHERE invoice_id='" + invoice_id + "'", null);
        if (cursor.moveToFirst()) {
            do {
                int qty = Integer.parseInt(cursor.getString(4));
                double price = Double.parseDouble(cursor.getString(5));
                total_price += price * qty;
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return total_price;
    }

    public double getTotalExpense(String type) {
        Cursor cursor;
        double total_cost = Utils.DOUBLE_EPSILON;
        switch (type) {
            case Constant.MONTHLY:
                String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM expense WHERE strftime('%m', expense_date) = '" + currentMonth + "' ", null);
                break;
            case Constant.YEARLY:
                String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM expense WHERE strftime('%Y', expense_date) = '" + currentYear + "' ", null);
                break;
            case Constant.DAILY:
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                SQLiteDatabase sQLiteDatabase = this.database;
                cursor = sQLiteDatabase.rawQuery("SELECT * FROM expense WHERE   expense_date='" + currentDate + "' ORDER BY expense_id DESC", null);
                break;
            default:
                cursor = this.database.rawQuery("SELECT * FROM expense", null);
                break;
        }
        if (cursor.moveToFirst()) {
            do {
                total_cost += Double.parseDouble(cursor.getString(3));
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return total_cost;
    }

    public double getTotalExpenseForGraph(String type, int currentYear) {
        Cursor cursor;
        double total_cost = Utils.DOUBLE_EPSILON;
        switch (type) {
            case Constant.MONTHLY:
                String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());
                cursor = this.database.rawQuery("SELECT * FROM expense WHERE strftime('%m', expense_date) = '" + currentMonth + "' ", null);
                break;
            case Constant.YEARLY:
                cursor = this.database.rawQuery("SELECT * FROM expense WHERE strftime('%Y', expense_date) = '" + currentYear + "' ", null);
                break;
            case Constant.DAILY:
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                SQLiteDatabase sQLiteDatabase = this.database;
                cursor = sQLiteDatabase.rawQuery("SELECT * FROM expense WHERE   expense_date='" + currentDate + "' ORDER BY expense_id DESC", null);
                break;
            default:
                cursor = this.database.rawQuery("SELECT * FROM expense", null);
                break;
        }
        if (cursor.moveToFirst()) {
            do {
                total_cost += Double.parseDouble(cursor.getString(3));
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return total_cost;
    }

    public ArrayList<HashMap<String, String>> getCustomers() {
        ArrayList<HashMap<String, String>> customer = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM customers ORDER BY customer_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.CUSTOMER_ID, cursor.getString(0));
                map.put(Constant.CUSTOMER_NAME, cursor.getString(1));
                map.put(Constant.CUSTOMER_CELL, cursor.getString(2));
                map.put(Constant.CUSTOMER_EMAIL, cursor.getString(3));
                map.put(Constant.CUSTOMER_ADDRESS, cursor.getString(4));

                customer.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return customer;
    }

    public ArrayList<HashMap<String, String>> getOrderType() {
        ArrayList<HashMap<String, String>> order_type = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM order_type ORDER BY order_type_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.ORDER_TYPE_ID, cursor.getString(0));
                map.put(Constant.ORDER_TYPE_NAME, cursor.getString(1));

                order_type.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return order_type;
    }

    public ArrayList<HashMap<String, String>> getPaymentMethod() {
        ArrayList<HashMap<String, String>> payment_method = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM payment_method ORDER BY payment_method_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.PAYMENT_METHOD_ID, cursor.getString(0));
                map.put(Constant.PAYMENT_METHOD_NAME, cursor.getString(1));

                payment_method.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return payment_method;
    }

    public ArrayList<HashMap<String, String>> searchCustomers(String search) {
        ArrayList<HashMap<String, String>> customer = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM customers WHERE customer_name LIKE '%" + search + "%' ORDER BY customer_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.CUSTOMER_ID, cursor.getString(0));
                map.put(Constant.CUSTOMER_NAME, cursor.getString(1));
                map.put(Constant.CUSTOMER_CELL, cursor.getString(2));
                map.put(Constant.CUSTOMER_EMAIL, cursor.getString(3));
                map.put(Constant.CUSTOMER_ADDRESS, cursor.getString(4));

                customer.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return customer;
    }

    public ArrayList<HashMap<String, String>> searchSuppliers(String search) {
        ArrayList<HashMap<String, String>> supplier = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM suppliers WHERE suppliers_name LIKE '%" + search + "%' ORDER BY suppliers_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.SUPPLIERS_ID, cursor.getString(0));
                map.put(Constant.SUPPLIERS_NAME, cursor.getString(1));
                map.put(Constant.SUPPLIERS_CONTACT_PERSON, cursor.getString(2));
                map.put(Constant.SUPPLIERS_CELL, cursor.getString(3));
                map.put(Constant.SUPPLIERS_EMAIL, cursor.getString(4));
                map.put(Constant.SUPPLIERS_ADDRESS, cursor.getString(5));

                supplier.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return supplier;
    }

    public ArrayList<HashMap<String, String>> getShopInformation() {
        ArrayList<HashMap<String, String>> shop_info = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM shop", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.SHOP_NAME, cursor.getString(1));
                map.put(Constant.SHOP_CONTACT, cursor.getString(2));
                map.put(Constant.SHOP_EMAIL, cursor.getString(3));
                map.put(Constant.SHOP_ADDRESS, cursor.getString(4));
                map.put(Constant.SHOP_CURRENCY, cursor.getString(5));
                map.put(Constant.TAX, cursor.getString(6));

                shop_info.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return shop_info;
    }

    public ArrayList<HashMap<String, String>> getProducts() {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM products ORDER BY product_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.PRODUCT_ID, cursor.getString(0));
                map.put(Constant.PRODUCT_NAME, cursor.getString(1));
                map.put(Constant.PRODUCT_CODE, cursor.getString(2));
                map.put(Constant.PRODUCT_CATEGORY, cursor.getString(3));
                map.put(Constant.PRODUCT_DESCRIPTION, cursor.getString(4));
                map.put(Constant.PRODUCT_BUY_PRICE, cursor.getString(5));
                map.put(Constant.PRODUCT_SELL_PRICE, cursor.getString(6));
                map.put(Constant.PRODUCT_SUPPLIER, cursor.getString(7));
                map.put(Constant.PRODUCT_IMAGE, cursor.getString(8));
                map.put(Constant.PRODUCT_STOCK, cursor.getString(9));
                map.put(Constant.PRODUCT_WEIGHT_UNIT_ID, cursor.getString(10));
                map.put(Constant.PRODUCT_WEIGHT, cursor.getString(11));

                product.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return product;
    }

    public ArrayList<HashMap<String, String>> getProductsInfo(String product_id) {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM products WHERE product_id='" + product_id + "'", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.PRODUCT_ID, cursor.getString(0));
                map.put(Constant.PRODUCT_NAME, cursor.getString(1));
                map.put(Constant.PRODUCT_CODE, cursor.getString(2));
                map.put(Constant.PRODUCT_CATEGORY, cursor.getString(3));
                map.put(Constant.PRODUCT_DESCRIPTION, cursor.getString(4));
                map.put(Constant.PRODUCT_BUY_PRICE, cursor.getString(5));
                map.put(Constant.PRODUCT_SELL_PRICE, cursor.getString(6));
                map.put(Constant.PRODUCT_SUPPLIER, cursor.getString(7));
                map.put(Constant.PRODUCT_IMAGE, cursor.getString(8));
                map.put(Constant.PRODUCT_STOCK, cursor.getString(9));
                map.put(Constant.PRODUCT_WEIGHT_UNIT_ID, cursor.getString(10));
                map.put(Constant.PRODUCT_WEIGHT, cursor.getString(11));

                product.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return product;
    }

    public ArrayList<HashMap<String, String>> getAllExpense() {
        ArrayList<HashMap<String, String>> expense = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM expense ORDER BY expense_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.EXPENSE_ID, cursor.getString(0));
                map.put(Constant.EXPENSE_NAME, cursor.getString(1));
                map.put(Constant.EXPENSE_NOTE, cursor.getString(2));
                map.put(Constant.EXPENSE_AMOUNT, cursor.getString(3));
                map.put(Constant.EXPENSE_DATE, cursor.getString(4));
                map.put(Constant.EXPENSE_TIME, cursor.getString(5));

                expense.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return expense;
    }

    public ArrayList<HashMap<String, String>> getProductCategory() {
        ArrayList<HashMap<String, String>> product_category = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM product_category ORDER BY category_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.CATEGORY_ID, cursor.getString(0));
                map.put(Constant.CATEGORY_NAME, cursor.getString(1));

                product_category.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return product_category;
    }

    public ArrayList<HashMap<String, String>> getProductWeight() {
        ArrayList<HashMap<String, String>> product_weight = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM product_weight ORDER BY weight_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.WEIGHT_ID, cursor.getString(0));
                map.put(Constant.WEIGHT_UNIT, cursor.getString(1));

                product_weight.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return product_weight;
    }

    public ArrayList<HashMap<String, String>> searchOrderType(String search) {
        ArrayList<HashMap<String, String>> order_type = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM order_type WHERE order_type_name LIKE '%" + search + "%' ORDER BY order_type_id DESC ", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.ORDER_TYPE_ID, cursor.getString(0));
                map.put(Constant.ORDER_TYPE_NAME, cursor.getString(1));

                order_type.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return order_type;
    }

    public ArrayList<HashMap<String, String>> searchProductCategory(String search) {
        ArrayList<HashMap<String, String>> product_category = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM product_category WHERE category_name LIKE '%" + search + "%' ORDER BY category_id DESC ", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.CATEGORY_ID, cursor.getString(0));
                map.put(Constant.CATEGORY_NAME, cursor.getString(1));

                product_category.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return product_category;
    }

    public ArrayList<HashMap<String, String>> searchPaymentMethod(String search) {
        ArrayList<HashMap<String, String>> payment_method = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM payment_method WHERE payment_method_name LIKE '%" + search + "%' ORDER BY payment_method_id DESC ", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.PAYMENT_METHOD_ID, cursor.getString(0));
                map.put(Constant.PAYMENT_METHOD_NAME, cursor.getString(1));

                payment_method.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return payment_method;
    }

    public ArrayList<HashMap<String, String>> searchProductWeight(String search) {
        ArrayList<HashMap<String, String>> product_weight = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM product_weight WHERE weight_unit LIKE '%" + search + "%' ORDER BY weight_id DESC ", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.WEIGHT_ID, cursor.getString(0));
                map.put(Constant.WEIGHT_UNIT, cursor.getString(1));

                product_weight.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return product_weight;
    }

    public ArrayList<HashMap<String, String>> getProductSupplier() {
        ArrayList<HashMap<String, String>> product_suppliers = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM suppliers", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.SUPPLIERS_ID, cursor.getString(0));
                map.put(Constant.SUPPLIERS_NAME, cursor.getString(1));

                product_suppliers.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return product_suppliers;
    }

    public ArrayList<HashMap<String, String>> getWeightUnit() {
        ArrayList<HashMap<String, String>> product_weight_unit = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM product_weight", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.WEIGHT_ID, cursor.getString(0));
                map.put(Constant.WEIGHT_UNIT, cursor.getString(1));

                product_weight_unit.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return product_weight_unit;
    }

    public ArrayList<HashMap<String, String>> searchExpense(String search) {
        ArrayList<HashMap<String, String>> expense = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM expense WHERE expense_name LIKE '%" + search + "%' ORDER BY expense_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.EXPENSE_ID, cursor.getString(0));
                map.put(Constant.EXPENSE_NAME, cursor.getString(1));
                map.put(Constant.EXPENSE_NOTE, cursor.getString(2));
                map.put(Constant.EXPENSE_AMOUNT, cursor.getString(3));
                map.put(Constant.EXPENSE_DATE, cursor.getString(4));
                map.put(Constant.EXPENSE_TIME, cursor.getString(5));

                expense.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return expense;
    }

    public ArrayList<HashMap<String, String>> getSearchProducts(String search) {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM products WHERE product_name LIKE '%" + search + "%' OR product_code LIKE '%" + search + "%' ORDER BY product_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.PRODUCT_ID, cursor.getString(0));
                map.put(Constant.PRODUCT_NAME, cursor.getString(1));
                map.put(Constant.PRODUCT_CODE, cursor.getString(2));
                map.put(Constant.PRODUCT_CATEGORY, cursor.getString(3));
                map.put(Constant.PRODUCT_DESCRIPTION, cursor.getString(4));
                map.put(Constant.PRODUCT_BUY_PRICE, cursor.getString(5));
                map.put(Constant.PRODUCT_SELL_PRICE, cursor.getString(6));
                map.put(Constant.PRODUCT_SUPPLIER, cursor.getString(7));
                map.put(Constant.PRODUCT_IMAGE, cursor.getString(8));
                map.put(Constant.PRODUCT_STOCK, cursor.getString(9));
                map.put(Constant.PRODUCT_WEIGHT_UNIT_ID, cursor.getString(10));
                map.put(Constant.PRODUCT_WEIGHT, cursor.getString(11));

                product.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return product;
    }

    public ArrayList<HashMap<String, String>> getSuppliers() {
        ArrayList<HashMap<String, String>> supplier = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM suppliers ORDER BY suppliers_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.SUPPLIERS_ID, cursor.getString(0));
                map.put(Constant.SUPPLIERS_NAME, cursor.getString(1));
                map.put(Constant.SUPPLIERS_CONTACT_PERSON, cursor.getString(2));
                map.put(Constant.SUPPLIERS_CELL, cursor.getString(3));
                map.put(Constant.SUPPLIERS_EMAIL, cursor.getString(4));
                map.put(Constant.SUPPLIERS_ADDRESS, cursor.getString(5));

                supplier.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return supplier;
    }

    public boolean deleteCustomer(String customer_id) {
        long check = this.database.delete(Constant.customers, "customer_id=?", new String[]{customer_id});
        close();
        return check == 1;
    }

    public boolean deleteCategory(String category_id) {
        long check = this.database.delete(Constant.PRODUCT_CATEGORY, "category_id=?", new String[]{category_id});
        close();
        return check == 1;
    }

    public boolean deleteOrderType(String delivery_id) {
        long check = this.database.delete(Constant.ORDER_TYPE, "order_type_id=?", new String[]{delivery_id});
        close();
        return check == 1;
    }

    public boolean deletePaymentMethod(String payment_method_id) {
        long check = this.database.delete(Constant.paymentMethod, "payment_method_id=?", new String[]{payment_method_id});
        close();
        return check == 1;
    }

    public boolean deleteWeight(String weight_id) {
        long check = this.database.delete(Constant.PRODUCT_WEIGHT, "weight_id=?", new String[]{weight_id});
        close();
        return check == 1;
    }

    public boolean updateOrder(String invoiceId, String orderStatus) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.ORDER_STATUS, orderStatus);
        this.database.update(Constant.orderDetails, contentValues, "invoice_id=?", new String[]{invoiceId});
        long check = this.database.update(Constant.orderList, contentValues, "invoice_id=?", new String[]{invoiceId});
        close();
        return check != -1;
    }

    public boolean deleteOrder(String invoice_id) {
        long check = this.database.delete("order_list", "invoice_id=?", new String[]{invoice_id});
        //long delete = this.database.delete("order_details", "invoice_id=?", new String[]{invoice_id});
        close();
        return check == 1;
    }

    public Boolean deleteProduct(String product_id) {
        long check = this.database.delete("products", "product_id=?", new String[]{product_id});
        //long delete = this.database.delete("product_cart", "product_id=?", new String[]{product_id});
        close();
        return check == 1;
    }

    public boolean deleteExpense(String expense_id) {
        long check = this.database.delete(Constant.expense, "expense_id=?", new String[]{expense_id});
        close();
        return check == 1;
    }

    public boolean deleteSupplier(String supplier_id) {
        long check = this.database.delete(Constant.suppliers, "suppliers_id=?", new String[]{supplier_id});
        close();
        return check == 1;
    }
}