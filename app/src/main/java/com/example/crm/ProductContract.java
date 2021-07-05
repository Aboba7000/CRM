package com.example.crm;

import android.provider.BaseColumns;

public class ProductContract {
    private ProductContract(){}

    public static final class ProductEntry implements BaseColumns {
        public static final String TABLE_NAME = "Products";
        public static final String COLUMN_ID = "ID_P";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_Price = "Price";
        public static final String COLUMN_TYPE = "Type";
    }


}
