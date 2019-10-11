package com.echo.iNote;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static ArrayList<Notes>noteList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    NotesRecyclerAdapter adapter;
    TitleRecyclerAdapter titleAdapter;
    SortByCatagoriesAdapters sortByCatagoriesAdapter;
    boolean isCategoriesView;
    static SharedPreferences sharedPreferences;
    static SharedPreferences recycleBinSharedPreference;
    static final String MY_PREFERENCE = "storage link";
    static final String RECYCLE_BIN_PREFERENCE = "recycle bin";
    private int selectedNotePosition;
    private ArrayList<Notes> sortByCategoriesList;
    private ArrayList<Notes> filterList;
    public static boolean isSearchView;
    private ArrayList<Notes> sortedFilterByCategories;
    static boolean showCategoriesView;
    static boolean showRecycleBin;
    static ArrayList<Notes> recycleBinNotes;
    private static String ARRAY_OF_RECYCLABLE_NOTES = "array_of_recyclable_notes";

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        selectedNotePosition = item.getGroupId();
        if (id == 100){
            addDeletedNotesToRecycleBin(selectedNotePosition);
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
        }
        else if(id == 101){
            openNote(selectedNotePosition);
        }else if (id == 102){
            sendAsMail();
        }

        return true;
    }

    private void addDeletedNotesToRecycleBin(int notePosition) {
        if(showRecycleBin){
            recycleBinNotes.add(noteList.get(notePosition));
            saveDeletedNoteToMemory();
        }
    }

    private void saveDeletedNoteToMemory() {
        recycleBinSharedPreference = getSharedPreferences(RECYCLE_BIN_PREFERENCE,MODE_PRIVATE);
        SharedPreferences.Editor editor = recycleBinSharedPreference.edit();
        Gson json = new Gson();
        String jsonToString = json.toJson(recycleBinNotes);
        editor.putString(ARRAY_OF_RECYCLABLE_NOTES,jsonToString);
        editor.apply();
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
            showRecycleBin = true;
            invalidateOptionsMenu();
        }else {
            showRecycleBin = false;
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
        androidx.preference.PreferenceManager.setDefaultValues(this,R.xml.root_preferences,false);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appRateDialogue();

        loadNotesFromMemory();
        loadRecycleBinFromMemory();
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

    public void appRateDialogue() {
        AppRate.with(this)
                .setInstallDays(1)
                .setLaunchTimes(3)
                .setRemindInterval(2)
                .setDebug(false).setOnClickButtonListener(
                new OnClickButtonListener() {
                    @Override
                    public void onClickButton(int which) {
                        if(which == -2){
                            AppRate.with(HomePage.this).clearSettingsParam();
                        }
                    }
                }
        )
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(HomePage.this);
    }

    public void displayNotes(){
        isSearchView = false;
        isTitleView = false;
        isCategoriesView = false;
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        DrawerSelectionHandler(R.id.nav_notes);
    }

    public void DrawerSelectionHandler(int id) {
        NavigationView navView = findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();
        menu.findItem(id).setChecked(true);
    }
public void displayRecycleBinNotes(){
        RecycleBinAdapter recycleBinAdapter = new RecycleBinAdapter(recycleBinNotes);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recycleBinAdapter);
}
    public void displayTitles(){
        isSearchView = false;
        isCategoriesView = false;
        isTitleView = true;
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(titleAdapter);
        DrawerSelectionHandler(R.id.nav_title_view);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        MenuItem searchMenu = menu.findItem(R.id.app_bar_search);
        MenuItem recycleBinMenu = menu.findItem(R.id.recycle_bin);
        if(showRecycleBin){
            recycleBinMenu.setVisible(true);
        }else{
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
                                SortByCatagoriesAdapters sortByCatagoriesAdapter =
                                        new SortByCatagoriesAdapters(sortedFilterByCategories);
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
                      if(noteList.isEmpty()){
                          Toast.makeText(HomePage.this,getString(R.string.toast_no_notes_found), Toast.LENGTH_SHORT).show();
                      }else{
                          noteList.clear();
                          saveNotesToMemory();
                          adapter.notifyDataSetChanged();
                          titleAdapter.notifyDataSetChanged();
                          sortByCatagoriesAdapter.notifyDataSetChanged();
                          Toast.makeText(HomePage.this,getString(R.string.dialogue_toast_message), Toast.LENGTH_SHORT).show();
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

        }else if(id == R.id.recycle_bin){
            displayRecycleBinNotes();
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

    public void loadRecycleBinFromMemory(){
        recycleBinSharedPreference = getSharedPreferences(RECYCLE_BIN_PREFERENCE,MODE_PRIVATE);
        Gson gson = new Gson();
        String gsonString = recycleBinSharedPreference.getString(ARRAY_OF_RECYCLABLE_NOTES,null);
        Type type = new TypeToken<ArrayList<Notes>>(){}.getType();
        recycleBinNotes = gson.fromJson(gsonString,type);
        if(recycleBinNotes == null){
            recycleBinNotes = new ArrayList<Notes>();
        }

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
    public void saveNotesToMemory(){
        sharedPreferences = getSharedPreferences(MY_PREFERENCE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonString = gson.toJson(noteList);
        editor.putString(ARRAY_OF_NOTES,jsonString);
        editor.apply();
    }
}
