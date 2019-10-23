package com.echo.iNote;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static ArrayList<Notes>noteList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    NotesRecyclerAdapter adapter;
    RecycleBinAdapter recycleBinAdapter;
    TitleRecyclerAdapter titleAdapter;
    SortByCatagoriesAdapters sortByCatagoriesAdapter;
    boolean isCategoriesView;
    static final int CONTACT_REQUEST_CODE = 31;
    FirebaseFirestore fireStore;
    Intent intent;
    String test;

    CircleImageView navProfileImage;
    DocumentReference documentReference;
    static SharedPreferences sharedPreferences;
    static SharedPreferences recycleBinPreference;

    TextView navHeaderEmail;

    TextView submit,resetPassword;
    EditText createPassword,confirmPassword,
            securityQuestion,securityAnswer,privateNotePassword,
            resetSecurityQuestion,resetSecurityAns;
    static ArrayList<Notes> protectedNotesArray;
    static SharedPreferences protectedNotesPref;
    static boolean isProtectedNotesView;
    static String PRIVATE_NOTES_PREFERENCE_KEY = "private_notes";
    static String ARRAY_OF_PRIVATE_NOTES_KEY = "private_notes_array_key";





    static final String MY_PREFERENCE_KEY = "storage link";
    static final String MY_RECYCLE_PREFERENCE_KEY = "recycleBin link";
    static final String ARRAY_OF_RECYCLED_NOTE_KEY = "deleted notes";
    static ArrayList<Notes> recycleBinArrayList;

    private int selectedNotePosition;
    private ArrayList<Notes> sortByCategoriesList;
    private ArrayList<Notes> filterList;
    public static boolean isSearchView;
    private ArrayList<Notes> sortedFilterByCategories;
   static boolean showCategoriesView;
   static boolean showRecycleBinMenuOption;
   static boolean isRecycleBinView;
    static boolean autoSync;
    static boolean isWifi;
    static boolean isWifiNet;
    static boolean isMobileNet;
    private MenuItem restoreAllMenu;
    private View createPasswordView;
    private View inputPasswordView;
    private View resetPasswordView;
    private AlertDialog passwordAlertDialogue;
    private PrivateNotesAdapter privateAdapter;
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    private View navigationViewHeaderView;
    private ConnectivityManager connection;
    private boolean loadedProfile;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*context menu params (groupID, itemID, menuPosition/Arrangement, StringResource*/
        selectedNotePosition = item.getGroupId();
        if (id == 100){
            if(showRecycleBinMenuOption){
                    recycleBinArrayList.add(0,noteList.get(selectedNotePosition));
            saveRecycleBinNotesToMemory();
            }if(!isProtectedNotesView){
                noteList.remove(selectedNotePosition);
                saveNotesToMemory();
                adapter.notifyDataSetChanged();
                titleAdapter.notifyDataSetChanged();
                Toast.makeText(this,getString(R.string.toast_message_deleted_successfully),Toast.LENGTH_SHORT).show();
            }if(isProtectedNotesView){
                protectedNotesArray.remove(selectedNotePosition);
                savePrivateNoteToMemory();
                privateAdapter.notifyDataSetChanged();
                //we do this to notify user that private notes don't go to the recycle bin
                if(showRecycleBinMenuOption){
                    Toast.makeText(this,"Deleted Private Notes Are Not Recyclable", Toast.LENGTH_LONG).show();
                }
            }

            if(isCategoriesView){
                sortByColor();
            }else if (isSearchView){
                searchView.setQuery("",true);
                searchView.setIconified(true);
            }
        }else if(id == 10){
            /*this is for the recycleBin ContextMenu*/
            //restore
            noteList.add(0,recycleBinArrayList.get(selectedNotePosition));
            recycleBinArrayList.remove(selectedNotePosition);
            recycleBinAdapter.notifyDataSetChanged();
            saveRecycleBinNotesToMemory();
            saveNotesToMemory();
            Toast.makeText(this,getString(R.string.context_menu_toast_note_restored),Toast.LENGTH_SHORT).show();
        }else if(id == 11){
            //delete
            recycleBinArrayList.remove(selectedNotePosition);
            recycleBinAdapter.notifyDataSetChanged();
            saveRecycleBinNotesToMemory();
            Toast.makeText(this,getString(R.string.toast_message_deleted_successfully),Toast.LENGTH_SHORT).show();
        }

        else if(id == 101){
                openNote(selectedNotePosition);
        }else if (id == 102){
            if(isProtectedNotesView){
                sendAsMail(protectedNotesArray);
            }else{
                sendAsMail(noteList);
            }
        }else if(id == 103){
            noteList.add(0,protectedNotesArray.get(selectedNotePosition));
            protectedNotesArray.remove(selectedNotePosition);
            adapter.notifyDataSetChanged();
            titleAdapter.notifyDataSetChanged(); privateAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Moved", Toast.LENGTH_SHORT).show();
            savePrivateNoteToMemory(); saveNotesToMemory();
        }else if(id == 104){
            //move note to private
            if(checkPasswordCreationValidation()){
                protectedNotesArray.add(0,noteList.get(selectedNotePosition));
                noteList.remove(selectedNotePosition);
                savePrivateNoteToMemory(); saveNotesToMemory();
                Toast.makeText(this, "Moved", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged(); privateAdapter.notifyDataSetChanged(); titleAdapter.notifyDataSetChanged();
            }else{
                Toast.makeText(this, "Generate A Private Key First", Toast.LENGTH_SHORT).show();
            }
        } else if (id == 105) {
            //check if we have permission to read contacts
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, ChatActivity.class);
                if (isProtectedNotesView) {
                    intent.putExtra("message", protectedNotesArray.get(selectedNotePosition));
                } else {
                    intent.putExtra("message", noteList.get(selectedNotePosition));
                }
                intent.putExtra("messageType", "direct");
                startActivity(intent);
            } else {
                requestPermissions();
            }
        } else if (id == 106) {
            if (isProtectedNotesView) {
                sendMultiChoice(protectedNotesArray);
            } else {
                sendMultiChoice(noteList);
            }
        }
        return true;
    }

    public void sendMultiChoice(ArrayList<Notes> notes) {
        String message = notes.get(selectedNotePosition).getTitle() +
                "\n\n" + notes.get(selectedNotePosition).getTextBody();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent);
        }
    }
    private void sendAsMail(ArrayList<Notes> arrayList) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT,arrayList.get(selectedNotePosition).getTitle());
        intent.putExtra(Intent.EXTRA_TEXT,arrayList.get(selectedNotePosition).getTextBody());
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }

    private void openNote(int position) {
        Intent intent = new Intent(HomePage.this,EdithNote.class);
        if(isProtectedNotesView){
            intent.putExtra("notes",protectedNotesArray.get(position));
        }else{
            intent.putExtra("notes",noteList.get(position));
        }
        intent.putExtra("p",position);
        startActivity(intent);
    }

    static final String ARRAY_OF_NOTES = "Saved notes";
    private SearchView searchView;
    boolean isTitleView;

    @Override
    protected void onResume() {
        super.onResume();
        SettingsScreen();
        adapter.notifyDataSetChanged();
        titleAdapter.notifyDataSetChanged();
        if(isSearchView){
            searchView.setQuery("",true);
            searchView.setIconified(true);
        } //check this first in order to avoid it being set back to false
        //in display notes section, besides onCreate gets called before onResume(Think about this reasons
        //like (if you're navigation back from editNote) we never go back into private notes
        if(isProtectedNotesView){
            displayPrivateNotes();
            privateAdapter.notifyDataSetChanged();
        }if(isCategoriesView){
            sortByColor();
        }
        if (loadedProfile) {
            loadProfile();
        }
    }

    private void SettingsScreen() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String categories = settings.getString("catView","");
        boolean sync = settings.getBoolean("sync", true);
        boolean wifi = settings.getBoolean("wifi", true);
        boolean recycleBin = settings.getBoolean("recycle",true);
        if("yes".equals(categories)){
           showCategoriesView = true;
        }else if ("no".equals(categories)){
            showCategoriesView = false;
        }if( recycleBin){
            showRecycleBinMenuOption = true;
            invalidateOptionsMenu();
            if(recycleBinArrayList == null){
                recycleBinArrayList = new ArrayList<>();
            }

        }else {
            showRecycleBinMenuOption = false;
            invalidateOptionsMenu();
        }
        if (sync) {
            System.out.println("Message : settings auto sync");
            autoSync = true;
            connection = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            connection.getActiveNetworkInfo();
            System.out.println("Message :  auto sync");
            if (wifi) {
                isWifi = true;
                System.out.println("Message : settings auto sync only on wifi");
                if (connection != null) {
                    NetworkInfo info = connection.getActiveNetworkInfo();
                    if (info != null) {
                        boolean isWifiNetwork = connection.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
                        if (isWifiNetwork) {
                            isWifiNet = true;
                            System.out.println("Message : settings auto sync wifi not connected");
                        } else {
                            isWifiNet = false;
                        }
                    }
                }
            } else {
                isWifi = false;
                System.out.println("Message : settings auto sync on any network");
            }
        } else {
            autoSync = false;
            System.out.println("Message : No auto sync enabled");
        }





        /*
        displayNotes(); is called again here cuz, i realize that after the showCategories settings change
        it only affects the items that are not inView yet (even when you call adapter.notifyDataSetChanged();
        so half gets affected and the others stays the same so we have to fix it by recreating the adapter
         */
        if(isTitleView){
            displayTitles();
        }else if (isCategoriesView){
            sortByColor();
        }else if(isProtectedNotesView){
            displayPrivateNotes();
        }
        else{
            displayNotes();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check if app is being installed for the first time
        if(!hasShownWelcomeScreen()){
            startActivity(new Intent(this,OnBoardingScreen.class)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
        /*
        this code toggles the rateMe dialogue

        AppRate.with(this)
                .setInstallDays(1); //default val 10;
                .setLaunchTimes(3); // default1 val 10
                .setRemindInterval(2); // when to show again after pushing remind me later
                .monitor();
                appRate.showRateDialogIfMeetsConditions(this);
                //if you want to show again even if user declines use
                //AppRate.with(this).showRateDialog(this);
                // you can override the stings.xml to change default text values
                

         */
        androidx.preference.PreferenceManager.setDefaultValues(this,R.xml.root_preferences,false);
        setContentView(R.layout.activity_home_page);
        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        user = firebaseAuth.getCurrentUser();
       /* navProfileImage =  view.findViewById(R.id.nav_profile_image);
        navProfileImage.setBackgroundColor(getColor(R.color.colorPrimaryDark));*/

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadNotesFromMemory();
        loadNotesFromRecycleBin();
        loadPrivateNotesFromMemory();
        syncNotesFromCloud();



       /* TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = telephonyManager.getSimCountryIso().toUpperCase();
        int number = PhoneNumberUtil.getInstance().getCountryCodeForRegion(countryIso);
        String us = PhoneNumberUtil.getInstance().getRegionCodeForCountryCode(20);
        String num = PhoneNumberUtil.getInstance().getExampleNumber("US").toString();
        String region = PhoneNumberUtil.getInstance().getNddPrefixForRegion("IQ", true);
*/

        recyclerView = findViewById(R.id.homepage_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        adapter = new NotesRecyclerAdapter(noteList);
        titleAdapter = new TitleRecyclerAdapter(noteList);
        sortByCategoriesList = new ArrayList<>();
        sortByCatagoriesAdapter =  new SortByCatagoriesAdapters(sortByCategoriesList);
        privateAdapter = new PrivateNotesAdapter(protectedNotesArray);
        displayNotes();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this,EdithNote.class);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationViewHeaderView = navigationView.getHeaderView(0);
        navProfileImage =  navigationViewHeaderView.findViewById(R.id.nav_profile_image);
        navHeaderEmail = navigationViewHeaderView.findViewById(R.id.nav_header_email);
        loadProfile();

    }

    private void loadProfile() {
        loadedProfile = true;
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        if(user != null){
            menu.findItem(R.id.nav_sign_in).setVisible(false);
            navHeaderEmail.setText(user.getEmail());
            documentReference = fireStore.collection("Users").document(user.getUid());
            documentReference.get().addOnSuccessListener(
                    new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UserContract userValues = documentSnapshot.toObject(UserContract.class);
                            if (userValues != null) {
                                String imageUrl = userValues.getImage();
                                if (imageUrl.equals("default")) {
                                    navProfileImage.setImageDrawable(getDrawable(R.drawable.user_profile_picture));
                                } else {
//else set what they've got in the database
                                    Glide.with(getApplicationContext())
                                            .load(userValues.getImage()).placeholder(getDrawable(R.drawable.user_profile_picture))
                                            .into(navProfileImage);
                                }
                            }

                        }
                    }
            );

        }else{
            menu.findItem(R.id.nav_log_out).setVisible(false);
            navProfileImage.setImageDrawable(getDrawable(R.drawable.user_profile_picture));
        }
    }

    public void displayNotes(){
        setAppBarTitle(getString(R.string.notes));
        isProtectedNotesView = false;
        isRecycleBinView = false;
        isSearchView = false;
        isTitleView = false;
        isCategoriesView = false;
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        DrawerSelectionHandler(R.id.nav_notes);
    }
    private void displayRecycleBinNotes() {
        setAppBarTitle(getString(R.string.recycle_bin_menu));
        invalidateOptionsMenu();
        isProtectedNotesView = false;
        isRecycleBinView = true;
        isSearchView = false;
        isTitleView = false;
        isCategoriesView = false;
        recyclerView.setLayoutManager(layoutManager);
        recycleBinAdapter = new RecycleBinAdapter(recycleBinArrayList);
        recyclerView.setAdapter(recycleBinAdapter);
    }

    private void setAppBarTitle(String title) {
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(title);
        }
    }

    public void DrawerSelectionHandler(int id) {
        NavigationView navView = findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();
        menu.findItem(id).setChecked(true);
    }

    public void displayTitles(){
        setAppBarTitle(getString(R.string.notes));
        isProtectedNotesView = false;
        isRecycleBinView = false;
        isSearchView = false;
        isCategoriesView = false;
        isTitleView = true;
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(titleAdapter);
        DrawerSelectionHandler(R.id.nav_title_view);
    }

    public void startProfileActivity(View view){
        startActivity(new Intent(this,ProfileActivity.class));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(isRecycleBinView){
            displayNotes();
            restoreAllMenu.setVisible(false);
            invalidateOptionsMenu();
        }else if (isProtectedNotesView){
            displayNotes();
        }

        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        MenuItem searchMenu = menu.findItem(R.id.app_bar_search);
        MenuItem recycleBinMenu = menu.findItem(R.id.recycle_bin);
        restoreAllMenu = menu.findItem(R.id.recycleBin_restore_all);
        //we only wanna show the recycleBin menu if we show the view
        if(isRecycleBinView){
searchMenu.setVisible(false);
        }else{
            /*we call this condition because invalidate will set it back to false irrespective
            * so we need to only hide if we aren't in the recycle bin*/
            restoreAllMenu.setVisible(false);
            searchMenu.setVisible(true);
        }if(isProtectedNotesView){
            searchMenu.setVisible(false);
        }else {
            searchMenu.setVisible(true);
        }

        if(showRecycleBinMenuOption){
            recycleBinMenu.setVisible(true);
        }
        else{
            recycleBinMenu.setVisible(false);
        }

        searchView = (SearchView) searchMenu.getActionView();
        searchView.setQueryHint(getString(R.string.search_by_title));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterList = new ArrayList<>();
                sortedFilterByCategories = new ArrayList<>();
                for(Notes note : noteList){
                    if(newText.trim().isEmpty() || newText.trim().length() == 0){
                        if(isTitleView){
                            displayTitles();
                        }else if (isCategoriesView){
                            sortByColor();
                        }else{
                            displayNotes();
                        }
                        filterList.clear();
                        sortedFilterByCategories.clear();
                    }else if(!newText.isEmpty()){
                        isSearchView = true;
                        if(note.getTitle().toLowerCase().contains(newText) && (noteList != null)){
                            filterList.add(note);
                            if(isTitleView){
                                TitleRecyclerAdapter  titleView = new TitleRecyclerAdapter(filterList);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(titleView);
                            }else if (isCategoriesView){
                                sortedFilterByCategories.clear();
                                int one = Color.parseColor("#607d8b");//blue grey uncategorized
                                int two = Color.parseColor("#7e57c2");//deep purple work
                                int three = Color.parseColor("#ef5350");//red family affair
                                int four = Color.parseColor("#42a5f5");//blue study
                                int five = Color.parseColor("#66bb6a");//green research
                                //int six = Color.parseColor("#66bb6a");//green research
                                categoriesLoop(filterList, sortedFilterByCategories,one);
                                categoriesLoop(filterList, sortedFilterByCategories,two);
                                categoriesLoop(filterList, sortedFilterByCategories,three);
                                categoriesLoop(filterList, sortedFilterByCategories,four);
                                categoriesLoop(filterList, sortedFilterByCategories,five);
                                SortByCatagoriesAdapters sortByCatagoriesAdapter = new SortByCatagoriesAdapters(sortedFilterByCategories);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(sortByCatagoriesAdapter);
                            }else{
                                NotesRecyclerAdapter adapter = new NotesRecyclerAdapter(filterList);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    }
                }

                return false;
            }
        });
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.app_bar_action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }else if(id == R.id.app_bar_delete_all){
//            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
//            View view = getLayoutInflater().inflate(R.layout.alert_dialog,null);
//            alertDialog.setView(view);
//            AlertDialog dialog = alertDialog.create();
//            dialog.show();
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.delete_all).setIcon(R.drawable.ic_delete_24dp);
            alert.setMessage(R.string.dialogue_deletion_warning);
            alert.setPositiveButton(R.string.dialogue_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(isRecycleBinView){
                        if (recycleBinArrayList.isEmpty()){
                            Toast.makeText(HomePage.this,getString(R.string.toast_no_notes_found), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            recycleBinArrayList.clear();
                            recycleBinAdapter.notifyDataSetChanged();
                            saveRecycleBinNotesToMemory();
                        }
                    }else if(isProtectedNotesView){
                        if(protectedNotesArray.isEmpty()){
                            Toast.makeText(HomePage.this,getString(R.string.toast_no_notes_found), Toast.LENGTH_SHORT).show();
                        }else{
                            protectedNotesArray.clear();
                            privateAdapter.notifyDataSetChanged();
                            savePrivateNoteToMemory();
                        }
                    }

                    else {
                        if(noteList.isEmpty()){
                            Toast.makeText(HomePage.this,getString(R.string.toast_no_notes_found), Toast.LENGTH_SHORT).show();
                        }else {
                            if(showRecycleBinMenuOption){
                                recycleBinArrayList.addAll(noteList);
                                saveRecycleBinNotesToMemory();
                            }
                                noteList.clear();
                                saveNotesToMemory();
                                adapter.notifyDataSetChanged();
                                titleAdapter.notifyDataSetChanged();
                                sortByCatagoriesAdapter.notifyDataSetChanged();
                                Toast.makeText(HomePage.this,getString(R.string.dialogue_toast_message), Toast.LENGTH_SHORT).show();
                        }
                    }


                }
            });alert.setNegativeButton(R.string.dialogue_cancel_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = alert.create();
            dialog.show();

        }else if(id == R.id.app_bar_mark_notes){

        }else if( id == R.id.recycle_bin){
            displayRecycleBinNotes();
            restoreAllMenu.setVisible(true);
            invalidateOptionsMenu();

        }else if(id == R.id.recycleBin_restore_all){

           if(recycleBinArrayList.isEmpty()){
               Toast.makeText(HomePage.this,"Oops! No Note Found", Toast.LENGTH_SHORT).show();
           }else{
               noteList.addAll(recycleBinArrayList);
               recycleBinArrayList.clear();
               recycleBinAdapter.notifyDataSetChanged();
               saveNotesToMemory(); saveRecycleBinNotesToMemory();
               //arrays will be notified in onResume
               Toast.makeText(HomePage.this,"All Notes Restored", Toast.LENGTH_SHORT).show();
           }
        }
        return super.onOptionsItemSelected(item);
    }


    private void sortByColor() {
        isProtectedNotesView = false;
        isSearchView = false;
        isCategoriesView = true;
        isTitleView = false;
        sortByCategoriesList.clear();
        int one = Color.parseColor("#607d8b");//blue grey uncategorized
        int two = Color.parseColor("#7e57c2");//deep purple work
        int three = Color.parseColor("#ef5350");//red family affair
        int four = Color.parseColor("#42a5f5");//blue study
        int five = Color.parseColor("#66bb6a");//green research
        categoriesLoop(noteList, sortByCategoriesList,one);
        categoriesLoop(noteList, sortByCategoriesList,two);
        categoriesLoop(noteList, sortByCategoriesList,three);
        categoriesLoop(noteList, sortByCategoriesList,four);
        categoriesLoop(noteList, sortByCategoriesList,five);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(sortByCatagoriesAdapter);
    }

    public void categoriesLoop(ArrayList<Notes> notes, ArrayList<Notes> newNote, int colorCode){
        for(Notes noteLists : notes)
            if (noteLists.getColors() == colorCode) {
                newNote.add(noteLists);
            }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes) {
            displayNotes();
        } else if (id == R.id.nav_title_view) {
            displayTitles();
        } else if (id == R.id.nav_sort_by_categories) {
            sortByColor();
        }else if (id == R.id.nav_rate){
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + getPackageName())));
            }catch (ActivityNotFoundException notFound){
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/app/details?id=" +getPackageName())));
            }
        }
        else if (id == R.id.nav_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/*");
            intent.putExtra(Intent.EXTRA_TEXT, "Check out iNote, i use it to take and manage notes or share them with the people i care about, get it for" +
                    " free at " + "market://details?id=" + getPackageName());
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }

        }else if(id == R.id.nav_contacts){
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS)
                    ==  PackageManager.PERMISSION_GRANTED){
                startActivity(new Intent(this, ChatActivity.class));
            }else{
                requestPermissions();
            }
        }else if(id == R.id.nav_private_notes){
            if (isProtectedNotesView) {
                //do not inflate the passLayout a second time dude :(
            } else {
                LayoutInflater createPassLayout = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                createPasswordView = createPassLayout.inflate(R.layout.create_password_layout, null);
                inputPasswordView = createPassLayout.inflate(R.layout.password_layout, null);
                if (!checkPasswordCreationValidation()) {
                    createNewPassword();
                } else {
                    privateNotePassword = inputPasswordView.findViewById(R.id.private_note_password_edit_text);
                    resetPassword = inputPasswordView.findViewById(R.id.private_note_reset_password);
                    createDialogue(inputPasswordView);
                    validatePassWord(privateNotePassword);
                }
            }

        }else if(id == R.id.nav_sign_in){
            startActivity(new Intent(this,LoginOrSignUp.class));
        }else if(id == R.id.nav_log_out){
            /*TODO dont forget to always check for network ion before logging out*/
            if (user != null) {
                connection = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connection != null) {
                    NetworkInfo networkInfo = connection.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        /*AlertDialog.Builder alert = new AlertDialog.Builder(this);
                        alert.setTitle("");
                        alert.setMessage("Log out and clear all notes(Device)? \n" +
                                "This prevents your notes from being accessed by someone else " +
                                "and/or being synced or added to newly logged in accounts\nYour notes will still be backed up on the cloud");
                        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(user != null){
                                    Toast.makeText(HomePage.this, "Syncing Notes...", Toast.LENGTH_SHORT).show();
                                    Gson gson = new Gson();
                                    String notes = gson.toJson(noteList);
                                    String privateNotes = gson.toJson(protectedNotesArray);
                                    Map<String,String> allNotes = new HashMap<>();
                                    allNotes.put("notes",notes);
                                    allNotes.put("privateNotes",privateNotes);
                                    fireStore.collection("Notes").document(user.getUid()).set(allNotes)
                                    .addOnCompleteListener(
                                            new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        firebaseAuth.signOut();
                                                        recreate();
                                                        noteList.clear();
                                                        protectedNotesArray.clear();
                                                        savePrivateNoteToMemory();
                                                        saveNotesToMemory();
                                                        startActivity(new Intent(getApplicationContext(),LoginOrSignUp.class)
                                                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                                                        Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                    );
                                    }
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                firebaseAuth.signOut();
                                recreate();
                            }
                        }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Do nothing if they cancel
                            }
                        }).create().show();*/
                        final ProgressDialog progressDialog = new ProgressDialog(this);
                        progressDialog.setTitle("Processing...");
                        progressDialog.show();
                        firebaseAuth.signOut();
                        progressDialog.cancel();
                        recreate();
                        startActivity(new Intent(getApplicationContext(), LoginOrSignUp.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getString(R.string.please_check_your_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createNewPassword() {
        createPassword = createPasswordView.findViewById(R.id.create_password);
        confirmPassword = createPasswordView.findViewById(R.id.confirm_password);
        securityQuestion = createPasswordView.findViewById(R.id.security_question);
        securityAnswer = createPasswordView.findViewById(R.id.security_question_answer);
        submit = createPasswordView.findViewById(R.id.password_submit_button);
        createDialogue(createPasswordView);
    }

    private void validatePassWord(final EditText passwordInput) {

        passwordInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

if(passwordInput.length() == 4 && passwordInput.getText().toString().equals(getPassword())){
    Toast.makeText(HomePage.this, "pass accepted", Toast.LENGTH_SHORT).show();
    passwordInput.setText("");
    passwordAlertDialogue.cancel();
    displayPrivateNotes();
}else if(passwordInput.length() == 4 && !passwordInput.getText().toString().equals(getPassword())){
    Toast.makeText(HomePage.this, "Wrong passkey", Toast.LENGTH_SHORT).show();
    passwordInput.setText("");

}
                    }
                }
        );
    }

    private void displayPrivateNotes() {
        setAppBarTitle("Private Notes");
        isProtectedNotesView = true;
        isRecycleBinView = false;
        isSearchView = false;
        isTitleView = false;
        isCategoriesView = false;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        privateAdapter = new PrivateNotesAdapter(protectedNotesArray);
        recyclerView.setAdapter(privateAdapter);
        invalidateOptionsMenu();
    }

    private void createDialogue(View layout) {
        AlertDialog.Builder alertDialog  = new AlertDialog.Builder(this);
        alertDialog.setView(layout);
        passwordAlertDialogue = alertDialog.create();
        passwordAlertDialogue.show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CONTACT_REQUEST_CODE) {//sad face :{
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, ChatActivity.class));
            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                requestPermissions();
                //requestPermission again to enter the reationale part, (winks!)

            }
        }

    }
    private void requestPermissions() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS)){
            /*TODO
             *  this is invoked when user cancels your permission request and you need to explain why you needed the permission
             *  */
            permissionRationale();
        }else{
            //request permission
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.READ_CONTACTS},CONTACT_REQUEST_CODE);
        }
    }

    private void permissionRationale() {
        AlertDialog.Builder permissionMessage = new AlertDialog.Builder(this);
        permissionMessage.setMessage("We noticed you've recently disabled the permission " +
                "for iNote to access your contacts, some authorizations are needed for this function to work," +
                " we promise to never pick or use your information without your consent.");
        permissionMessage.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);

                startActivity(intent);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        permissionMessage.create().show();
    }

    public boolean hasShownWelcomeScreen() {
        OnBoardingScreen.preferences = getSharedPreferences
                (OnBoardingScreen.WELCOME_PREFERENCE_KEY,Context.MODE_PRIVATE);
       return OnBoardingScreen.preferences.getBoolean
               (OnBoardingScreen.WELCOME_SCREEN_KEY,false);
}
    public void loadNotesFromMemory(){
        sharedPreferences = getSharedPreferences(MY_PREFERENCE_KEY, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonString = sharedPreferences.getString(ARRAY_OF_NOTES,null);
        Type type = new TypeToken<ArrayList<Notes>>(){}.getType();
        noteList = gson.fromJson(jsonString,type);
        if(noteList == null){
            noteList = new ArrayList<>();
        }
    }
    public void saveRecycleBinNotesToMemory() {
        recycleBinPreference = getSharedPreferences(MY_RECYCLE_PREFERENCE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = recycleBinPreference.edit();
        Gson gson = new Gson();
        String jsonString = gson.toJson(recycleBinArrayList);
        editor.putString(ARRAY_OF_RECYCLED_NOTE_KEY,jsonString);
        editor.apply();
    }
    public void loadNotesFromRecycleBin(){
        recycleBinPreference = getSharedPreferences(MY_RECYCLE_PREFERENCE_KEY,Context.MODE_PRIVATE);
        Gson gson = new Gson();
       String jsonString = recycleBinPreference.getString(ARRAY_OF_RECYCLED_NOTE_KEY,null);
       Type type = new TypeToken<ArrayList<Notes>>(){}.getType();
       recycleBinArrayList = gson.fromJson(jsonString,type);
       if(recycleBinArrayList == null){
           recycleBinArrayList = new ArrayList<>();
       }

    }
public void savePrivateNoteToMemory(){
        protectedNotesPref = getSharedPreferences(PRIVATE_NOTES_PREFERENCE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = protectedNotesPref.edit();
        Gson json = new Gson();
        String gsonArray = json.toJson(protectedNotesArray);
        editor.putString(ARRAY_OF_PRIVATE_NOTES_KEY,gsonArray);
        editor.apply();
}
public void loadPrivateNotesFromMemory(){
        protectedNotesPref = getSharedPreferences(PRIVATE_NOTES_PREFERENCE_KEY, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String result = protectedNotesPref.getString(ARRAY_OF_PRIVATE_NOTES_KEY,null);
        Type type = new TypeToken<ArrayList<Notes>>(){}.getType();
        protectedNotesArray = gson.fromJson(result,type);
        if(protectedNotesArray == null){
            protectedNotesArray = new ArrayList<>();
        }
}
public void createPrivateNotePassword(String password,String securityQues,String securityAns){
protectedNotesPref = getSharedPreferences(PRIVATE_NOTES_PREFERENCE_KEY,Context.MODE_PRIVATE);
SharedPreferences.Editor editor = protectedNotesPref.edit();
//too lazy to create constants
editor.putString("new_password",password);
editor.putString("new_security_ques",securityQues);
editor.putString("new_security_ans",securityAns);
editor.putBoolean("has_created_password",true);
editor.apply();
}
public Boolean checkPasswordCreationValidation(){
        protectedNotesPref = getSharedPreferences(PRIVATE_NOTES_PREFERENCE_KEY,Context.MODE_PRIVATE);
    return protectedNotesPref.getBoolean("has_created_password",false);
}


    public void saveNotesToMemory(){
        sharedPreferences = getSharedPreferences(MY_PREFERENCE_KEY,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonString = gson.toJson(noteList);
        editor.putString(ARRAY_OF_NOTES,jsonString);
        editor.apply();
    }
public void readNewPassword(View view){
    String createPass = createPassword.getText().toString().trim();
    String confirmPass = confirmPassword.getText().toString().trim();
    String securityQues = securityQuestion.getText().toString().trim();
    String securityAns = securityAnswer.getText().toString().trim();
    if(createPass.isEmpty() || confirmPass.isEmpty() ||
            securityAns.isEmpty() || securityQues.isEmpty()){
        Snackbar.make(view,getString(R.string.all_fields_are_required),Snackbar.LENGTH_LONG).show();
    }else if(!createPass.equals(confirmPass)){
                  Snackbar.make(view,"Password Mismatch !",Snackbar.LENGTH_LONG).show();
    }else if(createPass.length() != 4){
        Snackbar.make(view,"Password must be 4 digits long",Snackbar.LENGTH_LONG).show();
    }else{
        createPrivateNotePassword(createPass,securityQues,securityAns);
        passwordAlertDialogue.cancel();
        Toast.makeText(this, "Password created successfully", Toast.LENGTH_SHORT).show();
    }
}
public String getPassword(){
        protectedNotesPref = getSharedPreferences(PRIVATE_NOTES_PREFERENCE_KEY,Context.MODE_PRIVATE);
        return protectedNotesPref.getString("new_password",null);

}
public void resetPasswordButton(View view){
        //should have created a constant, now i've used these crazy string many times than expected... shit :(
    LayoutInflater createPassLayout = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    resetPasswordView = createPassLayout.inflate(R.layout.reset_password_layout,null);
    passwordAlertDialogue.cancel();
    createDialogue(resetPasswordView);
        protectedNotesPref = getSharedPreferences(PRIVATE_NOTES_PREFERENCE_KEY,Context.MODE_PRIVATE);
        String securityQuestion = protectedNotesPref.getString("new_security_ques","");
   resetSecurityQuestion = resetPasswordView.findViewById(R.id.reset_security_question);
   resetSecurityQuestion.setText(securityQuestion);
}
public void securityOkay(View view){
    resetSecurityAns =  resetPasswordView.findViewById(R.id.reset_security_question_answer);
    protectedNotesPref = getSharedPreferences(PRIVATE_NOTES_PREFERENCE_KEY,Context.MODE_PRIVATE);
    String securityAnswer = protectedNotesPref.getString("new_security_ans","");
    if(resetSecurityAns.getText().toString().trim().equals(securityAnswer)){
        Toast.makeText(this, "Approved", Toast.LENGTH_SHORT).show();
        passwordAlertDialogue.cancel();
        createNewPassword();

    }else{
        Toast.makeText(this, "Invalid Answer... ", Toast.LENGTH_SHORT).show();

    }
}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveNotesToCloud();
        //on destroy never gets called if they exit the
        //app using the minimize button
        //so we wanna call this method in on pause too
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveNotesToCloud();
    }

    public void saveNotesToCloud(){
        if(user != null){
            if (autoSync) {
                System.out.println("Message : Cloud auto sync enable");
                if (isWifi && isWifiNet) {
                    System.out.println("Message : Cloud auto sync enable only on wifi");
                    saveToCloud();
                } else if (!isWifi) {
                    //save it cuz this means we can sync on any network, not just on wifi
                    saveToCloud();
                    System.out.println("Message : Cloud auto sync enabled on any network");
                }
            }
        }
}

    private void saveToCloud() {
        Gson gson = new Gson();
        String notes = gson.toJson(noteList);
        String privateNotes = gson.toJson(protectedNotesArray);
        Map<String, String> allNotes = new HashMap<>();
        allNotes.put("notes", notes);
        allNotes.put("privateNotes", privateNotes);
        fireStore.collection("Notes").document(user.getUid()).set(allNotes);
    }

    public void syncNotesFromCloud() {
        if(user != null){
            DocumentReference cloudNotes = fireStore.collection("Notes").document(user.getUid());
            cloudNotes.get().addOnCompleteListener(
                    new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful() && task.getResult() != null){
                                CloudNotes cloudNotes = task.getResult().toObject(CloudNotes.class);
                                if (cloudNotes != null) {
                                    String one = cloudNotes.getNotes();
                                    String two = cloudNotes.getPrivateNotes();
                                    Gson gson = new Gson();
                                    Type noteType = new TypeToken<ArrayList<Notes>>() {
                                    }.getType();
                                    ArrayList<Notes> notesPlaceHolder = gson.fromJson(one, noteType);
                                    ArrayList<Notes> privateNotesHolder = gson.fromJson(two, noteType);
                                    /*Just in case someday one dutch bag someday needs to manage my codebase, i'll do you a favour by explaining
                                     * whats going on below, first we use a set with hashAndEquals defined in its class to get the notes from the dataBase
                                     * then we add the notes on the device to it, then the set checks for any duplicate note and removes them
                                     * making it a single note, in other words .. Cloud notes + mobile notes combined*/

                                    Set<Notes> filter = new LinkedHashSet<>(noteList);
                                    filter.addAll(notesPlaceHolder);
                                    Set<Notes> privateFilter = new LinkedHashSet<>(protectedNotesArray);
                                    privateFilter.addAll(privateNotesHolder);
                                    /*we clear noteList(mobile version) in order to add the newly filtered values*/
                                    noteList.clear();
                                    noteList.addAll(filter);
                                    protectedNotesArray.clear();
                                    protectedNotesArray.addAll(privateFilter);

                                    saveNotesToMemory();
                                    savePrivateNoteToMemory();
                                    adapter.notifyDataSetChanged();
                                    privateAdapter.notifyDataSetChanged();
                                } else {

                                }


                            }

                        }
                    }
            );

        }

}

}