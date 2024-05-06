package com.example.rentalhouse;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rentalhouse.databinding.ActivityEnquiryBinding;

public class EnquiryActivity extends AppCompatActivity {
    ActivityEnquiryBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEnquiryBinding.inflate(getLayoutInflater());
        setContentView( binding.getRoot());

        binding.imgEnquiryBack.setOnClickListener(v->{
            finish();
        });

        binding.btnSend.setOnClickListener(v->{
            if(TextUtils.isEmpty(binding.edtChanges.getText().toString())){
                Toast.makeText(this, "Please input your post name!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(binding.edtPostName.getText().toString())){
                Toast.makeText(this, "Please input your enquiry for edit post!", Toast.LENGTH_SHORT).show();
            }else{

                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:farihaadlina@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Enquiry Mail " + binding.edtPostName.getText().toString());
                intent.putExtra(Intent.EXTRA_TEXT, binding.edtChanges.getText().toString());
                startActivity(intent);
            }
        });
    }
}