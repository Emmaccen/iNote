package com.echo.iNote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.viewHolder> {
ArrayList<ChatModel> messageList;
Context context;
final static int MY_MESSAGE = 0;
final static int THEIR_MESSAGE = 1;
FirebaseUser firebaseUser;

public ChatAdapter(ArrayList<ChatModel> messageList,Context context){
    this.messageList = messageList;
    this.context = context;
}
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
if(viewType == MY_MESSAGE){
    View view = LayoutInflater.from(context).inflate(R.layout.sender_layout,parent,false);
    return new viewHolder(view);
}else{
    View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout,parent,false);
 return new viewHolder(view);
}
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
ChatModel chat = messageList.get(position);
if(chat.getSender().equals(firebaseUser.getUid())){
    holder.myMessage.setText(chat.getMessage());
}else{
    holder.theirMessage.setText(chat.getMessage());
}
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView myMessage;
        TextView theirMessage;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            myMessage = itemView.findViewById(R.id.my_messages);
            theirMessage = itemView.findViewById(R.id.their_messages);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(messageList.get(position).getSender().equals(firebaseUser.getUid())){
            return MY_MESSAGE;
        }else{
            return THEIR_MESSAGE;
        }
    }
}
