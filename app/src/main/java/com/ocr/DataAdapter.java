package com.ocr;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    /**
     * класс для связки данных и RecycleView
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView number;
        TextView date;
        TextView name;
        TextView type;
        TextView type2;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        ViewHolder(final View view){
            super(view);
            type2 = view.findViewById(R.id.type2);
            date = view.findViewById(R.id.date);
            number = view.findViewById(R.id.number);
            name = view.findViewById(R.id.name);
            type = view.findViewById(R.id.type);
        }
    }

    private LayoutInflater inflater;
    private List<DataOfCards> dishes;

    DataAdapter(Context context, List<DataOfCards> dishes) {
        this.dishes = dishes;
        this.inflater = LayoutInflater.from(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DataAdapter.ViewHolder viewHolder, final int i) {
        final DataOfCards card = dishes.get(i); // 1 экземпляр блюда со своими полями

        viewHolder.name.setText(card.getName());
        viewHolder.type.setText(card.getType());
        viewHolder.type2.setText(card.getType2());
        viewHolder.number.setText(card.getNumber());
        viewHolder.date.setText(card.getDate());
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }
}
