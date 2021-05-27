package com.example.finalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CollectingPointUpdate extends AppCompatActivity {

    private TextView start, end, date;

    private Button btnsave;

    private Button button;

    private EditText place, des;

    private DatabaseReference ref,DataRef;
    private String start1, end1, place1, date1, des1;

    private  int t1Hour, t1Minute,t2Hour,t2Minute;

    private ProgressDialog dialog;
    private ProgressDialog dialogpic;

    private ProgressDialog prg =null;

    private String currentUserID;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceUser;
    DatabaseReference databaseReference2;
    private DatabaseReference mUserDatabase;

    private String id;

    private DatePickerDialog pickerDialog;
    private Calendar c = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collecting_point_update);

        btnsave=findViewById(R.id.adddishowdCollectupdate);
        end =findViewById(R.id.endTimeshowCollectupdate);
        start =findViewById(R.id.startTimeshowCollectupdate);
        place=findViewById(R.id.placeshowCollectupdate);
        date=findViewById(R.id.disdateshowdCollectupdate);
        des=findViewById(R.id.disshowCollectupdate);

        //button=findViewById(R.id.dddishowdisupdate);

        Bundle bundle=getIntent().getExtras();

        id = bundle.getString("id");

        prg=new ProgressDialog(this);
        prg.setTitle("RegistrationActivity Data");
        prg.setMessage("Saving Data");
        prg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prg.setCancelable(false);
        // String end=bundle.getString("end");
        //String start =bundle.getString("start");
        //  String place =bundle.getString("place");

        //this.end.setText(end);
        //this.start.setText(start);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("CollectingPoint").child(id);

        ref = FirebaseDatabase.getInstance().getReference().child("CollectingPoint");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference = firebaseDatabase.getInstance().getReference("CollectingPoint");
        databaseReferenceUser = firebaseDatabase.getInstance().getReference("Users");

        databaseReference2 = firebaseDatabase.getInstance().getReference("CollectingPoint").child(id);

        getUserInfo();

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveUserInformation();
                Intent it4 =new Intent(CollectingPointUpdate.this,ShowCollectingPoint.class);
                Bundle b = new Bundle();
                b.putString("idCollecting", id);
                it4.putExtras(b);
                startActivity(it4);
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
                        place1= map.get("place").toString();
                        place.setText(place1);

                    }
                    if (map.get("description") != null) {
                        des1= map.get("description").toString();
                        des.setText(des1);

                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void StarTime(View view) {

        TimePickerDialog timePickerDialog=new TimePickerDialog(CollectingPointUpdate.this, new TimePickerDialog.OnTimeSetListener() {
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

        TimePickerDialog timePickerDialog=new TimePickerDialog(CollectingPointUpdate.this, new TimePickerDialog.OnTimeSetListener() {
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

        pickerDialog= new DatePickerDialog(CollectingPointUpdate.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                date.setText(dayOfMonth + "/" + month+1 + "/" + year);
            }
        },myyear,mymonth,myday);

        pickerDialog.show();
    }

    private void saveUserInformation() {
        des1 = des.getText().toString();
        place1 = place.getText().toString();
        end1 = end.getText().toString();
        start1 = start.getText().toString();
        date1 = date.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("description", des1);
        userInfo.put("place", place1);
        userInfo.put("end", end1);
        userInfo.put("start", start1);
        userInfo.put("date", date1);
        mUserDatabase.updateChildren(userInfo);


    }
}