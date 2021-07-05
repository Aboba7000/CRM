package com.example.crm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.Inflater;

public class SellsDynamicAnalyzeActivity extends AppCompatActivity {
    private Calendar calendar1 = Calendar.getInstance();
    private Calendar calendar2 = Calendar.getInstance();
    private EditText userInput1;
    private EditText userInput2;
    private String date1,date2;
    private ClientsDBHelper dbHelper;
    private static final String TAG = "APP";
    SQLiteDatabase DB;
    Menu mainMenu;
    DataPoint[] points;
    String[] labels;
    ArrayList<Integer> orders;
    ArrayList<String> months;
    Cursor historyCursor;
    TableLayout tableLayout;
    TextView textViewSumSells,textViewMinSells,textViewMaxSells;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sells_dynamic_analyze);
        Toolbar toolbar = findViewById(R.id.toolbarSellsDynamicAnalyze);
        toolbar.setTitle("Динамика продаж");
        setSupportActionBar(toolbar);

        
        TableLayout.LayoutParams lp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.leftMargin = 10;
        lp.rightMargin = 10;

        tableLayout = findViewById(R.id.tableLayoutDynamic);

        LinearLayout.LayoutParams cells = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dbHelper = new ClientsDBHelper(this);
        DB = dbHelper.getReadableDatabase();
        historyCursor = DB.query(HistoryContract.HistoryEntry.TABLE_NAME, null, null, null, "Date", null, "Date");
        historyCursor.moveToFirst();
        date1 = historyCursor.getString(historyCursor.getColumnIndex(HistoryContract.HistoryEntry.COLUMN_Date));
        date1 = date1.substring(0, date1.length() - 2) + "01";
        historyCursor.moveToLast();
        date2 = historyCursor.getString(historyCursor.getColumnIndex(HistoryContract.HistoryEntry.COLUMN_Date));
        date2 = date2.substring(0, date2.length() - 2) + "01";
        Integer nextDate,dateInt,lastDateInt;
        String date = date1;
        dateInt=Integer.parseInt(date);
        lastDateInt=Integer.parseInt(date2);


        Integer sum1,sum2;
        while (!dateInt.equals(lastDateInt)) {
            dateInt = Integer.parseInt(date);
            Integer month = Integer.parseInt(date.substring(4, 6));
            if (month < 12) {
                nextDate = dateInt + 100;
            } else {
                nextDate = dateInt + 10000;
                String s = nextDate.toString();
                s = s.substring(0, 4) + "0101";
                nextDate = Integer.parseInt(s);

            }
            TableRow tableRow1 = new TableRow(this);
            tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
            tableLayout.addView(tableRow1);

            historyCursor = DB.query(HistoryContract.HistoryEntry.TABLE_NAME, null, null, null, "Date", "Date >= " + date.substring(0,6) + "01" + " and Date <= " + date.substring(0,6) + "31", null);
            historyCursor.moveToFirst();
            if (historyCursor.getCount() == 0)
            {
                date = nextDate.toString();
                tableRow1.setVisibility(View.GONE);
                if (nextDate.equals(lastDateInt))break;
                continue;
            }
            Log.i(TAG,date.substring(0,6) + "01");

            Integer HID = historyCursor.getInt(historyCursor.getColumnIndex(HistoryContract.HistoryEntry.COLUMN_idH));
            Cursor cursorHP = DB.query(HistoryProductContract.HistoryProductEntry.TABLE_NAME,null,"History_id = ?",new String[]{HID.toString()},null,null,null);
            cursorHP.moveToFirst();
            sum1 = 0;
            for(int i = 0;i<cursorHP.getCount();i++)
            {
                Integer amount = cursorHP.getInt(cursorHP.getColumnIndex(HistoryProductContract.HistoryProductEntry.COLUMN_AMMOUNT));
                Integer idP = cursorHP.getInt(cursorHP.getColumnIndex(HistoryProductContract.HistoryProductEntry.COLUMN_PRODUCT));
                Cursor cursorProd = DB.query(ProductContract.ProductEntry.TABLE_NAME,null,"ID_P = ?",new String[]{idP.toString()},null,null,null);
                cursorProd.moveToFirst();
                Integer price = cursorProd.getInt(cursorProd.getColumnIndex(ProductContract.ProductEntry.COLUMN_Price));
                sum1 =sum1 + amount * price;
            }





            historyCursor = DB.query(HistoryContract.HistoryEntry.TABLE_NAME, null, null, null, "Date", "Date >= " + nextDate.toString().substring(0,6)+ "01" + " and Date <= " + nextDate.toString().substring(0,6) + "31", null);
            historyCursor.moveToFirst();
            if (historyCursor.getCount() == 0)
            {
                tableRow1.setVisibility(View.GONE);
                date = nextDate.toString();
                if (nextDate.equals(lastDateInt))break;
                continue;
            }
            HID = historyCursor.getInt(historyCursor.getColumnIndex(HistoryContract.HistoryEntry.COLUMN_idH));
            cursorHP = DB.query(HistoryProductContract.HistoryProductEntry.TABLE_NAME,null,"History_id = ?",new String[]{HID.toString()},null,null,null);
            cursorHP.moveToFirst();
            sum2 = 0;
            for(int i = 0;i<cursorHP.getCount();i++)
            {
                Integer amount = cursorHP.getInt(cursorHP.getColumnIndex(HistoryProductContract.HistoryProductEntry.COLUMN_AMMOUNT));
                Integer idP = cursorHP.getInt(cursorHP.getColumnIndex(HistoryProductContract.HistoryProductEntry.COLUMN_PRODUCT));
                Cursor cursorProd = DB.query(ProductContract.ProductEntry.TABLE_NAME,null,"ID_P = ?",new String[]{idP.toString()},null,null,null);
                cursorProd.moveToFirst();
                Integer price = cursorProd.getInt(cursorProd.getColumnIndex(ProductContract.ProductEntry.COLUMN_Price));
                sum2 =sum2 + amount * price;
            }

            Double r,p;
            String res;
            DecimalFormat decimalFormat = new DecimalFormat( "#" );
            p = (double)sum1 / 100;
            if (sum1>sum2)
            {
                p = (sum1 -sum2) / p;
                //p = 100 - p;
                res = "-" + decimalFormat.format(p) + "%";

            }
            else
            {
                p = (sum2 - sum1) / p + 100;
                //p = 100 - p;
                res = decimalFormat.format(p) + "%";
            }
            //первый месяц
            TextView textView = new TextView(this);
            textView.setText(dateString(date.substring(4,6)) + " " + date.substring(0,4));
            textView.setLayoutParams(cells);
            textView.setTextColor(Color.BLACK);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setPadding(5,10,5,10);
            textView.setBackgroundResource(R.drawable.cells_shape);
            tableRow1.addView(textView);
            //второй месяц
            textView = new TextView(this);
            textView.setText(dateString(nextDate.toString().substring(4,6)) + " " + nextDate.toString().substring(0,4));
            textView.setLayoutParams(cells);
            textView.setTextColor(Color.BLACK);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setPadding(5,10,5,10);
            textView.setBackgroundResource(R.drawable.cells_shape);
            tableRow1.addView(textView);

            //выручка 1м.
            textView = new TextView(this);
            textView.setText(sum1.toString());
            textView.setLayoutParams(cells);
            textView.setTextColor(Color.BLACK);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setPadding(5,10,5,10);
            textView.setBackgroundResource(R.drawable.cells_shape);
            tableRow1.addView(textView);
            //выручка 2.м
            textView = new TextView(this);
            textView.setText(sum2.toString());
            textView.setLayoutParams(cells);
            textView.setTextColor(Color.BLACK);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setPadding(5,10,5,10);
            textView.setBackgroundResource(R.drawable.cells_shape);
            tableRow1.addView(textView);

            textView = new TextView(this);
            textView.setText(res.toString());
            textView.setLayoutParams(cells);
            textView.setTextColor(Color.BLACK);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setPadding(5,10,5,10);
            textView.setBackgroundResource(R.drawable.cells_shape);
            tableRow1.addView(textView);



            date = nextDate.toString();
            if (nextDate.equals(lastDateInt))break;


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_sell_dynamic,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.calendarSellDynamic:
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                View alertView = layoutInflater.inflate(R.layout.alert_dialog_calendar,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(alertView);
                userInput1 = (EditText) alertView.findViewById(R.id.editTextDate1);

                userInput1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SetDate1(v);
                    }
                });
                userInput2 = (EditText) alertView.findViewById(R.id.editTextDate2);
                userInput2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SetDate2(v);
                    }
                });
                builder
                        .setCancelable(false)
                        .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //нажатие на кнопку ок диалогового окна
                                date1 = formatDate(userInput1.getText().toString());
                                date2 = formatDate(userInput2.getText().toString());
                                cleanTable(tableLayout);
                                makeTable();

                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
        }
        return true;
    }
    //форматирование даты календаря
    private String formatDate(String s)
    {
        //char[] textDate = {s.charAt(0),s.charAt(1),'/',s.charAt(3),s.charAt(4),'/',s.charAt(6),s.charAt(7),s.charAt(8),s.charAt(9)};
        char[] textDate = {s.charAt(6),s.charAt(7),s.charAt(8),s.charAt(9),s.charAt(3),s.charAt(4),s.charAt(0),s.charAt(1)};
        s = String.copyValueOf(textDate);
        return s;
    }

    public void makeTable()
    {
        Integer nextDate,dateInt,lastDateInt;
        String date = date1;
        dateInt=Integer.parseInt(date);
        lastDateInt=Integer.parseInt(date2);
        LinearLayout.LayoutParams cells = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Integer sum1,sum2;
        while (!dateInt.equals(lastDateInt)) {
            dateInt = Integer.parseInt(date);
            Integer month = Integer.parseInt(date.substring(4, 6));
            if (month < 12) {
                nextDate = dateInt + 100;
            } else {
                nextDate = dateInt + 10000;
                String s = nextDate.toString();
                s = s.substring(0, 4) + "0101";
                nextDate = Integer.parseInt(s);

            }
            TableRow tableRow1 = new TableRow(this);
            tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
            tableLayout.addView(tableRow1);

            historyCursor = DB.query(HistoryContract.HistoryEntry.TABLE_NAME, null, null, null, "Date", "Date >= " + date.substring(0,6) + "01" + " and Date <= " + date.substring(0,6) + "31", null);
            historyCursor.moveToFirst();
            if (historyCursor.getCount() == 0)
            {
                date = nextDate.toString();
                tableRow1.setVisibility(View.GONE);
                if (nextDate.equals(lastDateInt))break;
                continue;
            }
            if (dateInt>=lastDateInt) break;


            Integer HID = historyCursor.getInt(historyCursor.getColumnIndex(HistoryContract.HistoryEntry.COLUMN_idH));
            Cursor cursorHP = DB.query(HistoryProductContract.HistoryProductEntry.TABLE_NAME,null,"History_id = ?",new String[]{HID.toString()},null,null,null);
            cursorHP.moveToFirst();
            sum1 = 0;
            for(int i = 0;i<cursorHP.getCount();i++)
            {
                Integer amount = cursorHP.getInt(cursorHP.getColumnIndex(HistoryProductContract.HistoryProductEntry.COLUMN_AMMOUNT));
                Integer idP = cursorHP.getInt(cursorHP.getColumnIndex(HistoryProductContract.HistoryProductEntry.COLUMN_PRODUCT));
                Cursor cursorProd = DB.query(ProductContract.ProductEntry.TABLE_NAME,null,"ID_P = ?",new String[]{idP.toString()},null,null,null);
                cursorProd.moveToFirst();
                Integer price = cursorProd.getInt(cursorProd.getColumnIndex(ProductContract.ProductEntry.COLUMN_Price));
                sum1 =sum1 + amount * price;
            }





            historyCursor = DB.query(HistoryContract.HistoryEntry.TABLE_NAME, null, null, null, "Date", "Date >= " + nextDate.toString().substring(0,6)+ "01" + " and Date <= " + nextDate.toString().substring(0,6) + "31", null);
            historyCursor.moveToFirst();
            if (historyCursor.getCount() == 0)
            {
                tableRow1.setVisibility(View.GONE);
                date = nextDate.toString();
                if (nextDate.equals(lastDateInt))break;
                continue;
            }
            HID = historyCursor.getInt(historyCursor.getColumnIndex(HistoryContract.HistoryEntry.COLUMN_idH));
            cursorHP = DB.query(HistoryProductContract.HistoryProductEntry.TABLE_NAME,null,"History_id = ?",new String[]{HID.toString()},null,null,null);
            cursorHP.moveToFirst();
            sum2 = 0;
            for(int i = 0;i<cursorHP.getCount();i++)
            {
                Integer amount = cursorHP.getInt(cursorHP.getColumnIndex(HistoryProductContract.HistoryProductEntry.COLUMN_AMMOUNT));
                Integer idP = cursorHP.getInt(cursorHP.getColumnIndex(HistoryProductContract.HistoryProductEntry.COLUMN_PRODUCT));
                Cursor cursorProd = DB.query(ProductContract.ProductEntry.TABLE_NAME,null,"ID_P = ?",new String[]{idP.toString()},null,null,null);
                cursorProd.moveToFirst();
                Integer price = cursorProd.getInt(cursorProd.getColumnIndex(ProductContract.ProductEntry.COLUMN_Price));
                sum2 =sum2 + amount * price;
            }

            Double r;
            String res;
            DecimalFormat decimalFormat = new DecimalFormat( "#" );
            r = (double)sum1 / 100;

            //r = (double)sum1/(double)sum2;
            if (sum1>sum2)
            {
                r = (sum1 -sum2) / r;
                //p = 100 - p;
                res = "-" + decimalFormat.format(r) + "%";

            }
            else
            {
                r = (sum2 - sum1) / r;
                //r = r*100;
                res = decimalFormat.format(r) + "%";

            }
            //первый месяц
            TextView textView = new TextView(this);
            textView.setText(dateString(date.substring(4,6)) + " " + date.substring(0,4));
            textView.setLayoutParams(cells);
            textView.setTextColor(Color.BLACK);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setPadding(5,10,5,10);
            textView.setBackgroundResource(R.drawable.cells_shape);
            tableRow1.addView(textView);
            //второй месяц
            textView = new TextView(this);
            textView.setText(dateString(nextDate.toString().substring(4,6)) + " " + nextDate.toString().substring(0,4));
            textView.setLayoutParams(cells);
            textView.setTextColor(Color.BLACK);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setPadding(5,10,5,10);
            textView.setBackgroundResource(R.drawable.cells_shape);
            tableRow1.addView(textView);

            //выручка 1м.
            textView = new TextView(this);
            textView.setText(sum1.toString());
            textView.setLayoutParams(cells);
            textView.setTextColor(Color.BLACK);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setPadding(5,10,5,10);
            textView.setBackgroundResource(R.drawable.cells_shape);
            tableRow1.addView(textView);
            //выручка 2.м
            textView = new TextView(this);
            textView.setText(sum2.toString());
            textView.setLayoutParams(cells);
            textView.setTextColor(Color.BLACK);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setPadding(5,10,5,10);
            textView.setBackgroundResource(R.drawable.cells_shape);
            tableRow1.addView(textView);

            textView = new TextView(this);
            textView.setText(res.toString());
            textView.setLayoutParams(cells);
            textView.setTextColor(Color.BLACK);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setPadding(5,10,5,10);
            textView.setBackgroundResource(R.drawable.cells_shape);
            tableRow1.addView(textView);



            date = nextDate.toString();
            if (nextDate.equals(lastDateInt))break;


        }
    }

    public String dateString(String s)
    {
        switch (s){
            case "01":
                return "Январь";
            case "02":
                return "Февраль";
            case "03":
                return "Март";
            case "04":
                return "Апрель";
            case "05":
                return "Май";
            case "06":
                return "Июнь";
            case "07":
                return "Июль";
            case "08":
                return "Август";
            case "09":
                return "Сентябрь";
            case "10":
                return "Октябрь";
            case "11":
                return "Ноябрь";
            case "12":
                return "Декабрь";
            default:
                return null;
        }
    }

    //показ первого календаря
    public void SetDate1(View v)
    {
        new DatePickerDialog(this, d1,
                calendar1.get(Calendar.YEAR),
                calendar1.get(Calendar.MONTH),
                calendar1.get(Calendar.DAY_OF_MONTH))
                .show();

    }

    //показ 2 календаря
    public void SetDate2(View v)
    {
        new DatePickerDialog(this, d2,
                calendar2.get(Calendar.YEAR),
                calendar2.get(Calendar.MONTH),
                calendar2.get(Calendar.DAY_OF_MONTH))
                .show();

    }

    //обработчик для 1 календаря
    DatePickerDialog.OnDateSetListener d1=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar1.set(Calendar.YEAR, year);
            calendar1.set(Calendar.MONTH, monthOfYear);
            calendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime1();
        }
    };

    //обработчик для второго календаря
    DatePickerDialog.OnDateSetListener d2=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar2.set(Calendar.YEAR, year);
            calendar2.set(Calendar.MONTH, monthOfYear);
            calendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime2();
        }
    };

    //установка даты в 1 поле
    private void setInitialDateTime1() {
        userInput1.setText(DateUtils.formatDateTime(this,
                calendar1.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }
    //установка даты во 2 поле
    private void setInitialDateTime2() {
        userInput2.setText(DateUtils.formatDateTime(this,
                calendar2.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }
    private void cleanTable(TableLayout table) {

        int childCount = table.getChildCount();

        // Remove all rows except the first one
        if (childCount > 1) {
            table.removeViews(1, childCount - 1);
        }
    }
}