package com.echo.iNote;


import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
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
    NewMessagesAdapter newMessagesAdapter;
    RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private Set<String> chatIds;
    private View view;
    ArrayList<ContactListContract> contact;

    public Messages(Set<ContactListContract> contacts) {
        contact = new ArrayList<>(contacts);
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_messages, container, false);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        chatIds = new LinkedHashSet<>();
        if (user != null) {

            CollectionReference collection = firestore.collection("Chats");
            final CollectionReference usersCollection = firestore.collection("Users");
            collection.addSnapshotListener(
                    new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (user != null) {
                                ChatModel chats;
                                if (queryDocumentSnapshots != null) {
                                    for (QueryDocumentSnapshot docs : queryDocumentSnapshots) {
                                        chats = docs.toObject(ChatModel.class);
                                        if(chats.getReceiver().equals(user.getUid()) && chats.getIsSeen().equals("false")){
                                            String chat = chats.getSender();
                                            chatIds.add(chat);
                                        }
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
                                                                            if(PhoneNumberUtils.compare(finalCont.getContactPhoneNumber(),users.getPhoneNumber())){
                                                                                finalContact.add(finalCont);
                                                                            }
                                                                        }

                                                                    }
                                                                }
                                                            }
                                                            recyclerView = view.findViewById(R.id.messages_fragment_recycler_view);
                                                            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                                                            newMessagesAdapter = new NewMessagesAdapter(userMessageList, getContext(),finalContact);
                                                            recyclerView.setAdapter(newMessagesAdapter);
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


        return view;
    }

}
