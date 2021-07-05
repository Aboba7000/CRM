package com.example.crm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class AddClientActivity extends AppCompatActivity {

    String fullName,shortName,addressFact,addressUr,phone,email,INN_KPP,information;
    Integer type,id_C;
    Boolean check,checkType;
    ClientsDBHelper clientsDBHelper;
    SQLiteDatabase base;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);

        Toolbar toolbar = findViewById(R.id.toolbarAddClient);
        toolbar.setTitle("Клиент");
        setSupportActionBar(toolbar);

        checkType = false;



        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case -1:
                        check = false;
                        break;
                    case R.id.radioButton1:
                        type = 1;
                        checkType = true;
                        break;
                    case R.id.radioButton2:
                        type = 2;
                        checkType = true;
                        break;
                }
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_add_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId())
        {
            case R.id.addClient:
                check = true;
                EditText editText = findViewById(R.id.editTextName);
                if (editText.getText().toString().isEmpty()){
                    Toast.makeText(this,"Не заполнено поле 'Краткое наименование'!",Toast.LENGTH_SHORT).show();
                    check = false;
                    break;
                }
                else shortName = editText.getText().toString();

                editText = findViewById(R.id.editTextNameFull);
                if (editText.getText().toString().isEmpty()){
                    Toast.makeText(this,"Не заполнено поле 'Полное наименование'!",Toast.LENGTH_SHORT).show();
                    check =false;
                    break;
                }
                else fullName = editText.getText().toString();

                editText = findViewById(R.id.editTextAddressUr);
                if (editText.getText().toString().isEmpty()){
                    Toast.makeText(this,"Не заполнено поле 'Юридический адрес'!",Toast.LENGTH_SHORT).show();
                    check = false;
                    break;
                }
                else addressUr = editText.getText().toString();

                editText = findViewById(R.id.editTextAddressFact);
                addressFact = editText.getText().toString();

                editText = findViewById(R.id.editTextPhone);
                phone = editText.getText().toString();

                editText = findViewById(R.id.editTextEmail);
                email = editText.getText().toString();

                editText = findViewById(R.id.editTextINN);
                if (editText.getText().toString().isEmpty()){
                    Toast.makeText(this,"Не заполнено поле 'ИНН/КПП'!",Toast.LENGTH_SHORT).show();
                    check = false;
                    break;

                }
                else INN_KPP = editText.getText().toString();

                editText = findViewById(R.id.editTextInformation);
                information = editText.getText().toString();

                if (checkType == false)
                {
                    Toast.makeText(this,"Не выбрана категория клиента!",Toast.LENGTH_SHORT).show();
                    check = false;
                    break;
                }

                clientsDBHelper = new ClientsDBHelper(this);
                base = clientsDBHelper.getWritableDatabase();
                Cursor cursor = base.query(ClientsContract.ClientsEntry.TABLE_NAME,null,null,null,null,null,"id_C DESC");
                cursor.moveToFirst();
                id_C = cursor.getInt(cursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_IDC));
                id_C++;



                if (check)
                {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ClientsContract.ClientsEntry.COLUMN_IDC,id_C);
                    contentValues.put(ClientsContract.ClientsEntry.COLUMN_NAME,shortName);
                    contentValues.put(ClientsContract.ClientsEntry.COLUMN_NAME_FULL,fullName);
                    contentValues.put(ClientsContract.ClientsEntry.COLUMN_ADDRES_UR,addressUr);
                    contentValues.put(ClientsContract.ClientsEntry.COLUMN_ADDRESS,addressFact);
                    contentValues.put(ClientsContract.ClientsEntry.COLUMN_PHONE,phone);
                    contentValues.put(ClientsContract.ClientsEntry.COLUMN_EMAIL,email);
                    contentValues.put(ClientsContract.ClientsEntry.COLUMN_INN,INN_KPP);
                    contentValues.put(ClientsContract.ClientsEntry.COLUMN_TYPE,type);
                    contentValues.put(ClientsContract.ClientsEntry.COLUMN_INF,information);
                    contentValues.put(ClientsContract.ClientsEntry.COLUMN_FAV,0);

                    base.insert(ClientsContract.ClientsEntry.TABLE_NAME,null,contentValues);
                    this.finish();
                    Toast.makeText(this,"Клиент успешно добавлен",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this,ClientActivity.class);
                    intent.putExtra("idC",id_C.toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
                    startActivity(intent);
                }

        }
        return true;
    }
}