package com.example.crm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity{

    private BottomNavigationView MainNav;
    private FrameLayout MainFrame;
    private ClientsFragment clientsFragment;
    private HistoryFragment historyFragment;
    private ReportFragment reportFragment;
    private MenuInflater toolbarInflater;
    //private Menu menuToolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);






        setContentView(R.layout.activity_main);
        MainNav= findViewById(R.id.main_nav);
        MainFrame = findViewById(R.id.main_frame);

        //создаем фрагменты
        clientsFragment = new ClientsFragment();
        historyFragment = new HistoryFragment();
        reportFragment = new ReportFragment();
        toolbarInflater = getMenuInflater();
        //устанавливаем по умолчанию
        setFragment(clientsFragment);


        //обработка смены вкладки
        MainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.item_clients:
                    {
                        setFragment(clientsFragment);
                        return true;
                    }
                    case R.id.item_history:
                    {
                        setFragment(historyFragment);
                        return true;
                    }
                    case R.id.item_reports:
                    {
                        setFragment(reportFragment);
                        return true;
                    }
                    default:return false;
                }

            }
        });


        ClientsAdapter.OnClientClickListener onClientClickListener = new ClientsAdapter.OnClientClickListener() {
            @Override
            public void onClientClick(String id) {
                Intent intent = new Intent(MainActivity.this,ClientActivity.class);
                intent.putExtra("IDC",id);
                startActivity(intent);
            }
        };

        HistoryAdapter.OnHistoryClickListener onHistoryClickListener = new HistoryAdapter.OnHistoryClickListener() {

            @Override
            public void onHistoryClick(String id) {
                Intent intent = new Intent(MainActivity.this,ClientActivity.class);
                intent.putExtra("idH",id);
                startActivity(intent);
            }
        };



    }

    //всплывающее окно при закрытии
    @Override
    public void onBackPressed()
    {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View alertView = layoutInflater.inflate(R.layout.alert_dialog_exit,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(alertView);
        builder
                .setCancelable(false)
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();}
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog =builder.create();
        alertDialog.show();

    }

    //установка фрагмента
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();


    }





}