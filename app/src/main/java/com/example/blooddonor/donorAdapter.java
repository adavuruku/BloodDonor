package com.example.blooddonor;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class donorAdapter extends RecyclerView.Adapter<donorAdapter.RecyclerHolder>{
    private LayoutInflater inflater;
    private List<myModels.Donors> contacts;
    private String stat;
    private Context activity;
    private  OnItemClickListener mlistener;
    public donorAdapter(List<myModels.Donors> contacts, Context context, OnItemClickListener listener){
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
        View view = inflater.inflate(R.layout.customdonorslist,parent,false);
        RecyclerHolder holder= new RecyclerHolder(view,mlistener);
        return holder;
    }
    int prevpos=0;
    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {

        try {
            int g = holder.getAdapterPosition();
            myModels.Donors contact = contacts.get(g);
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
            holder.user.setText(contact.getFullname());
            holder.useremail.setText(contact.getEmail() + " / " + contact.getPhone());

            holder.state.setText(contact.getGender() + " / " +contact.getState() + " State / " + contact.getLocalGovt());
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
        ImageView bloodtype;

        ImageButton call,message, email, share;

        public RecyclerHolder(View itemView,final OnItemClickListener listener) {
            super(itemView);

            user =  itemView.findViewById(R.id.user);
            useremail =  itemView.findViewById(R.id.useremail);
            state = itemView.findViewById(R.id.state);
            bloodtype = itemView.findViewById(R.id.bloodtype);
            contactAddress = itemView.findViewById(R.id.contactAddress);

            call =  itemView.findViewById(R.id.call);
            message = itemView.findViewById(R.id.message);
            email =  itemView.findViewById(R.id.email);
            share = itemView.findViewById(R.id.share);

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

    public void setFilter(ArrayList<myModels.Donors> newList){
        contacts = new ArrayList<>();
        contacts.addAll(newList);
        notifyDataSetChanged();
    }


}

