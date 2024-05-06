package com.example.rentalhouse;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rentalhouse.databinding.ActivityProfileBinding;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView( binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        binding.edtUserName.setText(sharedPreferences.getString("username", ""));
        binding.edtEmail.setText(sharedPreferences.getString("email", ""));

        if(sharedPreferences.getString("type", "-1").equals("0")){
            binding.edtType.setText("Seller");
        }else if(sharedPreferences.getString("type", "-1").equals("1")){
            binding.edtType.setText("Buyer");
        }else{
            binding.edtType.setText("Admin");
        }

        binding.imgProfileBack.setOnClickListener(v->{
            finish();
        });


    }
}