package com.example.crm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AtomicFile;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class AbcAnalyzeActivity extends AppCompatActivity {
    private ClientsDBHelper dbHelper;
    ArrayList<Integer> idProduct;
    ArrayList<String> names;
    ArrayList<Integer> price;
    ArrayList<Integer> amount;
    ArrayList<Integer> fullPrice;
    ArrayList<Integer> type;
    ArrayList<Double> percentsPrice;
    ArrayList<Integer> idTypes;
    ArrayList<String> nameTypes;
    ArrayList<Double> percentsTypes;
    ArrayList<Integer> sumTypes;
    Integer amountSum = 0;
    Integer priceSum = 0;
    SQLiteDatabase base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abc_analyze);

        Toolbar toolbar = findViewById(R.id.toolbarABCAnalyzeActivity);
        toolbar.setTitle("ABC анализ");
        setSupportActionBar(toolbar);

        dbHelper = new ClientsDBHelper(this);
        base = dbHelper.getReadableDatabase();
        DBtoArray(base);
        byGroups(base);
        sortByPercentsType();
        printTable(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_abc,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            //по продуктам
            case R.id.productsABC:
                DBtoArray(base);
                sortByPercents();
                printTable(false);

                item.setChecked(true);
                break;
            //по категориям
            case R.id.typesABC:
                byGroups(base);
                sortByPercentsType();
                printTable(true);
                item.setChecked(true);
                break;
        }
        return true;
    }

    public void DBtoArray(SQLiteDatabase DB)
    {
        Cursor productsCursor = DB.query(ProductContract.ProductEntry.TABLE_NAME,null,null,null,null,null, ProductContract.ProductEntry.COLUMN_ID);
        productsCursor.moveToFirst();

        idProduct = new ArrayList<>();
        names = new ArrayList<>();
        price = new ArrayList<>();
        amount = new ArrayList<>();
        fullPrice = new ArrayList<>();
        type =new ArrayList<>();
        percentsPrice = new ArrayList<>();
        amountSum = 0;
        priceSum = 0;

        //заполнение списка продуктов
        for (int i = 0;i < productsCursor.getCount();i++)
        {
            idProduct.add(productsCursor.getInt(productsCursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_ID)));
            names.add(productsCursor.getString(productsCursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME)));
            price.add(productsCursor.getInt(productsCursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_Price)));
            type.add(productsCursor.getInt(productsCursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_TYPE)));

            productsCursor.moveToNext();
        }

        Cursor HPCursor = DB.query(HistoryProductContract.HistoryProductEntry.TABLE_NAME,null,null,null,null,null, null);
        HPCursor.moveToFirst();

        ///подсчет суммы
        for (int i =0;i<idProduct.size();i++)
        {
            Cursor cursor = DB.query(HistoryProductContract.HistoryProductEntry.TABLE_NAME,null,"Product_id = ?",new String[]{idProduct.get(i).toString()},null,null, null);
            cursor.moveToFirst();
            Integer am = 0;
            for (int j = 0;j < cursor.getCount();j++)
            {
                am = am + cursor.getInt(cursor.getColumnIndex(HistoryProductContract.HistoryProductEntry.COLUMN_AMMOUNT));
                cursor.moveToNext();
            }
            fullPrice.add(am*price.get(i));
            priceSum = priceSum + fullPrice.get(i);
            amount.add(am);
            amountSum = amountSum + am;
        }
        //вычисление доли
        for (int i = 0;i<idProduct.size();i++)
        {
            Double p = (double)priceSum/100;
            if (fullPrice.get(i)!=0) percentsPrice.add(fullPrice.get(i)/p);
            else percentsPrice.add(0.0);
        }

    }
    public void byGroups(SQLiteDatabase DB)
    {
        idTypes = new ArrayList<Integer>();
        nameTypes = new ArrayList<String>();
        percentsTypes = new ArrayList<Double>();
        sumTypes = new ArrayList<Integer>();
        //заполнение категорий
        Cursor cursor = DB.query(ProductsTypeContract.ProductsTypesEntry.TABLE_NAME,null,null,null,null,null, ProductsTypeContract.ProductsTypesEntry.COLUMN_idPT);
        cursor.moveToFirst();
        for (int i = 0;i < cursor.getCount();i++)
        {
            idTypes.add(cursor.getInt(cursor.getColumnIndex(ProductsTypeContract.ProductsTypesEntry.COLUMN_idPT)));
            nameTypes.add(cursor.getString(cursor.getColumnIndex(ProductsTypeContract.ProductsTypesEntry.COLUMN_Name)));
            sumTypes.add(0);
            percentsTypes.add(0.0);

            cursor.moveToNext();
        }

        //сумирование процентов в категории
        for (int i = 0;i < idProduct.size();i++)
        {
            Integer index = type.get(i) - 1;
            percentsTypes.set(index,percentsTypes.get(index) + percentsPrice.get(i));
            sumTypes.set(index,sumTypes.get(index) + (price.get(i) * amount.get(i)));
        }

    }

    //сортировка
    public void sortByPercents()
    {
        boolean needIteration = true;
        while (needIteration) {
            needIteration = false;
            for (int i = 1; i < percentsPrice.size(); i++) {
                if (percentsPrice.get(i) < percentsPrice.get(i-1)) {
                    swapInt(idProduct,i,i-1);
                    swapStr(names,i,i-1);
                    swapInt(price,i,i-1);
                    swapInt(amount,i,i-1);
                    swapInt(fullPrice,i,i-1);
                    swapInt(type,i,i-1);
                    swapD(percentsPrice, i, i-1);
                    needIteration = true;
                }
            }
        }
    }

    //сортировка по категориям
    public void sortByPercentsType()
    {
        boolean needIteration = true;
        while (needIteration) {
            needIteration = false;
            for (int i = 1; i < idTypes.size(); i++) {
                if (percentsTypes.get(i) < percentsTypes.get(i-1)) {
                    swapInt(idTypes,i,i-1);
                    swapStr(nameTypes,i,i-1);
                    swapD(percentsTypes,i,i-1);
                    swapInt(sumTypes,i,i-1);
                    needIteration = true;
                }
            }
        }
    }

    ///вывод таблицы
    public void printTable(Boolean t) {
        TableLayout tableLayout = findViewById(R.id.tableLayoutABC);
        cleanTable(tableLayout);
        TextView textView;
        TableLayout.LayoutParams lp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.leftMargin = 10;
        lp.rightMargin = 10;
        LinearLayout.LayoutParams cells = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Integer accum = 0;
        if (t) {
            for (int i = idTypes.size() -1; i >= 0; i--) {
                TableRow tableRow1 = new TableRow(this);
                tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                tableLayout.addView(tableRow1);
                Integer percent = (int) Math.round(percentsTypes.get(i));

                if (percent != 0 && sumTypes.get(i) !=0) {
                    textView = new TextView(this);
                    textView.setText(idTypes.get(i).toString());
                    textView.setLayoutParams(cells);
                    textView.setTextColor(Color.BLACK);
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView.setPadding(5, 10, 5, 10);
                    textView.setBackgroundResource(R.drawable.cells_shape);
                    tableRow1.addView(textView);

                    textView = new TextView(this);
                    textView.setText(nameTypes.get(i));
                    textView.setLayoutParams(cells);
                    textView.setTextColor(Color.BLACK);
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView.setPadding(5, 10, 5, 10);
                    textView.setBackgroundResource(R.drawable.cells_shape);
                    tableRow1.addView(textView);

                    textView = new TextView(this);
                    textView.setText(sumTypes.get(i).toString() + " р.");
                    textView.setLayoutParams(cells);
                    textView.setTextColor(Color.BLACK);
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView.setPadding(5, 10, 5, 10);
                    textView.setBackgroundResource(R.drawable.cells_shape);
                    tableRow1.addView(textView);


                    textView = new TextView(this);
                    textView.setText(percent.toString() + "%");
                    textView.setLayoutParams(cells);
                    textView.setTextColor(Color.BLACK);
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView.setPadding(5, 10, 5, 10);
                    textView.setBackgroundResource(R.drawable.cells_shape);
                    tableRow1.addView(textView);


                    accum = accum + percent;
                    textView = new TextView(this);
                    if (accum <= 100) textView.setText(accum + "%");
                    else textView.setText("100%");
                    textView.setLayoutParams(cells);
                    textView.setTextColor(Color.BLACK);
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView.setPadding(5, 10, 5, 10);
                    textView.setBackgroundResource(R.drawable.cells_shape);
                    tableRow1.addView(textView);

                    textView = new TextView(this);

                    textView.setLayoutParams(cells);
                    textView.setTextColor(Color.BLACK);
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView.setPadding(5, 10, 5, 10);
                    textView.setBackgroundResource(R.drawable.cells_shape);
                    tableRow1.addView(textView);

                    Double minP = percentsTypes.get(0);
                    Double maxP = percentsTypes.get(percentsTypes.size()-1);
                    Double midP = (minP + maxP) / 2;
                    Double step = (maxP - midP) / 3;
                    if (percent < midP) textView.setText("C");
                    else if(percent >= midP && percent >= maxP - step) textView.setText("A");
                    else textView.setText("B");
                    /*if(percent >= 40) textView.setText("A");
                    else
                    if (percent >= 15 && percent< 40) textView.setText("B");
                    else textView.setText("C");*/


                    TextView textViewGroup = findViewById(R.id.textViewGroup);
                    textViewGroup.setVisibility(View.VISIBLE);
                }
            }
        }
        else
            {
                for (int i = idProduct.size() - 1; i >= 0; i--) {
                    Integer sum = amount.get(i) * price.get(i);
                    Integer percent = (int) Math.round(percentsPrice.get(i));
                    if (sum != 0 && percent != 0) {
                        TableRow tableRow1 = new TableRow(this);
                        tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tableLayout.addView(tableRow1);

                        textView = new TextView(this);
                        textView.setText(idProduct.get(i).toString());
                        textView.setLayoutParams(cells);
                        textView.setTextColor(Color.BLACK);
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textView.setPadding(5, 10, 5, 10);
                        textView.setBackgroundResource(R.drawable.cells_shape);
                        tableRow1.addView(textView);

                        textView = new TextView(this);
                        textView.setText(names.get(i));
                        textView.setLayoutParams(cells);
                        textView.setTextColor(Color.BLACK);
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textView.setPadding(5, 10, 5, 10);
                        textView.setBackgroundResource(R.drawable.cells_shape);
                        tableRow1.addView(textView);


                        textView = new TextView(this);
                        textView.setText(sum.toString() + " р.");
                        textView.setLayoutParams(cells);
                        textView.setTextColor(Color.BLACK);
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textView.setPadding(5, 10, 5, 10);
                        textView.setBackgroundResource(R.drawable.cells_shape);
                        tableRow1.addView(textView);


                        textView = new TextView(this);
                        textView.setText(percent.toString() + "%");
                        textView.setLayoutParams(cells);
                        textView.setTextColor(Color.BLACK);
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textView.setPadding(5, 10, 5, 10);
                        textView.setBackgroundResource(R.drawable.cells_shape);
                        tableRow1.addView(textView);

                        accum = accum + percent;
                        textView = new TextView(this);
                        if (accum <= 100) textView.setText(accum + "%");
                        else textView.setText("100%");
                        textView.setLayoutParams(cells);
                        textView.setTextColor(Color.BLACK);
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textView.setPadding(5, 10, 5, 10);
                        textView.setBackgroundResource(R.drawable.cells_shape);
                        tableRow1.addView(textView);

                        TextView textViewGroup = findViewById(R.id.textViewGroup);
                        textViewGroup.setVisibility(View.GONE);
                    }


                }
            }
        }


    public void swapD(ArrayList<Double> arrayList,Integer ind1,Integer ind2)
    {
        Double tmp = arrayList.get(ind1);
        arrayList.set(ind1,arrayList.get(ind2));
        arrayList.set(ind2,tmp);
    }
    public void swapInt(ArrayList<Integer> arrayList,Integer ind1,Integer ind2)
    {
        Integer tmp = arrayList.get(ind1);
        arrayList.set(ind1,arrayList.get(ind2));
        arrayList.set(ind2,tmp);
    }
    public void swapStr(ArrayList<String> arrayList,Integer ind1,Integer ind2)
    {
        String tmp = arrayList.get(ind1);
        arrayList.set(ind1,arrayList.get(ind2));
        arrayList.set(ind2,tmp);
    }

    //очистка таблицы
    private void cleanTable(TableLayout table) {

        int childCount = table.getChildCount();

        // Remove all rows except the first one
        if (childCount > 1) {
            table.removeViews(1, childCount - 1);
        }
    }
}