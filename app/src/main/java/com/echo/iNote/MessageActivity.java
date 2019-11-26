package com.echo.iNote;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
RecyclerView recyclerView;
ChatAdapter chatAdapter;
EditText textField;
ImageButton sendButton;
FirebaseFirestore firestore;
FirebaseAuth firebaseAuth;
FirebaseUser user;
String myId;
TextView chatName;
    CircleImageView profileImage;
    private ArrayList<ChatModel> messageList;
    private String theirId;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = getIntent();
        if (intent != null) {
            String clearTask = intent.getStringExtra("type");
            if (clearTask != null) {
                startActivity(new Intent(this, HomePage.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        }

    }

 /*   @Override
    public boolean onContextItemSelected(MenuItem item) {
        ChatModel chatModel;
        int id = item.getItemId();
        int selectedPosition = item.getGroupId();
        if(id == 1){
            chatModel = new ChatModel();
         String chat =  messageList.get(selectedPosition).getMessage();
            Toast.makeText(this, "message is : " + chat, Toast.LENGTH_SHORT).show();
        }
        return true;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        MenuItem clearMessage = menu.findItem(R.id.chat_clear_messages);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.chat_clear_messages) {
            CollectionReference collectionReference = firestore.collection("Chats");
            /*collectionReference.addSnapshotListener(
                    new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            ChatModel chats ;
                            if(queryDocumentSnapshots != null){
                                for(QueryDocumentSnapshot docs : queryDocumentSnapshots){
                                    chats = docs.toObject(ChatModel.class);
                                    if(chats.getIsDeleted() != null){
                                        if(chats.getIsDeleted().equals("false")){
                                            docs.getReference().update("isDeleted",user.getUid());
                                            Toast.makeText(MessageActivity.this, "Chat Cleared", Toast.LENGTH_SHORT).show();
                                            firestore.collection("Users").document(user.getUid()).
                                                    collection("ChatList").document(theirId).delete();
                                        }else if(chats.getIsDeleted().equals(theirId)){
                                            docs.getReference().delete();
                                            firestore.collection("Users").document(user.getUid()).
                                                    collection("ChatList").document(theirId).delete();
                                            Toast.makeText(MessageActivity.this, "Chat Cleared", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                            }
                        }
                    }
            );*/
            collectionReference.get()
                    .addOnCompleteListener(
                            new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        ChatModel chats;
                                        for (QueryDocumentSnapshot docs : task.getResult()) {
                                            chats = docs.toObject(ChatModel.class);
                                            if (chats.getIsDeleted() != null) {
                                                if (chats.getIsDeleted().equals("false")) {
                                                    docs.getReference().update("isDeleted", user.getUid());
                                                    Toast.makeText(MessageActivity.this, "Chat Cleared", Toast.LENGTH_SHORT).show();
                                                    firestore.collection("Users").document(user.getUid()).
                                                            collection("ChatList").document(theirId).delete();
                                                } else if (chats.getIsDeleted().equals(theirId)) {
                                                    docs.getReference().delete();
                                                    firestore.collection("Users").document(user.getUid()).
                                                            collection("ChatList").document(theirId).delete();
                                                }
                                            }
                                        }
                                        Toast.makeText(MessageActivity.this, "Chat Cleared", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    ).addOnFailureListener(
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MessageActivity.this, "Error : " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        messageList = new ArrayList<>();
        Intent intent = getIntent();
        theirId = intent.getStringExtra("receiver");
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        myId = firebaseAuth.getUid();
        recyclerView = findViewById(R.id.messages_recycler_view);
        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setStackFromEnd(true);
        recyclerView.setLayoutManager(layout);
        textField = findViewById(R.id.chat_text);
        chatName = findViewById(R.id.chat_name);
        chatName.setText((intent.getStringExtra("contactName")));
        sendButton = findViewById(R.id.chat_send_button);
        profileImage = findViewById(R.id.chat_screen_profile_picture);
        loadProfileImage();

        if (intent != null) {
            String name = intent.getStringExtra("type");
            if (name != null) {
                Notes note = intent.getParcelableExtra("message");
                Gson json = new Gson();
                String notes = json.toJson(note);
                Date date = new Date();
                long time = date.getTime();
                Timestamp timestamp = new Timestamp(time);
                String currentTime = new SimpleDateFormat("HH:mm a", Locale.getDefault()).format(new Date());
                Map<String, String> chatData = new HashMap<>();
                chatData.put("time", timestamp.toString());
                chatData.put("sender", myId);
                chatData.put("receiver", theirId);
                chatData.put("message", notes);
                chatData.put("isNote", "true");
                chatData.put("phoneTime", currentTime);
                chatData.put("isSeen", "false");
                chatData.put("isDeleted", "false");
                chatData.put("isNoteSaved", "false");

                firestore.collection("Chats").document(timestamp.toString()).set(chatData);
                Map<String, String> chatList = new HashMap<>();
                Map<String, String> chatList2 = new HashMap<>();
                chatList.put("chatId", theirId);
                chatList2.put("chatId", user.getUid());

                firestore.collection("Users").document(user.getUid()).collection("ChatList").document(theirId).set(chatList);

                firestore.collection("Users").document(theirId).collection("ChatList").document(user.getUid()).set(chatList2);

            }
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
sendMessage(user.getUid(), theirId,textField);
            }
        });
        updateMessages();
    }

    private void loadProfileImage() {
        DocumentReference document = firestore.collection("Users").document(theirId);
        document.get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        UserContract users = task.getResult().toObject(UserContract.class);
                        if (task.isSuccessful()) {
                            if (users.getImage().equals("default")) {
                                profileImage.setImageDrawable(getDrawable(R.drawable.user_profile_picture));
                            } else {
                                Glide.with(getApplicationContext())
                                        .load(users.getImage())
                                        .into(profileImage);
                            }
                        }
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MessageActivity.this, "Error : " + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendMessage(String myId, String theirId, EditText message) {
    String text = message.getText().toString().trim();
    if(text.isEmpty()){
    }else{
        Date date= new Date();
        long time = date.getTime();
        Timestamp timestamp = new Timestamp(time);
        String currentTime = new SimpleDateFormat("HH:mm a", Locale.getDefault()).format(new Date());

        Map<String,String> chatData = new HashMap<>();
        chatData.put("time",timestamp.toString());
        chatData.put("sender",myId);
        chatData.put("receiver",theirId);
        chatData.put("message",text);
        chatData.put("isNote", "false");
        chatData.put("phoneTime", currentTime);
        chatData.put("isSeen", "false");
        chatData.put("isDeleted", "false");
        firestore.collection("Chats").document(timestamp.toString()).set(chatData);

        Map<String, String> chatList = new HashMap<>();
        Map<String, String> chatList2 = new HashMap<>();
        chatList.put("chatId", theirId);
        chatList2.put("chatId", user.getUid());
        firestore.collection("Users").document(user.getUid()).collection("ChatList").document(theirId).set(chatList);
        firestore.collection("Users").document(theirId).collection("ChatList").document(user.getUid()).set(chatList2);
        message.setText("");
        //date = new SimpleDateFormat("dd:MM:yy", Locale.getDefault()).format(new Date());
        // or date.toString ?
    }
}

public void updateMessages(){
        CollectionReference collection = FirebaseFirestore.getInstance().collection("Chats");
             collection.addSnapshotListener(
                     new EventListener<QuerySnapshot>() {

                         private ChatAdapter chatAdapter;

                         @Override
                         public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                             messageList.clear();
                             ChatModel chats;
                             for(QueryDocumentSnapshot docs : queryDocumentSnapshots){
                                 chats = docs.toObject(ChatModel.class);
                                     if(chats.getSender().equals(myId) && chats.getReceiver().equals(theirId)
                                             ||chats.getReceiver().equals(myId) && chats.getSender().equals(theirId)){
                                         messageList.add(chats);
                                     }
                                 if (chats.getReceiver().equals(user.getUid()) && chats.getSender().equals(theirId)) {
                                     docs.getReference().update("isSeen", "true");
                                 }
                                 chatAdapter = new ChatAdapter(messageList, getApplicationContext(), chatName.getText().toString());
                                 recyclerView.setAdapter(chatAdapter);
                            }
                         }
                     }
             );
}

}
