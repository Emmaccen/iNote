package com.example.iNote;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SortByCatagoriesAdapters extends RecyclerView.Adapter<SortByCatagoriesAdapters.ViewHolder>{
    ArrayList<Notes> notes;
    Context contex;
    SortByCatagoriesAdapters(ArrayList<Notes> notes){
        this.notes = notes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notes_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Notes note = notes.get(viewHolder.getAdapterPosition());
        viewHolder.title.setText(note.getTitle());
        viewHolder.textBody.setText(note.getTextBody());
        viewHolder.date.setText(note.getDate());
        viewHolder.category.setText("");
        if(note.getColors() == viewHolder.one){
            viewHolder.categoriesInitializer(viewHolder.one,contex.getString(R.string.toast_message_uncategorized));
        }else if (note.getColors() == viewHolder.two){
            viewHolder.categoriesInitializer(viewHolder.two,contex.getString(R.string.toast_message_work));
        }else if (note.getColors() == viewHolder.three){
            viewHolder.categoriesInitializer(viewHolder.three,contex.getString(R.string.toast_message_family_affair));
        }else if (note.getColors() == viewHolder.four){
            viewHolder.categoriesInitializer(viewHolder.four,contex.getString(R.string.toast_message_study));
        }else if (note.getColors() == viewHolder.five){
            viewHolder.categoriesInitializer(viewHolder.five,contex.getString(R.string.toast_message_personal));
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView title;
        TextView textBody;
        TextView date;
        TextView category;
        CardView cardView;
        CircleImageView circleImageView;
        FrameLayout frameLayout;
        int one = Color.parseColor("#607d8b");//blue grey uncategorized
        int two = Color.parseColor("#7e57c2");//deep purple work
        int three = Color.parseColor("#ef5350");//red family affair
        int four = Color.parseColor("#42a5f5");//blue study
        int five = Color.parseColor("#66bb6a");//green research
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            contex = itemView.getContext();
            title = itemView.findViewById(R.id.recy_title_text);
            textBody = itemView.findViewById(R.id.recy_note_body);
            date = itemView.findViewById(R.id.recy_date);
            category = itemView.findViewById(R.id.recy_category_text);
            circleImageView = itemView.findViewById(R.id.circleImageView);
            cardView = itemView.findViewById(R.id.recy_cardview);
            frameLayout = itemView.findViewById(R.id.recy_frame_layout);
            if(HomePage.showCategoriesView){
                circleImageView.setVisibility(View.VISIBLE);
            }else{
                circleImageView.setVisibility(View.GONE);
            }
            frameLayout.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // using HomePage.noteList.indexOf(notes.get(getAdapterPosition())) .... this helps me to convert the positon of
                    // the note in the new category list to the position of the of the original note in my noteList..
                    //this helps all modifications done in categories list to be made to my noteList as well ... okay?
                    openNote(itemView.getContext(),HomePage.noteList.indexOf(notes.get(getAdapterPosition())));
                    //Toast.makeText(v.getContext(), "position : " + HomePage.noteList.indexOf(notes.get(getAdapterPosition())), Toast.LENGTH_SHORT).show();
                }
            });
        }
        public void categoriesInitializer(int color, String categoryText){
            circleImageView.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_OVER);
            category.setTextColor(color);
            category.setText(categoryText);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            //HomePage.noteList.indexOf(notes.get(getAdapterPosition())) this converts the menu id to the
            //position of the real note since sort changes the ordering of the note
            //so that any modification we do like "delete" will be done to the original note and not the dynamic sorted note
            // am smart right? no need to say it ... i know i am :)
            menu.setHeaderTitle(contex.getString(R.string.select_an_option));
            menu.add(HomePage.noteList.indexOf(notes.get(getAdapterPosition())),100,0,v.getContext().getString(R.string.contextDelete));
            menu.add(HomePage.noteList.indexOf(notes.get(getAdapterPosition())),101,1,v.getContext().getString(R.string.contextEdit));
            menu.add(HomePage.noteList.indexOf(notes.get(getAdapterPosition())),102,2,v.getContext().getString(R.string.contextShare));


        }

        public void openNote(Context view,int position){
            Intent intent = new Intent(view,EdithNote.class);
            intent.putExtra("notes",HomePage.noteList.get(position));
            intent.putExtra("p",position);
            view.getApplicationContext().startActivity(intent);
        }
    }
}
