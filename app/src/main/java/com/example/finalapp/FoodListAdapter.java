package com.example.finalapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FoodListAdapter extends BaseAdapter {

    private ArrayList<Food> foodArrayList;
    private String pic_path;
    private Context context;
    private ArrayList<Bitmap> pics;

    private DatabaseReference ref=null;
    private String s = "profile.png";

    public FoodListAdapter(ArrayList<Food> foodArrayList, Context context, String pic_path)
    {
        this.foodArrayList = foodArrayList;
        this.context = context;
        this.pic_path=pic_path;
    }

    @Override
    public int getCount()
    {
        return this.foodArrayList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }


    public void deleteItem(int position) {
        foodArrayList.remove(foodArrayList.get(position));
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent)
    {

        ref= FirebaseDatabase.getInstance().getReference("Food");

        LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View show_food_search = layoutInflater.inflate(R.layout.foodrow, parent, false);
        TextView name= (TextView)show_food_search.findViewById(R.id.foodlist_name);
        TextView username= (TextView)show_food_search.findViewById(R.id.foodlist_des);

        name.setText(foodArrayList.get(position).getFoodName());
        username.setText(foodArrayList.get(position).getDescription());

        final CircleImageView imageView =show_food_search.findViewById(R.id.img_FoodList);

        String picname=this.foodArrayList.get(position).getFoodpic();

        String suffix=picname.substring(picname.lastIndexOf(".")+1);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(pic_path).child("foodPics").child(picname);

        try
        {
            final File localfile=File.createTempFile(picname,suffix);

            storageRef.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
                {
                    Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                    // set image based on selected text
                    imageView.setImageBitmap(bitmap);
                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

      /*  if (picname!= s)
        {
            Glide.clear(imageView);

            Glide.with(context)
                    .load(storageRef)
                    .into(imageView);
        }
        imageView.setImageBitmap(this.foodArrayList.get(position));*/





        // final String id=this.foodArrayList.get(position);


        return show_food_search;
    }
}
