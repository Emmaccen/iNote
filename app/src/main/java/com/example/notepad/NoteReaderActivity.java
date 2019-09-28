package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class NoteReaderActivity extends AppCompatActivity {
TextView title,textBody;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_reader);
        getSupportActionBar().setTitle("Reading Mode");
        title = findViewById(R.id.reader_mode_title);
        textBody = findViewById(R.id.reader_mode_text_body);
        Intent intent = getIntent();
        intent.getExtras();
        title.setText(intent.getStringExtra("title"));
        textBody.setText(intent.getStringExtra("text"));

    }
}
