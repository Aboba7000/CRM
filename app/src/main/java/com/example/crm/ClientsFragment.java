package com.example.crm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
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

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;



public class ClientsFragment extends Fragment  {

    private ClientsDBHelper DBHelper;
    private SQLiteDatabase DB;
    private ClientsAdapter clientsAdapter;
    private RecyclerView recyclerView;
    public String search = "";


    public ClientsFragment() {
        // Required empty public constructor
    }


    public static ClientsFragment newInstance(String param1, String param2) {
        ClientsFragment fragment = new ClientsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        //открытие бд
        DBHelper = new ClientsDBHelper(getActivity());
        try {
            DBHelper.updateDataBase();
        } catch (IOException e) {
            throw new  Error("Unable to update database!");
        }

        try {
            DB = DBHelper.getWritableDatabase();
        }catch (SQLException e){
            throw e;
        }




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v =inflater.inflate(R.layout.fragment_clients,null);
        recyclerView = v.findViewById(R.id.clientsResyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));


        Toolbar toolbar = v.findViewById(R.id.toolbar1);
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar!= null) {
            actionBar.setTitle("Клиенты");
        }

        ClientsAdapter.OnClientClickListener onClientClickListener = new ClientsAdapter.OnClientClickListener() {
            @Override
            public void onClientClick(String id) {
                //отыкрываем новую активити
                Intent intent = new Intent(getActivity(),ClientActivity.class);
                intent.putExtra("idC",id);
                startActivity(intent);
            }
        };
        clientsAdapter = new ClientsAdapter(getActivity(),getAllItems(1),onClientClickListener);
        recyclerView.setAdapter(clientsAdapter);


        return v;
    }

    private Cursor getAllItems(int sort){
        switch (sort)
        {
            case 1:
                return DB.query(ClientsContract.ClientsEntry.TABLE_NAME,null,null,null,null,null, ClientsContract.ClientsEntry.COLUMN_NAME);
            case 2:
                return DB.query(ClientsContract.ClientsEntry.TABLE_NAME,null,null,null,null,null, "Name DESC");
            case 3:
                return DB.rawQuery("select distinct * from Clients join History ON Clients.id_C = History.Customer group by id_C order by Date ASC",null);
            case 4:
                return DB.rawQuery("select distinct * from Clients join History ON Clients.id_C = History.Customer group by id_C order by Date DESC",null);
            case 5:
                return DB.query(ClientsContract.ClientsEntry.TABLE_NAME,null,null,null,null,null, "id_C ASC");
            case 6:
                return DB.query(ClientsContract.ClientsEntry.TABLE_NAME,null,null,null,null,null, "id_C DESC");
            case 7:
                return DB.query(ClientsContract.ClientsEntry.TABLE_NAME,null,"Type = ?",new String[]{"1"},null,null, null);
            case 8:
                return DB.query(ClientsContract.ClientsEntry.TABLE_NAME,null,"Type = ?",new String[]{"2"},null,null, null);
            case 9:
                return DB.query(ClientsContract.ClientsEntry.TABLE_NAME,null,"Name like ?",new String[]{"%" + search + "%"},null,null, null);
            case 10:
                return DB.query(ClientsContract.ClientsEntry.TABLE_NAME,null,"Favorite = ?",new String[]{"1"},null,null, null);
            default:
                return DB.query(ClientsContract.ClientsEntry.TABLE_NAME,null,null,null,null,null, null);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //подгрузка иконок в тулбаре

        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.toolbar_menu_clients,menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       final ClientsAdapter.OnClientClickListener onClientClickListener = new ClientsAdapter.OnClientClickListener() {
            @Override
            public void onClientClick(String id) {
                //отыкрываем новую активити
                Intent intent = new Intent(getActivity(),ClientActivity.class);
                intent.putExtra("idC",id);
                startActivity(intent);
            }
        };
        switch (item.getItemId()){
            case R.id.sortByIDCUp:
                clientsAdapter = new ClientsAdapter(getActivity(),getAllItems(6),onClientClickListener);
                recyclerView.setAdapter(clientsAdapter);
                item.setChecked(true);
                return true;
            case R.id.sortByIDCDown:
                clientsAdapter = new ClientsAdapter(getActivity(),getAllItems(5),onClientClickListener);
                recyclerView.setAdapter(clientsAdapter);
                item.setChecked(true);
                return true;
            case R.id.sortByName:
                //А-Я
                clientsAdapter = new ClientsAdapter(getActivity(),getAllItems(1),onClientClickListener);
                recyclerView.setAdapter(clientsAdapter);
                item.setChecked(true);
                return true;
            case R.id.sortByNameDesc:
                //Я-А
                clientsAdapter = new ClientsAdapter(getActivity(),getAllItems(2),onClientClickListener);
                recyclerView.setAdapter(clientsAdapter);
                item.setChecked(true);
                return true;
            case R.id.sortByDateUp:
                //по дате
                clientsAdapter = new ClientsAdapter(getActivity(),getAllItems(3),onClientClickListener);
                recyclerView.setAdapter(clientsAdapter);
                item.setChecked(true);
                return true;
            case R.id.sortByDateDown:
                //по дате
                clientsAdapter = new ClientsAdapter(getActivity(),getAllItems(4),onClientClickListener);
                recyclerView.setAdapter(clientsAdapter);
                item.setChecked(true);
                return true;
            case R.id.sortByTypeIP:
                //ИП
                clientsAdapter = new ClientsAdapter(getActivity(),getAllItems(7),onClientClickListener);
                recyclerView.setAdapter(clientsAdapter);
                item.setChecked(true);
                return true;
            case R.id.sortByTypeUr:
                //Юр
                clientsAdapter = new ClientsAdapter(getActivity(),getAllItems(8),onClientClickListener);
                recyclerView.setAdapter(clientsAdapter);
                item.setChecked(true);
                return true;
            case R.id.sortByFav:
                if (!item.isChecked())
                {
                    clientsAdapter = new ClientsAdapter(getActivity(),getAllItems(10),onClientClickListener);
                    item.setChecked(true);
                }
                else
                {
                    clientsAdapter = new ClientsAdapter(getActivity(),getAllItems(0),onClientClickListener);
                    item.setChecked(false);
                }


                recyclerView.setAdapter(clientsAdapter);

                return true;
            case R.id.refreshClients:
                //обновить
                clientsAdapter = new ClientsAdapter(getActivity(),getAllItems(0),onClientClickListener);
                recyclerView.setAdapter(clientsAdapter);
                return true;
            case R.id.searchClients:
                //поиск
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                View alertView = layoutInflater.inflate(R.layout.alert_dialog_search,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(alertView);
                final EditText userInput = (EditText) alertView.findViewById(R.id.input_text_search);
                builder
                        .setCancelable(false)
                        .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                search = userInput.getText().toString();
                                clientsAdapter = new ClientsAdapter(getActivity(),getAllItems(9),onClientClickListener);
                                recyclerView.setAdapter(clientsAdapter);
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog =builder.create();
                alertDialog.show();
                return true;
            case R.id.addClient:
                Intent intent = new Intent(getActivity(),AddClientActivity.class);
                startActivity(intent);
                return true;


           /* case R.id.support:
                Intent intent = new Intent(getActivity(),SupportActivity.class);
                startActivity(intent);*/
            default:  return super.onOptionsItemSelected(item);
        }



    }


}
