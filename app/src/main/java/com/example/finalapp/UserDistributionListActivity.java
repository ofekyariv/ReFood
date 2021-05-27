package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserDistributionListActivity extends AppCompatActivity {

    private EditText name;
    private ListView listView;
    private Button btnsearch;
    private ArrayList<Discarabution> lFood = new ArrayList<Discarabution>();
    private ArrayList<Discarabution> lFood2 = new ArrayList<Discarabution>();

    private ArrayList<String> foodid = new ArrayList<String>();
    private ArrayList<String> uids= new ArrayList<>();

    private String namestr;
    private Food foodclicked = new Food();
    private Boolean isSearched = false;
    private FirebaseDatabase db=null;
    private FirebaseAuth auth = null;
    private DatabaseReference ref=null;
    private DatabaseReference refUser=null;
    private DatabaseReference refFood=null;


    private String currentUserID;

    private String picpath="gs://fproject-bdf8a.appspot.com";

    private FoodListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_distribution_list);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        listView = findViewById(R.id.listFood);

        db = FirebaseDatabase.getInstance();
        ref=db.getReference("Users").child(currentUserID).child("Distribution");
        refUser=db.getReference("Users");
        refFood=db.getReference("Distribution");


        // FirebaseDatabase dbusers = FirebaseDatabase.getInstance();
        // DatabaseReference dbrefusers = dbusers.getReference().child("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // ArrayList<Food> lFood = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    // lFood.add(data.getValue(Food.class));
                    foodid.add(data.getKey());

                }
               // DiscarabutionAdapter mAdapter = new DiscarabutionAdapter(UserDistributionListActivity.this,lFood);
                //listView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        refFood.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<Discarabution> lFood = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    uids.add(data.getKey());

                    Discarabution f=data.getValue(Discarabution.class);
                    lFood2.add(f);


                }

                for(int i = 0; i < uids.size(); i++)
                {
                    for (int j = 0; j < foodid.size(); j++) {

                        if(uids.get(i).equals(foodid.get(j))){

                            lFood.add(lFood2.get(i));
                        }

                    }

                }
                DiscarabutionAdapter mAdapter = new DiscarabutionAdapter(UserDistributionListActivity.this,lFood);
                listView.setAdapter(mAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String uiddd=foodid.get(position).toString();

                // String id= lFood.get(position).getId().toString();

                       /* Discarabution discarabution=lFood.get(position);
                        String end= discarabution.getEnd();
                        String start= discarabution.getStart();
                        String place= discarabution.getPlace();
                        String date= discarabution.getDate();

                        Intent intent = new Intent(view.getContext(), ShowDiscarabution.class);
                        Bundle b = new Bundle();
                        b.putString("id", idd);
                        b.putString("end",end);
                        b.putString("start",start);
                        b.putString("place",place);
                        b.putString("date",date);
                        intent.putExtras(b);
                        view.getContext().startActivity(intent);*/

                Intent intent = new Intent(view.getContext(), ShowDiscarabution.class);
                Bundle b = new Bundle();
                b.putString("id", uiddd);
                intent.putExtras(b);
                startActivity(intent);


            }

        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                //  final int witch_item= position;

                new AlertDialog.Builder(UserDistributionListActivity.this)
                        .setTitle("AreYou Sure?")
                        .setMessage("Do you want to delete this item")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAdapter.deleteItem(position);
                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No",null)
                        .show();

                ref.child(foodid.get(position)).removeValue();
                refFood.child(foodid.get(position)).removeValue();

                return true;
            }
        });


    }
}