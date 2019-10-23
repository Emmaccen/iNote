package com.echo.iNote;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TitleRecyclerAdapter extends RecyclerView.Adapter<TitleRecyclerAdapter.ViewHolders> {
    ArrayList<Notes> notesArrayList;
Context contex;
    TitleRecyclerAdapter(ArrayList<Notes> notesArrayList){
        this.notesArrayList = notesArrayList;
    }
    @NonNull
    @Override
    public ViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.title_layout,viewGroup,false);
        return  new ViewHolders(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolders viewHolders, int i) {
        Notes note = notesArrayList.get(viewHolders.getAdapterPosition());
        viewHolders.title.setText(note.getTitle());
        viewHolders.date.setText(note.getDate());
        viewHolders.category.setText("");
        viewHolders.itemPosition = i;
        if(note.getColors() == viewHolders.one){
            viewHolders.categoriesInitializer(viewHolders.one,contex.getString(R.string.toast_message_uncategorized));
        }else if (note.getColors() == viewHolders.two){
            viewHolders.categoriesInitializer(viewHolders.two,contex.getString(R.string.toast_message_work));
        }else if (note.getColors() == viewHolders.three){
            viewHolders.categoriesInitializer(viewHolders.three,contex.getString(R.string.toast_message_family_affair));
        }else if (note.getColors() == viewHolders.four){
            viewHolders.categoriesInitializer(viewHolders.four,contex.getString(R.string.toast_message_study));
        }else if (note.getColors() == viewHolders.five){
            viewHolders.categoriesInitializer(viewHolders.five,contex.getString(R.string.toast_message_personal));
        }
    }

    @Override
    public int getItemCount() {
        return notesArrayList.size();
    }

    public class ViewHolders extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView title;
        TextView date;
        TextView category;
        CircleImageView circleImageView;
        FrameLayout frameLayout;
        int itemPosition;
        int one = Color.parseColor("#607d8b");//blue grey uncategorized
        int two = Color.parseColor("#7e57c2");//deep purple work
        int three = Color.parseColor("#ef5350");//red family affair
        int four = Color.parseColor("#42a5f5");//blue study
        int five = Color.parseColor("#66bb6a");//green research
        public ViewHolders(@NonNull final View itemView) {
            super(itemView);
            contex = itemView.getContext();
            title = itemView.findViewById(R.id.t_recy_title_text);
            date = itemView.findViewById(R.id.t_recy_date);
            category = itemView.findViewById(R.id.t_recy_category_text);
            circleImageView = itemView.findViewById(R.id.t_circleImageView);
            frameLayout  = itemView.findViewById(R.id.t_recy_frame_layout);
            if(HomePage.showCategoriesView){
                circleImageView.setVisibility(View.VISIBLE);
        }else{
                circleImageView.setVisibility(View.GONE);
            }
            frameLayout.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos;
                    if(HomePage.isSearchView){
                        pos = HomePage.noteList.indexOf(notesArrayList.get(getAdapterPosition()));
                        openNote(itemView.getContext(),pos);
                    }else{
                        openNote(itemView.getContext(),getAdapterPosition());
                    }

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
            int pos;
            // we use pos as is here in the else since its not changing the ordering of the note
            //as does the sorted arraylist or search view...
            if(HomePage.isSearchView){
                pos = HomePage.noteList.indexOf(notesArrayList.get(getAdapterPosition()));
            }else{
                pos = getAdapterPosition();
            }
            menu.setHeaderTitle(contex.getString(R.string.select_an_option));
            menu.add(pos,100,0, v.getContext().getString(R.string.contextDelete));
            menu.add(pos,101,1, v.getContext().getString(R.string.contextEdit));
            menu.add(pos,102,2, v.getContext().getString(R.string.contextShare));

            menu.add(pos, 106, 3, v.getContext().getString(R.string.share_multi_choice));

            menu.add(pos,104,4,"Move To Private");
            menu.add(pos, 105, 5, "Send To Contact");
        }
    }
    public void openNote(Context view,int position){
        Intent intent = new Intent(view,EdithNote.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("notes",HomePage.noteList.get(position));
        intent.putExtra("p",position);
        view.getApplicationContext().startActivity(intent);
    }
}
