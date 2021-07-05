package com.example.crm;

import android.provider.BaseColumns;

public class ClientsContract {
    private ClientsContract() {}

    public static final class ClientsEntry implements BaseColumns{
        public static final String TABLE_NAME = "Clients";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_IDC = "id_C";
        public static final String COLUMN_NAME_FULL = "Name_full";
        public static final String COLUMN_ADDRESS = "Address_fact";
        public static final String COLUMN_ADDRES_UR = "Address_ur";
        public static final String COLUMN_PHONE = "Phone";
        public static final String COLUMN_EMAIL = "Email";
        public static final String COLUMN_PA = "payment_account";
        public static final String COLUMN_INN = "INN_KPP";
        public static final String COLUMN_TYPE = "Type";
        public static final String COLUMN_INF = "Information";
        public static final String COLUMN_FAV = "Favorite";


    }
}
