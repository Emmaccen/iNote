package com.echo.iNote;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class EdithNote extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    TextView textTitle;
    TextView textBody;
    private boolean isNewNote;
    private Notes note;
    Intent intent;
    Bundle bundle;
    String originalText;
    String originalTextBody;
    private boolean isCancelling;
    private boolean isDeleting;
    private boolean isSaveIcon;
    private int pos;
    private String emailTitle;
    private String emailTextBody;
    CircleImageView circleImageView;
    BottomNavigationView bottomNavigationView;
    Menu menu;
    String date;
    int one;
    int two;
    int three;
    int four;
    int five;
    private int selectedMenuItemID;
    private Toast toast;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!isCancelling && !isDeleting && !isSaveIcon){
            //saveNote();
//            finish();
            /* I disabled saving of note in on destroy because on resume in
            Homepage is called before onDestroy here so there's no way it'll invalidate adapters
            unless i wanna do some extra work, and i don't, silly ;( (angry face)*/
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!isCancelling && !isDeleting && !isSaveIcon){
           saveNote();


//            int orientation = getResources().getConfiguration().orientation;
//            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                Toast.makeText(this,"landScape Mode",Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this,"Portrait Mode",Toast.LENGTH_SHORT).show();
//            }
            /*I initially called saveNote() and finish() here to save note imidiately the user leaves
            * the screen, this help just incase the user never comes back to the page again
            * but i figured it would have been a better practice in on destroy instead*/


           finish();

        }else{

        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edith_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
        textTitle = findViewById(R.id.note_title);
        textBody = findViewById(R.id.text_body);
        circleImageView = findViewById(R.id.circleImageView);

        intent = getIntent();

        date = new SimpleDateFormat("dd:MM:yy", Locale.getDefault()).format(new Date());
        bundle = intent.getExtras();
        initializeColors();
        getIntentAndReadValues();
        displayReadValues();
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        menu = bottomNavigationView.getMenu();
        initializeCheckedCategory();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(isNewNote){
                    newNoteCategoryInitializer(id);
                }else{
                    switch (id){
                        case  R.id.id_menu_uncategorize:
                            setToastAndColor(getString(R.string.toast_message_uncategorized),one);
                            return true;
                        case R.id.id_menu_work:
                            setToastAndColor(getString(R.string.toast_message_work),two);
                            return true;
                        case R.id.id_menu_family:
                            setToastAndColor(getString(R.string.toast_message_family_affair),three);
                            return true;
                        case R.id.id_menu_study:
                            setToastAndColor(getString(R.string.toast_message_study),four);
                            return true;
                        case R.id.personal:
                            setToastAndColor(getString(R.string.toast_message_personal),five);
                            return true;
                        default:
                            setToastAndColor(getString(R.string.toast_message_uncategorized),one);
                            break;
                    }
                }

                return false;
            }
        });
    }
    public void newNoteCategoryInitializer(int selectedMenuItemID){
        switch (selectedMenuItemID){
            case R.id.id_menu_uncategorize:
                toastMessage(getString(R.string.toast_message_uncategorized));
                menu.findItem(selectedMenuItemID).setChecked(true);
                break;
            case R.id.id_menu_work:
                toastMessage(getString(R.string.toast_message_work));
                menu.findItem(selectedMenuItemID).setChecked(true);
                break;
            case R.id.id_menu_family:
                toastMessage(getString(R.string.toast_message_family_affair));
                menu.findItem(selectedMenuItemID).setChecked(true);
                break;
            case R.id.id_menu_study:
                toastMessage(getString(R.string.toast_message_study));
                menu.findItem(selectedMenuItemID).setChecked(true);
                break;
            case R.id.personal:
                toastMessage(getString(R.string.toast_message_personal));
                menu.findItem(selectedMenuItemID).setChecked(true);
                break;

        }
    }
    public void toastMessage(String toastMessage){
        toast = Toast.makeText(EdithNote.this,toastMessage, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    public void newNoteColorInitializer(int id){
        switch (id){
            case  R.id.id_menu_uncategorize:
                id = one;
                break;
            case  R.id.id_menu_work:
                id = two;
                break;
            case  R.id.id_menu_family:
                id = three;
                break;
            case  R.id.id_menu_study:
                id = four;
                break;
            case  R.id.personal:
                id = five;
                break;
            default :
                id = one;
                break;
        }
    }
    public void initializeCheckedCategory(){
        if(isNewNote){
            menu.findItem(R.id.id_menu_uncategorize).setChecked(true);
        }else {
            int color = note.getColors();
            if(color == one){
                menu.findItem(R.id.id_menu_uncategorize).setChecked(true);
            }else if (color == two){
                menu.findItem(R.id.id_menu_work).setChecked(true);
            }else if (color == three){
                menu.findItem(R.id.id_menu_family).setChecked(true);
            }else if (color == four){
                menu.findItem(R.id.id_menu_study).setChecked(true);
            }else if (color == five){
                menu.findItem(R.id.personal).setChecked(true);
            }
        }
    }
    public void setToastAndColor(String toastMessage, int color){
        toast = Toast.makeText(EdithNote.this,toastMessage, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
        note.setColors(color);
    }
    public void displayReadValues(){
        if((!isNewNote) && (bundle != null)){
            textTitle.setText(note.getTitle());
            textBody.setText(note.getTextBody());
            getOriginalNoteValues();
        }else if ((isNewNote) && (bundle == null)){
            Toast.makeText(this,getString(R.string.toast_new_note), Toast.LENGTH_SHORT).show();

        }
    }
    private void getIntentAndReadValues() {
        if(bundle != null) {
            note = bundle.getParcelable("notes");
        }else{
            isNewNote = note == null;
        }
    }

    public void getOriginalNoteValues(){
        originalText = textTitle.getText().toString();
        originalTextBody = textBody.getText().toString();
    }
    public void setOriginalNoteValues(){
        textTitle.setText(originalText);
        textBody.setText(originalTextBody);
    }
    public void sendEmail(){
        if(!isNewNote){
            note.setTitle(textTitle.getText().toString());
            note.setTextBody(textBody.getText().toString());
            emailTitle = note.getTitle();
            emailTextBody = note.getTextBody();
        }else{
            emailTitle = textTitle.getText().toString();
            emailTextBody = textBody.getText().toString();
        }
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT,emailTitle);
        intent.putExtra(Intent.EXTRA_TEXT,emailTextBody);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        } }

    public void initializeColors(){
        one = Color.parseColor("#607d8b");//blue grey uncategorized
        two = Color.parseColor("#7e57c2");//deep purple work
        three = Color.parseColor("#ef5350");//red family affair
        four = Color.parseColor("#42a5f5");//blue study
        five = Color.parseColor("#66bb6a");//green research
    }
    public void saveNote() {
        //String date = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date())

        /*Hard to imagine that i actually let someone talk me into coding a passworded note part
        * PS : i didn't think it was a big deal until i carelessly messed up my entire code and had to fix bugs for hours :( */
        if ((!isNewNote) && (bundle != null)) {
            //  dateTextView.setText(date);
            pos = bundle.getInt("p");
            if(textTitle.getText().toString().isEmpty() && textBody.getText().toString().isEmpty()){
                Toast.makeText(this, getString(R.string.toast_message_empty_note_not_saved), Toast.LENGTH_SHORT).show();
            }else if(textTitle.getText().toString().isEmpty()){
                if(HomePage.isProtectedNotesView){
                    HomePage.protectedNotesArray.set(pos,new Notes(getString(R.string.toast_message_note),textBody.getText().toString(),date,note.getColors()));
                    HomePage.protectedNotesArray.add(0, HomePage.protectedNotesArray.get(pos));
                    HomePage.protectedNotesArray.remove(pos);
                }else{
                    HomePage.noteList.set(pos,new Notes(getString(R.string.toast_message_note),textBody.getText().toString(),date,note.getColors()));
                    HomePage.noteList.add(0, HomePage.noteList.get(pos));
                    HomePage.noteList.remove(pos);
                }
                Toast.makeText(this,getString(R.string.toast_message_saved),Toast.LENGTH_SHORT).show();
            }else if(textBody.getText().toString().isEmpty()){
                Toast.makeText(this,getString(R.string.toast_message_empty_note_not_saved),Toast.LENGTH_SHORT).show();
            }else{
                if(HomePage.isProtectedNotesView){
                    HomePage.protectedNotesArray.set(pos,new Notes(textTitle.getText().toString(),textBody.getText().toString(),date,note.getColors()));
                    HomePage.protectedNotesArray.add(0, HomePage.protectedNotesArray.get(pos));
                    HomePage.protectedNotesArray.remove(pos);
                }else{
                    HomePage.noteList.set(pos,new Notes(textTitle.getText().toString(),textBody.getText().toString(),date,note.getColors()));
                    HomePage.noteList.add(0, HomePage.noteList.get(pos));
                    HomePage.noteList.remove(pos);
                }
                Toast.makeText(this,getString(R.string.toast_message_saved),Toast.LENGTH_SHORT).show();
            }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        }else if((isNewNote) && (bundle == null)){
            selectedMenuItemID = bottomNavigationView.getSelectedItemId();
//            newNoteColorInitializer(selectedMenuItemID); using this method somehow makes the variable scope out of reach... returns nothing
            // have no idea why, couldnt debug cuz of slow ass system, had to use ma damn head !
            switch (selectedMenuItemID){
                case  R.id.id_menu_uncategorize:
                    selectedMenuItemID = one;
                    break;
                case  R.id.id_menu_work:
                    selectedMenuItemID = two;
                    break;
                case  R.id.id_menu_family:
                    selectedMenuItemID = three;
                    break;
                case  R.id.id_menu_study:
                    selectedMenuItemID = four;
                    break;
                case  R.id.personal:
                    selectedMenuItemID = five;
                    break;
                default :
                    selectedMenuItemID = one;
                    break;
            }
            if(textTitle.getText().toString().isEmpty() && textBody.getText().toString().isEmpty()){
                Toast.makeText(this,getString(R.string.toast_message_empty_note_not_saved),Toast.LENGTH_SHORT).show();
            }else if(textTitle.getText().toString().isEmpty()){
                if(HomePage.isProtectedNotesView){
                    HomePage.protectedNotesArray.add(0,new Notes(getString(R.string.toast_message_note),textBody.getText().toString(),date, selectedMenuItemID));
                }else {
                    HomePage.noteList.add(0,new Notes(getString(R.string.toast_message_note),textBody.getText().toString(),date, selectedMenuItemID));
                }
                Toast.makeText(this,getString(R.string.toast_message_saved),Toast.LENGTH_SHORT).show();
            }else if(textBody.getText().toString().isEmpty()){
                Toast.makeText(this,R.string.toast_message_empty_note_not_saved,Toast.LENGTH_SHORT).show();
            }else{
                if(HomePage.isProtectedNotesView){
                    HomePage.protectedNotesArray.add(0,new Notes(textTitle.getText().toString(),textBody.getText().toString(),date,selectedMenuItemID));
                }else{
                    HomePage.noteList.add(0,new Notes(textTitle.getText().toString(),textBody.getText().toString(),date,selectedMenuItemID));
                }
                Toast.makeText(this,getString(R.string.toast_message_new_note_added),Toast.LENGTH_SHORT).show();
            }
        }
        // i don't care which conditions are met, save the right one anyway ;(
        saveNotesToMemory(); savePrivateNotesToMemory();
    }

    private void savePrivateNotesToMemory() {
        HomePage.protectedNotesPref = getSharedPreferences(HomePage.PRIVATE_NOTES_PREFERENCE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = HomePage.protectedNotesPref.edit();
        Gson json = new Gson();
        String privateArray = json.toJson(HomePage.protectedNotesArray);
        editor.putString(HomePage.ARRAY_OF_PRIVATE_NOTES_KEY,privateArray);
        editor.apply();
    }

    public void saveDeletedNoteToMemory(){
        HomePage.recycleBinPreference = getSharedPreferences(
                HomePage.MY_RECYCLE_PREFERENCE_KEY,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = HomePage.recycleBinPreference.edit();
        Gson jsonString = new Gson();
        String note = jsonString.toJson(HomePage.recycleBinArrayList);
        editor.putString(HomePage.ARRAY_OF_RECYCLED_NOTE_KEY,note);
        editor.apply();
    }
    public void saveNotesToMemory(){
        HomePage.sharedPreferences = getSharedPreferences(
                HomePage.MY_PREFERENCE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = HomePage.sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonString = gson.toJson(HomePage.noteList);
        editor.putString(HomePage.ARRAY_OF_NOTES,jsonString);
        editor.apply();
    }

    public void clearNote(){
        textTitle.setText("");
        textBody.setText("");
    }
    public void deleteNote(){
        if(isNewNote){
            Toast.makeText(this,getString(R.string.toast_message_unsaved_note), Toast.LENGTH_SHORT).show();
        }else{
            int deletionPosition = bundle.getInt("p");
            if(HomePage.showRecycleBinMenuOption && !HomePage.isProtectedNotesView){
                HomePage.recycleBinArrayList.add(0,HomePage.noteList.get(deletionPosition));
                saveDeletedNoteToMemory();
                //also remove form noteList and saveIt below
            }if(HomePage.isProtectedNotesView){
                isDeleting = true;
                HomePage.protectedNotesArray.remove(deletionPosition);
                Toast.makeText(this,getString(R.string.toast_message_deleted_successfully), Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"Deleted Private Notes Are Not Recyclable", Toast.LENGTH_LONG).show();
                savePrivateNotesToMemory();
                finish();
            }if(!HomePage.isProtectedNotesView){
                isDeleting = true;
                HomePage.noteList.remove(deletionPosition);
                Toast.makeText(this,getString(R.string.toast_message_deleted_successfully), Toast.LENGTH_SHORT).show();
                saveNotesToMemory();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        // invalidateOptionsMenu();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_email) {
            sendEmail();
            return true;
        }else if(id == R.id.action_cancel){
            isCancelling = true;
            finish();
        }else if(id == R.id.action_restore){
            if(isNewNote){
                Toast.makeText(this,getString(R.string.cannot_perform_restore), Toast.LENGTH_SHORT).show();
            }else{
                setOriginalNoteValues();
            }
        }else if(id == R.id.action_clear){
            clearNote();
        }else if(id == R.id.action_delete){
            deleteNote();
        }else if(id == R.id.action_reader_mode){
            String title;
            if(textTitle.getText().toString().isEmpty()){
                title = "Note";
            }else {
                title = textTitle.getText().toString();
            }
            startActivity(new Intent(this,NoteReaderActivity.class)
            .putExtra("title",title
            ).putExtra("text",textBody.getText().toString()));
        }
        else if(id == R.id.action_save){
            isSaveIcon = true;
            // isSaveIcon here is set to true to avoid
            //saving the note twice ( here in the menu and up the onDestroy)
            saveNote();
            finish();
        }else if (id == R.id.action_reminder){
            if(bundle != null){
                int noteIntentExtra = bundle.getInt("note");
                ReminderNotification.notify(this,textTitle.getText().toString(),
                        textBody.getText().toString(),0,intent);
            }else{
                Toast.makeText(this, getString(R.string.toast_message_unsaved_note), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.send_to_contact) {
            //check if we have permission to read contacts
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                ArrayList<Notes> notes = new ArrayList<>();
                String title = textTitle.getText().toString();
                String body = textBody.getText().toString();
                notes.add(new Notes(title, body, date, one));
                if (notes.get(0).getTextBody().isEmpty()) {
                    Toast.makeText(this, "Empty Note", Toast.LENGTH_SHORT).show();
                } else if (notes.get(0).getTitle().isEmpty() && !notes.get(0).getTextBody().isEmpty()) {
                    notes.set(0, new Notes("Note", body, date, one));
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra("message", notes.get(0));
                    intent.putExtra("messageType", "direct");
                    startActivity(intent);
                    saveNote();
                } else {
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra("message", notes.get(0));
                    intent.putExtra("messageType", "direct");
                    startActivity(intent);
                    saveNote();
                }

            } else {
                requestPermissions();
            }

        } else if (id == R.id.send_as_general) {
            shareMessage();
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareMessage() {
        String message = textTitle.getText().toString() +
                "\n\n" + textBody.getText().toString();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent);
        }
    }

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS)) {
            /*TODO
             *  this is invoked when user cancels your permission request and you need to explain why you needed the permission
             *  */
            permissionRationale();
        } else {
            //request permission
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CONTACTS}, HomePage.CONTACT_REQUEST_CODE);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == HomePage.CONTACT_REQUEST_CODE) {//sad face :{
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, ChatActivity.class));
            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                requestPermissions();
                //requestPermission again to enter the rationale part, (winks!)

                /*THE METHOD ABOVE WILL NEVER BE SHOWN ! CUZ YOU'D HAVE AUTO SAVED THE NOTE AND THE PAGE WOULD HAVE CLOSED ACCORDING
                 * TO YOUR CODE, SO USE YOUR BRAIN MAN (angry face ;(    stop wasting coding energy*/

            }
        }

    }
}