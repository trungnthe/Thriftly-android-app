package com.mastercoding.thriftly.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.mastercoding.thriftly.Chat.AndroidUtil;
import com.mastercoding.thriftly.Chat.ChatActivity;
import com.mastercoding.thriftly.Chat.FirebaseUtil;
import com.mastercoding.thriftly.Models.ChatroomModel;
import com.mastercoding.thriftly.Models.UserModel;
import com.mastercoding.thriftly.R;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

                        // Retrieve the first document in the result
                        UserModel otherUserModel = task.getResult().getDocuments().get(0).toObject(UserModel.class);
                        if (otherUserModel != null) {
                            // Load profile picture
                            loadProfilePicture(otherUserModel, holder);

                            // Set username and last message
                            holder.usernameText.setText(otherUserModel.getUsername());
                            holder.lastMessageText.setText(lastMessageSentByMe ? "You : " + model.getLastMessage() : model.getLastMessage());
                            holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));


                            holder.itemView.setOnClickListener(v -> {
                                // Navigate to chat activity
                                Intent intent = new Intent(context, ChatActivity.class);
                                AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            });
                        }
                    }
                });
    }



    private void loadProfilePicture(UserModel user, ChatroomModelViewHolder holder) {
        if (user == null || TextUtils.isEmpty(user.getImage())) {
            holder.profilePic.setImageResource(R.drawable.ic_noimage); // Default image
            return;
        }

        Uri imageUri = Uri.parse(user.getImage());
        AndroidUtil.setProfilePic(context, imageUri, holder.profilePic);
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
