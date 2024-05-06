package com.example.rentalhouse.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rentalhouse.Model.PostModel;
import com.example.rentalhouse.Model.PostModelSerial;
import com.example.rentalhouse.PostDetails;
import com.example.rentalhouse.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder>{
    private ArrayList<PostModel> postModelArrayList;
    private Activity activity;
    private String userType;
    private final OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(PostModel item);
    }

    // RecyclerView recyclerView;
    public PostListAdapter(Activity activity, ArrayList<PostModel> postModelArrayList, String userType,
                           OnItemClickListener listener) {
        this.activity = activity;
        this.postModelArrayList = postModelArrayList;
        this.userType = userType;
        this.listener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.postlist, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        final PostModel myListData = postModelArrayList.get(position);

        if(userType.equals("2"))
            holder.linearAdminOptions.setVisibility(View.VISIBLE);
        else
            holder.linearAdminOptions.setVisibility(View.GONE);

        if(myListData.getAvailability().equalsIgnoreCase("available")){
            holder.txtAvailable.setText("Available");
            holder.txtAvailable.setBackground(ContextCompat.getDrawable(activity, R.drawable.round_corners_light_green));
        }else if(myListData.getAvailability().equalsIgnoreCase("Not Available")){
            holder.txtAvailable.setText("Not Available");
            holder.txtAvailable.setBackground(ContextCompat.getDrawable(activity, R.drawable.round_corners_light_red));
        }else{
            holder.txtAvailable.setText("Sold");
            holder.txtAvailable.setBackground(ContextCompat.getDrawable(activity, R.drawable.round_corners_light_red));
        }
        

        holder.txtPostDescription.setText(myListData.getDescription());
        holder.txtPostPrice.setText(myListData.getPrice());
        holder.txtPostContact.setText(myListData.getContact());
        holder.txtPostName.setText(myListData.getName());
        holder.txtPostLocation.setText(myListData.getLocation());

        if(myListData.getImagesList() != null)
            Glide.with(activity)
                    .load(myListData.getImagesList().get(0))
                    .placeholder(R.drawable.no_pictures)
                    .error(R.drawable.no_pictures)
                    .into(holder.imgPost);

        holder.relativelayout_post.setOnClickListener(view -> {
            PostModelSerial postModelSerial = new PostModelSerial(myListData.getId(), myListData.getImagesList(), myListData.getDescription(),
                    myListData.getPrice(), myListData.getContact(), myListData.getName(), myListData.getLocation(), myListData.getAvailability());

            Intent intent = new Intent(activity, PostDetails.class);
            intent.putExtra("postModelSerial", postModelSerial);
            intent.putExtra("isEdit", false);
            activity.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v->{
            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setTitle("Delete Post");
            alert.setMessage("Are you sure you want to delete?");
            alert.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                // continue with delete
                listener.onItemClick(myListData);

            });
            alert.setNegativeButton(android.R.string.no, (dialog, which) -> {
                // close dialog
                dialog.cancel();
            });
            alert.show();
        });

        holder.btnEdit.setOnClickListener(v->{
            PostModelSerial postModelSerial = new PostModelSerial(myListData.getId(),myListData.getImagesList(), myListData.getDescription(),
                    myListData.getPrice(), myListData.getContact(), myListData.getName(), myListData.getLocation(), myListData.getAvailability());

            Intent intent = new Intent(activity, PostDetails.class);
            intent.putExtra("postModelSerial", postModelSerial);
            intent.putExtra("isEdit", true);
            activity.startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return postModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgPost;
        public TextView txtPostDescription;
        public TextView txtPostPrice;
        public TextView txtPostContact;
        public TextView txtPostName;
        public TextView txtPostLocation;
        public RelativeLayout relativelayout_post;
        public LinearLayout linearAdminOptions;
        public Button btnDelete;
        public Button btnEdit;
        public TextView txtAvailable;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imgPost = itemView.findViewById(R.id.imgPost);
            this.txtPostDescription = itemView.findViewById(R.id.txtPostDescription);
            this.txtPostPrice = itemView.findViewById(R.id.txtPostPrice);
            this.txtPostContact = itemView.findViewById(R.id.txtPostContact);
            this.txtPostName = itemView.findViewById(R.id.txtPostName);
            this.txtPostLocation = itemView.findViewById(R.id.txtPostLocation);
            this.relativelayout_post = itemView.findViewById(R.id.relativelayout_post);
            this.linearAdminOptions = itemView.findViewById(R.id.linearAdminOptions);
            this.btnDelete = itemView.findViewById(R.id.btnDelete);
            this.btnEdit = itemView.findViewById(R.id.btnEdit);
            this.txtAvailable = itemView.findViewById(R.id.txtAvailable);
        }
    }

    // method for filtering our recyclerview items.
    public void filterList(ArrayList<PostModel> filterlist) {
        // below line is to add our filtered
        // list in our course array list.
        postModelArrayList = filterlist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

}
