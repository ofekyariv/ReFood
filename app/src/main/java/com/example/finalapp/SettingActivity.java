package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.EditText;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {

    private boolean isUser = true;

    public static final int PICK_IMAGE=2;

    private EditText mNameField, mPhoneField;
    private TextView memail, tv, bday, gender;
    private Button mBack, mConfirm, mLogout;
    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private StorageReference mypicref;
    private StorageReference mStorageRef;
    private FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();

    private ProgressDialog dialog;
    private ProgressDialog dialogpic;

    private String userId, name, phone, profileImageUrl, userSex, email, birthday;

    private Uri resultUri;

    private  BatteryReceiver batteryReceiver;

    private String picpath="gs://fproject-bdf8a.appspot.com";

    private static Uri selectedImage=null;

    private boolean defaultpic=true;
    private static String imgDecodableString="profile.png";
    private String id;

    private static String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        tv=findViewById(R.id.tv);
        batteryReceiver =new BatteryReceiver(tv);

        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.phone);
        memail = (TextView) findViewById(R.id.email);

        gender = (TextView) findViewById(R.id.gender);
        bday = (TextView) findViewById(R.id.bday);

        mProfileImage = (ImageView) findViewById(R.id.profileImage);

        mConfirm = (Button) findViewById(R.id.confirm);
        mLogout=(Button) findViewById(R.id.logout);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        Bundle bundle=getIntent().getExtras();

        id = bundle.getString("U");

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //intent.setType("pics/");
                startActivityForResult(intent, 1);
            }
        });
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
                Intent it4 =new Intent(SettingActivity.this, ShowUserActivity.class);
                startActivity(it4);
                finish();
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logot();
                finish();
            }
        });

        dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Data!!!");

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        dialogpic = new ProgressDialog(this);
        dialogpic.setMessage("Saving Your Picture!");

        dialogpic.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        checkUserOrAdmin();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
    }

    //----------------------------------------------
    private void UpdateImageField()
    {
        mUserDatabase.child("pic").setValue(imgDecodableString);
    }
    private void Delete_Old_Image(String oldpic)
    {
        mypicref=mStorageRef.child("pics/" + oldpic);

        mypicref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                Toast.makeText(SettingActivity.this, "Your data has been Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //----------------------------------------------

    private void AddMyPicture()
    {
        mypicref=mStorageRef.child("pics/" + imgDecodableString);

        mypicref.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                if (dialogpic.isShowing())
                {
                    dialogpic.dismiss();
                    UpdateImageField();
                    Delete_Old_Image(imgDecodableString);
                }

            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message=e.getMessage();
                Toast.makeText(SettingActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ShowImage(String picname)
    {
       // String picname=StaticValues.getUser().getPic();

        String suffix=picname.substring(picname.lastIndexOf(".")+1);

        StorageReference storageReference=firebaseStorage.getReferenceFromUrl(picpath).child("pics").child(picname);

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
                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    dialog.dismiss();
                    Toast.makeText(SettingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
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
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void saveUserInformation() {
       name = mNameField.getText().toString();
       phone = mPhoneField.getText().toString();

       Map userInfo = new HashMap();
       userInfo.put("name", name);
       userInfo.put("phone", phone);

       if (!defaultpic) {
           dialogpic.show();
          // this.user_pic = imgDecodableString;
           AddMyPicture();
       }
       //AddPicture();

        mUserDatabase.updateChildren(userInfo);
        if (resultUri != null) {
            final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("pic").child(userId);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filepath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Map newImage = new HashMap();
                            newImage.put("pic", uri.toString());
                            mUserDatabase.updateChildren(newImage);
                            Toast.makeText(SettingActivity.this, "Your data has been Updated", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            finish();
                        }
                    });
                }
            });
        }
   }

    public void logot(){
        StaticValues.setUser(null);
        StaticValues.setUid("");
        StaticValues.setList(new ArrayList<User>());
        Intent it =new Intent(SettingActivity.this,MainActivity.class);
        startActivity(it);
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
                Intent it =new Intent(SettingActivity.this, SettingActivity.class);
                startActivity(it);
                return true;
            case R.id.item3:
                Toast.makeText(this, "Food List", Toast.LENGTH_SHORT).show();
                Intent it2 =new Intent(SettingActivity.this,FoodListActivity.class);
                startActivity(it2);
                return true;
            case R.id.item4:
                Toast.makeText(this, "Add Food", Toast.LENGTH_SHORT).show();
                Intent it3 =new Intent(SettingActivity.this,AddFood.class);
                startActivity(it3);
                return true;
            case R.id.item5:
                Toast.makeText(this, "Users List", Toast.LENGTH_SHORT).show();
                Intent it4 =new Intent(SettingActivity.this,UsersListActivity.class);
                startActivity(it4);
                return true;
            case R.id.item6:
                Toast.makeText(this, "Distribution List", Toast.LENGTH_SHORT).show();
                Intent it5 =new Intent(SettingActivity.this,DistributionListActivity.class);
                startActivity(it5);
                return true;
            case R.id.item7:
                Toast.makeText(this, "Add Distribution", Toast.LENGTH_SHORT).show();
                Intent it6 =new Intent(SettingActivity.this,AddDiscrabution.class);
                startActivity(it6);
                return true;
            case R.id.item8:
                Toast.makeText(this, "Add Collecting Point", Toast.LENGTH_SHORT).show();
                Intent it7 =new Intent(SettingActivity.this,AddCollectingPoint.class);
                startActivity(it7);
                return true;
            case R.id.item9:
                Toast.makeText(this, "Collecting Point List", Toast.LENGTH_SHORT).show();
                Intent it8 =new Intent(SettingActivity.this,CollectingPointListActivity.class);
                startActivity(it8);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}