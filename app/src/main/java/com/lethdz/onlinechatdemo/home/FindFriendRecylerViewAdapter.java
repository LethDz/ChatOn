package com.lethdz.onlinechatdemo.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lethdz.onlinechatdemo.R;
import com.lethdz.onlinechatdemo.dao.FirebaseDAO;
import com.lethdz.onlinechatdemo.modal.UserDetail;

import java.util.List;

public class FindFriendRecylerViewAdapter extends RecyclerView.Adapter<FindFriendRecylerViewAdapter.ViewHolder> {
    private List<UserDetail> listUser;
    // Initiate firestore database
    private FirebaseDAO firebaseDAO = new FirebaseDAO();
    public FindFriendRecylerViewAdapter(List<UserDetail> listUser) {
        this.listUser = listUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserDetail userDetail = listUser.get(position);
        if (userDetail.getPhotoURL() == null) {
            holder.avatar.setImageResource(R.drawable.icon_profile);
        } else {
            holder.avatar.setImageResource(userDetail.getPhotoURL().getPort());
        }
        holder.displayName.setText(userDetail.getEmail());
    }


    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatar;
        public TextView displayName;
        public Button btnAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.iv_avatar);
            displayName = itemView.findViewById(R.id.txt_displayName);
            btnAdd = itemView.findViewById(R.id.btn_addFriend);

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    UserDetail user = listUser.get(position);
                    firebaseDAO.addFriend(user, v, listUser, FindFriendFragment.adapter);
                }
            });
        }
    }
}

