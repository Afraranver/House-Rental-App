package com.example.rentalhouse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.rentalhouse.Model.PostModel;
import com.example.rentalhouse.databinding.ActivityAddPostBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class AddPostActivity extends AppCompatActivity implements LocationListener {
    ActivityAddPostBinding binding;
    private Integer CHOOSE_IMAGE = 0;
    private Integer imgViewSelector = -1;
    private ArrayList<String> imageList = new ArrayList<>();
    private DatabaseReference mDatabase;
    private LocationManager locationManager;
    private String city, state = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPostBinding.inflate(getLayoutInflater());
        setContentView( binding.getRoot());
        mDatabase = FirebaseDatabase.getInstance().getReference();

        binding.imgAddImage1.setOnClickListener(v->{
            imgViewSelector = 1;
            updateImg();
        });

        binding.imgAddImage2.setOnClickListener(v->{
            imgViewSelector = 2;
            updateImg();
        });

        binding.imgAddImage3.setOnClickListener(v->{
            imgViewSelector = 3;
            updateImg();
        });

        binding.btnAddPost.setOnClickListener(v->{
            if(TextUtils.isEmpty(binding.edtName.getText())){
                Toast.makeText(this, "Please input a Name!", Toast.LENGTH_SHORT).show();
            }else if(imageList.size() == 0){
                Toast.makeText(this, "Please select an image!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(binding.edtDescription.getText())){
                Toast.makeText(this, "Please input description!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(binding.edtPrice.getText())){
                Toast.makeText(this, "Please input your price!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(binding.edtContact.getText())){
                Toast.makeText(this, "Please input your contact!", Toast.LENGTH_SHORT).show();
            }else{
                addNewPost(imageList, binding.edtDescription.getText().toString(),
                        binding.edtPrice.getText().toString(),
                        binding.edtContact.getText().toString(),
                        binding.edtName.getText().toString());
            }
        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationEnabled();
        getLocation();

    }

    private void updateImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "Failed to get Image, Please try again!!", Toast.LENGTH_SHORT).show();
                return;
            }

            // create an inputStream from the intent result we have just created
            InputStream inputStream;
            try {
                inputStream = AddPostActivity.this.getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));
                uploadImageToFirebase(inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebase(InputStream rInputStream) {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String strUserName = sharedPreferences.getString("username", "");

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference mountainsRef = storageRef.child("uploads/"+strUserName+System.currentTimeMillis());
        // running the task
        UploadTask uploadTask = mountainsRef.putStream(rInputStream);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // show error
                Log.e("onFailure", e.toString());
                Toast.makeText(AddPostActivity.this, "Failed to upload Image, Please try again!!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.e("uploadImageToFirebase", taskSnapshot.toString());

                mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.e("Uri", uri.toString());
                        imageList.add(uri.toString());
                        ImageView view;
                        // do ui stuff
                        if(imgViewSelector == 1){
                            view = binding.imgAddImage1;
                        }else if (imgViewSelector == 2){
                            view = binding.imgAddImage2;
                        }else{
                            view = binding.imgAddImage3;
                        }

                        Glide.with(getApplicationContext())
                                .load(uri)
                                .placeholder(R.drawable.no_pictures)
                                .error(R.drawable.image)
                                .into(view);
                    }
                });
                Toast.makeText(getApplicationContext(), "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addNewPost(ArrayList<String> imageList, String description, String price, String contact, String name) {
        try{
            PostModel postModel = new PostModel(UUID.randomUUID().toString(), imageList, description, price, contact, name, city +" "+ state, "Available");
            DatabaseReference Ref = mDatabase.child("posts");
            String key = Ref.push().getKey();
            assert key != null;
            mDatabase.child("posts").child(key).setValue(postModel);

            Toast.makeText(this, "Your post uploaded successfully.", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void locationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(AddPostActivity.this)
                    .setTitle("Enable GPS Service")
                    .setMessage("We need your GPS location to show Near Places around you.")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, (LocationListener) this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NotNull Location location) {
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Log.e("addresses", addresses.get(0).getLocality());
            Log.e("addresses", addresses.get(0).getAdminArea());
            city =  addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
//            Log.e("addresses", addresses.get(0).getCountryName());
//            Log.e("addresses", addresses.get(0).getPostalCode());
//            Log.e("addresses", addresses.get(0).getAddressLine(0));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NotNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NotNull String provider) {

    }


}