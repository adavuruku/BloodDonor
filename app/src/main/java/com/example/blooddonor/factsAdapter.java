package com.example.blooddonor;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class factsAdapter extends RecyclerView.Adapter<factsAdapter.RecyclerHolder>{
    private LayoutInflater inflater;
    private List<myModels.Facts> contacts;
    private String stat;
    private Context activity;
    private  OnItemClickListener mlistener;
    public factsAdapter(List<myModels.Facts> contacts, Context context, OnItemClickListener listener){
        this.activity = context;
        this.inflater = LayoutInflater.from(context);
        this.mlistener = listener;
        this.contacts = contacts;
    }
    public interface OnItemClickListener{
    }
    public void setOnitemClickListener(OnItemClickListener listener){
        mlistener = listener;
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.customfacts,parent,false);
        RecyclerHolder holder= new RecyclerHolder(view,mlistener);
        return holder;
    }
    int prevpos=0;
    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {

        try {
            int g = holder.getAdapterPosition();
            myModels.Facts contact = contacts.get(g);
            holder.facts.setText(contact.getFacts());

            if (position > prevpos) {
                AnimationUtils.animate(holder, true);
            } else {
                AnimationUtils.animate(holder, false);
            }
            prevpos = position;
        }catch (Exception e){

        }

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    //create the holder class
    class RecyclerHolder extends RecyclerView.ViewHolder{
        TextView facts;

        public RecyclerHolder(View itemView,final OnItemClickListener listener) {
            super(itemView);

            facts =  itemView.findViewById(R.id.facts);

        }
    }

    public void setFilter(ArrayList<myModels.Facts> newList){
        contacts = new ArrayList<>();
        contacts.addAll(newList);
        notifyDataSetChanged();
    }


}

