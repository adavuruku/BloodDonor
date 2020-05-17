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

public class requestAdapter extends RecyclerView.Adapter<requestAdapter.RecyclerHolder>{
    private LayoutInflater inflater;
    private List<myModels.Request> contacts;
    private String stat;
    private Context activity;
    private  OnItemClickListener mlistener;
    public requestAdapter(List<myModels.Request> contacts, Context context, OnItemClickListener listener){
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
        void onCancelClick(View v, int position);
        void onDeleteClick(View v, int position);
    }
    public void setOnitemClickListener(OnItemClickListener listener){
        mlistener = listener;
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.customrequestlist,parent,false);
        RecyclerHolder holder= new RecyclerHolder(view,mlistener);
        return holder;
    }
    int prevpos=0;
    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {

        try {
            int g = holder.getAdapterPosition();
            myModels.Request contact = contacts.get(g);
            String bltype = contact.getBloodtype();

            if(bltype.equals("A+")){
                holder.bloodtype.setImageResource(R.drawable.aplus);
            }else if(bltype.equals("A-")){
                holder.bloodtype.setImageResource(R.drawable.aminus);
            }else if(bltype.equals("B+")){
                holder.bloodtype.setImageResource(R.drawable.bplus);
            }else if(bltype.equals("B-")){
                holder.bloodtype.setImageResource(R.drawable.bminus);
            }else if(bltype.equals("AB+")){
                holder.bloodtype.setImageResource(R.drawable.abp);
            }else if(bltype.equals("AB-")){
                holder.bloodtype.setImageResource(R.drawable.abminus);
            }else if(bltype.equals("O+")){
                holder.bloodtype.setImageResource(R.drawable.oplus);
            }else{
                holder.bloodtype.setImageResource(R.drawable.ominus);
            }

            holder.by.setText("Requested By : "+contact.getRequestAuthor());
            if(contact.getMytype()){
                holder.by.setVisibility(View.GONE);
                holder.call.setVisibility(View.GONE);
                holder.message.setVisibility(View.GONE);
                holder.email.setVisibility(View.GONE);
            }else{
                holder.delete.setVisibility(View.GONE);
                holder.cancel.setVisibility(View.GONE);
            }
            holder.phone.setText(contact.getPhone());
            holder.dateRecord.setText(contact.getUnit() + " Unit(s) / " + contact.getDateReg());


            holder.state.setText(contact.getState() + " State / " + contact.getLocalGovt() + " / " + (contact.getType().equals("0")? "Emergency":"Normal"));
            holder.contactAddress.setText(contact.getAddress());

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

        TextView by, phone, dateRecord, state, contactAddress;
        ImageView bloodtype;

        ImageButton call,message, email, share,cancel,delete;

        public RecyclerHolder(View itemView,final OnItemClickListener listener) {
            super(itemView);

            by =  itemView.findViewById(R.id.by);
            phone =  itemView.findViewById(R.id.phone);
            state = itemView.findViewById(R.id.state);
            bloodtype = itemView.findViewById(R.id.bloodtype);
            contactAddress = itemView.findViewById(R.id.contactAddress);
            dateRecord = itemView.findViewById(R.id.dateRecord);

            call =  itemView.findViewById(R.id.call);
            message = itemView.findViewById(R.id.message);
            email =  itemView.findViewById(R.id.email);
            share = itemView.findViewById(R.id.share);

            delete = itemView.findViewById(R.id.delete);
            cancel = itemView.findViewById(R.id.cancel);

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

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onCancelClick(view, position);
                        }
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(view, position);
                        }
                    }
                }
            });

        }
    }

    public void setFilter(ArrayList<myModels.Request> newList){
        contacts = new ArrayList<>();
        contacts.addAll(newList);
        notifyDataSetChanged();
    }


}

