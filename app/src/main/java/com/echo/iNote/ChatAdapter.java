package com.echo.iNote;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.viewHolder> {
    private final static int MY_MESSAGE = 0;
Context context;
    private final static int THEIR_MESSAGE = 1;
    private final static int NOTE_MESSAGE = 2;
    FirebaseFirestore firestore;
    private ArrayList<ChatModel> messageList;
    private FirebaseUser firebaseUser;
    private int adapterPosition;
    private String name;

    ChatAdapter(ArrayList<ChatModel> messageList, Context context, String name) {
    this.messageList = messageList;
    this.context = context;
        firestore = FirebaseFirestore.getInstance();
        this.name = name;


}
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
if(viewType == MY_MESSAGE){
    View view = LayoutInflater.from(context).inflate(R.layout.sender_layout,parent,false);
    return new viewHolder(view);
}
        if (viewType == NOTE_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.notes_layout, parent, false);
            return new viewHolder(view);
        } else if (viewType == THEIR_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout, parent, false);
            return new viewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout, parent, false);
            view.setVisibility(View.GONE);
            return new viewHolder(view);
            /*this line above is a scam, i have no idea how to return an empty view when all messages have been deleted
             * like for specific messages , so i returned the wrong view and set it to Gone*/
        }
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        adapterPosition = holder.getAdapterPosition();
ChatModel chat = messageList.get(position);
        if (holder.getItemViewType() == -1) {

        } else {
            if (chat.getSender().equals(firebaseUser.getUid()) && chat.getIsNote().equals("false")) {
                //Am sending chat
                holder.myMessage.setText(chat.getMessage());
                holder.senderTime.setText(chat.getPhoneTime());
                if (chat.getIsSeen().equals("true") && adapterPosition == messageList.size() - 1) {
                    holder.seenText.setText("Seen");
                } else if ((chat.getIsSeen().equals("false")) && adapterPosition == messageList.size() - 1) {
                    holder.seenText.setText("Sent");
                }
            } else if (chat.getSender().equals(firebaseUser.getUid()) && chat.getIsNote().equals("true")) {
                //Am sending Note
                setNoteValues(holder, chat, "sender");

            } else if (!chat.getSender().equals(firebaseUser.getUid()) && chat.getIsNote().equals("true")) {
                //Am receiving note
                setNoteValues(holder, chat, "receiver");
                if (chat.getIsNoteSaved().equals("false")) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Notes>() {
                    }.getType();
                    String noteMessage = chat.getMessage();
                    Notes note = gson.fromJson(noteMessage, type);
                    note.setTitle(note.getTitle() + " (New note from : " + name + " )");
                    HomePage.noteList.add(0, note);
                    saveNotesToMemory();
                    Toast.makeText(context, "Note message saved", Toast.LENGTH_SHORT).show();
                    registerNoteAsSaved();
                }
            } else {
                holder.theirMessage.setText(chat.getMessage());
                holder.receiverTime.setText(chat.getPhoneTime());
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatModel chats = messageList.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String isNote = messageList.get(position).getIsNote();
        if (chats.getIsDeleted().equals(firebaseUser.getUid())) {
            return -1;
        } else {
            if (messageList.get(position).getSender().equals(firebaseUser.getUid()) && isNote.equals("false")) {
                return MY_MESSAGE;
            } else if (messageList.get(position).getSender().equals(firebaseUser.getUid()) && isNote.equals("true")) {
                return NOTE_MESSAGE;
            } else if (!messageList.get(position).getSender().equals(firebaseUser.getUid()) && isNote.equals("true")) {
                return NOTE_MESSAGE;
            } else {
                return THEIR_MESSAGE;
            }
        }

    }

    public void setNoteValues(viewHolder holder, ChatModel chat, String messageType) {
        Gson gson = new Gson();
        Type type = new TypeToken<Notes>() {
        }.getType();
        Notes note = gson.fromJson(chat.getMessage(), type);
        holder.title.setText(note.getTitle());
        holder.textBody.setText(note.getTextBody());
        holder.date.setText(note.getDate());
        holder.category.setText(context.getResources().getString(R.string.category_uncategorized));
        holder.circleImageView.setVisibility(View.GONE);
        if (messageType.equals("receiver")) {
            holder.cardView.setBackground(context.getDrawable(R.drawable.receiver_note_drawable));
        } else {
            holder.cardView.setBackground(context.getDrawable(R.drawable.sender_note_drawable));
        }
    }

    /* public class ChatContext implements OnCreateContextMenuListener{

         @Override
         public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
             contextMenu.setHeaderTitle(context.getString(R.string.select_an_option));
             *//*context menu params (groupID, itemID, menuPosition/Arrangement, StringResource*//*

            contextMenu.add(adapterPosition,1,1,"Details");
            contextMenu.add(adapterPosition,2,2,context.getString(R.string.contextDelete));
        }
    }*/
   /* public class NoteContext implements OnCreateContextMenuListener{
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle(context.getString(R.string.select_an_option));
            *//*context menu params (groupID, itemID, menuPosition/Arrangement, StringResource*//*

            contextMenu.add(adapterPosition,0,0,"Open");
            contextMenu.add(adapterPosition,1,1,"Details");
            contextMenu.add(adapterPosition,2,2,context.getString(R.string.contextDelete));
        }
    }*/
    public void saveNotesToMemory() {
        HomePage.sharedPreferences = context.getSharedPreferences(HomePage.MY_PREFERENCE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = HomePage.sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonString = gson.toJson(HomePage.noteList);
        editor.putString(HomePage.ARRAY_OF_NOTES, jsonString);
        editor.apply();
    }

    public void registerNoteAsSaved() {
        CollectionReference collectionReference = firestore.collection("Chats");
        collectionReference.get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ChatModel chats;
                            for (QueryDocumentSnapshot docs : task.getResult()) {
                                chats = docs.toObject(ChatModel.class);

                                if (chats.getIsNoteSaved() != null) {
                                    if (chats.getIsNoteSaved().equals("false")) {
                                        docs.getReference().update("isNoteSaved", "true");
                                    }
                                }
                            }
                        }
                    }
                }
        );

    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView myMessage;
        TextView theirMessage;
        TextView title, textBody, date, category, senderTime, receiverTime, seenText;
        CardView cardView;
        CircleImageView circleImageView;
        FrameLayout frameLayout;
        LinearLayout senderParent, receiverParent;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            myMessage = itemView.findViewById(R.id.my_messages);
            theirMessage = itemView.findViewById(R.id.their_messages);
            title = itemView.findViewById(R.id.recy_title_text);
            senderTime = itemView.findViewById(R.id.sender_time);
            receiverTime = itemView.findViewById(R.id.receiver_time);
            textBody = itemView.findViewById(R.id.recy_note_body);
            date = itemView.findViewById(R.id.recy_date);
            seenText = itemView.findViewById(R.id.seen_text);
            category = itemView.findViewById(R.id.recy_category_text);
            circleImageView = itemView.findViewById(R.id.circleImageView);
            cardView = itemView.findViewById(R.id.recy_cardview);
            frameLayout = itemView.findViewById(R.id.recy_frame_layout);
            senderParent = itemView.findViewById(R.id.sender_parent_layout);
            receiverParent = itemView.findViewById(R.id.receiver_parent_layout);


        }
    }
}

