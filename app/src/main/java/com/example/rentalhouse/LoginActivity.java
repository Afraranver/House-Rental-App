package com.example.rentalhouse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rentalhouse.databinding.ActivityLoginBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    ActivityLoginBinding binding;
    private DatabaseReference mDatabase;
    private boolean userExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView( binding.getRoot());

        binding.btnLogin.setOnClickListener(v->{
            if(TextUtils.isEmpty(binding.edtEmail.getText())){
                Toast.makeText(this, "Please input your email address!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(binding.edtPassword.getText())){
                Toast.makeText(this, "Please input your password!", Toast.LENGTH_SHORT).show();
            }else{
                //Get datasnapshot at your "users" root node
                mDatabase= FirebaseDatabase.getInstance().getReference().child("users");

                // Attach a listener to read the data at our posts reference
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        try{
                            Log.e("onDataChange", "true");
                            userExist = false;
                            checkLogin((Map<String,Object>) Objects.requireNonNull(dataSnapshot.getValue()));
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                        Toast.makeText(LoginActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

        binding.btnRegister.setOnClickListener(v-> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));


    }

    private void checkLogin(Map<String,Object> users) {
        Log.e("checkLogin", "true");
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){
            Log.e("Map.Entry", "true");
            //Get user map
            Map data = (Map) entry.getValue();
            //Get phone field and append to list
            if(data != null){
                Log.e("Map.data", "true");
                if(Objects.requireNonNull(data.get("email")).toString().equals(binding.edtEmail.getText().toString())
                && Objects.requireNonNull(data.get("password")).toString().equals(binding.edtPassword.getText().toString())){
                    userExist = true;

                    Toast.makeText(this, "Successfully Logged In.", Toast.LENGTH_SHORT).show();
                    Log.e("Type", Objects.requireNonNull(data.get("type")).toString());
                    // Creating a shared pref object with a file name "MySharedPref" in private mode
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();

                    // write all the data entered by the user in SharedPreference and apply
                    myEdit.putString("type", Objects.requireNonNull(data.get("type")).toString());
                    myEdit.putBoolean("login", true);
                    myEdit.putString("username", Objects.requireNonNull(data.get("username")).toString());
                    myEdit.putString("email", Objects.requireNonNull(data.get("email")).toString());
                    myEdit.apply();

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                    break;
                }
            }else{
                Log.e("Map.data", "false");
            }
        }

        if(!userExist)
            Toast.makeText(this, "User not found, please Register first!", Toast.LENGTH_SHORT).show();
    }
}