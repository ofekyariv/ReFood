package com.example.finalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ShowCollectingPoint extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    private boolean isUser = true;
    private TextView start, end, place, date, des;
    private Button btnsave;
    private ImageView button;
    private DatabaseReference ref, DataRef;
    private String start1, end1, place1, date1, des1;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_collecting_point);

        btnsave = findViewById(R.id.adddishowCollect);
        end = findViewById(R.id.endTimeshowCollect);
        start = findViewById(R.id.startTimeshowCollect);
        place = findViewById(R.id.placeshowCollect);
        date = findViewById(R.id.disdateshowCollect);
        des = findViewById(R.id.desshowCollect);
        button = findViewById(R.id.updetCollect);
        checkUserOrAdmin();

        Bundle bundle = getIntent().getExtras();

        final String id = bundle.getString("idCollecting");
        // String end=bundle.getString("end");
        //String start =bundle.getString("start");
        //  String place =bundle.getString("place");

        //this.end.setText(end);
        //this.start.setText(start);

        ref = FirebaseDatabase.getInstance().getReference().child("CollectingPoint");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("CollectingPoint");
        databaseReference2 = FirebaseDatabase.getInstance().getReference("CollectingPoint").child(id);

        getUserInfo();

        checkUserOrAdmin();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it4 = new Intent(ShowCollectingPoint.this, CollectingPointUpdate.class);
                Bundle b = new Bundle();
                b.putString("id", id);
                it4.putExtras(b);
                startActivity(it4);

            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String id = databaseReference.getKey();

                databaseReference.child(id).child("CollectingPoint_Details").child(currentUserID).setValue(false);
                Intent i = new Intent(ShowCollectingPoint.this, CollectingPointUsersListActivity.class);
                Bundle b = new Bundle();
                b.putString("id", id);
                i.putExtras(b);
                startActivity(i);


            }
        });


    }


    private void getUserInfo() {
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("date") != null) {
                        date1 = map.get("date").toString();
                        date.setText(date1);
                    }
                    if (map.get("end") != null) {
                        end1 = map.get("end").toString();
                        end.setText(end1);
                    }
                    if (map.get("start") != null) {
                        start1 = map.get("start").toString();
                        start.setText(start1);

                    }
                    if (map.get("place") != null) {
                        place1 = map.get("place").toString();
                        place.setText(place1);

                    }
                    if (map.get("description") != null) {
                        des1 = map.get("description").toString();
                        des.setText(des1);

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void checkUserOrAdmin() {
        MyFireBase_RTDB.checkIfAdmin(new MyFireBase_RTDB.CallBack_DataIsReady() {
            @Override
            public void getResult(Boolean data) {
                isUser = !data;
                refreshMenu();
                if (isUser) {
                    button.setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflater = getMenuInflater();
        if (isUser)
            inflater.inflate(R.menu.menu_user, menu);
        else
            inflater.inflate(R.menu.menu, menu);

        return true;

    }


    public void refreshMenu() {
        invalidateOptionsMenu();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item2:
                Toast.makeText(this, "My Profile", Toast.LENGTH_SHORT).show();
                Intent it = new Intent(ShowCollectingPoint.this, ShowUserActivity.class);
                startActivity(it);
                return true;
            case R.id.item3:
                Toast.makeText(this, "Food List", Toast.LENGTH_SHORT).show();
                Intent it2 = new Intent(ShowCollectingPoint.this, FoodListActivity.class);
                startActivity(it2);
                return true;
            case R.id.item4:
                Toast.makeText(this, "Add Food", Toast.LENGTH_SHORT).show();
                Intent it3 = new Intent(ShowCollectingPoint.this, AddFood.class);
                startActivity(it3);
                return true;
            case R.id.item5:
                Toast.makeText(this, "Users List", Toast.LENGTH_SHORT).show();
                Intent it4 = new Intent(ShowCollectingPoint.this, UsersListActivity.class);
                startActivity(it4);
                return true;
            case R.id.item6:
                Toast.makeText(this, "Distribution List", Toast.LENGTH_SHORT).show();
                Intent it5 = new Intent(ShowCollectingPoint.this, DistributionListActivity.class);
                startActivity(it5);
                return true;
            case R.id.item7:
                Toast.makeText(this, "Add Distribution", Toast.LENGTH_SHORT).show();
                Intent it6 = new Intent(ShowCollectingPoint.this, AddDiscrabution.class);
                startActivity(it6);
                return true;
            case R.id.item8:
                Toast.makeText(this, "Add Collecting Point", Toast.LENGTH_SHORT).show();
                Intent it7 = new Intent(ShowCollectingPoint.this, AddCollectingPoint.class);
                startActivity(it7);
                return true;
            case R.id.item9:
                Toast.makeText(this, "Collecting Point List", Toast.LENGTH_SHORT).show();
                Intent it8 = new Intent(ShowCollectingPoint.this, CollectingPointListActivity.class);
                startActivity(it8);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}