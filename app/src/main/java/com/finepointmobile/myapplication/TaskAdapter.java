package com.finepointmobile.myapplication;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by danielmalone on 10/27/17.
 */

class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    public List<Task> tasks;
    boolean isDate;
    public TaskAdapter(List<Task> tasks, boolean isDate) {
        this.tasks = tasks;
        this.isDate = isDate;
    }
    public TaskAdapter(List<Task> tasks) {
        this.tasks = tasks;
        this.isDate = true;
    }
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskAdapter.ViewHolder holder, int position) {
        holder.textShort.setText(tasks.get(position).shortText);
        String date = tasks.get(position).dateText;
        if(date != null && date.length() > 5) {
            if(!date.substring(date.length() - 5, date.length()).equals("15 Jan"))
                holder.textTime.setText(date.substring(date.length() - 5, date.length()));
            if(isDate) {
                holder.textDate.setText(date.substring(0, date.length() - 5));
            }
            else{
                holder.textTime.setTextSize(25);
                holder.textTime.setGravity(Gravity.TOP);
            }
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textShort;
        public TextView textTime;
        public TextView textDate;
        public ViewHolder(View itemView) {
            super(itemView);
            textShort = itemView.findViewById(R.id.textShort);
            textTime = itemView.findViewById(R.id.textTime);
            textDate = itemView.findViewById(R.id.textDate);
        }
    }
}
