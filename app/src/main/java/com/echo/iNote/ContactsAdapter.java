package com.echo.iNote;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.viewHolder>{
ArrayList<UserContract> contactList;
UserContract contacts;
Context context;
int position;

public ContactsAdapter(ArrayList<UserContract> contactList,Context context){
    this.contactList = contactList;
    this.context = context;
}
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_list_layout,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {
       position = holder.getAdapterPosition();
        contacts = contactList.get(position);
        holder.contactName.setText(contacts.getEmail());
        if(contacts.getImage().equals("default")){
            //set the profile image
        }else{
            // just leave the default image
        }
        holder.container.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(view.getContext(),MessageActivity.class)
                                .putExtra("email",contactList.get(holder.getAdapterPosition()).getEmail())
                                .putExtra("receiver",contactList.get(holder.getAdapterPosition()).getUserId()));
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
TextView contactName;
CircleImageView profileImage;
LinearLayoutCompat container;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contact_list_name);
            profileImage = itemView.findViewById(R.id.recycler_view_profile_pic);
            container = itemView.findViewById(R.id.contact_list_Onclick);
        }
    }
}
