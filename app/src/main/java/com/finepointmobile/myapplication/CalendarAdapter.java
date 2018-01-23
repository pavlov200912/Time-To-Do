package com.finepointmobile.myapplication;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Роман on 21.01.2018.
 */

class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    public ArrayList<String> mData = null;
    String[] mColors = {"#65dcff","#65bcff","#658eff","#6d65ff","#a065ff","#ff65ce","#ff659e","#ff6565","#ff8965","#ffb565","#ffdf00","#b8e986","#86e99e","#86e9d0","#86dde9"};
    Context context;
    public CalendarAdapter(ArrayList<String> mData, Context context) {
        this.context = context;
        for (int i = 0;i<mData.size();i++) {
            if(mData.get(i).length() == 5){
                mData.set(i,mData.get(i) + ' ');
            }
        }
        this.mData = mData;
    }
    AppDatabase db;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_row,parent,false);
        ViewHolder vh = new ViewHolder(v);
        db = Room.databaseBuilder(context, AppDatabase.class, "production")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Typeface type = ResourcesCompat.getFont(context, R.font.idinaxx);
        holder.textMon.setText(mData.get(position).substring(0,4));
        holder.textView.setText(mData.get(position).substring(mData.get(position).length() - 2,mData.get(position).length()));
        holder.cardView.setCardBackgroundColor(Color.parseColor(mColors[position % mColors.length]));
        TaskAdapter adapter = new TaskAdapter(db.taskDao().getTaskByDate(mData.get(position)) , false);
        //Log.d("ROMAN", "onBindViewHolder: " + position + "date" + mData.get(position) + " db.size()" + db.taskDao().getTaskByDate(mData.get(position)).size());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setAdapter(adapter);
        holder.recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position1) {

                        Log.d("ROMAN", "onItemClick: " + position + " date:" + mData.get(position));
                        if(adapter.tasks.size() > 0) {
                            Intent intent = new Intent(context,TaskActivity.class);
                            String card_id = String.valueOf(adapter.tasks.get(position1).getTaskId());
                            intent.putExtra("edit", false);
                            intent.putExtra("id", card_id);
                            context.startActivity(intent);
                        }
                    }
                })
        );
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView , textMon;
        RecyclerView recyclerView;
        CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.integer);
            textMon = itemView.findViewById(R.id.month);
            recyclerView = itemView.findViewById(R.id.recycler_taskC);
            cardView = itemView.findViewById(R.id.card_viewC);
        }
    }
}
