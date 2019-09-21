package com.example.notepad;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    FirebaseFirestore fireStore;


    static SharedPreferences sharedPreferences;
    static SharedPreferences recycleBinPreference;
    static final String MY_PREFERENCE = "storage link";
    static final String MY_RECYCLE_PREFERENCE = "recycleBin link";
    final String ARRAY_OF_RECYCLED_NOTE_KEY = "deleted notes";
    ArrayList<Notes> recycleBinArrayList;

    private int selectedNotePosition;
    private ArrayList<Notes> sortByCategoriesList;
    private ArrayList<Notes> filterList;
    public static boolean isSearchView;
    private ArrayList<Notes> sortedFilterByCategories;
   static boolean showCategoriesView;
   static boolean showRecycleBinMenuOption;
   static boolean isRecycleBinView;
    private MenuItem restoreAllMenu;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        selectedNotePosition = item.getGroupId();
        if (id == 100){
            if(showRecycleBinMenuOption){
                    recycleBinArrayList.add(noteList.get(selectedNotePosition));
            saveRecycleBinNotesToMemory();
            }
            noteList.remove(selectedNotePosition);
            saveNotesToMemory();
            adapter.notifyDataSetChanged();
            titleAdapter.notifyDataSetChanged();
            Toast.makeText(this,getString(R.string.toast_message_deleted_successfully),Toast.LENGTH_SHORT).show();
            if(isCategoriesView){
                sortByColor();
            }else if (isSearchView){
                searchView.setQuery("",true);
                searchView.setIconified(true);
            }
        }else if(id == 10){
            /*this is for the recycleBin ContextMenu*/
            //restore
            noteList.add(recycleBinArrayList.get(selectedNotePosition));
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
            sendAsMail();
        }

        return true;
    }

    private void sendAsMail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT,noteList.get(selectedNotePosition).getTitle());
        intent.putExtra(Intent.EXTRA_TEXT,noteList.get(selectedNotePosition).getTextBody());
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }

    private void openNote(int position) {
        Intent intent = new Intent(HomePage.this,EdithNote.class);
        intent.putExtra("notes",noteList.get(position));
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
        }else if(isCategoriesView){
            sortByColor();
        }
    }

    private void SettingsScreen() {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String categories = settings.getString("catView","");
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
        /*
        displayNotes(); is called again here cuz, i realize that after the showcategories settings change
        it only affects the items that are not inview yet (even when you call adapter.notifydataSetChanged();
        so half gets affected and the other stays the same so we have to fix it by recreating the adapter
         */
        if(isTitleView){
            displayTitles();
        }else if (isCategoriesView){
            sortByColor();
        }else{
            displayNotes();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        this code toggles the rateMe dialoge

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
        fireStore = FirebaseFirestore.getInstance();
//adding stuffs to the dataBase
      /*  Map<String,Object> contact = new HashMap<>();
        contact.put("name", "Emmanuel Oriola");
        contact.put("address","Detroit");
        contact.put("phone", "07020680817");

        fireStore.collection("ContactBook").document("2")
                .set(contact).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(HomePage.this,"Added Safely",Toast.LENGTH_LONG).show();
                    }
                }
        ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomePage.this,"Failed to connect", Toast.LENGTH_LONG).show();
                    }
                }
        );*/
      // Reading stuffs from tbe dataBase

       /* DocumentReference documentReference = fireStore.collection("ContactBook")
                .document("1");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot result = task.getResult();
                if(task.isSuccessful()){
                    StringBuilder builder = new StringBuilder();
                    builder.append(result.get("name"))
                            .append(" ")
                            .append(result.get("address"));
                    Toast.makeText(HomePage.this,builder,Toast.LENGTH_LONG)
                            .show();

                }
            }
        }).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(HomePage.this,"the fetch has failed", Toast.LENGTH_LONG)
                                .show();
                    }
                }
        );*/













        androidx.preference.PreferenceManager.setDefaultValues(this,R.xml.root_preferences,false);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadNotesFromMemory();
        loadNotesFromRecycleBin();
        recyclerView = findViewById(R.id.homepage_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        adapter = new NotesRecyclerAdapter(noteList);
        titleAdapter = new TitleRecyclerAdapter(noteList);
        sortByCategoriesList = new ArrayList<>();
        sortByCatagoriesAdapter =  new SortByCatagoriesAdapters(sortByCategoriesList);
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
    }

    public void displayNotes(){
        setAppBarTitle(getString(R.string.notes));
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

        }else{
            /*we call this condition because invalidate will set it back to false irrespective
            * so we need to only hide if we aren't in the recycle bin*/
            restoreAllMenu.setVisible(false);
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
                    }else {
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
           /* for(int i = 0; i < recycleBinArrayList.size(); i++){
                noteList.add(recycleBinArrayList.get(i));
                recycleBinArrayList.remove(i);
                recycleBinAdapter.notifyDataSetChanged();
            }*/
           // lets make it a oneLiner code
           if(recycleBinArrayList.isEmpty()){
               Toast.makeText(HomePage.this,"Oops! No Note Found", Toast.LENGTH_SHORT).show();
           }else{
               noteList.addAll(recycleBinArrayList);
               recycleBinArrayList.clear();
               recycleBinAdapter.notifyDataSetChanged();
               Toast.makeText(HomePage.this,"All Notes Restored", Toast.LENGTH_SHORT).show();
           }
        }
        return super.onOptionsItemSelected(item);
    }


    private void sortByColor() {
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

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void loadNotesFromMemory(){
        sharedPreferences = getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonString = sharedPreferences.getString(ARRAY_OF_NOTES,null);
        Type type = new TypeToken<ArrayList<Notes>>(){}.getType();
        noteList = gson.fromJson(jsonString,type);
        if(noteList == null){
            noteList = new ArrayList<>();
        }
    }
    public void saveRecycleBinNotesToMemory() {
        recycleBinPreference = getSharedPreferences(MY_RECYCLE_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = recycleBinPreference.edit();
        Gson gson = new Gson();
        String jsonString = gson.toJson(recycleBinArrayList);
        editor.putString(ARRAY_OF_RECYCLED_NOTE_KEY,jsonString);
        editor.apply();
    }
    public void loadNotesFromRecycleBin(){
        recycleBinPreference = getSharedPreferences(MY_RECYCLE_PREFERENCE,Context.MODE_PRIVATE);
        Gson gson = new Gson();
       String jsonString = recycleBinPreference.getString(ARRAY_OF_RECYCLED_NOTE_KEY,null);
       Type type = new TypeToken<ArrayList<Notes>>(){}.getType();
       recycleBinArrayList = gson.fromJson(jsonString,type);
       if(recycleBinArrayList == null){
           recycleBinArrayList = new ArrayList<>();
       }

    }

    public void saveNotesToMemory(){
        sharedPreferences = getSharedPreferences(MY_PREFERENCE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonString = gson.toJson(noteList);
        editor.putString(ARRAY_OF_NOTES,jsonString);
        editor.apply();
    }

}