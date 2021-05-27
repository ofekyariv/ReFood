package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DistributionHowCameList extends AppCompatActivity {


    private CheckBox checkBox;
    private Button button;
    private EditText name;
    private ListView listView;
    private Button btnsearch;
    private ArrayList<String> userslist = new ArrayList<String>();
    private ArrayList<User> userssearched = new ArrayList<User>();
    private ArrayList<String> uids2 = new ArrayList<String>();
    private ArrayList<String> uids= new ArrayList<>();
    private ArrayList<User> finallist = new ArrayList<User>();
    private ArrayList<String> finaluids= new ArrayList<>();
    private String namestr;
    private User userclicked = new User();
    private Boolean isSearched = false;

    private static ArrayList<User> listUser = new ArrayList<User>();


    private FirebaseDatabase db=null;
    private FirebaseAuth auth = null;
    private DatabaseReference ref=null;
    private DatabaseReference refuser=null;
     DistributionHowCameListAdapter adapter;

    private String picpath="gs://fproject-bdf8a.appspot.com";

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distribution_how_came_list);

        listView = findViewById(R.id.HowCameList);
        button=findViewById(R.id.savehowcame);

        id = getIntent().getStringExtra("id");

        db = FirebaseDatabase.getInstance();
        ref=db.getReference("Distribution").child(id).child("Distribution_Details");
        refuser=db.getReference("Users");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    String uu = data.getKey();
                    userslist.add(uu);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        refuser.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                for(DataSnapshot singleRow:dataSnapshot.getChildren())
                {
                    uids.add(singleRow.getKey());
                    User uu = singleRow.getValue(User.class);
                    userssearched.add(uu);
                }

                for(int i = 0; i < uids.size(); i++)
                {
                    for (int j = 0; j < userslist.size(); j++) {

                        if(uids.get(i).equals(userslist.get(j))){
                            finallist.add(userssearched.get(i));
                            finaluids.add(uids.get(i));
                        }
                    }
                }

             adapter = new DistributionHowCameListAdapter(DistributionHowCameList.this, finallist, picpath);
            listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }

        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int pos:adapter.getSelectedItems()) {
                    ref.child(String.valueOf(finaluids.get(pos))).setValue(true);
                }
                finish();
            }
        });
    }
}