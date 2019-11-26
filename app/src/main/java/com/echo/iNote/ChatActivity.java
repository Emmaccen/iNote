package com.echo.iNote;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.echo.iNote.ui.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedHashSet;
import java.util.Set;

public class ChatActivity extends AppCompatActivity {
    static Intent intent;
    private static final String CONTACT_PREF_KEY = "contact_pref_key";
    private Cursor cursor;
    ContactsAdapter contactsAdapter;
//    FirebaseFirestore fireStore;
    FirebaseUser user;
    static ProgressDialog loadProgress;
    private Set<ContactListContract> rawContacts;
    private String phoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        intent = getIntent();
        user = FirebaseAuth.getInstance().getCurrentUser();
        rawContacts = new LinkedHashSet<>();
        loadProgress = new ProgressDialog(this);
        background back = new background();
        back.execute();

    }
public class background extends AsyncTask<Void,Void,Void>{
    @Override
    protected void onPreExecute() {
        loadProgress.setTitle("Loading From Database");
        loadProgress.show();
    }
    @Override
    protected Void doInBackground(Void... voids) {
        getAllContacts();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (user == null) {
             Toast.makeText(getApplicationContext(), getString(R.string.no_user_account_detected), Toast.LENGTH_SHORT).show();
loadProgress.cancel();
        } else {
            SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(ChatActivity.this, getSupportFragmentManager(), rawContacts);
            ViewPager viewPager = findViewById(R.id.view_pager);
            viewPager.setOffscreenPageLimit(2);
            viewPager.setAdapter(sectionsPagerAdapter);
            TabLayout tabs = findViewById(R.id.tabs);
            tabs.setupWithViewPager(viewPager);
        }
    }
}

    private void getAllContacts() {
        if (user == null) {
            //can't show toast message here buddy :)
           /* Toast.makeText(this, getString(R.string.no_user_account_detected), Toast.LENGTH_SHORT).show();*/
        } else {
            ContentResolver contentResolver = this.getContentResolver();
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
//                            phoneNumber = phoneNum.replace(" ", "");
                          phoneNumber =  PhoneNumberUtils.stripSeparators(phoneNum);
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

    }

}