package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DistributionUserListActivity extends AppCompatActivity {

    private boolean isUser = true;

    private ImageButton imageButton;
    private EditText name;
    private ListView listView;
    private Button btnsearch;
    private ArrayList<String> userslist = new ArrayList<String>();
    private ArrayList<User> userssearched = new ArrayList<User>();
    private ArrayList<String> uids= new ArrayList<>();
    private ArrayList<User> finallist = new ArrayList<User>();

    private String namestr;
    private User userclicked = new User();
    private Boolean isSearched = false;

    private static ArrayList<User> listUser = new ArrayList<User>();


    private FirebaseDatabase db=null;
    private FirebaseAuth auth = null;
    private DatabaseReference ref=null;
    private DatabaseReference refuser=null;


    private String picpath="gs://fproject-bdf8a.appspot.com";

    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distribution_user_list);

        checkUserOrAdmin();


        id = getIntent().getStringExtra("id");



        listView = findViewById(R.id.rvUsersDistribution);
        imageButton=findViewById(R.id.imageB);
        checkUserOrAdmin();

        db = FirebaseDatabase.getInstance();
        ref=db.getReference("Distribution").child(id).child("Distribution_Details");
        refuser=db.getReference("Users");


        // FirebaseDatabase dbusers = FirebaseDatabase.getInstance();
        // DatabaseReference dbrefusers = dbusers.getReference().child("Users");
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
                        }

                    }

                }

                final DistributionUserListAdapter adapter = new DistributionUserListAdapter(DistributionUserListActivity.this, finallist,picpath);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DistributionUserListActivity.this, DistributionHowCameList.class);
                Bundle b = new Bundle();
                b.putString("id", id);
                intent.putExtras(b);
                startActivity(intent);

            }
        });


       // final DistributionUserListAdapter adapter = new DistributionUserListAdapter(this, finallist,picpath);
        //listView.setAdapter(adapter);
    }
    private void checkUserOrAdmin() {
        MyFireBase_RTDB.checkIfAdmin(new MyFireBase_RTDB.CallBack_DataIsReady() {
            @Override
            public void getResult(Boolean data) {
                isUser = !data;
                refreshMenu();
                if(isUser){
                    imageButton.setVisibility(View.GONE);
                }
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(isUser)
        {
            inflater.inflate(R.menu.menu_user, menu);
        }
        else
        {
            inflater.inflate(R.menu.menu, menu);
        }

        return true;

    }


    public  void refreshMenu()
    {
        invalidateOptionsMenu();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item2:
                Toast.makeText(this, "My Profile", Toast.LENGTH_SHORT).show();
                Intent it =new Intent(DistributionUserListActivity.this, ShowUserActivity.class);
                startActivity(it);
                return true;
            case R.id.item3:
                Toast.makeText(this, "Food List", Toast.LENGTH_SHORT).show();
                Intent it2 =new Intent(DistributionUserListActivity.this,FoodListActivity.class);
                startActivity(it2);
                return true;
            case R.id.item4:
                Toast.makeText(this, "Add Food", Toast.LENGTH_SHORT).show();
                Intent it3 =new Intent(DistributionUserListActivity.this,AddFood.class);
                startActivity(it3);
                return true;
            case R.id.item5:
                Toast.makeText(this, "Users List", Toast.LENGTH_SHORT).show();
                Intent it4 =new Intent(DistributionUserListActivity.this,UsersListActivity.class);
                startActivity(it4);
                return true;
            case R.id.item6:
                Toast.makeText(this, "Distribution List", Toast.LENGTH_SHORT).show();
                Intent it5 =new Intent(DistributionUserListActivity.this,DistributionListActivity.class);
                startActivity(it5);
                return true;
            case R.id.item7:
                Toast.makeText(this, "Add Distribution", Toast.LENGTH_SHORT).show();
                Intent it6 =new Intent(DistributionUserListActivity.this,AddDiscrabution.class);
                startActivity(it6);
                return true;
            case R.id.item8:
                Toast.makeText(this, "Add Collecting Point", Toast.LENGTH_SHORT).show();
                Intent it7 =new Intent(DistributionUserListActivity.this,AddCollectingPoint.class);
                startActivity(it7);
                return true;
            case R.id.item9:
                Toast.makeText(this, "Collecting Point List", Toast.LENGTH_SHORT).show();
                Intent it8 =new Intent(DistributionUserListActivity.this,CollectingPointListActivity.class);
                startActivity(it8);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}