package com.saicharanreddy.group15_hw05;
/*
               *Assignment : Home Work 5
               * File Name : Group15_HW05
               * Full Name : Manideep Reddy Nukala, Sai Charan Reddy Vallapureddy
               *
 */
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by valla on 29-03-2019.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    ArrayList<Expense> mData;
    OnItemClickListener mlistener;
    public interface OnItemClickListener{
        void onEditClick(int position);
        void onItemClick(int position);
    }
    public MyAdapter(){

    }
    public MyAdapter(ArrayList<Expense> mData) {
        this.mData = mData;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expenselayout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view,mlistener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Expense email = mData.get(position);
        holder.textExpenseName.setText(email.getName());
        holder.textCost.setText(email.getCost());
        holder.textDate.setText(email.getDate());
    }

    @Override
    public int getItemViewType(int position) {

        Log.d("hell",mData.get(position).toString());
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mlistener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textExpenseName;
        TextView textCost;
        TextView textDate;
        ImageButton editButton;
        public MyViewHolder(View itemView, final OnItemClickListener listener) {

            super(itemView);
            textExpenseName = (TextView) itemView.findViewById(R.id.expenseName);
            textCost = (TextView) itemView.findViewById(R.id.costInputHeading);
            textDate = (TextView) itemView.findViewById(R.id.date);
            editButton = (ImageButton) itemView.findViewById(R.id.editButton);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION)
                    {
                        listener.onEditClick(position);
                    }

                }
            });
        }
    }

}

