//package com.echo.iNote;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.PorterDuff;
//import android.graphics.drawable.Drawable;
//import android.graphics.drawable.GradientDrawable;
//import android.graphics.drawable.shapes.Shape;
//import android.support.v4.content.ContextCompat;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.Switch;
//import android.widget.TextView;
//
//import org.w3c.dom.Text;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//import java.util.Random;
//
//public class NoteAdapter extends ArrayAdapter<Notes> {
//    int color;
//    String title;
//    String textBody;
//    Notes note;
//    Context context;
//    int resource;
//    String date;
//    public NoteAdapter(Context context, int resource,List<Notes> objects) {
//        super(context, resource, objects);
//        this.context = context;
//        this.resource = resource;
//    }
//
//    @Override
//    public View getView(int position,View convertView,ViewGroup parent) {
//        title = getItem(position).getTitle();
//        textBody = getItem(position).getTextBody();
//        date = getItem(position).getDate();
//        note = new Notes(title,textBody,date);
//        if(convertView == null){
//            convertView = LayoutInflater.from(getContext()).inflate(resource,parent, false);
//        }
////        String date = new SimpleDateFormat("MM:dd:yyy", Locale.getDefault()).format(new Date());
//        TextView dateView = convertView.findViewById(R.id.date);
//        ImageView imageColor = convertView.findViewById(R.id.category_color);
//        TextView categoryText = convertView.findViewById(R.id.category_text);
//        TextView textTitle = convertView.findViewById(R.id.title_text);
//        TextView textBod = convertView.findViewById(R.id.note_body);
//        textTitle.setText(title);
//        textBod.setText(textBody);
//        dateView.setText(date);
//
//        int one = Color.parseColor("#607d8b");//blue grey uncategorized
//        int two = Color.parseColor("#7e57c2");//deep purple work
//        int three = Color.parseColor("#ef5350");//red family affair
//        int four = Color.parseColor("#42a5f5");//blue study
//        int five = Color.parseColor("#ffeb3b");// yellow personal
//        int six = Color.parseColor("#66bb6a");//green research
////        int seven = Color.parseColor("#ffeb3b");
////        int eight = Color.parseColor("#43a047");
////        int ten = Color.parseColor("#FFFFFF");
////        int nine = Color.parseColor("#00796b");
//        int rand = (int) (Math.random() * 6);
//        for(int i = 0; i <1 ;i++){
//            if(rand == 1){
//                imageColor.getBackground().setColorFilter(one, PorterDuff.Mode.SRC_OVER);
//                categoryText.setTextColor(one);
//                categoryText.setText("Uncategorized");
//                // imageColor.setBackgroundColor(one);
//            }else if(rand == 2){
//                imageColor.getBackground().setColorFilter(two, PorterDuff.Mode.SRC_OVER);
//                categoryText.setTextColor(two);
//                categoryText.setText("Work");
//                //imageColor.setBackgroundColor(two);
//            }else if(rand == 3){
//                imageColor.getBackground().setColorFilter(three, PorterDuff.Mode.SRC_OVER);
//                categoryText.setTextColor(three);
//                categoryText.setText("Family Affair");
//             //   imageColor.setBackgroundColor(three);
//            }else if(rand == 4){
//                imageColor.getBackground().setColorFilter(four, PorterDuff.Mode.SRC_OVER);
//                categoryText.setTextColor(four);
//                categoryText.setText("Study");
//               // imageColor.setBackgroundColor(four);
//            }else if(rand == 5){
//                imageColor.getBackground().setColorFilter(five, PorterDuff.Mode.SRC_OVER);
//                categoryText.setTextColor(five);
//                categoryText.setText("personal");
////                imageColor.setBackgroundColor(five);
//            }else if(rand == 6){
//                imageColor.getBackground().setColorFilter(six, PorterDuff.Mode.SRC_OVER);
//                categoryText.setTextColor(six);
//                categoryText.setText("Research");
////                imageColor.setBackgroundColor(six);
////            }else if(rand == 7){
////                imageColor.getBackground().setColorFilter(seven, PorterDuff.Mode.SRC_OVER);
//////                imageColor.setBackgroundColor(seven);
////            }else if(rand == 8){
////                imageColor.getBackground().setColorFilter(eight, PorterDuff.Mode.SRC_OVER);
//////                imageColor.setBackgroundColor(eight);
////            }else if(rand == 9){
////                imageColor.getBackground().setColorFilter(nine, PorterDuff.Mode.SRC_OVER);
//////                imageColor.setBackgroundColor(nine);
////            }else if(rand == 10){
////                imageColor.getBackground().setColorFilter(ten, PorterDuff.Mode.SRC_OVER);
////                imageColor.setBackgroundColor(ten);
//            }else{
//               // imageColor.getBackground().setColorFilter(one, PorterDuff.Mode.SRC_OVER);
//                //imageColor.setBackgroundColor(seven);
//            }
//
//        }
//        return convertView;
//
//    }
//}
