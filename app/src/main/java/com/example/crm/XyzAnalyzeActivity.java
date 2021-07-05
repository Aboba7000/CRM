package com.example.crm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class XyzAnalyzeActivity extends AppCompatActivity {
    private ClientsDBHelper dbHelper;
    SQLiteDatabase base;
    String year;
    ArrayList<String> periods1;
    ArrayList<String> periods2;
    ArrayList<Integer> idProduct;
    ArrayList <String> nameProduct;
    ArrayList <Integer> typeProduct;
    ArrayList<Integer> KV1;
    ArrayList<Integer> KV2;
    ArrayList<Integer> KV3;
    ArrayList<Integer> KV4;
    ArrayList<Double> V;
    ArrayList<Integer> KV1T;
    ArrayList<Integer> KV2T;
    ArrayList<Integer> KV3T;
    ArrayList<Integer> KV4T;
    ArrayList<Integer> idType;
    ArrayList<String> nameType;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xyz_analyze);

        Toolbar toolbar = findViewById(R.id.toolbarXYZAnalyzeActivity);
        toolbar.setTitle("XYZ анализ");
        setSupportActionBar(toolbar);
        year = "2020";
        dbHelper = new ClientsDBHelper(this);
        base = dbHelper.getReadableDatabase();
        dbToArray(base);
        findV();
        sortByPercentsType();
        printTable();


    }

    private void dbToArray(SQLiteDatabase DB)
    {
        idProduct= new ArrayList<>();
        nameProduct = new ArrayList<>();
        typeProduct = new ArrayList<>();
        KV1 = new ArrayList<>();
        KV2 = new ArrayList<>();
        KV3 = new ArrayList<>();
        KV4 = new ArrayList<>();
        V = new ArrayList<>();
        KV1T = new ArrayList<>();
        KV2T = new ArrayList<>();
        KV3T = new ArrayList<>();
        KV4T = new ArrayList<>();
        idType = new ArrayList<>();
        nameType = new ArrayList<>();
        Cursor cursor = DB.query(ProductContract.ProductEntry.TABLE_NAME,null,null,null,null,null, ProductContract.ProductEntry.COLUMN_ID);
        cursor.moveToFirst();
        while (cursor.getPosition() != cursor.getCount())
        {
            idProduct.add(cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_ID)));
            nameProduct.add(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME)));
            typeProduct.add(cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_TYPE)));
            cursor.moveToNext();
            KV1.add(0);
            KV2.add(0);
            KV3.add(0);
            KV4.add(0);
        }
        yearsToPeriod();

        for (int i = 0;i < periods1.size();i++)
        {
            cursor = DB.query(HistoryContract.HistoryEntry.TABLE_NAME,null,null,null,
                    HistoryContract.HistoryEntry.COLUMN_Date,"Date >= " + periods1.get(i) +" and Date <= "+periods2.get(i),null);
            cursor.moveToFirst();
            for (int j = 0;j < cursor.getCount();j++)
            {
                Integer idH = cursor.getInt(cursor.getColumnIndex(HistoryContract.HistoryEntry.COLUMN_idH));
                Cursor HPCursor = DB.query(HistoryProductContract.HistoryProductEntry.TABLE_NAME,null,"History_id = ?",
                        new String[]{idH.toString()},null,null,null);
                HPCursor.moveToFirst();
                for (int k = 0;k <HPCursor.getCount();k++)
                {
                    Integer pID = HPCursor.getInt(HPCursor.getColumnIndex(HistoryProductContract.HistoryProductEntry.COLUMN_PRODUCT));
                    Integer am = HPCursor.getInt(HPCursor.getColumnIndex(HistoryProductContract.HistoryProductEntry.COLUMN_AMMOUNT));
                    switch (i)
                    {
                        case 0:
                            KV1.set(pID-1,KV1.get(pID-1)+am);
                            break;
                        case 1:
                            KV2.set(pID-1,KV1.get(pID-1)+am);
                            break;
                        case 2:
                            KV3.set(pID-1,KV1.get(pID-1)+am);
                            break;
                        case 3:
                            KV4.set(pID-1,KV1.get(pID-1)+am);
                            break;
                    }
                    HPCursor.moveToNext();
                }
                cursor.moveToNext();
            }
        }
        Cursor typeCursor = DB.query(ProductsTypeContract.ProductsTypesEntry.TABLE_NAME,null,null,null,null,null, ProductsTypeContract.ProductsTypesEntry.COLUMN_idPT);
        typeCursor.moveToFirst();
        while (typeCursor.getPosition() != typeCursor.getCount())
        {
            idType.add(typeCursor.getInt(typeCursor.getColumnIndex(ProductsTypeContract.ProductsTypesEntry.COLUMN_idPT)));
            nameType.add(typeCursor.getString(typeCursor.getColumnIndex(ProductsTypeContract.ProductsTypesEntry.COLUMN_Name)));
            V.add(0.0);
            KV1T.add(0);
            KV2T.add(0);
            KV3T.add(0);
            KV4T.add(0);
            typeCursor.moveToNext();
        }

        for (int i = 0;i < idProduct.size();i++)
        {

                int index = typeProduct.get(i)-1;
                KV1T.set(index,KV1T.get(index)+KV1.get(i));
                KV2T.set(index,KV2T.get(index)+KV2.get(i));
                KV3T.set(index,KV3T.get(index)+KV3.get(i));
                KV4T.set(index,KV4T.get(index)+KV4.get(i));
        }


    }

    private void findV()
    {
        for (int i = 0;i < KV1T.size();i++)
        {
            int mid = (KV1T.get(i) + KV2T.get(i) + KV3T.get(i) +KV4T.get(i))/4;
            if (mid != 0)
            {
                Double q = Math.sqrt((Math.pow(KV1T.get(i) - mid,2) + Math.pow(KV2T.get(i) - mid,2) + Math.pow(KV3T.get(i) - mid,2) + Math.pow(KV4T.get(i) - mid,2))/4);
                q = (q / mid) * 100;
                V.set(i,q);
            }
        }



    }
    public void sortByPercentsType()
    {
        boolean needIteration = true;
        while (needIteration) {
            needIteration = false;
            for (int i = 1; i < idType.size(); i++) {
                if (V.get(i) < V.get(i-1)) {
                    swapInt(idType,i,i-1);
                    swapStr(nameType,i,i-1);
                    swapInt(KV1T,i,i-1);
                    swapInt(KV2T,i,i-1);
                    swapInt(KV3T,i,i-1);
                    swapInt(KV4T,i,i-1);
                    swapD(V,i,i-1);
                    needIteration = true;
                }
            }
        }
    }

    private void printTable()
    {
        TableLayout tableLayout = findViewById(R.id.tableLayoutXYZ);
        cleanTable(tableLayout);
        TextView textView;
        TableLayout.LayoutParams lp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.leftMargin = 10;
        lp.rightMargin = 10;
        LinearLayout.LayoutParams cells = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout;

        for (int i = 0;i < idType.size();i++)
        {
            TableRow tableRow1 = new TableRow(this);
            tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableLayout.addView(tableRow1);

            Integer r =(int) Math.round(V.get(i));

            if (r !=0) {
                textView = new TextView(this);
                textView.setText(idType.get(i).toString());
                textView.setLayoutParams(cells);
                textView.setTextColor(Color.BLACK);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setPadding(5, 10, 5, 10);
                textView.setBackgroundResource(R.drawable.cells_shape);
                tableRow1.addView(textView);

                textView = new TextView(this);
                textView.setText(nameType.get(i).toString());
                textView.setLayoutParams(cells);
                textView.setTextColor(Color.BLACK);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setPadding(5, 10, 5, 10);
                textView.setBackgroundResource(R.drawable.cells_shape);
                tableRow1.addView(textView);

                textView = new TextView(this);
                textView.setText(KV1T.get(i).toString());
                textView.setLayoutParams(cells);
                textView.setTextColor(Color.BLACK);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setPadding(5, 10, 5, 10);
                textView.setBackgroundResource(R.drawable.cells_shape);
                tableRow1.addView(textView);

                textView = new TextView(this);
                textView.setText(KV2T.get(i).toString());
                textView.setLayoutParams(cells);
                textView.setTextColor(Color.BLACK);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setPadding(5, 10, 5, 10);
                textView.setBackgroundResource(R.drawable.cells_shape);
                tableRow1.addView(textView);

                textView = new TextView(this);
                textView.setText(KV3T.get(i).toString());
                textView.setLayoutParams(cells);
                textView.setTextColor(Color.BLACK);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setPadding(5, 10, 5, 10);
                textView.setBackgroundResource(R.drawable.cells_shape);
                tableRow1.addView(textView);

                textView = new TextView(this);
                textView.setText(KV4T.get(i).toString());
                textView.setLayoutParams(cells);
                textView.setTextColor(Color.BLACK);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setPadding(5, 10, 5, 10);
                textView.setBackgroundResource(R.drawable.cells_shape);
                tableRow1.addView(textView);

                textView = new TextView(this);
                textView.setText(r.toString() + "%");
                textView.setLayoutParams(cells);
                textView.setTextColor(Color.BLACK);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setPadding(5, 10, 5, 10);
                textView.setBackgroundResource(R.drawable.cells_shape);
                tableRow1.addView(textView);

                textView = new TextView(this);
                textView.setLayoutParams(cells);
                textView.setTextColor(Color.BLACK);
                if (r <= 30) {
                    TextView tV = new TextView(this);
                    linearLayout = findViewById(R.id.linearLayoutGroupsX);
                    tV.setText(nameType.get(i));
                    tV.setLayoutParams(cells);
                    tV.setTextColor(Color.BLACK);
                    linearLayout.addView(tV);
                    textView.setText("X");
                }
                else if (r > 30 && r <= 50) {
                    textView.setText("Y");
                    TextView tV = new TextView(this);
                    linearLayout = findViewById(R.id.linearLayoutGroupsY);
                    tV.setText(nameType.get(i));
                    tV.setLayoutParams(cells);
                    tV.setTextColor(Color.BLACK);
                    linearLayout.addView(tV);
                }
                else {
                    textView.setText("Z");
                    TextView tV = new TextView(this);
                    linearLayout = findViewById(R.id.linearLayoutGroupsZ);
                    tV.setText(nameType.get(i));
                    tV.setLayoutParams(cells);
                    tV.setTextColor(Color.BLACK);
                    linearLayout.addView(tV);
                }
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setPadding(5, 10, 5, 10);
                textView.setBackgroundResource(R.drawable.cells_shape);
                tableRow1.addView(textView);

            }
            else
            {

                LinearLayout linearLayout1 = findViewById(R.id.linearLayout3);
                textView = new TextView(this);
                textView.setText(nameType.get(i));
                textView.setLayoutParams(cells);
                textView.setTextColor(Color.BLACK);
                linearLayout1.addView(textView);

            }


        }
    }

    private void yearsToPeriod()
    {
        periods1 = new ArrayList<>();
        periods1.add(year + "0101");
        periods1.add(year + "0401");
        periods1.add(year + "0701");
        periods1.add(year + "1001");

        periods2 = new ArrayList<>();
        periods2.add(year + "0331");
        periods2.add(year + "0631");
        periods2.add(year + "0931");
        periods2.add(year + "1231");

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
    private void cleanTable(TableLayout table) {

        int childCount = table.getChildCount();

        // Remove all rows except the first one
        if (childCount > 1) {
            table.removeViews(1, childCount - 1);
        }
    }
}