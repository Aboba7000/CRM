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
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class CrossAnalyzeActivity extends AppCompatActivity {
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
    ArrayList<Integer> KV1;
    ArrayList<Integer> KV2;
    ArrayList<Integer> KV3;
    ArrayList<Integer> KV4;
    ArrayList<Double> V;
    ArrayList<Integer> KV1T;
    ArrayList<Integer> KV2T;
    ArrayList<Integer> KV3T;
    ArrayList<Integer> KV4T;
    String year;
    ArrayList<String> periods1;
    ArrayList<String> periods2;
    ArrayList<String> ABC;
    ArrayList<String> XYZ;
    int[] textViews = {R.id.textView11,R.id.textView12,R.id.textView13,R.id.textView21,R.id.textView22,R.id.textView23,R.id.textView31,R.id.textView32,R.id.textView33};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cross_analyze);

        Toolbar toolbar = findViewById(R.id.toolbarCrossAnalyzeActivity);
        toolbar.setTitle("Кросс анализ");
        setSupportActionBar(toolbar);
        year = "2020";

        dbHelper = new ClientsDBHelper(this);
        SQLiteDatabase base = dbHelper.getReadableDatabase();
        CrossAnalyze(base);
        makeTable();
        formatTextView();

    }
    private void CrossAnalyze(SQLiteDatabase DB)
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
        KV1 = new ArrayList<>();
        KV2 = new ArrayList<>();
        KV3 = new ArrayList<>();
        KV4 = new ArrayList<>();
        V = new ArrayList<>();
        KV1T = new ArrayList<>();
        KV2T = new ArrayList<>();
        KV3T = new ArrayList<>();
        KV4T = new ArrayList<>();
        ABC = new ArrayList<>();
        XYZ = new ArrayList<>();

        //список продуктов
        for (int i = 0;i < productsCursor.getCount();i++)
        {
            idProduct.add(productsCursor.getInt(productsCursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_ID)));
            names.add(productsCursor.getString(productsCursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME)));
            price.add(productsCursor.getInt(productsCursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_Price)));
            type.add(productsCursor.getInt(productsCursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_TYPE)));
            KV1.add(0);
            KV2.add(0);
            KV3.add(0);
            KV4.add(0);

            productsCursor.moveToNext();
        }

        //деление года на кварталы
        yearsToPeriod();

        Cursor HPCursor = DB.query(HistoryProductContract.HistoryProductEntry.TABLE_NAME,null,null,null,null,null, null);
        HPCursor.moveToFirst();

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

        //рассчет процентной доли
        for (int i = 0;i<idProduct.size();i++)
        {
            Double p = (double)priceSum/100;
            if (fullPrice.get(i)!=0) {
                percentsPrice.add(fullPrice.get(i)/p);
            }
            else percentsPrice.add(0.0);
        }

        idTypes = new ArrayList<Integer>();
        nameTypes = new ArrayList<String>();
        percentsTypes = new ArrayList<Double>();
        sumTypes = new ArrayList<Integer>();
        Cursor cursor = DB.query(ProductsTypeContract.ProductsTypesEntry.TABLE_NAME,null,null,null,null,null, ProductsTypeContract.ProductsTypesEntry.COLUMN_idPT);
        cursor.moveToFirst();

        //список типов
        for (int i = 0;i < cursor.getCount();i++)
        {
            idTypes.add(cursor.getInt(cursor.getColumnIndex(ProductsTypeContract.ProductsTypesEntry.COLUMN_idPT)));
            nameTypes.add(cursor.getString(cursor.getColumnIndex(ProductsTypeContract.ProductsTypesEntry.COLUMN_Name)));
            sumTypes.add(0);
            percentsTypes.add(0.0);
            KV1T.add(0);
            KV2T.add(0);
            KV3T.add(0);
            KV4T.add(0);
            V.add(0.0);
            cursor.moveToNext();
        }
        //процентная доля по типам
        for (int i = 0;i < idProduct.size();i++)
        {
            Integer index = type.get(i) - 1;
            percentsTypes.set(index,percentsTypes.get(index) + percentsPrice.get(i));
            sumTypes.set(index,sumTypes.get(index) + (price.get(i) * amount.get(i)));

        }

        //количество продаж в период
        for (int i = 0;i < periods1.size();i++)
        {
            cursor = DB.query(HistoryContract.HistoryEntry.TABLE_NAME,null,null,null,
                    HistoryContract.HistoryEntry.COLUMN_Date,"Date >= " + periods1.get(i) +" and Date <= "+periods2.get(i),null);
            cursor.moveToFirst();
            for (int j = 0;j < cursor.getCount();j++)
            {
                Integer idH = cursor.getInt(cursor.getColumnIndex(HistoryContract.HistoryEntry.COLUMN_idH));
                HPCursor = DB.query(HistoryProductContract.HistoryProductEntry.TABLE_NAME,null,"History_id = ?",
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

        //количество по группам
        for (int i = 0;i < idProduct.size();i++)
        {

            int index = type.get(i)-1;
            KV1T.set(index,KV1T.get(index)+KV1.get(i));
            KV2T.set(index,KV2T.get(index)+KV2.get(i));
            KV3T.set(index,KV3T.get(index)+KV3.get(i));
            KV4T.set(index,KV4T.get(index)+KV4.get(i));
        }

        //поиск V
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
        Double minP = 100.0;
        Double maxP = 0.0;

        for (int i =0;i < idTypes.size();i++)
        {
            if (percentsTypes.get(i) > maxP) maxP = percentsTypes.get(i);
            if (percentsTypes.get(i) < minP) minP = percentsTypes.get(i);
        }
        Double midP = (minP + maxP) / 2;
        Double step = (maxP - midP) / 3;
        for (int i =0;i < idTypes.size();i++)
        {
            if (V.get(i) <= 30) XYZ.add("X");
            else if (V.get(i) > 30 && V.get(i) <= 50) XYZ.add("Y");
            else XYZ.add("Z");


            if (percentsTypes.get(i) < midP) ABC.add("C");
            else if(percentsTypes.get(i) >= midP && percentsTypes.get(i) >= maxP - step) ABC.add("A");
            else ABC.add("B");
        }

    }

    private void makeTable()
    {
        TextView textView;
        for (int i = 0;i < idTypes.size();i++)
        {
            if(ABC.get(i) == "A")
            {
                switch (XYZ.get(i))
                {
                    case "X":
                        textView = findViewById(R.id.textView11);
                        textView.setText(textView.getText()+" "+idTypes.get(i) + ",");
                        break;
                    case"Y":
                        textView = findViewById(R.id.textView12);
                        textView.setText(textView.getText()+" "+idTypes.get(i) + ",");
                        break;
                    case "Z":
                        textView = findViewById(R.id.textView13);
                        textView.setText(textView.getText()+" "+idTypes.get(i) + ",");
                        break;
                }
            }
            if(ABC.get(i) == "B")
            {
                switch (XYZ.get(i))
                {
                    case "X":
                        textView = findViewById(R.id.textView21);
                        textView.setText(textView.getText()+" "+idTypes.get(i) + ",");
                        break;
                    case"Y":
                        textView = findViewById(R.id.textView22);
                        textView.setText(textView.getText()+" "+idTypes.get(i) + ",");
                        break;
                    case "Z":
                        textView = findViewById(R.id.textView23);
                        textView.setText(textView.getText()+" "+idTypes.get(i) + ",");
                        break;
                }
            }
            if(ABC.get(i) == "C")
            {
                switch (XYZ.get(i))
                {
                    case "X":
                        textView = findViewById(R.id.textView31);
                        textView.setText(textView.getText() +" "+idTypes.get(i) + ",");
                        break;
                    case"Y":
                        textView = findViewById(R.id.textView32);
                        textView.setText(textView.getText()+" "+idTypes.get(i) + ",");
                        break;
                    case "Z":
                        textView = findViewById(R.id.textView33);
                        textView.setText(textView.getText()+" "+idTypes.get(i) + ",");
                        break;
                }
            }
        }
        LinearLayout linearLayout = findViewById(R.id.linearLayoutCross);
        LinearLayout.LayoutParams text = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        text.leftMargin = 5;
        View view = new View(this);
        LinearLayout.LayoutParams stroke = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
        stroke.leftMargin = 4;
        stroke.rightMargin = 4;
        view.setLayoutParams(stroke);
        view.setBackgroundColor(Color.parseColor("#7b7b7b"));
        for (int i=0;i < idTypes.size();i++)
        {
            textView = new TextView(this);
            textView.setLayoutParams(text);
            textView.setTextColor(Color.BLACK);
            textView.setText(idTypes.get(i).toString() +" - " + nameTypes.get(i));
            linearLayout.addView(textView);

            view = new View(this);
            view.setLayoutParams(stroke);
            view.setBackgroundColor(Color.parseColor("#7b7b7b"));
            linearLayout.addView(view);
        }
    }

    private void formatTextView()
    {
        TextView textView;
        for (int i = 0;i<9;i++)
        {
            textView = findViewById(textViews[i]);
            if (textView.getText() != "")
            textView.setText(textView.getText().toString().substring(0,textView.getText().length()-1));
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
}