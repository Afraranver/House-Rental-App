package com.example.rentalhouse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.rentalhouse.Adapter.PostListAdapter;
import com.example.rentalhouse.Model.PostModel;
import com.example.rentalhouse.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private DatabaseReference mDatabase;
    private ArrayList<PostModel> postModelsList = new ArrayList<>();
    private PostListAdapter postListAdapter;
    private String userType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView( binding.getRoot());

        mDatabase= FirebaseDatabase.getInstance().getReference().child("posts");

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        userType = sharedPreferences.getString("type", "");
        if(!userType.equals("")){
            if(userType.equals("0"))
                binding.btnAddNewPost.setVisibility(View.VISIBLE);
            else
                binding.btnAddNewPost.setVisibility(View.GONE);
        }

        binding.btnAddNewPost.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), AddPostActivity.class));
        });


        binding.pullToRefresh.setOnRefreshListener(() -> {
             // your code
            postModelsList.clear();
            getAllDataFromDB();
            binding.pullToRefresh.setRefreshing(false);
        });

//        binding.edtSearch.setOnClickListener(v->{
//            binding.edtSearch.requestFocus();
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(binding.edtSearch, InputMethodManager.SHOW_IMPLICIT);
//        });

        binding.edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.edtSearch.getWindowToken(), 0);
                    filter(binding.edtSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });


        //hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.edtSearch.getWindowToken(), 0);
        //call firebase db data here
//        getAllDataFromDB();
    }

    @Override
    protected void onResume() {
        super.onResume();
        postModelsList.clear();
        getAllDataFromDB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dotmenu, menu);
        if(userType.equals("0")){
            menu.findItem(R.id.menu_contact_admin).setVisible(true);
        }else {
            menu.findItem(R.id.menu_contact_admin).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.menu_profile){
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }else if(id == R.id.menu_logout){

            SharedPreferences preferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
            //remove single
//            preferences.edit().remove("login").apply();
            //remove all
            preferences.edit().clear().apply();

            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }else if(id == R.id.menu_contact_admin){
            startActivity(new Intent(getApplicationContext(), EnquiryActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void getAllDataFromDB(){
        // Attach a listener to read the data at our posts reference
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                try{
                    getPost((Map<String,Object>) Objects.requireNonNull(dataSnapshot.getValue()));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


    }

    public void getPost(Map<String,Object> users){
        try{
            for (Map.Entry<String, Object> entry : users.entrySet()){
                //Get user map
                Map data = (Map) entry.getValue();
                //Get phone field and append to list
                if(data != null){
                    List<Object> imageobject = Collections.singletonList(Objects.requireNonNull(data.get("imagesList")));
                    Log.e("imageobject", String.valueOf(imageobject));

                    List<Object> images = (List<Object>) imageobject.get(0);

                    ArrayList<String> imagesList = new ArrayList<>(imageobject.size());
                    for (Object object : images) {
                        imagesList.add(Objects.toString(object, null).replace("[", "").replace("]", ""));
                    }

                    PostModel postModel = new PostModel( Objects.requireNonNull(data.get("id")).toString(),
                            imagesList,
                            Objects.requireNonNull(data.get("description")).toString(),
                            Objects.requireNonNull(data.get("price")).toString(),
                            Objects.requireNonNull(data.get("contact")).toString(),
                            Objects.requireNonNull(data.get("name")).toString(),
                            Objects.requireNonNull(data.get("location")).toString(),
                            Objects.requireNonNull(data.get("availability")).toString());

                    postModelsList.add(postModel);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        postListAdapter = new PostListAdapter(MainActivity.this, postModelsList, userType, item -> {
            Log.e("recyclerViewPost", "catch here");
            Query postQuery = mDatabase.orderByChild("id").equalTo(item.getId());

            postQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        postSnapshot.getRef().removeValue();
                    }
                    finish();
                    overridePendingTransition( 0, 0);
                    startActivity(getIntent());
                    overridePendingTransition( 0, 0);
                }

                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {
                    Log.e("onCancelled", databaseError.toException().toString());
                }
            });
        });

        binding.recyclerViewPost.setHasFixedSize(true);
        binding.recyclerViewPost.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewPost.setAdapter(postListAdapter);

        if(Objects.requireNonNull(binding.recyclerViewPost.getAdapter()).getItemCount() < 0){
            binding.recyclerViewPost.setVisibility(View.GONE);
            binding.imgNoPost.setVisibility(View.VISIBLE);
        }else{
            binding.recyclerViewPost.setVisibility(View.VISIBLE);
            binding.imgNoPost.setVisibility(View.GONE);
        }
    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<PostModel> filteredlist = new ArrayList<PostModel>();

        // running a for loop to compare elements.
        for (PostModel item : postModelsList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getDescription().toLowerCase().contains(text.toLowerCase()) || item.getPrice().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            postListAdapter.filterList(filteredlist);
        }
    }


}