package com.example.iNote;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecycleBinAdapter extends RecyclerView.Adapter<RecycleBinAdapter.ViewHolder> {
Context context;
ArrayList<Notes> recyclableNotes;

RecycleBinAdapter(ArrayList<Notes> notes){
    this.recyclableNotes = notes;
}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
        return new ViewHolder(view);

    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notes note = recyclableNotes.get(holder.getAdapterPosition());
        holder.title.setText(note.getTitle());
        holder.textBody.setText(note.getTextBody());
        holder.date.setText(note.getDate());
        holder.category.setText("");
        if(note.getColors() == holder.one){
            holder.categoriesInitializer(holder.one,context.getString(R.string.toast_message_uncategorized));
        }else if (note.getColors() == holder.two){
            holder.categoriesInitializer(holder.two,context.getString(R.string.toast_message_work));
        }else if (note.getColors() == holder.three){
            holder.categoriesInitializer(holder.three,context.getString(R.string.toast_message_family_affair));
        }else if (note.getColors() == holder.four){
            holder.categoriesInitializer(holder.four,context.getString(R.string.toast_message_study));
        }else if (note.getColors() == holder.five){
            holder.categoriesInitializer(holder.five,context.getString(R.string.toast_message_personal));
        }
    }

    @Override
    public int getItemCount() {
        return recyclableNotes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            title = itemView.findViewById(R.id.recy_title_text);
            textBody = itemView.findViewById(R.id.recy_note_body);
            date = itemView.findViewById(R.id.recy_date);
            category = itemView.findViewById(R.id.recy_category_text);
            cardView = itemView.findViewById(R.id.recy_cardview);
            category = itemView.findViewById(R.id.recy_category_text);
            circleImageView = itemView.findViewById(R.id.circleImageView);
            if(HomePage.showCategoriesView){
                circleImageView.setVisibility(View.VISIBLE);
            }else{
                circleImageView.setVisibility(View.GONE);
            }
            frameLayout = itemView.findViewById(R.id.recy_frame_layout);

        }
        public void categoriesInitializer(int color, String categoryText){
            circleImageView.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_OVER);
            category.setTextColor(color);
            category.setText(categoryText);
        }
    }
}

