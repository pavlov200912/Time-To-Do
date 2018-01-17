package com.finepointmobile.myapplication;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Роман on 16.01.2018.
 */

class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.ViewHolder> {
    public List<Check> checks;

    public CheckAdapter(List<Check> checks) {
        this.checks = checks;
    }

    @Override
    public CheckAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_row, parent, false);
        return new ViewHolder(view);
    }
    public void addData(Check data){
        checks.add(data);
    }
    @Override
    public void onBindViewHolder(CheckAdapter.ViewHolder holder, final int position) {
        holder.checkBox.setText(checks.get(position).checkText);
        holder.checkBox.setChecked(checks.get(position).isComplete == 1);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("Checkbox", "clicked");
                if(checks.size() > position)
                    checks.get(position).setIsComplete(checks.get(position).getIsComplete() ^ 1);
            }
        });
    }
    public void removeItem(int position) {
        checks.remove(position);

        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return checks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            this.setIsRecyclable(false);
            checkBox = itemView.findViewById(R.id.rowName);
        }
    }
}

