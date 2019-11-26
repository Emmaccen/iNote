package com.echo.iNote;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class Messages extends Fragment {
    ArrayList<UserContract> userMessageList;
    private ArrayList<ContactListContract> finalContact;
    MessageAdapter messageAdapter;
    RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private Set<String> chatIds;
    private View view;
    ArrayList<ContactListContract> contact;

    public Messages(Set<ContactListContract> contacts) {
contact = new ArrayList<>(contacts);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_messages, container, false);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        chatIds = new LinkedHashSet<>();
        if (user != null) {

            CollectionReference collection = firestore.collection("Users").document(user.getUid()).collection("ChatList");
            final CollectionReference usersCollection = firestore.collection("Users");
            collection.addSnapshotListener(
                    new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (user != null) {
                                ChatList chatList;
                                if (queryDocumentSnapshots != null) {
                                    for (QueryDocumentSnapshot docs : queryDocumentSnapshots) {
                                        chatList = docs.toObject(ChatList.class);
                                        String friend = chatList.getChatId();
                                        chatIds.add(friend);
                                    }
                                    usersCollection.get().addOnCompleteListener(
                                            new OnCompleteListener<QuerySnapshot>() {

                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if (task.getResult() != null) {
                                                            finalContact = new ArrayList<>();
                                                            userMessageList = new ArrayList<>();
                                                            finalContact.clear();
                                                            userMessageList.clear();
                                                            UserContract users;
                                                            for (QueryDocumentSnapshot docs : task.getResult()) {
                                                                users = docs.toObject(UserContract.class);
                                                                for (String id : chatIds) {
                                                                    if (users.getUserId().equals(id)) {
                                                                        userMessageList.add(users);
                                                                        for(ContactListContract finalCont : contact){
                                                                            if(finalCont.getContactPhoneNumber().contains(users.getPhoneNumber())){
                                                                                finalContact.add(finalCont);
                                                                            }
                                                                        }

                                                                    }
                                                                }
                                                            }
                                                            recyclerView = view.findViewById(R.id.message_fragment_recycler_view);
                                                            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                                                            messageAdapter = new MessageAdapter(userMessageList, getContext(),finalContact);
                                                            recyclerView.setAdapter(messageAdapter);
                                                            // adapter here
                                                        }
                                                    }
                                                }
                                            }
                                    );
                                }
                            }
                        }
                    }
            );
        }



     /*   CollectionReference collectionReference = firestore.collection("Chats");
        collectionReference.addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                       chatIds.clear();
                       ChatModel users;
                        assert queryDocumentSnapshots != null;
                        for(QueryDocumentSnapshot docs: queryDocumentSnapshots){
                           users = docs.toObject(ChatModel.class);
                           if(user != null){
                               if(!user.getUid().equals(users.getSender())){
                                        chatIds.add(users.getSender());
                               }else if((!user.getUid().equals(users.getReceiver()))){
                                   chatIds.add(users.getReceiver());
                               }

                           }

                       }
                    }
                }
        );*/

        return view;
    }

}
