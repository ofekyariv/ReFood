package com.example.finalapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static com.example.finalapp.SettingActivity.PICK_IMAGE;

public class ShowUserActivity extends AppCompatActivity {


    private static Uri selectedImage = null;
    private static String imgDecodableString = "profile.png";
    private static final String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private boolean isUser = true;
    private TextView mNameField, mPhoneField;
    private TextView memail, tv, gender, bday;
    private Button mBack, mShowFood, mLogout;
    private ImageView mProfileImage, gotosetting1;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mUserDatabase2;
    private StorageReference mypicref;
    private StorageReference mStorageRef;
    private final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private String birthday, name, phone, profileImageUrl, userSex, email;
    private Uri resultUri;
    private BatteryReceiver batteryReceiver;
    private final String picpath = "gs://fproject-bdf8a.appspot.com";
    private String id;
    private boolean defaultpic = true;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);

        tv = findViewById(R.id.userbattery);
        batteryReceiver = new BatteryReceiver(tv);

        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        mNameField = findViewById(R.id.username);
        mPhoneField = findViewById(R.id.userphone);
        memail = findViewById(R.id.useremail);
        gender = findViewById(R.id.usergender);
        bday = findViewById(R.id.userbday);
        gotosetting1 = findViewById(R.id.gotosetting);

        mShowFood = findViewById(R.id.showlistfood);

        mProfileImage = findViewById(R.id.userimg);

        mAuth = FirebaseAuth.getInstance();
        //userId = mAuth.getCurrentUser().getUid();

        //Bundle bundle=getIntent().getExtras();

        //   id = bundle.getString("U");

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase2 = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        mLogout = findViewById(R.id.logout2);

        // getUserInfo();
        getUserInfo2();

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logot();
                finish();
            }
        });

        mShowFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it2 = new Intent(ShowUserActivity.this, FoodUserListActivity.class);
                startActivity(it2);
            }
        });

        gotosetting1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShowUserActivity.this, "Setting", Toast.LENGTH_SHORT).show();
                Intent it4 = new Intent(ShowUserActivity.this, SettingActivity.class);
                Bundle b = new Bundle();
                b.putString("U", currentUserID);
                it4.putExtras(b);
                startActivity(it4);
            }
        });
        checkUserOrAdmin();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
    }

    //----------------------------------------------

    private void ShowImage(String picname) {
        // String picname=StaticValues.getUser().getPic();

        String suffix = picname.substring(picname.lastIndexOf(".") + 1);

        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(picpath).child("pics").child(picname);

        try {
            final File localfile = File.createTempFile(picname, suffix);

            storageReference.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                    mProfileImage.setImageBitmap(bitmap);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ShowUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && data != null) {
            // Get the Image from data
            selectedImage = data.getData();

            // Image View Shows the image - not from Camera
            mProfileImage.setImageURI(selectedImage);

            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            // Get the cursor
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            // String imgDecodableString contains the path of selected Image
            imgDecodableString = cursor.getString(columnIndex).substring(cursor.getString(columnIndex).lastIndexOf("/") + 1);

            defaultpic = false;
        }
    }

    private void getUserInfo2() {
        mUserDatabase2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        name = map.get("name").toString();
                        mNameField.setText(name);
                    }
                    if (map.get("phone") != null) {
                        phone = map.get("phone").toString();
                        mPhoneField.setText(phone);
                    }
                    if (map.get("gender") != null) {
                        userSex = map.get("gender").toString();
                        gender.setText(userSex);
                    }
                    if (map.get("birthDay") != null) {
                        birthday = map.get("birthDay").toString();
                        bday.setText(birthday);
                    }
                    if (map.get("pic") != null) {
                        profileImageUrl = map.get("pic").toString();
                        ShowImage(profileImageUrl);
                    }
                    if (map.get("email") != null) {
                        email = map.get("email").toString();
                        memail.setText(email);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void logot() {
        StaticValues.setUser(null);
        StaticValues.setUid("");
        StaticValues.setList(new ArrayList<User>());
        Intent it = new Intent(ShowUserActivity.this, MainActivity.class);
        startActivity(it);
        FirebaseAuth.getInstance().signOut();
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
                Intent it = new Intent(ShowUserActivity.this, ShowUserActivity.class);
                startActivity(it);
                return true;
            case R.id.item3:
                Toast.makeText(this, "Food List", Toast.LENGTH_SHORT).show();
                Intent it2 = new Intent(ShowUserActivity.this, FoodListActivity.class);
                startActivity(it2);
                return true;
            case R.id.item4:
                Toast.makeText(this, "Add Food", Toast.LENGTH_SHORT).show();
                Intent it3 = new Intent(ShowUserActivity.this, AddFood.class);
                startActivity(it3);
                return true;
            case R.id.item5:
                Toast.makeText(this, "Users List", Toast.LENGTH_SHORT).show();
                Intent it4 = new Intent(ShowUserActivity.this, UsersListActivity.class);
                startActivity(it4);
                return true;
            case R.id.item6:
                Toast.makeText(this, "Distribution List", Toast.LENGTH_SHORT).show();
                Intent it5 = new Intent(ShowUserActivity.this, DistributionListActivity.class);
                startActivity(it5);
                return true;
            case R.id.item7:
                Toast.makeText(this, "Add Distribution", Toast.LENGTH_SHORT).show();
                Intent it6 = new Intent(ShowUserActivity.this, AddDiscrabution.class);
                startActivity(it6);
                return true;
            case R.id.item8:
                Toast.makeText(this, "Add Collecting Point", Toast.LENGTH_SHORT).show();
                Intent it7 = new Intent(ShowUserActivity.this, AddCollectingPoint.class);
                startActivity(it7);
                return true;
            case R.id.item9:
                Toast.makeText(this, "Collecting Point List", Toast.LENGTH_SHORT).show();
                Intent it8 = new Intent(ShowUserActivity.this, CollectingPointListActivity.class);
                startActivity(it8);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
