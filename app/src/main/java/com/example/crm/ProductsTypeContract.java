package com.example.crm;

import android.provider.BaseColumns;

public class ProductsTypeContract {
    private ProductsTypeContract(){}
    public static final class ProductsTypesEntry implements BaseColumns {
        public static final String TABLE_NAME = "products_type";
        public static final String COLUMN_idPT = "id_PT";
        public static final String COLUMN_Name = "Name";

    }
}

