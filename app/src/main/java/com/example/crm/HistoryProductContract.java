package com.example.crm;

import android.provider.BaseColumns;

public class HistoryProductContract {
    private HistoryProductContract(){}

    public static final class HistoryProductEntry implements BaseColumns{
        public static final String TABLE_NAME = "History_product";
        public static final String COLUMN_ID = "History_id";
        public static final String COLUMN_PRODUCT = "Product_id";
        public static final String COLUMN_AMMOUNT = "Ammount";
    }
}
