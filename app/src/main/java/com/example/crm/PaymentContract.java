package com.example.crm;

import android.provider.BaseColumns;

public class PaymentContract {
    private PaymentContract(){}
    public static final class PaymentEntry implements BaseColumns {
        public static final String TABLE_NAME = "Payment";
        public static final String COLUMN_idClient = "id_Client";
        public static final String COLUMN_BANK = "Bank";
        public static final String COLUMN_Account = "Account";


    }
}
