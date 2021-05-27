package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CollectingPointUsersListActivity extends AppCompatActivity {

    private boolean isUser = true;


    private EditText name;
    private ListView listView;
    private Button btnsearch;
    private ArrayList<String> userslist = new ArrayList<String>();
    private ArrayList<User> userssearched = new ArrayList<User>();
    private ArrayList<String> uids= new ArrayList<>();
    private ArrayList<String> uidsfinal= new ArrayList<>();
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
        setContentView(R.layout.activity_collecting_point_users_list);


        id = getIntent().getStringExtra("id");



        listView = findViewById(R.id.collectList);


        db = FirebaseDatabase.getInstance();
        ref=db.getReference("CollectingPoint").child(id).child("CollectingPoint_Details");
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

                final CollectingPointUsersListAdapter adapter = new CollectingPointUsersListAdapter(CollectingPointUsersListActivity.this, finallist,picpath);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });






        checkUserOrAdmin();
    }

    private void checkUserOrAdmin() {
        MyFireBase_RTDB.checkIfAdmin(new MyFireBase_RTDB.CallBack_DataIsReady() {
            @Override
            public void getResult(Boolean data) {
                if(data==false)
                {
                    isUser = true;

                }

                else
                {
                    isUser = false;


                }
                refreshMenu();
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflater = getMenuInflater();
        if(isUser)
            inflater.inflate(R.menu.menu_user, menu);
        else
            inflater.inflate(R.menu.menu, menu);

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
                Intent it =new Intent(CollectingPointUsersListActivity.this, ShowUserActivity.class);
                startActivity(it);
                return true;
            case R.id.item3:
                Toast.makeText(this, "Food List", Toast.LENGTH_SHORT).show();
                Intent it2 =new Intent(CollectingPointUsersListActivity.this,FoodListActivity.class);
                startActivity(it2);
                return true;
            case R.id.item4:
                Toast.makeText(this, "Add Food", Toast.LENGTH_SHORT).show();
                Intent it3 =new Intent(CollectingPointUsersListActivity.this,AddFood.class);
                startActivity(it3);
                return true;
            case R.id.item5:
                Toast.makeText(this, "Users List", Toast.LENGTH_SHORT).show();
                Intent it4 =new Intent(CollectingPointUsersListActivity.this,UsersListActivity.class);
                startActivity(it4);
                return true;
            case R.id.item6:
                Toast.makeText(this, "Distribution List", Toast.LENGTH_SHORT).show();
                Intent it5 =new Intent(CollectingPointUsersListActivity.this,DistributionListActivity.class);
                startActivity(it5);
                return true;
            case R.id.item7:
                Toast.makeText(this, "Add Distribution", Toast.LENGTH_SHORT).show();
                Intent it6 =new Intent(CollectingPointUsersListActivity.this,AddDiscrabution.class);
                startActivity(it6);
                return true;
            case R.id.item8:
                Toast.makeText(this, "Add Collecting Point", Toast.LENGTH_SHORT).show();
                Intent it7 =new Intent(CollectingPointUsersListActivity.this,AddCollectingPoint.class);
                startActivity(it7);
                return true;
            case R.id.item9:
                Toast.makeText(this, "Collecting Point List", Toast.LENGTH_SHORT).show();
                Intent it8 =new Intent(CollectingPointUsersListActivity.this,CollectingPointListActivity.class);
                startActivity(it8);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}