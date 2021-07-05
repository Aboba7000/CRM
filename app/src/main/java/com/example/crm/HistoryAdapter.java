package com.example.crm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    //создаем слушателя
    private OnHistoryClickListener onHistoryClickListener;

    //конструктор слушателя
    interface OnHistoryClickListener{
        void onHistoryClick (String id);
    }

    public HistoryAdapter(Context context, Cursor cursor,OnHistoryClickListener onHistoryClickListener)
    {
        mContext = context;
        mCursor = cursor;
        this.onHistoryClickListener = onHistoryClickListener;
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder{
        public TextView id_H;
        public TextView customerText;
        public TextView dateText;
        public TextView timeText;
        public TextView SumText;
        public View layout;


        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            //определяем TextView для записи
            id_H = itemView.findViewById(R.id.textViewIDH);
            customerText = itemView.findViewById(R.id.textViewHistoryName);
            dateText = itemView.findViewById(R.id.textViewHistoryDate);
            timeText = itemView.findViewById(R.id.textViewHistoryTime);
            SumText = itemView.findViewById(R.id.textViewHistorySum);
            layout = itemView.findViewById(R.id.linearLayoutHistoryItem);
            //слушатель для нажатий на шаблон элемента
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = id_H.getText().toString();
                    onHistoryClickListener.onHistoryClick(id);
                }
            });
        }
    }
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        //указыввем шаблон элемента
        View view = inflater.inflate(R.layout.item_history,parent,false);
        return new HistoryAdapter.HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)){
            return;
        }

        int IDH;
        int Customer;
        String date;
        String Time;
        int Sum = 0;
        int Ammount = 0;
        int id_P;
        int Price = 0;
        IDH = mCursor.getInt(mCursor.getColumnIndex(HistoryContract.HistoryEntry.COLUMN_idH));
        Customer = mCursor.getInt(mCursor.getColumnIndex(HistoryContract.HistoryEntry.COLUMN_Customer));
        date = mCursor.getString(mCursor.getColumnIndex(HistoryContract.HistoryEntry.COLUMN_Date));
        Time = mCursor.getString(mCursor.getColumnIndex(HistoryContract.HistoryEntry.COLUMN_Time));
        holder.id_H.setText(Integer.toString(IDH));
        //форматирование даты
        char[] textDate = {date.charAt(6),date.charAt(7),'/',date.charAt(4),date.charAt(5),'/',date.charAt(0),date.charAt(1),date.charAt(2),date.charAt(3)};
        date = String.copyValueOf(textDate);
        holder.dateText.setText(date);
        holder.timeText.setText(Time);
        //дсотаем клмента по id
        ClientsDBHelper DBHelper = new ClientsDBHelper(mContext);
        SQLiteDatabase DBClients = DBHelper.getReadableDatabase();
        Cursor cursor = DBClients.query(ClientsContract.ClientsEntry.TABLE_NAME,new String[]{ClientsContract.ClientsEntry.COLUMN_NAME},"id_C = ?",new String[] {Integer.toString(Customer)},null,null,null);
        cursor.moveToFirst();
        String Client = cursor.getString(cursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_NAME));
        holder.customerText.setText(Client);

        //подсчет суммы
        cursor = DBClients.query(HistoryProductContract.HistoryProductEntry.TABLE_NAME,new String[]{HistoryProductContract.HistoryProductEntry.COLUMN_ID,
                        HistoryProductContract.HistoryProductEntry.COLUMN_PRODUCT,
                        HistoryProductContract.HistoryProductEntry.COLUMN_AMMOUNT},
                "History_id = ?",new String[]{Integer.toString(IDH)},null,null,null);
        Cursor cursorProduct;
        cursor.moveToFirst();
        while (cursor.getPosition() != cursor.getCount())
        {
            Ammount =cursor.getInt(cursor.getColumnIndex(HistoryProductContract.HistoryProductEntry.COLUMN_AMMOUNT));
            id_P = cursor.getInt(cursor.getColumnIndex(HistoryProductContract.HistoryProductEntry.COLUMN_PRODUCT));
            cursorProduct = DBClients.query(ProductContract.ProductEntry.TABLE_NAME,new String[]{ProductContract.ProductEntry.COLUMN_ID,
                            ProductContract.ProductEntry.COLUMN_NAME, ProductContract.ProductEntry.COLUMN_Price},
                    "ID_P = ?",new String[]{Integer.toString(id_P)},null,null,null);
            cursorProduct.moveToFirst();
            Price = cursorProduct.getInt(cursorProduct.getColumnIndex(ProductContract.ProductEntry.COLUMN_Price));
            Sum = Sum + (Price * Ammount);
            cursor.moveToNext();
        }
        holder.SumText.setText(Integer.toString(Sum) + " р.");


    }



    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    //для добавления новых элементов
    public void swapCursor (Cursor newCursor){
        if (mCursor != null){
            mCursor.close();
        }
        mCursor = newCursor;
        if (newCursor != null){
            notifyDataSetChanged();
        }
    }
}
