package com.example.crm;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;


public class HistoryFragment extends Fragment {

    private ClientsDBHelper DBHelper;
    private SQLiteDatabase DB;
    private HistoryAdapter historyAdapter;
    private RecyclerView recyclerView;
    private Calendar calendar1 = Calendar.getInstance();
    private Calendar calendar2 = Calendar.getInstance();
    private EditText userInput1;
    private EditText userInput2;
    private String date1,date2;


    public HistoryFragment() {
        // Required empty public constructor
    }


    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        //подключение к базе
        DBHelper = new ClientsDBHelper(getActivity());
        try {
            DBHelper.updateDataBase();
        } catch (IOException e) {
            throw new Error("Unable to update database!");
        }

        try {
            DB = DBHelper.getWritableDatabase();
        } catch (SQLException e) {
            throw e;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //устанавливаем фрагмент
        View v = inflater.inflate(R.layout.fragment_history, null);
        recyclerView = v.findViewById(R.id.historyResyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //устанавливаем тулбар
        Toolbar toolbar = v.findViewById(R.id.toolbar2);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("История");
        }

        //слушатель для нажатий
        HistoryAdapter.OnHistoryClickListener onHistoryClickListener = new HistoryAdapter.OnHistoryClickListener() {
            @Override
            public void onHistoryClick(String id) {
                //отыкрываем новую активити
                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                intent.putExtra("idH", id);
                startActivity(intent);
            }
        };
        //устанавливаем адаптер
        historyAdapter = new HistoryAdapter(getActivity(), getAllItems(1), onHistoryClickListener);
        recyclerView.setAdapter(historyAdapter);
        return v;
    }

    private Cursor getAllItems(int sort) {

        switch (sort) {
            case 1:
                return DB.query(HistoryContract.HistoryEntry.TABLE_NAME, null, null, null, null, null, "id_H ASC");
            case 2:
                return DB.query(HistoryContract.HistoryEntry.TABLE_NAME, null, null, null, null, null, "id_H DESC");
            case 3:
                return DB.query(HistoryContract.HistoryEntry.TABLE_NAME, null, null, null, null, null, "Date ASC");
            case 4:
                return DB.query(HistoryContract.HistoryEntry.TABLE_NAME, null, null, null, null, null, "Date DESC");
            case 5:
                return DB.query(HistoryContract.HistoryEntry.TABLE_NAME, null, null, null, "Date", "Date >= " + formatDate(date1) +" and Date <= "+formatDate(date2), null);
            default:
                return DB.query(HistoryContract.HistoryEntry.TABLE_NAME, null, null, null, null, null, null);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //подгрузка иконок в тулбаре
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu_history, menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final HistoryAdapter.OnHistoryClickListener onHistoryClick = new HistoryAdapter.OnHistoryClickListener() {
            @Override
            public void onHistoryClick(String id) {
                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                intent.putExtra("idH", id);
                startActivity(intent);
            }
        };
        switch (item.getItemId()) {
            case R.id.refreshHistory:
                //обновить
                historyAdapter = new HistoryAdapter(getActivity(), getAllItems(0), onHistoryClick);
                recyclerView.setAdapter(historyAdapter);
                item.setChecked(true);
                return true;
            case R.id.sortByIDHUp:
                //по IDH
                historyAdapter = new HistoryAdapter(getActivity(), getAllItems(1), onHistoryClick);
                recyclerView.setAdapter(historyAdapter);
                item.setChecked(true);
                return true;
            case R.id.sortByIDHDown:
                //по IDH
                historyAdapter = new HistoryAdapter(getActivity(), getAllItems(2), onHistoryClick);
                recyclerView.setAdapter(historyAdapter);
                item.setChecked(true);
                return true;
            case R.id.sortByDateHistoryUp:
                //по дате
                historyAdapter = new HistoryAdapter(getActivity(), getAllItems(3), onHistoryClick);
                recyclerView.setAdapter(historyAdapter);
                item.setChecked(true);
                return true;
            case R.id.sortByDateHistoryDown:
                //по дате
                historyAdapter = new HistoryAdapter(getActivity(), getAllItems(4), onHistoryClick);
                recyclerView.setAdapter(historyAdapter);
                item.setChecked(true);
                return true;
            case R.id.calendar:
                //выбор периода - вызов всплывающего окна
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                View alertView = layoutInflater.inflate(R.layout.alert_dialog_calendar,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(alertView);
                userInput1 = (EditText) alertView.findViewById(R.id.editTextDate1);
                userInput1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //открыть календарь 1
                        SetDate1(v);
                    }
                });
                userInput2 = (EditText) alertView.findViewById(R.id.editTextDate2);
                userInput2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //открыть календарь 2
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
                                historyAdapter = new HistoryAdapter(getActivity(), getAllItems(5), onHistoryClick);
                                recyclerView.setAdapter(historyAdapter);
                                //Toast.makeText(getContext(),formatDate(date1),Toast.LENGTH_SHORT).show();
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
            default:return super.onOptionsItemSelected(item);
        }
    }

    //показ первого календаря
    public void SetDate1(View v)
    {
        new DatePickerDialog(getContext(), d1,
                calendar1.get(Calendar.YEAR),
                calendar1.get(Calendar.MONTH),
                calendar1.get(Calendar.DAY_OF_MONTH))
                .show();

    }

    //показ 2 календаря
    public void SetDate2(View v)
    {
        new DatePickerDialog(getContext(), d2,
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
        userInput1.setText(DateUtils.formatDateTime(getContext(),
                calendar1.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }
    //установка даты во 2 поле
    private void setInitialDateTime2() {
        userInput2.setText(DateUtils.formatDateTime(getContext(),
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



}