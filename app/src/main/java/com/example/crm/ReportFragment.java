package com.example.crm;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Calendar;


public class ReportFragment extends Fragment {




    public ReportFragment() {
        // Required empty public constructor
    }


    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_report, null);
        Toolbar toolbar = v.findViewById(R.id.toolbarReports);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Отчеты");
        }


        LinearLayout linearLayoutGraph1 = v.findViewById(R.id.linearLayoutGraph1);
        linearLayoutGraph1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(),"SSSSSS",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),SellsReportActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout linearLayoutGraph2 = v.findViewById(R.id.linearLayoutGraph2);
        linearLayoutGraph2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ClientsReportActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout linearLayoutAnalyze1 = v.findViewById(R.id.linearLayoutAnalyze1);
        linearLayoutAnalyze1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),SellsDynamicAnalyzeActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout linearLayoutAnalyze2 = v.findViewById(R.id.linearLayoutAnalyze2);
        linearLayoutAnalyze2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AbcAnalyzeActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout linearLayoutAnalyze3 = v.findViewById(R.id.linearLayoutAnalyze3);
        linearLayoutAnalyze3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),XyzAnalyzeActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout linearLayoutAnalyze4 = v.findViewById(R.id.linearLayoutAnalyze4);
        linearLayoutAnalyze4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),CrossAnalyzeActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu_reports,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}