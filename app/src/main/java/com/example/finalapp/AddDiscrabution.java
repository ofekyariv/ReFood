package com.example.finalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddDiscrabution extends AppCompatActivity {

    private boolean isUser = true;

    private TextView  start, end, date;

    private EditText place, disdisupdate1;

    private Button btnsave;

    private DatePickerDialog pickerDialog;
    private Calendar c = null;

    private  int t1Hour, t1Minute,t2Hour,t2Minute;

    private ProgressDialog dialog;
    private ProgressDialog dialogpic;

    private FirebaseDatabase db=null;
    private FirebaseAuth auth = null;
    private DatabaseReference ref=null;

    private ProgressDialog prg =null;

    private String currentUserID;
    private DatabaseReference mDatabaseUser;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_discrabution);
        checkUserOrAdmin();
        db = FirebaseDatabase.getInstance();
        ref=db.getReference("Distribution");

        databaseReference = firebaseDatabase.getInstance().getReference().child("Distribution");
        databaseReferenceUser = firebaseDatabase.getInstance().getReference().child("Users");

        prg=new ProgressDialog(this);
        prg.setTitle("RegistrationActivity Data");
        prg.setMessage("Saving Data");
        prg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prg.setCancelable(false);

        btnsave=findViewById(R.id.adddi);
        end =findViewById(R.id.endTime);
        start =findViewById(R.id.startTimee);
        place=findViewById(R.id.place);
        date=findViewById(R.id.disdate);
        disdisupdate1=findViewById(R.id.disdisupdate);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        c = Calendar.getInstance();


        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String endTime = end.getText().toString();
                String startTime = start.getText().toString();
                String place1 = place.getText().toString();
                String date1 = date.getText().toString();
                String des = disdisupdate1.getText().toString();

                String id = databaseReference.push().getKey();

                Discarabution discarabution = new Discarabution(endTime, startTime, place1,date1, des);
                databaseReference.child(id).setValue(discarabution);
                //addToUser();

                Intent i = new Intent(AddDiscrabution.this, DistributionListActivity.class);
                Bundle b = new Bundle();
                b.putString("id", id);
                i.putExtras(b);
                AddDiscrabution.this.startActivity(i);

            }
        });
    }


    public void StarTime(View view) {

        TimePickerDialog timePickerDialog=new TimePickerDialog(AddDiscrabution.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                t1Hour=hourOfDay;
                t1Minute=minute;
                Calendar calendar=Calendar.getInstance();
                calendar.set(0,0,0,t1Hour,t1Minute);
                start.setText(t1Hour + ":" + t1Minute);

            }
        },12,0,false);
        timePickerDialog.updateTime(t1Hour,t1Minute);
        timePickerDialog.show();
    }

    public void EndTime(View view) {

        TimePickerDialog timePickerDialog=new TimePickerDialog(AddDiscrabution.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                t2Hour=hourOfDay;
                t2Minute=minute;
                Calendar calendar=Calendar.getInstance();
                calendar.set(0,0,0,t2Hour,t2Minute);
                end.setText(t2Hour + ":" + t2Minute);

            }
        },12,0,false);
        timePickerDialog.updateTime(t2Hour,t2Minute);
        timePickerDialog.show();
    }

    public void ShowDate(View view) {

        final int myday=c.get(Calendar.DAY_OF_MONTH);
        int mymonth=c.get(Calendar.MONTH);
        int myyear=c.get(Calendar.YEAR);

        pickerDialog= new DatePickerDialog(AddDiscrabution.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                date.setText(dayOfMonth + "/" + month+1 + "/" + year);
            }
        },myyear,mymonth,myday);

        pickerDialog.show();
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
                Intent it =new Intent(AddDiscrabution.this, ShowUserActivity.class);
                startActivity(it);
                return true;
            case R.id.item3:
                Toast.makeText(this, "Food List", Toast.LENGTH_SHORT).show();
                Intent it2 =new Intent(AddDiscrabution.this,FoodListActivity.class);
                startActivity(it2);
                return true;
            case R.id.item4:
                Toast.makeText(this, "Add Food", Toast.LENGTH_SHORT).show();
                Intent it3 =new Intent(AddDiscrabution.this,AddFood.class);
                startActivity(it3);
                return true;
            case R.id.item5:
                Toast.makeText(this, "Users List", Toast.LENGTH_SHORT).show();
                Intent it4 =new Intent(AddDiscrabution.this,UsersListActivity.class);
                startActivity(it4);
                return true;
            case R.id.item6:
                Toast.makeText(this, "Distribution List", Toast.LENGTH_SHORT).show();
                Intent it5 =new Intent(AddDiscrabution.this,DistributionListActivity.class);
                startActivity(it5);
                return true;
            case R.id.item7:
                Toast.makeText(this, "Add Distribution", Toast.LENGTH_SHORT).show();
                Intent it6 =new Intent(AddDiscrabution.this,AddDiscrabution.class);
                startActivity(it6);
                return true;
            case R.id.item8:
                Toast.makeText(this, "Add Collecting Point", Toast.LENGTH_SHORT).show();
                Intent it7 =new Intent(AddDiscrabution.this,AddCollectingPoint.class);
                startActivity(it7);
                return true;
            case R.id.item9:
                Toast.makeText(this, "Collecting Point List", Toast.LENGTH_SHORT).show();
                Intent it8 =new Intent(AddDiscrabution.this,CollectingPointListActivity.class);
                startActivity(it8);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}