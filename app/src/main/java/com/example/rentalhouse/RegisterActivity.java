package com.example.rentalhouse;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.rentalhouse.Model.RegisterModel;
import com.example.rentalhouse.databinding.ActivityRegisterBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    private DatabaseReference mDatabase;
    private Integer type = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView( binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference();

        binding.btnBuyer.setOnClickListener(v->{
            //Buyer type is 1
            type = 1;
            binding.btnSeller.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_corners_light_green));
            binding.btnBuyer.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_corners_green));
        });

        binding.btnSeller.setOnClickListener(v->{
            //Seller type is 0
            type = 0;
            binding.btnBuyer.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_corners_light_green));
            binding.btnSeller.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_corners_green));
        });

        binding.btnRegister.setOnClickListener(v->{
            if(TextUtils.isEmpty(binding.edtUserName.getText())){
                Toast.makeText(this, "Please input your username!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(binding.edtEmail.getText())){
                Toast.makeText(this, "Please input your email address!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(binding.edtPassword.getText())){
                Toast.makeText(this, "Please input your password", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(binding.edtConfirmPassword.getText())){
                Toast.makeText(this, "Please input your confirm password", Toast.LENGTH_SHORT).show();
            }else if (type == -1){
                Toast.makeText(this, "Please select user type", Toast.LENGTH_SHORT).show();
            }else{
                if(binding.edtPassword.getText().toString().equals(binding.edtConfirmPassword.getText().toString())){
                    writeNewUser(binding.edtUserName.getText().toString(),
                            binding.edtEmail.getText().toString(),
                            binding.edtPassword.getText().toString(), type);
                }else{
                    Toast.makeText(this, "Passwords don't match, please try again", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void writeNewUser(String name, String email, String password, Integer type) {
        try{
            RegisterModel registerModel = new RegisterModel(name, email, password, type);
            DatabaseReference Ref = mDatabase.child("users");
            String key = Ref.push().getKey();
            assert key != null;
            mDatabase.child("users").child(key).setValue(registerModel);
            Toast.makeText(this, "You have successfully registered! Please login.", Toast.LENGTH_SHORT).show();


            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}