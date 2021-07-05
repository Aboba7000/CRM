package com.example.crm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

public class HistoryActivity extends AppCompatActivity {

    public String idH;
    public ClientsDBHelper DBHelper ;
    public Cursor historyCursor;
    public Integer idC;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = findViewById(R.id.toolbarHistory);
        toolbar.setTitle("Детали заказа");
        setSupportActionBar(toolbar);

        //получаем idH
        idH = getIntent().getExtras().getString("idH");
        DBHelper = new ClientsDBHelper(this);
        SQLiteDatabase clientDB = DBHelper.getReadableDatabase();

        //подключение к таблице history
        historyCursor = clientDB.query(HistoryContract.HistoryEntry.TABLE_NAME,
                new String[]{HistoryContract.HistoryEntry.COLUMN_idH, HistoryContract.HistoryEntry.COLUMN_Customer, HistoryContract.HistoryEntry.COLUMN_Date, HistoryContract.HistoryEntry.COLUMN_Time},
                "id_H = ?",new String[] {idH},null,null,null);

        historyCursor.moveToFirst();

        TextView textViewDate = findViewById(R.id.textViewDateOfOrder);
        TextView textViewTime = findViewById(R.id.textViewTimeOfOrder);
        TextView textViewCustomer = findViewById(R.id.textViewNameOfClient);
        TextView textViewSum = findViewById(R.id.textViewHistorySum);

        textViewDate.setText(DateFormat(historyCursor.getString(historyCursor.getColumnIndex(HistoryContract.HistoryEntry.COLUMN_Date))));
        textViewTime.setText(historyCursor.getString(historyCursor.getColumnIndex(HistoryContract.HistoryEntry.COLUMN_Time)));
        idC = historyCursor.getInt(historyCursor.getColumnIndex(HistoryContract.HistoryEntry.COLUMN_Customer));
        Cursor clientsCursor = clientDB.query(ClientsContract.ClientsEntry.TABLE_NAME,new String[]{ClientsContract.ClientsEntry.COLUMN_IDC, ClientsContract.ClientsEntry.COLUMN_NAME_FULL},"id_C = ?",new String[]{Integer.toString(idC)},null,null,null);
        clientsCursor.moveToFirst();
        textViewCustomer.setText(clientsCursor.getString(clientsCursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_NAME_FULL)));

        LinearLayout linearLayout = findViewById(R.id.linearLayoutList);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams leftmargin10 = layoutParams1;
        leftmargin10.leftMargin = 10;
        LinearLayout.LayoutParams priceDisp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        priceDisp.leftMargin = 10;
        priceDisp.topMargin = 30;

        LinearLayout.LayoutParams stroke = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
        stroke.leftMargin = 4;
        stroke.rightMargin = 4;


        //подключение к таблице History_Product
        Cursor historyProductCursor = clientDB.query(HistoryProductContract.HistoryProductEntry.TABLE_NAME,
                new String[]{HistoryProductContract.HistoryProductEntry.COLUMN_ID, HistoryProductContract.HistoryProductEntry.COLUMN_PRODUCT, HistoryProductContract.HistoryProductEntry.COLUMN_AMMOUNT},
                "History_id = ?",new String[]{idH},null,null,null);
        historyProductCursor.moveToFirst();

        Cursor productCusror;
        Integer idP;
        Integer Ammount = 0;
        Integer Price = 0;
        Integer Sum = 0;
        //Toast.makeText(this,Integer.toString(historyProductCursor.getCount()),Toast.LENGTH_SHORT).show();
        while (historyProductCursor.getPosition() < historyProductCursor.getCount())
        {
            //получаем id товара
            idP = historyProductCursor.getInt(historyProductCursor.getColumnIndex(HistoryProductContract.HistoryProductEntry.COLUMN_PRODUCT));
            //получение товара по id
            productCusror = clientDB.query(ProductContract.ProductEntry.TABLE_NAME,
                    new String[]{ProductContract.ProductEntry.COLUMN_ID, ProductContract.ProductEntry.COLUMN_NAME, ProductContract.ProductEntry.COLUMN_Price},"ID_P = ?",new String[]{Integer.toString(idP)},null,null,null);
            productCusror.moveToFirst();
            //запись в макет названий
            TextView textViewProduct = new TextView(this);
            textViewProduct.setText(productCusror.getString(productCusror.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME)));
            textViewProduct.setLayoutParams(priceDisp);
            textViewProduct.setTextColor(Color.BLACK);
            linearLayout.addView(textViewProduct);
            //запись в макет количества
            TextView textViewAmmount = new TextView(this);
            int a = historyProductCursor.getInt(historyProductCursor.getColumnIndex(HistoryProductContract.HistoryProductEntry.COLUMN_AMMOUNT));
            textViewAmmount.setText("Количество: " + Integer.toString(historyProductCursor.getInt(historyProductCursor.getColumnIndex(HistoryProductContract.HistoryProductEntry.COLUMN_AMMOUNT))) + " шт.");
            textViewAmmount.setLayoutParams(leftmargin10);
            linearLayout.addView(textViewAmmount);
            //запись в макет цен
            TextView textViewPrice = new TextView(this);
            int p = productCusror.getInt(productCusror.getColumnIndex(ProductContract.ProductEntry.COLUMN_Price));
            textViewPrice.setText("Цена: " + Integer.toString(productCusror.getInt(productCusror.getColumnIndex(ProductContract.ProductEntry.COLUMN_Price))) + " руб.");
            textViewPrice.setLayoutParams(leftmargin10);
            linearLayout.addView(textViewPrice);

            TextView textView = new TextView(this);
            textView.setText("Сумма: " + Integer.toString(p*a) + " руб.");
            textView.setLayoutParams(leftmargin10);
            linearLayout.addView(textView);

            View view = new View(this);
            view.setLayoutParams(stroke);
            view.setBackgroundColor(Color.parseColor("#7b7b7b"));
            linearLayout.addView(view);
            //подсчет итоговой суммы
            Ammount = historyProductCursor.getInt(historyProductCursor.getColumnIndex(HistoryProductContract.HistoryProductEntry.COLUMN_AMMOUNT));
            Price = productCusror.getInt(productCusror.getColumnIndex(ProductContract.ProductEntry.COLUMN_Price));
            Sum = Sum + (Ammount * Price);

            historyProductCursor.moveToNext();
        }

        textViewSum.setText(Integer.toString(Sum) + "руб.");


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_history2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.findClient:
                Intent intent = new Intent(HistoryActivity.this,ClientActivity.class);
                intent.putExtra("idC",Integer.toString(idC));
                startActivity(intent);
                return true;

            case R.id.refreshHistory2:
                recreate();
                return true;
            default:return true;
        }
    }

    public String DateFormat (String s)
    {
        char[] textDate = {s.charAt(6),s.charAt(7),'/',s.charAt(4),s.charAt(5),'/',s.charAt(0),s.charAt(1),s.charAt(2),s.charAt(3)};
        return String.copyValueOf(textDate);
    }
}