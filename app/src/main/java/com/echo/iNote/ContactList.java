package com.echo.iNote;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactList extends Fragment {
    private static final String CONTACT_PREF_KEY = "contact_pref_key";
    View view;
    private Cursor cursor;
RecyclerView contactsRecyclerView;
ContactsAdapter contactsAdapter;
FirebaseFirestore fireStore;
FirebaseUser user;
FirebaseAuth firebaseAuth;
ArrayList<UserContract> resultList;
    private Set<ContactListContract> rawContacts;
    private Intent intent;
    private String phoneNumber;

    public ContactList(Set<ContactListContract> contactList) {
        // Required empty public constructor
        this.rawContacts = contactList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        fireStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        resultList = new ArrayList<>();
        LoadContactsInBackground load = new LoadContactsInBackground();
        load.execute();
        return view;
    }

    /*private void getAllContacts() {
        if (user == null) {
            Toast.makeText(view.getContext(), getString(R.string.no_user_account_detected), Toast.LENGTH_SHORT).show();
        } else {
            ContentResolver contentResolver = view.getContext().getContentResolver();
            // cursor will be initialized in background
            cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
            if ((cursor != null ? cursor.getCount() : 0) > 0) {
                //above(if cursor is !null return getCount else return 0
                // then check if any of either is greater then zero
                while (cursor != null && cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex
                            (ContactsContract.Contacts._ID));
                    String names = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME
                    ));
//                rawContacts.add(names);
                    if (cursor.getInt(cursor.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER
                    )) > 0) {
                        Cursor phoneNumberCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
                        );//ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
                        assert phoneNumberCursor != null;
                        while (phoneNumberCursor.moveToNext()) {
                            String phoneNum = phoneNumberCursor.getString(phoneNumberCursor.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER
                            ));
                            phoneNumber = phoneNum.replace(" ", "");
                        }
                        phoneNumberCursor.close();
                    }
//                progressBar.setProgress(cursor.getCount() * (100 / cursor.getCount()));
                    rawContacts.add(new ContactListContract(names, phoneNumber));

                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

    }*/

    public void saveContactsToMemory() {
        SharedPreferences contacts = view.getContext().getSharedPreferences(CONTACT_PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = contacts.edit();
        Gson gson = new Gson();
        String contactsGson = gson.toJson(rawContacts);
        editor.putString("Contacts", contactsGson);
        editor.apply();
    }
    public class sortContacts implements Comparator<ContactListContract>{

        @Override
        public int compare(ContactListContract contactListContract, ContactListContract t1) {
            return contactListContract.getContactName().compareTo(t1.getContactName());
        }
    }

    public void LoadContactsFromMemory() {
        SharedPreferences contacts = view.getContext().getSharedPreferences(CONTACT_PREF_KEY, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ContactListContract>>() {
        }.getType();
        String result = contacts.getString("Contacts", null);
        rawContacts = gson.fromJson(result, type);
        if (rawContacts == null) {
            rawContacts = new LinkedHashSet<>();
        }
    }

    public class LoadContactsInBackground extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            CollectionReference collection = FirebaseFirestore.getInstance().collection("Users");
            collection.get().addOnCompleteListener(
                    new OnCompleteListener<QuerySnapshot>() {
                        ArrayList<ContactListContract> finalContacts = new ArrayList<>();
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot documents : task.getResult()) {
                                UserContract users = documents.toObject(UserContract.class);
                                if (user != null && !user.getUid().equals(users.getUserId())) {
                                    for (ContactListContract contacts : rawContacts) {
                                        if (contacts.getContactPhoneNumber().contains(users.getPhoneNumber())) {
                                            resultList.add(users);
                                            finalContacts.add(contacts);
                                        }
                                    }
                                }
                            }
                            rawContacts.clear();
                            contactsRecyclerView = view.findViewById(R.id.contacts_recyc_view);
                            contactsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                            contactsAdapter = new ContactsAdapter(finalContacts, resultList, view.getContext());
                            contactsRecyclerView.setAdapter(contactsAdapter);
                            ChatActivity.loadProgress.cancel();
                        }
                    }
            ).addOnFailureListener(
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            ChatActivity.loadProgress.cancel();
                        }
                    }
            );
            return null;
        }

    }

}
