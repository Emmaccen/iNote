package com.echo.iNote;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactList extends Fragment {
    View view;
    private Cursor cursor;
    private String phoneNum;
RecyclerView contactsRecyclerView;
ContactsAdapter contactsAdapter;
private ProgressDialog loadProgress;
FirebaseFirestore fireStore;
FirebaseUser user;
FirebaseAuth firebaseAuth;
ArrayList<UserContract> resultList;
    private Set<ContactListContract> rawContacts;

    public ContactList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_contact_list, container, false);


        fireStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        resultList = new ArrayList<>();

        CollectionReference collection = FirebaseFirestore.getInstance().collection("Users");
        collection.get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot documents : task.getResult()){
                            UserContract users = documents.toObject(UserContract.class);
                            if(user != null && !user.getUid().equals(users.getUserId()) ){
                                resultList.add(users);
                            }
                            contactsRecyclerView = view.findViewById(R.id.contacts_recyc_view);
                            contactsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                            contactsAdapter = new ContactsAdapter(resultList,view.getContext());
                            contactsRecyclerView.setAdapter(contactsAdapter);
                        }

                    }
                }
        );
        return view;
    }

    private Set<ContactListContract> getAllContacts(){
        rawContacts = new HashSet<>();
        ContentResolver contentResolver = view.getContext().getContentResolver();
        // cursor will be initialized in background
        cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null,null,null,ContactsContract.Contacts.DISPLAY_NAME + " ASC");
        if((cursor != null ? cursor.getCount() : 0) > 0){
            //above(if cursor is !null return getCount else return 0
            // then check if any of either is greater then zero
            while (cursor != null && cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndex
                        (ContactsContract.Contacts._ID));
                String names = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                ));
//                rawContacts.add(names);
                if(cursor.getInt(cursor.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER
                ))> 0){
                    Cursor phoneNumberCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
                    );//ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
                    assert phoneNumberCursor != null;
                    while (phoneNumberCursor.moveToNext()){
                        phoneNum = phoneNumberCursor.getString(phoneNumberCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                        ));
                    }
                    phoneNumberCursor.close();
                }
//                progressBar.setProgress(cursor.getCount() * (100 / cursor.getCount()));
                rawContacts.add(new ContactListContract(names,phoneNum));

            }
            if(cursor != null){
                cursor.close();
            }
        }

    return rawContacts;
    }

    public class sortContacts implements Comparator<ContactListContract>{

        @Override
        public int compare(ContactListContract contactListContract, ContactListContract t1) {
            return contactListContract.getContactName().compareTo(t1.getContactName());
        }
    }

}
