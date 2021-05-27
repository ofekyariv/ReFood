package com.example.finalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DistributionListActivity extends AppCompatActivity {

    private final ArrayList<Discarabution> lFood = new ArrayList<Discarabution>();
    private final ArrayList<String> uidd = new ArrayList<String>();
    private final Food foodclicked = new Food();
    private final Boolean isSearched = false;
    private final FirebaseAuth auth = null;
    private final DatabaseReference refUser = null;
    private final Discarabution userclicked = new Discarabution();
    private final ArrayList<discarabutionLObj> resultsMatches = new ArrayList<discarabutionLObj>();
    private EditText name;
    private ListView listView;
    private Button btnsearch;
    private String namestr;
    private FirebaseDatabase db = null;
    private DatabaseReference ref = null;
    private boolean isUser = true;
    private String idd;
    private ListAdapter mMatchesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discrabution_list);

        listView = findViewById(R.id.discrabutionList);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference("Distribution");

        Bundle bundle = getIntent().getExtras();

        //  mMatchesAdapter = new DiscarabutionAdapter(DistributionListActivity.this, (ArrayList<discarabutionLObj>) getDataSetMatches());
        //  listView.setAdapter( mMatchesAdapter);

        // getUserMatchId();


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String userId = dataSnapshot.getKey();
                    // FetchMatchInformation(data.getKey());
                    lFood.add(data.getValue(Discarabution.class));
                    uidd.add(data.getKey());
                }
                DiscarabutionAdapter mAdapter = new DiscarabutionAdapter(DistributionListActivity.this, lFood);
                listView.setAdapter(mAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long long1) {

                        String uiddd = uidd.get(position);
                        Intent intent = new Intent(view.getContext(), ShowDiscarabution.class);
                        Bundle b = new Bundle();
                        b.putString("id", uiddd);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        checkUserOrAdmin();
    }

    private void checkUserOrAdmin() {
        MyFireBase_RTDB.checkIfAdmin(new MyFireBase_RTDB.CallBack_DataIsReady() {
            @Override
            public void getResult(Boolean data) {
                isUser = !data;
                refreshMenu();
            }
        });
    }

    private void getUserMatchId() {

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot match : dataSnapshot.getChildren()) {
                        FetchMatchInformation(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void FetchMatchInformation(String key) {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userId = dataSnapshot.getKey();
                    String name = "";
                    String profileImageUrl = "";
                    if (dataSnapshot.child("place").getValue() != null) {
                        name = dataSnapshot.child("place").getValue().toString();
                    }
                    if (dataSnapshot.child("start").getValue() != null) {
                        profileImageUrl = dataSnapshot.child("start").getValue().toString();
                    }


                    discarabutionLObj obj = new discarabutionLObj(userId, name, profileImageUrl);
                    resultsMatches.add(obj);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private List<discarabutionLObj> getDataSetMatches() {
        return resultsMatches;
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
                Intent it = new Intent(DistributionListActivity.this, ShowUserActivity.class);
                startActivity(it);
                return true;
            case R.id.item3:
                Toast.makeText(this, "Food List", Toast.LENGTH_SHORT).show();
                Intent it2 = new Intent(DistributionListActivity.this, FoodListActivity.class);
                startActivity(it2);
                return true;
            case R.id.item4:
                Toast.makeText(this, "Add Food", Toast.LENGTH_SHORT).show();
                Intent it3 = new Intent(DistributionListActivity.this, AddFood.class);
                startActivity(it3);
                return true;
            case R.id.item5:
                Toast.makeText(this, "Users List", Toast.LENGTH_SHORT).show();
                Intent it4 = new Intent(DistributionListActivity.this, UsersListActivity.class);
                startActivity(it4);
                return true;
            case R.id.item6:
                Toast.makeText(this, "Distribution List", Toast.LENGTH_SHORT).show();
                Intent it5 = new Intent(DistributionListActivity.this, DistributionListActivity.class);
                startActivity(it5);
                return true;
            case R.id.item7:
                Toast.makeText(this, "Add Distribution", Toast.LENGTH_SHORT).show();
                Intent it6 = new Intent(DistributionListActivity.this, AddDiscrabution.class);
                startActivity(it6);
                return true;
            case R.id.item8:
                Toast.makeText(this, "Add Collecting Point", Toast.LENGTH_SHORT).show();
                Intent it7 = new Intent(DistributionListActivity.this, AddCollectingPoint.class);
                startActivity(it7);
                return true;
            case R.id.item9:
                Toast.makeText(this, "Collecting Point List", Toast.LENGTH_SHORT).show();
                Intent it8 = new Intent(DistributionListActivity.this, CollectingPointListActivity.class);
                startActivity(it8);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}