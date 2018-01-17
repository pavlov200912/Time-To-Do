package com.finepointmobile.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by danielmalone on 10/27/17.
 */

class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    public List<Task> tasks;

    public TaskAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskAdapter.ViewHolder holder, int position) {
        holder.textShort.setText(tasks.get(position).shortText);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textShort;

        public ViewHolder(View itemView) {
            super(itemView);
            textShort = itemView.findViewById(R.id.text_short);
        }
    }
}
