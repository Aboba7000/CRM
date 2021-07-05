package com.example.crm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class ClientActivity extends AppCompatActivity {
    public String idC;
    public ClientsDBHelper DBHelper ;
    public Cursor clientsCursor;
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter;
    public boolean phoneCheck,emailCheck,showOrders,showEvents;
    public Integer fav;
    LinearLayout linearLayoutEvents;
    final Context context = this;

    Calendar dateAndTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_client);
        Toolbar toolbar = findViewById(R.id.toolbarClient);
        toolbar.setTitle("Клиент");
        setSupportActionBar(toolbar);
        showOrders = false;

        linearLayoutEvents = findViewById(R.id.linearLayoutEvents);

        //получаем idc
        idC = getIntent().getExtras().getString("idC");
        DBHelper = new ClientsDBHelper(this);
        SQLiteDatabase clientDB = DBHelper.getReadableDatabase();
        clientsCursor = clientDB.query(ClientsContract.ClientsEntry.TABLE_NAME, null,
                "id_C = ?", new String[]{idC},null,null,null);

        clientsCursor.moveToFirst();
        fav = clientsCursor.getInt(clientsCursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_FAV));
        //создаем TextView
        final TextView textViewFullName = findViewById(R.id.textViewFullName);
        textViewFullName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editFields(ClientsContract.ClientsEntry.COLUMN_NAME_FULL,textViewFullName.getText().toString(),"Полное наименование");
                return false;
            }
        });
        textViewFullName.setText(clientsCursor.getString(clientsCursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_NAME_FULL)));
        String addressFact = clientsCursor.getString(clientsCursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_ADDRESS));
        String addressUr = clientsCursor.getString(clientsCursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_ADDRES_UR));
        final TextView textViewAddressFact = findViewById(R.id.textViewAddressFact);
        final TextView textViewAddressUr = findViewById(R.id.textViewAddressUr);
        textViewAddressFact.setText(addressFact);
        textViewAddressFact.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editFields(ClientsContract.ClientsEntry.COLUMN_ADDRESS,textViewAddressFact.getText().toString(),"Фактический адрес");
                return false;
            }
        });
        //проверка адреосов
        if (addressUr.isEmpty() || addressUr == "" || addressFact == addressUr || clientsCursor.getString(clientsCursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_ADDRES_UR))==null)
        {
            LinearLayout linearLayout = findViewById(R.id.linearLayoutAddress);
            linearLayout.setVisibility(View.GONE);
        }
        else
        {
            textViewAddressUr.setText(addressUr);
            textViewAddressUr.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    editFields(ClientsContract.ClientsEntry.COLUMN_ADDRES_UR,textViewAddressUr.getText().toString(),"Юридический адрес");
                    return false;
                }
            });
        }
        final TextView textViewPhone = findViewById(R.id.textViewPhone);
        //проверяем есть ли телефон
        if (clientsCursor.getString(clientsCursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_PHONE)) != null && !clientsCursor.getString(clientsCursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_PHONE)).isEmpty())
        {
            phoneCheck = true;
            textViewPhone.setText(clientsCursor.getString(clientsCursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_PHONE)));
            textViewPhone.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    editFields(ClientsContract.ClientsEntry.COLUMN_PHONE,textViewPhone.getText().toString(),"Телефон");
                    return false;
                }
            });
        }
        else
        {
            LinearLayout linearLayout = findViewById(R.id.linearLayoutPhone);
            linearLayout.setVisibility(View.GONE);
            phoneCheck = false;
        }

        final TextView textViewEmail = findViewById(R.id.textViewEmail);
        LinearLayout linearLayoutEmail = findViewById(R.id.linearLayoutEmail);
        //проверяем есть ли Email
        if (clientsCursor.getString(clientsCursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_EMAIL)) != null && !clientsCursor.getString(clientsCursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_EMAIL)).isEmpty())
        {

            textViewEmail.setText(clientsCursor.getString(clientsCursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_EMAIL)));
            emailCheck = true;
            textViewEmail.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    editFields(ClientsContract.ClientsEntry.COLUMN_EMAIL,textViewEmail.getText().toString(),"Email");
                    return false;
                }
            });
        }
        else
        {
            emailCheck = false;
            linearLayoutEmail.setVisibility(View.GONE);
        }
        final TextView textViewINN_KPP = findViewById(R.id.textViewINN_KPP);
        textViewINN_KPP.setText(clientsCursor.getString(clientsCursor.getColumnIndex("INN_KPP")));
        textViewINN_KPP.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editFields(ClientsContract.ClientsEntry.COLUMN_INN,textViewINN_KPP.getText().toString(),"ИНН/КПП");
                return false;
            }
        });
        //рассчетные счета
        LinearLayout layoutPA = findViewById(R.id.layoutPayment);
        LinearLayout.LayoutParams stroke = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
        stroke.leftMargin = 4;
        stroke.rightMargin = 4;

        /*if (!clientsCursor.isNull(clientsCursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_PA)))
        {
            layoutPA.setVisibility(View.VISIBLE);
            Integer idPay = clientsCursor.getInt(clientsCursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_PA));
            Cursor paCursor = clientDB.query(PaymentContract.PaymentEntry.TABLE_NAME, new String[]{PaymentContract.PaymentEntry.COLUMN_BANK, PaymentContract.PaymentEntry.COLUMN_Account},"id_Pay = ?", new String[] {Integer.toString(idPay)},null,null,null);
            paCursor.moveToFirst();
            TextView textViewPA1 = findViewById(R.id.textViewPA1);
            TextView textViewPA2 = findViewById(R.id.textViewPA2);
            textViewPA1.setText(paCursor.getString(paCursor.getColumnIndex(PaymentContract.PaymentEntry.COLUMN_BANK)));
            textViewPA2.setText(paCursor.getString(paCursor.getColumnIndex(PaymentContract.PaymentEntry.COLUMN_Account)));
        }
        else
            layoutPA.setVisibility(View.GONE);*/

        Cursor PACursor = clientDB.query(PaymentContract.PaymentEntry.TABLE_NAME,null,"id_Client = ?",new String[]{idC},null,null,null);
        PACursor.moveToFirst();
        LinearLayout LL;

        if (PACursor.getCount() != 0)
        {
            while (PACursor.getPosition() != PACursor.getCount())
            {
                LL = new LinearLayout(this);
                LL.setOrientation(LinearLayout.VERTICAL);
                LL.setPadding(4,0,4,0);

                final TextView textView1 = new TextView(this);
                textView1.setText(PACursor.getString(PACursor.getColumnIndex(PaymentContract.PaymentEntry.COLUMN_BANK)));
                textView1.setTextColor(Color.BLACK);
                //textView1.setPadding(4,0,4,0);
                LL.addView(textView1);

                final TextView textView2 = new TextView(this);
                textView2.setText(PACursor.getString(PACursor.getColumnIndex(PaymentContract.PaymentEntry.COLUMN_Account)));
                textView2.setTextColor(Color.BLACK);
                //textView2.setPadding(4,0,4,0);
                LL.addView(textView2);



                View view = new View(this);
                view.setLayoutParams(stroke);
                view.setBackgroundColor(Color.parseColor("#7b7b7b"));
                LL.addView(view);
                layoutPA.addView(LL);
                LL.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View alertView = layoutInflater.inflate(R.layout.alert_dialog_payment_edit,null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setView(alertView);
                        final EditText userInput1 = (EditText) alertView.findViewById(R.id.edit_text_bank_name);
                        final EditText userInput2 = (EditText) alertView.findViewById(R.id.edit_text_payment_account);
                        userInput1.setText(textView1.getText());
                        userInput2.setText(textView2.getText());
                        builder
                                .setCancelable(false)
                                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SQLiteDatabase DB = DBHelper.getWritableDatabase();
                                        String s1 = userInput1.getText().toString();
                                        String s2 = userInput2.getText().toString();
                                        ContentValues updateValues = new ContentValues();
                                        updateValues.put(PaymentContract.PaymentEntry.COLUMN_BANK,s1);
                                        updateValues.put(PaymentContract.PaymentEntry.COLUMN_Account,s2);
                                        //String where = "Account like '" + textView2.getText() + "' and id_Client = " + idC;
                                        String where = "Account like '" + textView2.getText() + "' and Bank like '" + textView1.getText() + "'";
                                        DB.update(PaymentContract.PaymentEntry.TABLE_NAME,updateValues,where,null);
                                        recreate();
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
                    }
                });
                PACursor.moveToNext();
            }
        }
        else layoutPA.setVisibility(View.GONE);



        if (!clientsCursor.isNull(clientsCursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_INF)))
        {
            final TextView textViewInformation = findViewById(R.id.textViewClientInformation);
            textViewInformation.setText(clientsCursor.getString(clientsCursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_INF)));
            textViewInformation.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    editFields(ClientsContract.ClientsEntry.COLUMN_INF,textViewInformation.getText().toString(),"Дополнительная информация");
                    return false;
                }
            });
        }
        else
        {
            LinearLayout linearLayout = findViewById(R.id.linearLayoutInf);
            linearLayout.setVisibility(View.GONE);
        }


        HistoryAdapter.OnHistoryClickListener onHistoryClickListener = new HistoryAdapter.OnHistoryClickListener() {
            @Override
            public void onHistoryClick(String id) {
                Intent intent = new Intent(ClientActivity.this, HistoryActivity.class);
                intent.putExtra("idH", id);
                startActivity(intent);
            }
        };

        //отображение истории
        recyclerView=findViewById(R.id.historyResyclerViewClient);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        Cursor cursorHistory = clientDB.query(HistoryContract.HistoryEntry.TABLE_NAME,null,"Customer = ?",new String[]{idC},null,null,null);
        historyAdapter = new HistoryAdapter(this,cursorHistory,onHistoryClickListener);
        recyclerView.setAdapter(historyAdapter);

        //отображение событий
        Cursor cursorEvents = clientDB.query("Events",null,"Customer = ?",new String[]{idC},null,null,"Date DESC");
        cursorEvents.moveToFirst();
        while (cursorEvents.getPosition() < cursorEvents.getCount())
        {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView = new TextView(this);
            textView.setText("Дата: " + formatDate(cursorEvents.getString(cursorEvents.getColumnIndex("Date"))));
            textView.setTextColor(Color.BLACK);
            textView.setPadding(15,5,40,0);
            layout.addView(textView);

            textView = new TextView(this);
            textView.setText("Время: " + cursorEvents.getString(cursorEvents.getColumnIndex("Time")));
            textView.setTextColor(Color.BLACK);
            layout.addView(textView);
            linearLayoutEvents.addView(layout);

            int t = cursorEvents.getInt(cursorEvents.getColumnIndex("Type"));
            Cursor cursor = clientDB.query("Event_Type",null,"ID_ET = ?",new String[]{Integer.toString(t)},null,null,null);
            cursor.moveToFirst();
            Log.i("SS",Integer.toString(t));

            textView = new TextView(this);
            textView.setText("Тип: " + cursor.getString(cursor.getColumnIndex("Type")));
            textView.setTextColor(Color.BLACK);
            textView.setPadding(15,0,0,0);
            linearLayoutEvents.addView(textView);

            textView = new TextView(this);
            textView.setText("Описание: " + cursorEvents.getString(cursorEvents.getColumnIndex("Annotation")));
            textView.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            textView.setTextColor(Color.BLACK);
            textView.setPadding(15,0,0,0);
            linearLayoutEvents.addView(textView);


            View view = new View(this);
            view.setLayoutParams(stroke);
            view.setBackgroundColor(Color.parseColor("#7b7b7b"));
            linearLayoutEvents.addView(view);


            cursorEvents.moveToNext();


        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_client_activity, menu);
        MenuItem menuItem = menu.findItem(R.id.addFavorite);
        switch (fav){
            case 0:
                menuItem.setIcon(R.drawable.outline_star_border_white_36);
                break;
            case 1:
                menuItem.setIcon(R.drawable.outline_star_purple500_white_36);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final Cursor cursor;
        LinearLayout linearLayout;
        String phoneNo;
        LayoutInflater layoutInflater;
        final View alertView;

        final SQLiteDatabase DBW = DBHelper.getWritableDatabase();
        final SQLiteDatabase DBR = DBHelper.getReadableDatabase();

        switch (item.getItemId())
        {

            case R.id.addFavorite:
                //в избранное
                cursor = DBW.query(ClientsContract.ClientsEntry.TABLE_NAME, new String[]{ClientsContract.ClientsEntry.COLUMN_FAV},"id_C = ?",new String[]{idC},null,null,null);
                cursor.moveToFirst();
                if (cursor.getInt(cursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_FAV)) == 0)
                {
                    item.setIcon(R.drawable.outline_star_purple500_white_36);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ClientsContract.ClientsEntry.COLUMN_FAV,1);
                    String where = "id_C = " + idC;
                    DBW.update(ClientsContract.ClientsEntry.TABLE_NAME,contentValues,where,null);
                    fav = 1;
                    //recreate();
                }
                else
                {
                    item.setIcon(R.drawable.outline_star_border_white_36);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ClientsContract.ClientsEntry.COLUMN_FAV,0);
                    String where = "id_C = " + idC;
                    fav = 0;
                    DBW.update(ClientsContract.ClientsEntry.TABLE_NAME,contentValues,where,null);
                }

                return true;
            case R.id.email:
                //отправить письмо
                cursor = DBR.query(ClientsContract.ClientsEntry.TABLE_NAME, new String[]{ClientsContract.ClientsEntry.COLUMN_NAME_FULL,ClientsContract.ClientsEntry.COLUMN_EMAIL},"id_C = ?",new String[]{idC},null,null,null);
                cursor.moveToFirst();
                if (!cursor.isNull(cursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_EMAIL)))
                {
                    String em = cursor.getString(cursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_EMAIL));
                    String name = cursor.getString(cursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_NAME_FULL));
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{em});
                    email.putExtra(Intent.EXTRA_SUBJECT, "ООО 'Пожснаб плюс'");
                    email.putExtra(Intent.EXTRA_TEXT, "Добрый день. Данное письмо направлено компании " + name + " от ООО 'Пожснаб плюс'.");
                    email.setType("message/rfc822");
                    startActivity(Intent.createChooser(email, "Выберите почтовую программу:"));
                }
                else
                {
                    Toast.makeText(this,"У клиента отсутствует Email",Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.addEvents:
                //добавление событий
                layoutInflater = LayoutInflater.from(this);
                alertView = layoutInflater.inflate(R.layout.alert_dialog_add_event,null);
                AlertDialog.Builder builderAddEvent = new AlertDialog.Builder(this);
                builderAddEvent.setView(alertView);
                final EditText eventDate = alertView.findViewById(R.id.input_text_date_event);
                final EditText eventTime = alertView.findViewById(R.id.input_text_time_event);
                eventTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTime(alertView);
                    }
                });
                Spinner spinner = alertView.findViewById(R.id.spinner);
                EditText eventInf = findViewById(R.id.input_text_inf_event);
                cursor=DBW.query("Event_Type",null,null,null,null,null,null);
                cursor.moveToFirst();
                ArrayList<String> types = new ArrayList<>();
                while (cursor.getPosition() < cursor.getCount())
                {
                    types.add(cursor.getString(cursor.getColumnIndex("Type")));
                    cursor.moveToNext();
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,types);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(arrayAdapter);

                builderAddEvent
                        .setCancelable(false)
                        .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {



                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialogAddEvent =builderAddEvent.create();

                alertDialogAddEvent.show();

                return true;
            case R.id.addEmail:
                //добавить Email
                final MenuItem item1 = item;
                layoutInflater = LayoutInflater.from(this);
                alertView = layoutInflater.inflate(R.layout.aler_dialog_email,null);
                AlertDialog.Builder builderEmail = new AlertDialog.Builder(this);
                builderEmail.setView(alertView);
                final EditText userInputEmail = (EditText) alertView.findViewById(R.id.input_text_email);
                builderEmail
                        .setCancelable(false)
                        .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String s = userInputEmail.getText().toString();
                                ContentValues updateValues = new ContentValues();
                                updateValues.put(ClientsContract.ClientsEntry.COLUMN_EMAIL,s);
                                String where = "id_C = " + idC;
                                DBW.update(ClientsContract.ClientsEntry.TABLE_NAME,updateValues,where,null);
                                recreate();
                                invalidateOptionsMenu();
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialogEmail =builderEmail.create();
                alertDialogEmail.show();
                emailCheck = true;
                return true;
            case R.id.addPhone:
                //добавить телефон
                layoutInflater = LayoutInflater.from(this);
                alertView = layoutInflater.inflate(R.layout.alert_dialog_phone,null);
                AlertDialog.Builder builderPhone = new AlertDialog.Builder(this);
                builderPhone.setView(alertView);
                final EditText userInputPhone = (EditText) alertView.findViewById(R.id.input_text_phone);
                builderPhone
                        .setCancelable(false)
                        .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String s = userInputPhone.getText().toString();
                                ContentValues updateValues = new ContentValues();
                                updateValues.put(ClientsContract.ClientsEntry.COLUMN_PHONE,s);
                                String where = "id_C = " + idC;
                                DBW.update(ClientsContract.ClientsEntry.TABLE_NAME,updateValues,where,null);
                                recreate();
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialogPhone =builderPhone.create();
                alertDialogPhone.show();
                phoneCheck = true;
                return true;
                //добавление рассчетного счета
            case R.id.addPay:
                layoutInflater = LayoutInflater.from(this);
                alertView = layoutInflater.inflate(R.layout.alert_dialog_payment,null);
                AlertDialog.Builder builderPayment = new AlertDialog.Builder(this);
                builderPayment.setView(alertView);
                final EditText userInputBank = (EditText) alertView.findViewById(R.id.input_text_bank_name);
                final EditText userInputAccount = (EditText) alertView.findViewById(R.id.input_text_payment_account);
                builderPayment
                        .setCancelable(false)
                        .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String s1 = userInputBank.getText().toString();
                                String s2 = userInputAccount.getText().toString();
                                if (s2.length() == 20)
                                {
                                    if (s1.length() > 0)
                                    {
                                        ContentValues updateValues = new ContentValues();
                                        updateValues.put(PaymentContract.PaymentEntry.COLUMN_idClient,idC);
                                        updateValues.put(PaymentContract.PaymentEntry.COLUMN_BANK,s1);
                                        updateValues.put(PaymentContract.PaymentEntry.COLUMN_Account,s2);
                                        DBW.insert(PaymentContract.PaymentEntry.TABLE_NAME,null,updateValues);
                                    }
                                    else Toast.makeText(context,"Строка 'Наименование банка' не заполнена",Toast.LENGTH_SHORT).show();

                                }
                                else
                                {
                                    Toast.makeText(context,"Количество символов в строке 'Номер счета' должно быть равно 20",Toast.LENGTH_SHORT).show();

                                }

                                recreate();
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialogPayment = builderPayment.create();
                alertDialogPayment.show();
                return true;
            case R.id.phone:
                //звонок
                cursor = DBW.query(ClientsContract.ClientsEntry.TABLE_NAME,new String[]{ClientsContract.ClientsEntry.COLUMN_PHONE},"id_C = ?",new String[]{idC},null,null,null);
                cursor.moveToFirst();
                if (!cursor.isNull(cursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_PHONE))) {
                    phoneNo = cursor.getString(cursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_PHONE));
                    phoneNo = phoneFormat(phoneNo);
                    //Toast.makeText(this,phoneNo,Toast.LENGTH_SHORT).show();
                    if(!TextUtils.isEmpty(phoneNo))
                    {
                        String dial = "tel:"+phoneNo;
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                    }
                }
                else
                {
                    Toast.makeText(this,"У клиента отстутсвует номер",Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.sms:
                // отправить смс
                cursor = DBW.query(ClientsContract.ClientsEntry.TABLE_NAME,new String[]{ClientsContract.ClientsEntry.COLUMN_NAME_FULL,ClientsContract.ClientsEntry.COLUMN_PHONE},"id_C = ?",new String[]{idC},null,null,null);
                cursor.moveToFirst();
                if(!cursor.isNull(cursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_PHONE)))
                {
                    phoneNo = cursor.getString(cursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_PHONE));
                    phoneNo = phoneFormat(phoneNo);
                    if(!TextUtils.isEmpty(phoneNo))
                    {
                        String sms = "smsto:"+phoneNo;
                        String name = cursor.getString(cursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_NAME_FULL));
                        String message = "Добрый день. Данное письмо направлено компании " + name + " от ООО 'Пожснаб плюс'.";
                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse(sms));
                        smsIntent.putExtra("sms_body",message);
                        startActivity(smsIntent);
                    }
                }
                else
                {
                    Toast.makeText(this,"У клиента отстутсвует номер",Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.showOrders:
                linearLayout = findViewById(R.id.linearLayoutOrders);
                linearLayout.setVisibility(View.VISIBLE);
                showOrders = true;
                return true;
            case R.id.hideOrders:
                //спрятать заказы
                linearLayout = findViewById(R.id.linearLayoutOrders);
                linearLayout.setVisibility(View.GONE);
                showOrders = false;
                return true;
            case R.id.showEvents:
                linearLayoutEvents.setVisibility(View.VISIBLE);
                showEvents = true;
                return true;
            case R.id.hideEvents:
                showEvents = false;
                linearLayoutEvents.setVisibility(View.GONE);
                return true;
            case R.id.delClient:
                //удаление клиента
                cursor = DBW.query(HistoryContract.HistoryEntry.TABLE_NAME,null,"Customer = ?",new String[]{idC},null,null,null);
                if (cursor.getCount() > 0)
                {
                    Toast.makeText(this,"Данные клиента используются в заказах!",Toast.LENGTH_SHORT).show();
                    return true;
                }else
                {
                    DBR.delete(ClientsContract.ClientsEntry.TABLE_NAME,"id_C = ?",new String[]{idC});
                    Toast.makeText(this,"Клиент успешно удален!",Toast.LENGTH_SHORT).show();
                    this.finish();
                    return true;
                }

            default:return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item;
        //есть ли телефон
        if (!phoneCheck){
            item = menu.findItem(R.id.addPhone);
            item.setVisible(true);
        }
        else
        {
            item = menu.findItem(R.id.addPhone);
            item.setVisible(false);
        }
        //есть ли email
        if (!emailCheck){
            item = menu.findItem(R.id.addEmail);
            item.setVisible(true);
        }
        else
        {
            item = menu.findItem(R.id.addEmail);
            item.setVisible(false);
        }
        //показать заказы
        if(showOrders)
        {
            item=menu.findItem(R.id.showOrders);
            item.setVisible(false);
            item=menu.findItem(R.id.hideOrders);
            item.setVisible(true);
        }
        else
        {
            item=menu.findItem(R.id.showOrders);
            item.setVisible(true);
            item=menu.findItem(R.id.hideOrders);
            item.setVisible(false);
        }
        if(showEvents)
        {
            item=menu.findItem(R.id.showEvents);
            item.setVisible(false);
            item=menu.findItem(R.id.hideEvents);
            item.setVisible(true);
        }
        else
        {
            item=menu.findItem(R.id.showEvents);
            item.setVisible(true);
            item=menu.findItem(R.id.hideEvents);
            item.setVisible(false);
        }

        return true;
    }
    public static String  phoneFormat(String s){
        //удаление лишних символов из номера телеофна
        String newStr="";
        for (int i = 0;i<s.length();i++)
        {
            newStr += s.charAt(i) != '-' && s.charAt(i) != '(' && s.charAt(i) != ')' && s.charAt(i) != ' ' ? s.charAt(i) : "";

        }
        return newStr;
    }

    public void editFields(final String columnName, String content, String fieldName)
    {
        final SQLiteDatabase DB = DBHelper.getReadableDatabase();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View alertView = layoutInflater.inflate(R.layout.alert_dialog_edit,null);
        TextView textView = alertView.findViewById(R.id.textViewEdit);
        textView.setText(fieldName);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(alertView);
        final EditText userInput = (EditText) alertView.findViewById(R.id.input_text_edit);
        userInput.setText(content);
        builder
                .setCancelable(false)
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = userInput.getText().toString();
                        ContentValues updateValues = new ContentValues();
                        updateValues.put(columnName,s);
                        String where = "id_C = " + idC;
                        DB.update(ClientsContract.ClientsEntry.TABLE_NAME,updateValues,where,null);
                        recreate();
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

    }

    private String formatDate(String s)
    {
        char[] textDate = {s.charAt(6),s.charAt(7),'/',s.charAt(4),s.charAt(5),'/',s.charAt(0),s.charAt(1),s.charAt(2),s.charAt(3)};
        s = String.copyValueOf(textDate);
        return s;
    }
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            //setInitialDateTime();
        }
    };
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            //setInitialDateTime();
        }
    };

    public void setTime(View v) {
        new TimePickerDialog(ClientActivity.this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }

    public void setDate(View v) {
        new DatePickerDialog(ClientActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

}