package com.example.rentalhouse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rentalhouse.Model.RegisterModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class SplashActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Objects.requireNonNull(getSupportActionBar()).hide();

        // Attach a listener to read the data at our posts reference
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                try{
                    checkAdmin((Map<String,Object>) Objects.requireNonNull(dataSnapshot.getValue()));
                }catch(Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                Toast.makeText(SplashActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // Check if user is signed in (non-null) and update UI accordingly.
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                Boolean currentUser = sharedPreferences.getBoolean("login", false);

                Log.e("currentUser", String.valueOf(currentUser));
                if(currentUser){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else{
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
                finish();
            }
        }, 3000);


    }

    public void writeAdmin() {
        try{
            RegisterModel registerModel = new RegisterModel("Admin", "admin@mail.com", "123456", 2);
            DatabaseReference Ref = mDatabase.child("users");
            String key = Ref.push().getKey();
            assert key != null;
            mDatabase.child("users").child(key).setValue(registerModel);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void checkAdmin(Map<String,Object> users) {
        boolean adminExist = false;
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){
            //Get user map
            Map data = (Map) entry.getValue();
            //Get phone field and append to list
            if(data != null){
                if(Objects.requireNonNull(data.get("email")).toString().equals("admin@mail.com")
                        && Objects.requireNonNull(data.get("password")).toString().equals("123456")){
                    adminExist = true;
                    break;
                }
            }
        }

        if(!adminExist)
           writeAdmin();
    }

}