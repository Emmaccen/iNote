package com.echo.iNote;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    private ArrayList<ChatModel> messageList;
    private String theirId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        chatName.setText((intent.getStringExtra("email")));
        sendButton = findViewById(R.id.chat_send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
sendMessage(user.getUid(), theirId,textField);
            }
        });
        updateMessages();
    }

public void sendMessage(String myId, String theirId, EditText message){
    String text = message.getText().toString().trim();
    if(text.isEmpty()){
        Toast.makeText(this, "Empty Text !", Toast.LENGTH_SHORT).show();
    }else{

        Date date= new Date();
        long time = date.getTime();
        Timestamp timestamp = new Timestamp(time);

        Map<String,String> chatData = new HashMap<>();
        chatData.put("time",timestamp.toString());
        chatData.put("sender",myId);
        chatData.put("receiver",theirId);
        chatData.put("message",text);
        firestore.collection("Chats").document(timestamp.toString()).set(chatData);
        message.setText("");
        //date = new SimpleDateFormat("dd:MM:yy", Locale.getDefault()).format(new Date());
        // or date.toString ?
    }
}
public void updateMessages(){
        CollectionReference collection = FirebaseFirestore.getInstance().collection("Chats");
             collection.addSnapshotListener(
                     new EventListener<QuerySnapshot>() {
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
                            ChatAdapter chatAdapter = new ChatAdapter(messageList,getApplicationContext());
                                 recyclerView.setAdapter(chatAdapter);
                            }
                         }
                     }
             );
}


}
