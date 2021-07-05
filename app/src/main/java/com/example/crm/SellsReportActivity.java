package com.example.crm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SellsReportActivity extends AppCompatActivity {
    private Calendar calendar1 = Calendar.getInstance();
    private Calendar calendar2 = Calendar.getInstance();
    private EditText userInput1;
    private EditText userInput2;
    private String date1,date2;
    private ClientsDBHelper dbHelper;
    Integer sumSells,maxSells,minSells;
    private static final String TAG = "APP";
    Menu mainMenu;
    DataPoint[] points;
    String[] labels;
    ArrayList<Integer> orders;
    ArrayList<String> months;
    Cursor historyCursor;
    GraphView graphView;
    TextView textViewSumSells,textViewMinSells,textViewMaxSells;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sells_report);

        Toolbar toolbar = findViewById(R.id.toolbarSellsReport);
        toolbar.setTitle("Отчет");
        setSupportActionBar(toolbar);
        sumSells = 0;
        maxSells = 0;
        minSells = 500000;
        dbHelper = new ClientsDBHelper(this);

        //график за весь период
        if(date1 == null || date2 == null) {
            dbHelper = new ClientsDBHelper(this);
            SQLiteDatabase DB = dbHelper.getReadableDatabase();
            historyCursor = DB.query(HistoryContract.HistoryEntry.TABLE_NAME, null, null, null, "Date", null, "Date");
            historyCursor.moveToFirst();
            String firstDate = historyCursor.getString(historyCursor.getColumnIndex(HistoryContract.HistoryEntry.COLUMN_Date));
            //date1 = firstDate;
            firstDate = firstDate.substring(0, firstDate.length() - 2) + "01";
            historyCursor.moveToLast();
            String lastDate = historyCursor.getString(historyCursor.getColumnIndex(HistoryContract.HistoryEntry.COLUMN_Date));
            lastDate = lastDate.substring(0, lastDate.length() - 2) + "01";
            //date2=lastDate;
            String date = firstDate;
            Log.i(TAG, "first date - " + firstDate);
            Log.i(TAG, "date - " + date);
            Log.i(TAG, "last date - " + lastDate);

            Integer dateInt, lastDateInt,nextDate;
            lastDateInt = Integer.parseInt(lastDate);
            dateInt = Integer.parseInt(date);

            orders = new ArrayList<>();
            months = new ArrayList<>();
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
                historyCursor = DB.query(HistoryContract.HistoryEntry.TABLE_NAME, null, null, null, "Date", "Date >= " + date + " and Date <= " + nextDate, null);
                historyCursor.moveToFirst();
                orders.add(historyCursor.getCount());
                if (maxSells < historyCursor.getCount()) maxSells = historyCursor.getCount();
                if (minSells > historyCursor.getCount()) minSells = historyCursor.getCount();
                sumSells = sumSells + historyCursor.getCount();

                Log.i(TAG, "Mon - " + month.toString());
                months.add(date.substring(0, date.length() - 4) + "-" + date.substring(4, 6));
                date = nextDate.toString();
                //months.add(Date.substring(0,Date.length()-2));\

                //months.add(sdf.format(Date));
                //Log.i(TAG, "DATE - " + dateInt.toString());

            }
            labels = new String[months.size()];
            points = new DataPoint[months.size()];
            for (int i = 0; i < months.size(); i++) {
                points[i] = new DataPoint(i, orders.get(i));
                labels[i] = months.get(i);
            }

            graphView = findViewById(R.id.Graph1);
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);



            series.setDrawDataPoints(true);
            series.setDataPointsRadius(10);
            series.setThickness(8);

            graphView.setTitle("График продаж");
            graphView.setTitleTextSize(60);

            GridLabelRenderer renderer = graphView.getGridLabelRenderer();

            LabelFormatter labelFormatter = new LabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX){
                        if(value < months.size())
                        {

                            return months.get((int)value);
                        }
                        else {
                            return null;
                        }
                    }
                    else {
                        return Double.toString(value).substring(0,1);
                    }

                }

                @Override
                public void setViewport(Viewport viewport) {

                }
            };



            renderer.setLabelFormatter(labelFormatter);
            renderer.setHorizontalLabelsAngle(90);
            renderer.setLabelsSpace(20);
            graphView.getViewport().setMaxX(months.size()-1);
            graphView.getViewport().setMinX(0);
            renderer.setNumVerticalLabels(6);
            renderer.setHumanRounding(true);


            graphView.getViewport().setScalable(true);
            //graphView.getViewport().setScrollable(true);
            graphView.addSeries(series);

            textViewSumSells = findViewById(R.id.textViewSumSells);
            textViewSumSells.setText(Integer.toString(sumSells));
            textViewMaxSells=findViewById(R.id.textViewMaxSells);
            textViewMaxSells.setText(Integer.toString(maxSells));
            textViewMinSells = findViewById(R.id.textViewMinSells);
            textViewMinSells.setText(Integer.toString(minSells));
        }
    }

    public String FormatDateWithPoints(String s)
    {
        //char[] text = {s.charAt(6),s.charAt(7),'.',s.charAt(4),s.charAt(5),'.',s.charAt(0),s.charAt(1),s.charAt(2),s.charAt(3)};
        char[] text = {s.charAt(0),s.charAt(1),s.charAt(2),s.charAt(3),'.',s.charAt(5),s.charAt(6)};
        s = String.copyValueOf(text);
        return s;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.toolbar_menu_graph,menu);
       mainMenu = menu;
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.graphCalendar:
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                View alertView = layoutInflater.inflate(R.layout.alert_dialog_calendar,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(alertView);
                userInput1 = (EditText) alertView.findViewById(R.id.editTextDate1);
                if (date1!=null) userInput1.setText(date1);

                userInput1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SetDate1(v);
                    }
                });
                userInput2 = (EditText) alertView.findViewById(R.id.editTextDate2);
                if(date2!=null) userInput2.setText(date2);
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
                                date1 = userInput1.getText().toString();
                                date2 = userInput2.getText().toString();
                                graphView.removeAllSeries();
                                MenuItem lin = mainMenu.findItem(R.id.lin);
                                onOptionsItemSelected(lin);

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
            case R.id.lin:
                sumSells=0;
                minSells = 50000;
                maxSells = 0;
                if (date1 != null && date2 != null) {
                    graphData(2);

                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
                    series.setDrawDataPoints(true);
                    series.setDataPointsRadius(10);
                    series.setThickness(8);

                    graphView.setTitle("График продаж");
                    graphView.setTitleTextSize(60);

                    StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
                    staticLabelsFormatter.setHorizontalLabels(labels);
                    graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
                    GridLabelRenderer renderer = graphView.getGridLabelRenderer();



                    LabelFormatter labelFormatter = new LabelFormatter() {
                        @Override
                        public String formatLabel(double value, boolean isValueX) {
                            if (isValueX){
                                if(value < months.size())
                                {
                                    Log.i(TAG,"INDeX = " + value);
                                    return months.get((int)value);
                                }
                                else {
                                    return null;
                                }
                            }
                            else {
                                return Double.toString(value).substring(0,1);
                            }

                        }

                        @Override
                        public void setViewport(Viewport viewport) {

                        }
                    };

                    renderer.setHumanRounding(true);

                    renderer.setLabelFormatter(labelFormatter);

                    //graphView.getViewport().setScrollable(true);
                    renderer.setHorizontalLabelsAngle(90);
                    renderer.setLabelsSpace(20);
                    graphView.getViewport().setMaxY(maxSells);
                    graphView.getViewport().setMinY(0);
                    graphView.getViewport().setMaxX(months.size());
                    graphView.getViewport().setMinX(0);
                    graphView.getViewport().setYAxisBoundsManual(true);
                    graphView.getViewport().setXAxisBoundsManual(true);
                    renderer.setNumVerticalLabels(maxSells+1);
                    graphView.getViewport().setScalable(true);
                    graphView.addSeries(series);


                    textViewSumSells.setText(Integer.toString(sumSells));
                    textViewMaxSells.setText(Integer.toString(maxSells));
                    textViewMinSells.setText(Integer.toString(minSells));


                    item.setChecked(true);
                }
                else
                {
                    Toast.makeText(this,"Не выбран период",Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.bar:
                sumSells=0;
                minSells = 50000;
                maxSells = 0;
                if (date1 != null && date2 != null) {
                    graphData(2);

                    BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);

                    series.setSpacing(15);


                    graphView.setTitle("График продаж");
                    graphView.setTitleTextSize(60);


                    StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
                    staticLabelsFormatter.setHorizontalLabels(labels);
                    graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

                    GridLabelRenderer renderer = graphView.getGridLabelRenderer();

                    LabelFormatter labelFormatter = new LabelFormatter() {
                        @Override
                        public String formatLabel(double value, boolean isValueX) {
                            if (isValueX){
                                if(value < months.size())
                                {
                                    Log.i(TAG,"INDeX = " + value);
                                    return months.get((int)value);
                                }
                                else {
                                    return null;
                                }
                            }
                            else {
                                return Double.toString(value).substring(0,1);
                            }

                        }

                        @Override
                        public void setViewport(Viewport viewport) {

                        }
                    };

                    renderer.setHumanRounding(true);

                    renderer.setLabelFormatter(labelFormatter);
                    renderer.setHorizontalLabelsAngle(90);
                    renderer.setLabelsSpace(20);
                    graphView.getViewport().setMaxY(maxSells+1);
                    graphView.getViewport().setMinY(0);
                    graphView.getViewport().setYAxisBoundsManual(true);
                    renderer.setNumVerticalLabels(maxSells+1);
                    graphView.addSeries(series);

                    textViewSumSells.setText(Integer.toString(sumSells));
                    textViewMaxSells.setText(Integer.toString(maxSells));
                    textViewMinSells.setText(Integer.toString(minSells));
                    item.setChecked(true);
                }
                else
                {
                    Toast.makeText(this,"Не выбран период",Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return true;
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

    private void graphData(Integer scale)
    {
        //1-year 2 - month 3-day
        switch (scale){
            case 2:
                SQLiteDatabase DB = dbHelper.getReadableDatabase();
                String firstDate = formatDate(date1);
                firstDate = firstDate.substring(0, firstDate.length() - 2) + "01";
                String lastDate = formatDate(date2);
                lastDate = lastDate.substring(0, lastDate.length() - 2) + "01";
                String date = firstDate;
                Integer dateInt, lastDateInt,nextDate;
                lastDateInt = Integer.parseInt(lastDate);
                dateInt = Integer.parseInt(date);

                orders = new ArrayList<>();
                months = new ArrayList<>();
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
                    historyCursor = DB.query(HistoryContract.HistoryEntry.TABLE_NAME, null, null, null, "Date", "Date >= " + date + " and Date <= " + nextDate, null);
                    historyCursor.moveToFirst();
                    orders.add(historyCursor.getCount());
                    if (maxSells < historyCursor.getCount()) maxSells = historyCursor.getCount();
                    if (minSells > historyCursor.getCount()) minSells = historyCursor.getCount();
                    sumSells = sumSells + historyCursor.getCount();

                    Log.i(TAG, "Mon - " + month.toString());

                    months.add(date.substring(0, date.length() - 4) + "-" + date.substring(4, 6));
                    date = nextDate.toString();
                }

                labels = new String[months.size()];
                points = new DataPoint[months.size()];
                for (int i = 0; i < months.size(); i++) {
                    points[i] = new DataPoint(i, orders.get(i));
                    labels[i] = months.get(i);
                }

                graphView = findViewById(R.id.Graph1);
                graphView.removeAllSeries();




        }
    }
}