package com.example.rentalhouse;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.rentalhouse.Model.PostModelSerial;
import com.example.rentalhouse.databinding.ActivityPostDetailsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.synnapps.carouselview.ImageListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class PostDetails extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ActivityPostDetailsBinding binding;
    private ArrayList<String> imageList = new ArrayList<>();
    private DatabaseReference mDatabase;
    private PostModelSerial postModelSerial;
    String[] postTypes = {"Available", "Not Available", "Sold"};
    private String itemSelected;
    private String selection;
    private int spinnerPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailsBinding.inflate(getLayoutInflater());
        setContentView( binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("posts");
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, postTypes);

        Intent intent = getIntent();
        if(intent != null){
            postModelSerial = (PostModelSerial) intent.getSerializableExtra("postModelSerial");
            if(intent.getBooleanExtra("isEdit", false)){
                selection = "Available";
                binding.btnUpdate.setVisibility(View.VISIBLE);
            } else{
                selection = postModelSerial.getAvailability();
                binding.btnUpdate.setVisibility(View.GONE);
                binding.edtPostName.setFocusable(false);
                binding.edtPostDescription.setFocusable(false);
                binding.edtPostPrice.setFocusable(false);
                binding.edtPostContact.setFocusable(false);
                binding.edtPostLocation.setFocusable(false);
                binding.idAvailabilitySpinner.setEnabled(false);
                binding.idAvailabilitySpinner.setClickable(false);
            }

            assert postModelSerial != null;
            imageList = postModelSerial.getImagesList();
            binding.carouselView.setPageCount(imageList.size());
            binding.carouselView.setImageListener(imageListener);

            binding.edtPostDescription.setText(postModelSerial.getDescription());
            binding.edtPostPrice.setText(postModelSerial.getPrice());
            binding.edtPostContact.setText(postModelSerial.getContact());
            binding.edtPostName.setText(postModelSerial.getName());
            binding.edtPostLocation.setText(postModelSerial.getLocation());
            itemSelected = postModelSerial.getAvailability();

            binding.edtPostContact.setOnClickListener(v->{
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://api.whatsapp.com/send?phone="+postModelSerial.getContact()));
                startActivity(i);
            });
        }

        binding.imgBack.setOnClickListener(v-> finish());
        binding.btnUpdate.setOnClickListener(v-> updateDetails());


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.idAvailabilitySpinner.setAdapter(adapter);
        binding.idAvailabilitySpinner.setOnItemSelectedListener(this);
        spinnerPosition = adapter.getPosition(selection);
        binding.idAvailabilitySpinner.setSelection(spinnerPosition);

    }


    ImageListener imageListener = (position, imageView) -> {
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(PostDetails.this)
                .load(imageList.get(position))
                .placeholder(R.drawable.no_pictures)
                .fitCenter()
                .error(R.drawable.no_pictures)
                .into(imageView);
    };

    public Boolean updateDetails(){
        Query postQuery = mDatabase.orderByChild("id").equalTo(postModelSerial.getId());

        postQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    try{
                        if(TextUtils.isEmpty(binding.edtPostName.getText())){
                            Toast.makeText(PostDetails.this, "Please input a Name!", Toast.LENGTH_SHORT).show();
                        }else if(imageList.size() == 0){
                            Toast.makeText(PostDetails.this, "Please select an image!", Toast.LENGTH_SHORT).show();
                        }else if(TextUtils.isEmpty(binding.edtPostDescription.getText())){
                            Toast.makeText(PostDetails.this, "Please input description!", Toast.LENGTH_SHORT).show();
                        }else if(TextUtils.isEmpty(binding.edtPostPrice.getText())){
                            Toast.makeText(PostDetails.this, "Please input your price!", Toast.LENGTH_SHORT).show();
                        }else if(TextUtils.isEmpty(binding.edtPostContact.getText())){
                            Toast.makeText(PostDetails.this, "Please input your contact!", Toast.LENGTH_SHORT).show();
                        }else{
                            postSnapshot.getRef().child("id").setValue(postModelSerial.getId());
                            postSnapshot.getRef().child("name").setValue(binding.edtPostName.getText().toString());
                            postSnapshot.getRef().child("contact").setValue(binding.edtPostContact.getText().toString());
                            postSnapshot.getRef().child("description").setValue(binding.edtPostDescription.getText().toString());
                            postSnapshot.getRef().child("location").setValue(binding.edtPostLocation.getText().toString());
                            postSnapshot.getRef().child("price").setValue(binding.edtPostLocation.getText().toString());
                            postSnapshot.getRef().child("imagesList").setValue(postModelSerial.getImagesList());
                            postSnapshot.getRef().child("availability").setValue(itemSelected);
                            Toast.makeText(PostDetails.this, "Post has been updated successfully.", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(PostDetails.this, "Post failed to update!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                Log.e("onCancelled", databaseError.toException().toString());
                Toast.makeText(PostDetails.this, "Post failed to update!! " + databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        itemSelected = postTypes[i];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}