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
import android.widget.TableRow;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Calendar;

public class ClientsReportActivity extends AppCompatActivity {
    private Calendar calendar1 = Calendar.getInstance();
    private Calendar calendar2 = Calendar.getInstance();
    private EditText userInput1;
    private EditText userInput2;
    private String date1,date2;
    private ClientsDBHelper dbHelper;
    GraphView graphView;
    ArrayList<Integer> idClients;
    ArrayList<String> nameClients;
    ArrayList<Integer> orders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients_report);

        Toolbar toolbar = findViewById(R.id.toolbarClientsReport);
        toolbar.setTitle("Отчет");
        setSupportActionBar(toolbar);

        dbHelper = new ClientsDBHelper(this);
        SQLiteDatabase base = dbHelper.getReadableDatabase();
        Cursor historyCursor = base.query(HistoryContract.HistoryEntry.TABLE_NAME,null,null,null,"Date",null,"Date");
        historyCursor.moveToFirst();
        date1 = historyCursor.getString(historyCursor.getColumnIndex(HistoryContract.HistoryEntry.COLUMN_Date));
        historyCursor.moveToLast();
        date2 = historyCursor.getString(historyCursor.getColumnIndex(HistoryContract.HistoryEntry.COLUMN_Date));

        dbToArray(base);
        makeGraph();
    }

    private void dbToArray(SQLiteDatabase DB)
    {
        idClients = new ArrayList<>();
        nameClients = new ArrayList<>();
        orders = new ArrayList<>();
        Cursor cursor = DB.query(ClientsContract.ClientsEntry.TABLE_NAME,null,null,null,null,null,ClientsContract.ClientsEntry.COLUMN_IDC);
        cursor.moveToFirst();
        while (cursor.getPosition() != cursor.getCount())
        {
            idClients.add(cursor.getInt(cursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_IDC)));
            nameClients.add(cursor.getString(cursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_NAME)));
            orders.add(0);
            cursor.moveToNext();
        }

        cursor = DB.query(HistoryContract.HistoryEntry.TABLE_NAME,null,null,null,"Date","Date >= " + date1 + " and Date <= " + date2,null);
        cursor.moveToFirst();
        Integer index;

        for (int i = 0;i< idClients.size();i++)
        {
            index = i +1;
            cursor = DB.query(HistoryContract.HistoryEntry.TABLE_NAME,null,"Customer = ?",new String[]{index.toString()},"Date","Date >= " + date1 + " and Date <= " + date2,null);
            cursor.moveToFirst();
            orders.set(i,cursor.getCount());
            Log.i("SS",idClients.get(i).toString());
            Log.i("SS",nameClients.get(i));
            Log.i("SS",orders.get(i).toString());
        }

    }
    private void makeGraph()
    {
        DataPoint[] points;
        String[] labels;
        labels = new String[idClients.size()];
        points = new DataPoint[orders.size()];
        for (int i = 0; i < orders.size(); i++) {
            points[i] = new DataPoint(i+1, orders.get(i));
            labels[i] = nameClients.get(i);
        }

        graphView = findViewById(R.id.Graph2);
        //LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);
        series.setSpacing(15);


        graphView.setTitle("График по клиентам");
        graphView.setTitleTextSize(60);

        GridLabelRenderer renderer = graphView.getGridLabelRenderer();
        LabelFormatter labelFormatter = new LabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX){
                    if(value % 1 == 0)
                    {
                        return Integer.toString((int)value);
                    }
                    else {
                        return null;
                    }
                }
                else {
                    if(value % 1 == 0)
                    {
                        return Double.toString(value).substring(0,1);
                    }
                    else return null;
                    //return null;
                }

            }

            @Override
            public void setViewport(Viewport viewport) {

            }
        };
        int interval = 1;
        if (idClients.size() <= 55)
        {
            interval = 5;
        }
        else
        {
            interval = 10;
        }
        renderer.setLabelFormatter(labelFormatter);
        renderer.setNumHorizontalLabels(idClients.size() / interval +1 );
        //renderer.setHorizontalLabelsAngle(90);
       // renderer.setLabelsSpace(50);

        graphView.getViewport().setMinX(1);
        graphView.getViewport().setMaxX(idClients.size()+1);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setScalable(true);
        //graphView.getViewport().setScrollable(true);
        graphView.addSeries(series);

        LinearLayout linearLayout = findViewById(R.id.linearLayoutClientsGraph);
        TextView textView;
        View view = new View(this);
        LinearLayout.LayoutParams stroke = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
        stroke.leftMargin = 4;
        stroke.rightMargin = 4;
        view.setLayoutParams(stroke);
        view.setBackgroundColor(Color.parseColor("#7b7b7b"));
        LinearLayout.LayoutParams text = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        text.leftMargin = 5;
        for (int i=0;i < idClients.size();i++)
        {
            textView = new TextView(this);
            textView.setLayoutParams(text);
            textView.setTextColor(Color.BLACK);
            textView.setText(idClients.get(i).toString() +" - " + nameClients.get(i));
            linearLayout.addView(textView);

            view = new View(this);
            view.setLayoutParams(stroke);
            view.setBackgroundColor(Color.parseColor("#7b7b7b"));
            linearLayout.addView(view);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_graph2,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {

            case R.id.graphCalendar2:
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                View alertView = layoutInflater.inflate(R.layout.alert_dialog_calendar,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(alertView);
                userInput1 = (EditText) alertView.findViewById(R.id.editTextDate1);
                if (date1!=null) userInput1.setText(formatDateRev(date1));

                userInput1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SetDate1(v);
                    }
                });
                userInput2 = (EditText) alertView.findViewById(R.id.editTextDate2);
                if(date2!=null) userInput2.setText(formatDateRev(date2));
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
                                graphView.removeAllSeries();
                                SQLiteDatabase base = dbHelper.getReadableDatabase();
                                dbToArray(base);
                                makeGraph();
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
            default:return true;
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

    //форматирование даты календаря
    private String formatDate(String s)
    {
        //char[] textDate = {s.charAt(0),s.charAt(1),'/',s.charAt(3),s.charAt(4),'/',s.charAt(6),s.charAt(7),s.charAt(8),s.charAt(9)};
        char[] textDate = {s.charAt(6),s.charAt(7),s.charAt(8),s.charAt(9),s.charAt(3),s.charAt(4),s.charAt(0),s.charAt(1)};
        s = String.copyValueOf(textDate);
        return s;
    }
    private String formatDateRev(String s)
    {
        //char[] textDate = {s.charAt(0),s.charAt(1),'/',s.charAt(3),s.charAt(4),'/',s.charAt(6),s.charAt(7),s.charAt(8),s.charAt(9)};
        char[] textDate = {s.charAt(6),s.charAt(7),'.',s.charAt(4),s.charAt(5),'.',s.charAt(0),s.charAt(1),s.charAt(2),s.charAt(3)};
        s = String.copyValueOf(textDate);
        return s;
    }
}