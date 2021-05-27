package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity extends AppCompatActivity {

    private EditText editem,editpa;
    private Button btns;
    //-------------------------------
    private FirebaseAuth auth=null;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private FirebaseDatabase db= FirebaseDatabase.getInstance();
    private DatabaseReference reference=null;

    private String currentUId;


    {
        reference=db.getReference().child("Users");

        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot myrow:dataSnapshot.getChildren())
                    StaticValues.getList().add(myrow.getValue(User.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText(LogInActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        editem=findViewById(R.id.editem);
        editpa=findViewById(R.id.editpa);

        btns=findViewById(R.id.btns);

        auth=FirebaseAuth.getInstance();

        reference=db.getReference("Users");

        final DataSnapshot dataSnapshot;

        auth = FirebaseAuth.getInstance();

      //  currentUId=auth.getCurrentUser().getUid();


        btns.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String email=editem.getText().toString();
                final String pass=editpa.getText().toString();

                auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(LogInActivity.this, "You are in", Toast.LENGTH_SHORT).show();

                            StaticValues.setUid(auth.getUid());

                            reference=reference.child(auth.getUid());

                            startActivity(new Intent(LogInActivity.this,
                                    ShowUserActivity.class));

                        }

                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(LogInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
