package com.example.crm;

import android.provider.BaseColumns;

public class HistoryContract {
    private HistoryContract(){}
     public static final class HistoryEntry implements BaseColumns{
        public static final String TABLE_NAME = "History";
        public static final String COLUMN_idH = "id_H";
        public static final String COLUMN_Customer = "Customer";
        public static final String COLUMN_Date = "Date";
        public static final String COLUMN_Time = "Time";
     }


}
