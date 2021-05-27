package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import java.util.Map;

import static com.example.finalapp.SettingActivity.PICK_IMAGE;

public class ShowFoodActivity extends AppCompatActivity {

    private boolean isUser = true;

    private DatabaseReference ref,DataRef;

    private TextView des;
    private TextView name, expdate, num, address;

    private ImageView mProfileImage;

    private String des2, name2,expdate2, num2, profileImageUrl,address2;

    private String currentUserID;

    private static final int REQUEST_CALL = 1;
    private Button mEditTextNumber, navigate;

    private String id, phone, userid;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceUser;
    DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
    private StorageReference mStorageRef;

    private String picpath="gs://fproject-bdf8a.appspot.com";

    private static Uri selectedImage=null;

    private static String imgDecodableString="profile.png";

    private boolean defaultpic=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_food);

        des=findViewById(R.id.showeditdescription);
        name=findViewById(R.id.showeditFoodname);
        expdate=findViewById(R.id.showtxtexpdate);
        num=findViewById(R.id.shownum);
        address=findViewById(R.id.showfoodadress);

        mEditTextNumber = findViewById(R.id.mEditTextNumber);
        navigate = findViewById(R.id.navigate);

        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a Uri from an intent string. Use the result to create an Intent.
                Uri googleMapsIntentUri = Uri.parse("google.navigation:q=" + address.getText().toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, (googleMapsIntentUri));
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        databaseReferenceUser = firebaseDatabase.getInstance().getReference().child("Users");
        databaseReference= firebaseDatabase.getInstance().getReference().child("Food");

        mStorageRef = FirebaseStorage.getInstance().getReference();

        id = getIntent().getStringExtra("id");

        ref = FirebaseDatabase.getInstance().getReference("Food");
        DataRef= FirebaseDatabase.getInstance().getReference("Food").child(id);

     //   userid = ref.child(id).child("foodName").toString();

        mProfileImage = (ImageView) findViewById(R.id.showimgcrl);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ref.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    userid =dataSnapshot.child("id").getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getUserInfo();

        mEditTextNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });

        checkUserOrAdmin();

    }

    private void makePhoneCall() {
         findPhoneUser(userid);
    }

    public String findPhoneUser (String idUser) {
        DatabaseReference refUsers = FirebaseDatabase.getInstance().getReference("Users");

        refUsers.child(idUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String phoneOfUser = dataSnapshot.child("phone").getValue().toString();

                if (phoneOfUser.trim().length() > 0) {
                    if (ContextCompat.checkSelfPermission(ShowFoodActivity.this,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ShowFoodActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                    } else {
                        String dial = "tel:" + phoneOfUser;
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                    }
                } else {
                    Toast.makeText(ShowFoodActivity.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return idUser;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void ShowImage(String picname)
    {
        // String picname=StaticValues.getUser().getPic();

        String suffix=picname.substring(picname.lastIndexOf(".")+1);

        StorageReference storageReference=firebaseStorage.getReferenceFromUrl(picpath).child("foodPics").child(picname);

        try
        {
            final File localfile=File.createTempFile(picname,suffix);

            storageReference.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
                {
                    Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                    mProfileImage.setImageBitmap(bitmap);

                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(ShowFoodActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE && data!=null)
        {
            // Get the Image from data
            selectedImage=data.getData();

            // Image View Shows the image - not from Camera
            mProfileImage.setImageURI(selectedImage);

            String[] filePathColumn={ MediaStore.Images.Media.DATA };

            // Get the cursor
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            // String imgDecodableString contains the path of selected Image
            imgDecodableString = cursor.getString(columnIndex).substring(cursor.getString(columnIndex).lastIndexOf("/")+ 1);

            defaultpic=false;
        }
    }

    private void getUserInfo() {
        DataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("foodName") != null) {
                        name2 = map.get("foodName").toString();
                        name.setText(name2);
                    }
                    if (map.get("description") != null) {
                        des2 = map.get("description").toString();
                        des.setText(des2);
                    }
                    if (map.get("expiration_date") != null) {
                        expdate2 = map.get("expiration_date").toString();
                        expdate.setText(expdate2);
                    }
                    if (map.get("num") != null) {
                        num2 = map.get("num").toString();
                        num.setText(num2);
                    }
                    if (map.get("address") != null) {
                        address2 = map.get("address").toString();
                        address.setText(address2);
                    }
                    if (map.get("foodpic") != null) {
                        profileImageUrl = map.get("foodpic").toString();
                        ShowImage(profileImageUrl);
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
                Intent it =new Intent(ShowFoodActivity.this, ShowUserActivity.class);
                startActivity(it);
                return true;
            case R.id.item3:
                Toast.makeText(this, "Food List", Toast.LENGTH_SHORT).show();
                Intent it2 =new Intent(ShowFoodActivity.this,FoodListActivity.class);
                startActivity(it2);
                return true;
            case R.id.item4:
                Toast.makeText(this, "Add Food", Toast.LENGTH_SHORT).show();
                Intent it3 =new Intent(ShowFoodActivity.this,AddFood.class);
                startActivity(it3);
                return true;
            case R.id.item5:
                Toast.makeText(this, "Users List", Toast.LENGTH_SHORT).show();
                Intent it4 =new Intent(ShowFoodActivity.this,UsersListActivity.class);
                startActivity(it4);
                return true;
            case R.id.item6:
                Toast.makeText(this, "Distribution List", Toast.LENGTH_SHORT).show();
                Intent it5 =new Intent(ShowFoodActivity.this,DistributionListActivity.class);
                startActivity(it5);
                return true;
            case R.id.item7:
                Toast.makeText(this, "Add Distribution", Toast.LENGTH_SHORT).show();
                Intent it6 =new Intent(ShowFoodActivity.this,AddDiscrabution.class);
                startActivity(it6);
                return true;
            case R.id.item8:
                Toast.makeText(this, "Add Collecting Point", Toast.LENGTH_SHORT).show();
                Intent it7 =new Intent(ShowFoodActivity.this,AddCollectingPoint.class);
                startActivity(it7);
                return true;
            case R.id.item9:
                Toast.makeText(this, "Collecting Point List", Toast.LENGTH_SHORT).show();
                Intent it8 =new Intent(ShowFoodActivity.this,CollectingPointListActivity.class);
                startActivity(it8);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}