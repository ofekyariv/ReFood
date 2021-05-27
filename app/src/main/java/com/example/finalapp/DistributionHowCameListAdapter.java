package com.example.finalapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DistributionHowCameListAdapter extends BaseAdapter {

    Context contex;
    ArrayList<User> usersList;
    private String pic_path;
    ArrayList<Integer> selectedItems = new ArrayList<Integer>();

    public DistributionHowCameListAdapter(Context c, ArrayList<User> usersList, String pic_path)
    {
        this.contex = c;
        this.usersList = usersList;
        this.pic_path=pic_path;
    }


    @Override
    public int getCount() {
        return usersList.size();
    }

    @Override
    public Object getItem(int position) {
        return usersList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public ArrayList<Integer> getSelectedItems(){
        return selectedItems;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) this.contex.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View show_users_search = layoutInflater.inflate(R.layout.howcamerow, parent, false);
        TextView fullname= (TextView)show_users_search.findViewById(R.id.howcame_name);
        TextView username= (TextView)show_users_search.findViewById(R.id.howcame_des);
        CheckBox checkBox = show_users_search.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedItems.add(position);
                }else{
                    selectedItems.remove(position);
                }

            }
        });
        fullname.setText(usersList.get(position).getName());
        username.setText(usersList.get(position).getEmail());

        final CircleImageView imageView =show_users_search.findViewById(R.id.img_HowCame);

        String picname=this.usersList.get(position).getPic();

        String suffix=picname.substring(picname.lastIndexOf(".")+1);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReferenceFromUrl(pic_path).child("pics").child(picname);

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



        return show_users_search;
    }

  /*  private ArrayList<UserObj> userList = new ArrayList<>();

    private Context context;

    public UsersListAdapter(ArrayList<UserObj> userList, Context context)
    {
        this.userList = userList;
        this.context = context;

    }

    @Override
    public UsersListViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        UsersListViewHolders rcv = new UsersListViewHolders(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull UsersListViewHolders holder, int position) {

        holder.uidTv.setText(userList.get(position).getOUserId());
        holder.nameTv.setText(userList.get(position).getOName());
        if(!userList.get(position).getProfileImageUrl().equals("default")){
            Glide.with(context).load(userList.get(position).getProfileImageUrl()).into(holder.proimg);
        }

    }
    @Override
    public int getItemCount() {
        return this.userList.size();
    }*/


}

