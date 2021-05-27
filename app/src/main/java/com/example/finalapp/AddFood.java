package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFood extends AppCompatActivity {
    private boolean isUser = true;


    public static final int PICK_IMAGE=1;
    private Spinner spnkid;

    private EditText editfoodname, editdescription, num, address;
    private TextView txtexpdate;

     private ImageButton btnpic;


    private Button btnsave;


    private DatePickerDialog pickerDialog;
    private Calendar c = null;


    private CircleImageView imgcrl;
    private static String choosenPic ="profile.png";
    private boolean defaultpic=false;

    private StorageReference storageReference=null;
    private static Uri selectedImage=null;

    private ProgressDialog dialog;
    private ProgressDialog dialogpic;

    private FirebaseDatabase db=null;
    private FirebaseAuth auth = null;
    private DatabaseReference ref=null;

    private ProgressDialog prg =null;

    private String currentUserID;
     DatabaseReference mDatabaseUser;

      FirebaseDatabase firebaseDatabase;
         DatabaseReference databaseReference;
    DatabaseReference databaseReferenceUser;
    //-/-/-/-/-/-/-/-//-/-/-/-/-/-/-/-/-/-/-/-/-//-/-/-//-/-/-//-/-//-/-/-/-/-/-/-/-/-/-/-/-/-/-

    private void AddPicture()
    {
        StorageReference pic_storage= storageReference.child("foodPics/"  + choosenPic);

        pic_storage.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(AddFood.this, "Image Updated!!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddFood.this,FoodListActivity.class));
            }
        }).addOnFailureListener(AddFood.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(AddFood.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && data !=null)
        {
            selectedImage= data.getData();

            imgcrl.setImageURI(selectedImage);

            defaultpic=true;

            String[] filepathColumn={MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,filepathColumn,null,null,null);

            cursor.moveToFirst();

            int columnIndex=cursor.getColumnIndex(filepathColumn[0]);

            choosenPic =cursor.getString(columnIndex);

            choosenPic = choosenPic.substring(choosenPic.lastIndexOf("/")+1);


        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        db = FirebaseDatabase.getInstance();
        ref=db.getReference("Food");

        databaseReference = firebaseDatabase.getInstance().getReference().child("Food");
        databaseReferenceUser = firebaseDatabase.getInstance().getReference().child("Users");
        storageReference= FirebaseStorage.getInstance().getReference();

        prg=new ProgressDialog(this);
        prg.setTitle("RegistrationActivity Data");
        prg.setMessage("Saving Data");
        prg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prg.setCancelable(false);

        btnsave=findViewById(R.id.btnsaveFood);
        editdescription =findViewById(R.id.editdescription);
        txtexpdate =findViewById(R.id.txtexpdate);
        num=findViewById(R.id.num);
        editfoodname=findViewById(R.id.editFoodname);
        address=findViewById(R.id.addfoodadress);


         btnpic=findViewById(R.id.btnpic);

         imgcrl=findViewById(R.id.imgcrl);


        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

          SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                c = Calendar.getInstance();
                String date = sdf.format(c.getTime());



                   btnpic.setOnClickListener(new View.OnClickListener()
                   {
                       @Override
                       public void onClick(View v)
                       {
                           Intent it = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                           startActivityForResult(it,1);
                       }
                   });


        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddPicture();

                String foodname= editfoodname.getText().toString();
                String foodnum=num.getText().toString();
                String date = txtexpdate.getText().toString();
                String editdes= editdescription.getText().toString();
                String address1=address.getText().toString();
                String profileImageUrl ="profile.png";

                if(defaultpic==true)
                    profileImageUrl= choosenPic;

                String id = databaseReference.push().getKey();

                 Food food=new Food(foodname,editdes,foodnum,date,profileImageUrl, currentUserID,address1);
                  databaseReference.child(id).setValue(food);
                //addToUser();
                  databaseReferenceUser.child(currentUserID).child("Food").child(id).setValue(true);

                Intent i = new Intent(AddFood.this, FoodListActivity.class);
                AddFood.this.startActivity(i);


                checkUserOrAdmin();

              /*  ref.setValue(food).addOnCompleteListener(AddFood.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            Toast.makeText(AddFood.this, "Added", Toast.LENGTH_SHORT).show();
                            prg.dismiss();

                            if (defaultpic==true)
                                AddPicture();
                        }
                        else
                            startActivity(new Intent(AddFood.this, SettingActivity.class));


                    }
                });*/

            }
        });

    }

    private void addToUser()
    {
        DatabaseReference currentUserConnectionsDb = databaseReferenceUser.child(currentUserID);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String key = FirebaseDatabase.getInstance().getReference().child("Food").push().getKey();
                    databaseReferenceUser.child("connections").setValue(key);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void ShowDate(View view) {

        final int myday=c.get(Calendar.DAY_OF_MONTH);
        int mymonth=c.get(Calendar.MONTH);
        int myyear=c.get(Calendar.YEAR);

        pickerDialog= new DatePickerDialog(AddFood.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                txtexpdate.setText(dayOfMonth + "/" + month + "/" + year);
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
                Intent it =new Intent(AddFood.this, ShowUserActivity.class);
                startActivity(it);
                return true;
            case R.id.item3:
                Toast.makeText(this, "Food List", Toast.LENGTH_SHORT).show();
                Intent it2 =new Intent(AddFood.this,FoodListActivity.class);
                startActivity(it2);
                return true;
            case R.id.item4:
                Toast.makeText(this, "Add Food", Toast.LENGTH_SHORT).show();
                Intent it3 =new Intent(AddFood.this,AddFood.class);
                startActivity(it3);
                return true;
            case R.id.item5:
                Toast.makeText(this, "Users List", Toast.LENGTH_SHORT).show();
                Intent it4 =new Intent(AddFood.this,UsersListActivity.class);
                startActivity(it4);
                return true;
            case R.id.item6:
                Toast.makeText(this, "Distribution List", Toast.LENGTH_SHORT).show();
                Intent it5 =new Intent(AddFood.this,DistributionListActivity.class);
                startActivity(it5);
                return true;
            case R.id.item7:
                Toast.makeText(this, "Add Distribution", Toast.LENGTH_SHORT).show();
                Intent it6 =new Intent(AddFood.this,AddDiscrabution.class);
                startActivity(it6);
                return true;
            case R.id.item8:
                Toast.makeText(this, "Add Collecting Point", Toast.LENGTH_SHORT).show();
                Intent it7 =new Intent(AddFood.this,AddCollectingPoint.class);
                startActivity(it7);
                return true;
            case R.id.item9:
                Toast.makeText(this, "Collecting Point List", Toast.LENGTH_SHORT).show();
                Intent it8 =new Intent(AddFood.this,CollectingPointListActivity.class);
                startActivity(it8);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
