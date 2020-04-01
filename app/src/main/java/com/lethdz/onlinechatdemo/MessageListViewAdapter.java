package com.lethdz.onlinechatdemo;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.lethdz.onlinechatdemo.modal.RoomMessage;
import com.lethdz.onlinechatdemo.modal.UserDetail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageListViewAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private List<RoomMessage> messageList;

    public MessageListViewAdapter(List<RoomMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        RoomMessage message = messageList.get(position);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (message.getOwner().equals(auth.getCurrentUser().getUid())) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RoomMessage message = messageList.get(position);

        switch (getItemViewType(position)) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.txt_messageBody);
            timeText = itemView.findViewById(R.id.txt_messageTime);
            nameText = itemView.findViewById(R.id.txt_name);
            profileImage = itemView.findViewById(R.id.img_profile);
        }

        void bind(RoomMessage message) {
            messageText.setText(message.getMessage());
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            // Format the stored timestamp into a readable String using method.

            timeText.setText(formatDate(message.getTimeStamp().toDate()));
            for (UserDetail element: MessageListActivity.getChatRoom().getMembers()) {
                if (!element.getUid().equals(mAuth.getCurrentUser().getUid())) {
                    nameText.setText(element.getDisplayName());
                    if (element.getPhotoURL().equals("")) {
                        profileImage.setImageResource(R.drawable.icon_profile);
                    } else {
                        Glide.with(MessageListActivity.activity).load(Uri.parse(element.getPhotoURL())).into(profileImage);
                    }
                }
            }
        }
    }

    public class SentMessageHolder extends  RecyclerView.ViewHolder {
        TextView messageText, timeText;

        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.txt_message_body);
            timeText = itemView.findViewById(R.id.txt_message_time);
        }

        void bind(RoomMessage message) {
            messageText.setText(message.getMessage());
            timeText.setText(formatDate(message.getTimeStamp().toDate()));
        }
    }

    public String formatDate(Date date) {
        String pattern = "EEE-HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }
}
