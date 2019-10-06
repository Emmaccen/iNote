package com.example.iNote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static ArrayList<Notes>noteList;
    ListView notesListView;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    NotesRecyclerAdapter adapter;
    int notePosition;
   // NoteAdapter notesArrayAdapter;
   static SharedPreferences sharedPreferences;
    static final String MY_PREFERENCE = "storage link";
    static final String ARRAY_OF_NOTES = "Saved notes";

    @Override
    protected void onResume() {
        super.onResume();
       // notesArrayAdapter.notifyDataSetChanged();
adapter.notifyDataSetChanged();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //registerForContextMenu(l);
     //  loadNotesFromMemory();
//        noteList = new ArrayList<>();
//        noteList.add(new Notes("title text baby :)", "And this shit right here is the note body","2019" ));
//        noteList.add(new Notes("title text baby :)", "And this shit right here is the note body","2019" ));
//        noteList.add(new Notes("title text baby :)", "And this shit right here is the note body","2019" ));
//        noteList.add(new Notes("title text baby :)", "And this shit right here is the note body","2019" ));
//        noteList.add(new Notes("title text baby :)", "And this shit right here is the note body","2019" ));
//        noteList.add(new Notes("title text baby :)", "And this shit right here is the note body","2019" ));
        recyclerView = findViewById(R.id.recycler_view_layout);
        layoutManager = new LinearLayoutManager(this);
        adapter = new NotesRecyclerAdapter(noteList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
//SimpleDateFormat sdm = new SimpleDateFormat("MM:dd:yyyy");
//Date today = new Date();
//String thisDate = sdm.format(today);

//        noteList = new ArrayList<>();
//        noteList.add(new Notes("title text baby :)", "And this shit right here is the note body" )); noteList.add(new Notes("this is a title", "And this shit right here is the note body"));
//        noteList.add(new Notes("Created by oriola emmanuel :)", "And this shit right here is the note body")); noteList.add(new Notes(date, "And this shit right here is the note body"));
//        noteList.add(new Notes("this is a title", "And this shit right here is the note body")); noteList.add(new Notes("this is a title", "And this shit right here is the note body"));
//        noteList.add(new Notes("this is a title", "And this shit right here is the note body")); noteList.add(new Notes("this is a title", "And this shit right here is the note body"));
//        noteList.add(new Notes("this is a title", "And this shit right here is the note body")); noteList.add(new Notes("this is a title", "And this shit right here is the note body"));
//        noteList.add(new Notes("this is a title", "And this shit right here is the note body")); noteList.add(new Notes("this is a title", "And this shit right here is the note body"));
//        noteList.add(new Notes("this is a title", "And this shit right here is the note body")); noteList.add(new Notes("this is a title", "And this shit right here is the note body"));
//        noteList.add(new Notes("this is a title", "And this shit right here is the note body")); noteList.add(new Notes("this is a title", "And this shit right here is the note body"));
//        noteList.add(new Notes("this is a title", "And this shit right here is the note body")); noteList.add(new Notes("this is a title", "And this shit right here is the note body"));
//        noteList.add(new Notes("this is a title", "And this shit right here is the note body")); noteList.add(new Notes("this is a title", "And this shit right here is the note body"));
//        noteList.add(new Notes("this is a title", "And this shit right here is the note body")); noteList.add(new Notes("this is a title", "And this shit right here is the note body"));
//        noteList.add(new Notes("this is a title", "And this shit
//        right here is the note body")); noteList.add(new Notes("this is a title", "And this shit right here is the note body"));
//        noteList.add(new Notes("this is a title", "And this shit right here is the note body")); noteList.add(new Notes("this is a title", "And this shit right here is the note body"));
//        noteList.add(new Notes("this is a title", "And this shit right here is the note body")); noteList.add(new Notes("this is a title", "And this shit right here is the note body"));
//        noteList.add(new Notes("this is a title", "And this shit right here is the note body"));
    //    displayArrayNotes();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,EdithNote.class);
                startActivity(intent);
            }
        });
    }
//    private void displayArrayNotes() {
//        notesArrayAdapter = new NoteAdapter(this,R.layout.notes_layout,noteList);
//        notesListView = findViewById(R.id.notesListView);
//        notesListView.setAdapter(notesArrayAdapter);
//        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Notes notes =(Notes)  notesListView.getItemAtPosition(position);
//                Intent intent = new Intent(MainActivity.this,EdithNote.class);
//                intent.putExtra("notes",notes);
//                notePosition = position;
//                intent.putExtra("p",notePosition);
//                //  Toast.makeText(MainActivity.this, String.valueOf(notePosition), Toast.LENGTH_SHORT).show();
//                startActivity(intent);
//            }
//        });
//        notesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(MainActivity.this,RecyclerViewAct.class);
//                startActivity(intent);
//                Toast.makeText(MainActivity.this,"Note Deleted Successfully", Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });
//    }
//public void saveNotesToMemory(){
//sharedPreferences = getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
//SharedPreferences.Editor editor = sharedPreferences.edit();
//Gson gson = new Gson();
//String jsonString = gson.toJson(noteList);
//editor.putString(ARRAY_OF_NOTES,jsonString);
//editor.apply();
//}
public void loadNotesFromMemory(){
        sharedPreferences = getSharedPreferences(MY_PREFERENCE,Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonString = sharedPreferences.getString(ARRAY_OF_NOTES,null);
        Type type = new TypeToken<ArrayList<Notes>>(){}.getType();
        noteList = gson.fromJson(jsonString,type);
        if(noteList == null){
            noteList = new ArrayList<>();
        }
}
}
