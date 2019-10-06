package com.example.iNote;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactList extends Fragment {
    View view;
    private Cursor cursor;
    private String phoneNum;
RecyclerView contactsRecyclerView;
ContactsAdapter contactsAdapter;
ProgressDialog loadProgress;

    public ContactList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        loadProgress = new ProgressDialog(view.getContext());
        contactsRecyclerView = view.findViewById(R.id.contacts_recyc_view);
        loadProgress.setTitle("Loading...");
        loadProgress.show();
        new doInBack().execute();
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
       return view;
    }

    private Set<ContactListContract> getAllContacts(){
        Set<ContactListContract> nameList = new HashSet<>();
        ContentResolver contentResolver = view.getContext().getContentResolver();
        // cursor will be initialized in background
        cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null,null,null,ContactsContract.Contacts.DISPLAY_NAME + " ASC");
        if((cursor != null ? cursor.getCount() : 0) > 0){
            //above(if cursor is !null return getCount else return 0
            // then check if any of each is greater then zero
            while (cursor != null && cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndex
                        (ContactsContract.Contacts._ID));
                String names = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                ));
//                nameList.add(names);
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

                nameList.add(new ContactListContract(names,phoneNum));
            }
            if(cursor != null){
                cursor.close();
            }

        }
       return nameList;
    }

    private  class doInBack extends AsyncTask<Void,Void,Set<ContactListContract>>{
        @Override
        protected void onPostExecute(Set<ContactListContract> strings) {
            ArrayList<ContactListContract> resultContact = new ArrayList<>(strings);
            Collections.sort(resultContact,new SortContatcts());
           contactsAdapter = new ContactsAdapter(resultContact,view.getContext());
            contactsRecyclerView.setAdapter(contactsAdapter);
            loadProgress.cancel();
            Toast.makeText(getContext(), strings.size()+"", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Set<ContactListContract> doInBackground(Void... voids) {
            return getAllContacts();
        }
    }

    public class SortContatcts implements Comparator<ContactListContract>{

        @Override
        public int compare(ContactListContract contactListContract, ContactListContract t1) {
            return contactListContract.getContactName().compareTo(t1.getContactName());
        }
    }
}
