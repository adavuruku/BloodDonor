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

public class hospitalAdapter extends RecyclerView.Adapter<hospitalAdapter.RecyclerHolder>{
    private LayoutInflater inflater;
    private List<myModels.Hospitals> contacts;
    private String stat;
    private Context activity;
    private  OnItemClickListener mlistener;
    public hospitalAdapter(List<myModels.Hospitals> contacts, Context context, OnItemClickListener listener){
        this.activity = context;
        this.inflater = LayoutInflater.from(context);
        this.mlistener = listener;
        this.contacts = contacts;
    }
    public interface OnItemClickListener{
        void onCallClick(View v, int position);
        void onMessageClick(View v, int position);
        void onEmailClick(View v, int position);
        void onShareClick(View v, int position);
    }
    public void setOnitemClickListener(OnItemClickListener listener){
        mlistener = listener;
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.customhospitallist,parent,false);
        RecyclerHolder holder= new RecyclerHolder(view,mlistener);
        return holder;
    }
    int prevpos=0;
    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {

        try {
            int g = holder.getAdapterPosition();
            myModels.Hospitals contact = contacts.get(g);

            if(contact.getAp() <= 0){
                holder.ap.setVisibility(View.GONE);
            }

            if(contact.getAn() <= 0){
                holder.an.setVisibility(View.GONE);
            }
            if(contact.getBp() <= 0){
                holder.bp.setVisibility(View.GONE);
            }
            if(contact.getBp() <= 0){
                holder.bn.setVisibility(View.GONE);
            }

            if(contact.getAbn() <= 0){
                holder.abn.setVisibility(View.GONE);
            }
            if(contact.getAbp() <= 0){
                holder.abp.setVisibility(View.GONE);
            }
            if(contact.getOn() <= 0){
                holder.on.setVisibility(View.GONE);
            }
            if(contact.getOp() <= 0){
                holder.op.setVisibility(View.GONE);
            }
            holder.user.setText(contact.getFullname());
            holder.useremail.setText(contact.getEmail() + " / " + contact.getPhone());

            holder.state.setText(contact.getType() + " / " +contact.getState() + " State / " + contact.getLocalGovt());
            holder.contactAddress.setText( contact.getAddress());

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
        //the view items send here is from custom_row and is received here as itemView
        TextView useremail, state,user,contactAddress;
        ImageView ap, an, bp, bn, abp, abn, op, on;

        ImageButton call,message, email, share;

        public RecyclerHolder(View itemView,final OnItemClickListener listener) {
            super(itemView);

            user =  itemView.findViewById(R.id.user);
            useremail =  itemView.findViewById(R.id.useremail);
            state = itemView.findViewById(R.id.state);
            contactAddress = itemView.findViewById(R.id.contactAddress);

            call =  itemView.findViewById(R.id.call);
            message = itemView.findViewById(R.id.message);
            email =  itemView.findViewById(R.id.email);
            share = itemView.findViewById(R.id.share);

            ap = itemView.findViewById(R.id.ap);
            an = itemView.findViewById(R.id.an);
            bp = itemView.findViewById(R.id.bp);
            bn = itemView.findViewById(R.id.bn);
            abp = itemView.findViewById(R.id.abp);
            abn = itemView.findViewById(R.id.abn);
            op = itemView.findViewById(R.id.op);
            on = itemView.findViewById(R.id.on);

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onCallClick(view, position);
                        }
                    }
                }
            });

            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onMessageClick(view, position);
                        }
                    }
                }
            });

            email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onEmailClick(view, position);
                        }
                    }
                }
            });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onShareClick(view, position);
                        }
                    }
                }
            });

        }
    }

    public void setFilter(ArrayList<myModels.Hospitals> newList){
        contacts = new ArrayList<>();
        contacts.addAll(newList);
        notifyDataSetChanged();
    }


}

