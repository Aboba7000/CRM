package com.example.crm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Layout;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.ClientsViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    //создаем слушателя
    private OnClientClickListener onClientClickListener;

    //конструктор слушателя
    interface OnClientClickListener{
        void onClientClick (String id);
    }


    public ClientsAdapter(Context context, Cursor cursor,OnClientClickListener onClientClickListener){
        mContext = context;
        mCursor = cursor;

        this.onClientClickListener = onClientClickListener;

    }


    public class ClientsViewHolder extends RecyclerView.ViewHolder{
        public TextView nameText;
        public TextView phoneText;
        public TextView typeText;
        public TextView dateText;
        public TextView idCText;
        public View layout;

        public ClientsViewHolder(@NonNull View itemView) {
            super(itemView);

            //определяем TextView для записи
            nameText = itemView.findViewById(R.id.textViewName);
            phoneText= itemView.findViewById(R.id.textViewPhone);
            typeText = itemView.findViewById(R.id.textViewType);
            dateText = itemView.findViewById(R.id.textViewDate);
            idCText = itemView.findViewById(R.id.textViewIDC);
            layout = itemView.findViewById(R.id.linearLayoutClientsItem);
            //слушатель для нажатий на шаблон элемента
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = idCText.getText().toString();
                    onClientClickListener.onClientClick(id);
                }
            });
        }
    }

    @NonNull
    @Override
    public ClientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        //указыввем шаблон элемента
        View view = inflater.inflate(R.layout.item_clients,parent,false);
        return new ClientsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientsViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)){
            return;
        }

        String name;
        String phone;
        int type;
        int idC;

        idC= mCursor.getInt(mCursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_IDC));
        name = mCursor.getString(mCursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_NAME));
        phone = mCursor.getString(mCursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_PHONE));
        type = mCursor.getInt(mCursor.getColumnIndex(ClientsContract.ClientsEntry.COLUMN_TYPE));
        holder.nameText.setText(name);
        holder.phoneText.setText(phone);
        if (type == 1)
            holder.typeText.setText("ИП");
        else
            holder.typeText.setText("Юр. лицо");
        holder.idCText.setText(Integer.toString(idC));
        //достаем дату
        ClientsDBHelper DBHelper = new ClientsDBHelper(mContext);
        SQLiteDatabase DBHistory = DBHelper.getReadableDatabase();
        Cursor cursorHistory = DBHistory.query(HistoryContract.HistoryEntry.TABLE_NAME, new String[]{"Date"}, "Customer = ?", new String[]{Integer.toString(idC)}, null, null, "Date DESC");
        //форматирование даты
        cursorHistory.moveToFirst();
        if (cursorHistory.getCount() != 0)
        {
            String date = cursorHistory.getString(cursorHistory.getColumnIndex("Date"));
            char[] textDate = {date.charAt(6),date.charAt(7),'/',date.charAt(4),date.charAt(5),'/',date.charAt(0),date.charAt(1),date.charAt(2),date.charAt(3)};
            date = String.copyValueOf(textDate);
            holder.dateText.setText(date);
        }
        else holder.dateText.setText("");




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
