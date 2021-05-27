package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editemail,editpass,editname,editphone,editdate;
    private ImageView img;
    private Button btnsave;
    private ImageButton btnpic;
    private RadioButton rdmale,rdbfemale;
    private RadioGroup rdb;

    //-----------------------------
    private FirebaseDatabase db=null;
    private FirebaseAuth auth = null;
    private DatabaseReference ref=null;
    private StorageReference storageReference=null;

    private static String choosenPic="profile.png";
    private static boolean ichoose=false;
    private static String imgDecodableString="profile.png";


    private ProgressDialog prg =null;

    private static Uri selectedImage=null;
    //---------------------------------------------
    private void AddPicture()
    {
        StorageReference pic_storage=storageReference.child("pics/"  + imgDecodableString);

        pic_storage.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(RegistrationActivity.this, "Image Updated!!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegistrationActivity.this, LogInActivity.class));
            }
        }).addOnFailureListener(RegistrationActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && data !=null)
        {
            selectedImage= data.getData();

            img.setImageURI(selectedImage);

            ichoose=true;

            String[] filepathColumn={MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,filepathColumn,null,null,null);

            cursor.moveToFirst();

            int columnIndex=cursor.getColumnIndex(filepathColumn[0]);

          //  choosenPic=cursor.getString(columnIndex);

            //choosenPic=choosenPic.substring(choosenPic.lastIndexOf("/")+1);
            imgDecodableString = cursor.getString(columnIndex).substring(cursor.getString(columnIndex).lastIndexOf("/")+ 1);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Permission p = new Permission(this);
        p.verifyPermissions();

        editemail=findViewById(R.id.editemail);
        editpass=findViewById(R.id.editpass);
        editdate=findViewById(R.id.editdate);
        editname=findViewById(R.id.editname);
        editphone=findViewById(R.id.editphone);

        rdbfemale=findViewById(R.id.rdbfemale);
        rdmale=findViewById(R.id.rdbmale);

        rdb=findViewById(R.id.rdbg);

        img=findViewById(R.id.img);

        btnpic=findViewById(R.id.btnpic);
        btnsave=findViewById(R.id.btnsave);

        db = FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        ref=db.getReference("Users");
        storageReference= FirebaseStorage.getInstance().getReference();

        prg=new ProgressDialog(this);
        prg.setTitle("RegistrationActivity Data");
        prg.setMessage("Saving Data");
        prg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prg.setCancelable(false);


        btnpic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent it = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(it,1);
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                prg.show();

                auth.createUserWithEmailAndPassword(editemail.getText().toString(),editpass.getText().toString()).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            String email=editemail.getText().toString();
                            String pass = editpass.getText().toString();
                            String phone=editphone.getText().toString();
                            String BirthhDay=editdate.getText().toString();
                            String name=editname.getText().toString();
                            String profileImageUrl ="profile.png";

                            if(ichoose==true)
                                profileImageUrl=imgDecodableString;

                            String sex="";

                            int id=rdb.getCheckedRadioButtonId();

                            if(id==R.id.rdbmale)
                                sex="male";
                            else
                                sex="female";

                            User user=new User(email,pass,sex,phone,BirthhDay,name,profileImageUrl);

                            ref.child(auth.getUid()).setValue(user).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(RegistrationActivity.this, "Database Created!!!!", Toast.LENGTH_SHORT).show();
                                        prg.dismiss();

                                        if(ichoose==true)
                                            AddPicture();
                                        else
                                            startActivity(new Intent(RegistrationActivity.this, SettingActivity.class));
                                    }
                                }
                            });

                        }
                    }
                }).addOnFailureListener(RegistrationActivity.this, new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        prg.dismiss();
                    }
                });
            }
        });
    }
}
