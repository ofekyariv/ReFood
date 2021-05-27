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
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FoodListActivity extends AppCompatActivity {

    private boolean isUser = true;

    private EditText name;
    private ListView listView;
    private Button btnsearch;
    private ArrayList<Food> lFood = new ArrayList<Food>();
    private ArrayList<String> foodid = new ArrayList<String>();
    private ArrayList<Food> userssearched = new ArrayList<Food>();
    private ArrayList<String> uids= new ArrayList<>();

    private String namestr;
    private Food foodclicked = new Food();
    private Boolean isSearched = false;
    private FirebaseDatabase db=null;
    private FirebaseAuth auth = null;
    private DatabaseReference ref=null;
    private DatabaseReference refUser=null;

    private String picpath="gs://fproject-bdf8a.appspot.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        name = findViewById(R.id.editserchfood);
        btnsearch = findViewById(R.id.btnserchfood);

        listView = findViewById(R.id.listFood);

        db = FirebaseDatabase.getInstance();
        ref=db.getReference("Food");
        refUser=db.getReference("Users");

        // FirebaseDatabase dbusers = FirebaseDatabase.getInstance();
        // DatabaseReference dbrefusers = dbusers.getReference().child("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    lFood.add(data.getValue(Food.class));
                    foodid.add(data.getKey());
                }
                FoodListAdapter mAdapter = new FoodListAdapter(lFood,FoodListActivity.this, picpath);
                listView.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isSearched = true;
                namestr = name.getText().toString();
                if (namestr.equals(""))
                    Toast.makeText(FoodListActivity.this, "Choose name of food", Toast.LENGTH_SHORT).show();
                else {
                    for (int i = 0; i < lFood.size(); i++) {
                        String fname = lFood.get(i).getFoodName();
                        if (fname.equals(namestr) )
                        {
                            userssearched.add(lFood.get(i));
                            uids.add(foodid.get(i));
                        }
                    }

                    if (userssearched.size() == 0)
                        Toast.makeText(FoodListActivity.this, "There are no foods to find", Toast.LENGTH_SHORT).show();
                    else {
                        FoodListAdapter adapter3 = new FoodListAdapter(userssearched,FoodListActivity.this, picpath);
                        listView.setAdapter(null);
                        listView.setAdapter(adapter3);
                    }
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               // String uiddd=foodid.get(position).toString();

                String uiddd;
                String uidd;
                Intent it=new Intent(FoodListActivity.this, ShowFoodActivity.class);
                if (isSearched){
                    uiddd = uids.get(position).toString();
                    //userclicked = userssearched.get(position);
                    //it.putExtra("userclicked", (Parcelable) userclicked);
                    Bundle b = new Bundle();
                    b.putString("id", uiddd);
                    it.putExtras(b);
                    startActivity(it);
                }

                else
                {
                    uidd = foodid.get(position).toString();

                    // userclicked = userslist.get(position);
                    //it.putExtra("userclicked", (Parcelable) userclicked);
                    Bundle b = new Bundle();
                    b.putString("id", uidd);
                    it.putExtras(b);
                }
                startActivity(it);
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
                Intent it =new Intent(FoodListActivity.this, SettingActivity.class);
                startActivity(it);
                return true;
            case R.id.item3:
                Toast.makeText(this, "Food List", Toast.LENGTH_SHORT).show();
                Intent it2 =new Intent(FoodListActivity.this,FoodListActivity.class);
                startActivity(it2);
                return true;
            case R.id.item4:
                Toast.makeText(this, "Add Food", Toast.LENGTH_SHORT).show();
                Intent it3 =new Intent(FoodListActivity.this,AddFood.class);
                startActivity(it3);
                return true;
            case R.id.item5:
                Toast.makeText(this, "Users List", Toast.LENGTH_SHORT).show();
                Intent it4 =new Intent(FoodListActivity.this,UsersListActivity.class);
                startActivity(it4);
                return true;
            case R.id.item6:
                Toast.makeText(this, "Distribution List", Toast.LENGTH_SHORT).show();
                Intent it5 =new Intent(FoodListActivity.this,DistributionListActivity.class);
                startActivity(it5);
                return true;
            case R.id.item7:
                Toast.makeText(this, "Add Distribution", Toast.LENGTH_SHORT).show();
                Intent it6 =new Intent(FoodListActivity.this,AddDiscrabution.class);
                startActivity(it6);
                return true;
            case R.id.item8:
                Toast.makeText(this, "Add Collecting Point", Toast.LENGTH_SHORT).show();
                Intent it7 =new Intent(FoodListActivity.this,AddCollectingPoint.class);
                startActivity(it7);
                return true;
            case R.id.item9:
                Toast.makeText(this, "Collecting Point List", Toast.LENGTH_SHORT).show();
                Intent it8 =new Intent(FoodListActivity.this,CollectingPointListActivity.class);
                startActivity(it8);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}