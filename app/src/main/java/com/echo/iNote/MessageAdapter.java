package com.echo.iNote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.viewHolder> {
    Context context;
    private ArrayList<UserContract> userMessageList;
    private UserContract users;
    private Intent intent;

    MessageAdapter(ArrayList<UserContract> userMessageList, Context context) {
        this.userMessageList = userMessageList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_list_layout, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {
        users = userMessageList.get(holder.getAdapterPosition());
        holder.contactName.setText(users.getPhoneNumber());
        if (users.getImage().equals("default")) {
            //set the default profile image
            holder.profileImage.setImageDrawable(context.getDrawable(R.drawable.user_profile_picture));
        } else {
            Glide.with(context)
                    .load(users.getImage())
                    .into(holder.profileImage);
        }
        holder.container.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intent = ((Activity) context).getIntent();
                        if (intent != null) {
                            String type = intent.getStringExtra("messageType");
                            Notes note = intent.getParcelableExtra("message");
                            context.startActivity(new Intent(view.getContext(), MessageActivity.class)
                                    .putExtra("contactName", userMessageList.get(holder.getAdapterPosition()).getPhoneNumber())
                                    .putExtra("receiver", userMessageList.get(holder.getAdapterPosition()).getUserId())
                                    .putExtra("message", note).putExtra("type", type));

                        } else {
                            context.startActivity(new Intent(view.getContext(), MessageActivity.class)
                                    .putExtra("contactName", userMessageList.get(holder.getAdapterPosition()).getPhoneNumber())
                                    .putExtra("receiver", userMessageList.get(holder.getAdapterPosition()).getUserId()));
                        }
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
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
