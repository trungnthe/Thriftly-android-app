package com.mastercoding.thriftly.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mastercoding.thriftly.Chat.AndroidUtil;
import com.mastercoding.thriftly.Chat.ChatActivity;
import com.mastercoding.thriftly.Chat.FirebaseUtil;
import com.mastercoding.thriftly.Models.UserModel;
import com.mastercoding.thriftly.R;

import java.util.ArrayList;
import java.util.List;

public class SearchUserRecyclerAdapter extends RecyclerView.Adapter<SearchUserRecyclerAdapter.UserModelViewHolder> {

    private Context context;
    private List<UserModel> users;

    // Constructor
    public SearchUserRecyclerAdapter(Context context, List<UserModel> users) {
        this.context = context;
        this.users = new ArrayList<>(users); // Avoid modifying the original list
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserModelViewHolder holder, int position) {
        UserModel user = users.get(position);
        holder.usernameText.setText(getDisplayUsername(user));
        holder.phoneText.setText(user.getPhone());
        loadProfilePicture(user, holder);
        holder.itemView.setOnClickListener(v -> openChatActivity(user));
    }

    private String getDisplayUsername(UserModel user) {
        String currentUserId = FirebaseUtil.currentUserId();
        return !TextUtils.isEmpty(user.getUserId()) && user.getUserId().equals(currentUserId)
                ? user.getUsername() + " (Me)"
                : user.getUsername();
    }

    private void loadProfilePicture(UserModel user, UserModelViewHolder holder) {
        if (user == null || TextUtils.isEmpty(user.getImage())) {
            holder.profilePic.setImageResource(R.drawable.ic_noimage); // Default image
            return;
        }

        Uri imageUri = Uri.parse(user.getImage());
        AndroidUtil.setProfilePic(context, imageUri, holder.profilePic);
    }


    private void openChatActivity(UserModel user) {
        Intent intent = new Intent(context, ChatActivity.class);
        AndroidUtil.passUserModelAsIntent(intent, user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateUsers(List<UserModel> newUsers) {
        users.clear();
        users.addAll(newUsers);
        notifyDataSetChanged();
    }

    static class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView phoneText;
        ImageView profilePic;

        UserModelViewHolder(View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            phoneText = itemView.findViewById(R.id.phone_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
