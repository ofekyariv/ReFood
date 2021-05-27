package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

public class FoodUserListActivity extends AppCompatActivity {

    private boolean isUser = true;


    private EditText name;
    private ListView listView;
    private Button btnsearch;
    private ArrayList<Food> lFood = new ArrayList<Food>();
    private ArrayList<Food> lFood2 = new ArrayList<Food>();

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
        setContentView(R.layout.activity_food_user_list);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        listView = findViewById(R.id.listFood);

        db = FirebaseDatabase.getInstance();
        ref=db.getReference("Users").child(currentUserID).child("Food");
        refUser=db.getReference("Users");
        refFood=db.getReference("Food");


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
               FoodListAdapter mAdapter = new FoodListAdapter(lFood,FoodUserListActivity.this, picpath);
                listView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        refFood.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<Food> lFood = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    uids.add(data.getKey());
                    Food f=data.getValue(Food.class);
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
                FoodListAdapter mAdapter = new FoodListAdapter(lFood,FoodUserListActivity.this, picpath);
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
                Intent intent = new Intent(view.getContext(), ShowFoodActivity.class);
                Bundle b = new Bundle();
                b.putString("id", uiddd);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        checkUserOrAdmin();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

              //  final int witch_item= position;

                new AlertDialog.Builder(FoodUserListActivity.this)
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

    private void checkUserOrAdmin() {
        MyFireBase_RTDB.checkIfAdmin(new MyFireBase_RTDB.CallBack_DataIsReady() {
            @Override
            public void getResult(Boolean data) {
                isUser = !data;
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
                Intent it =new Intent(FoodUserListActivity.this, SettingActivity.class);
                startActivity(it);
                return true;
            case R.id.item3:
                Toast.makeText(this, "Food List", Toast.LENGTH_SHORT).show();
                Intent it2 =new Intent(FoodUserListActivity.this,FoodListActivity.class);
                startActivity(it2);
                return true;
            case R.id.item4:
                Toast.makeText(this, "Add Food", Toast.LENGTH_SHORT).show();
                Intent it3 =new Intent(FoodUserListActivity.this,AddFood.class);
                startActivity(it3);
                return true;
            case R.id.item5:
                Toast.makeText(this, "Users List", Toast.LENGTH_SHORT).show();
                Intent it4 =new Intent(FoodUserListActivity.this,UsersListActivity.class);
                startActivity(it4);
                return true;
            case R.id.item6:
                Toast.makeText(this, "Distribution List", Toast.LENGTH_SHORT).show();
                Intent it5 =new Intent(FoodUserListActivity.this,DistributionListActivity.class);
                startActivity(it5);
                return true;
            case R.id.item7:
                Toast.makeText(this, "Add Distribution", Toast.LENGTH_SHORT).show();
                Intent it6 =new Intent(FoodUserListActivity.this,AddDiscrabution.class);
                startActivity(it6);
                return true;
            case R.id.item8:
                Toast.makeText(this, "Add Collecting Point", Toast.LENGTH_SHORT).show();
                Intent it7 =new Intent(FoodUserListActivity.this,AddCollectingPoint.class);
                startActivity(it7);
                return true;
            case R.id.item9:
                Toast.makeText(this, "Collecting Point List", Toast.LENGTH_SHORT).show();
                Intent it8 =new Intent(FoodUserListActivity.this,CollectingPointListActivity.class);
                startActivity(it8);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}