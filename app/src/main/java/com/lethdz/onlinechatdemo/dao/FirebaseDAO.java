package com.lethdz.onlinechatdemo.dao;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lethdz.onlinechatdemo.MessageListActivity;
import com.lethdz.onlinechatdemo.R;
import com.lethdz.onlinechatdemo.modal.ChatRoom;
import com.lethdz.onlinechatdemo.modal.RoomMessage;
import com.lethdz.onlinechatdemo.modal.User;
import com.lethdz.onlinechatdemo.modal.UserChatRoom;
import com.lethdz.onlinechatdemo.modal.UserDetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FirebaseDAO {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private boolean duplicatedFriend = false;
    private boolean firstLoadMessage = true;

    public void addFriend(UserDetail user, View v, List<UserDetail> getListUser, RecyclerView.Adapter getAdapter, Activity activity) {
        final UserDetail userAdding = user;
        final View view = v;
        final RecyclerView.Adapter adapter = getAdapter;
        final List<UserDetail> listUser = getListUser;
        final ProgressBar progressBar = activity.findViewById(R.id.toolbarprogresshome);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(60, true);

        String documentName = user.getUid() + auth.getCurrentUser().getUid();
        Timestamp timeCreated = new Timestamp(new Date());
        // Value for the friend
        UserChatRoom chatRoomFriend = new UserChatRoom(
                documentName,
                auth.getCurrentUser().getEmail(),
                "",
                timeCreated);

        // Value for the current user
        UserChatRoom chatRoomUser = new UserChatRoom(
                documentName,
                user.getEmail(),
                "",
                timeCreated);

        // Value for the chat room
        List<UserDetail> members = new ArrayList<UserDetail>();
        members.add(new UserDetail(user.getUid(), user.getEmail(), user.getDisplayName(), user.getPhotoURL()));
        members.add(new UserDetail(auth.getCurrentUser().getUid(),
                auth.getCurrentUser().getEmail(),
                auth.getCurrentUser().getDisplayName(),
                auth.getCurrentUser().getPhotoUrl().toString()));
        ChatRoom chatRoom = new ChatRoom(documentName, "", "", timeCreated, members, new ArrayList<RoomMessage>());

        // Update the Friend.
        DocumentReference userDetailRef = db.collection("UserDetail").document(user.getUid());
        userDetailRef.update("chatRoom", FieldValue.arrayUnion(chatRoomFriend));

        // Update current user
        DocumentReference currentUserRef = db.collection("UserDetail").document(auth.getCurrentUser().getUid());
        currentUserRef.update("chatRoom", FieldValue.arrayUnion(chatRoomUser));

        // Add chat room
        db.collection("ChatRoom").
                document(documentName).
                set(chatRoom).
                addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(view.getContext(), "Add " + userAdding.getDisplayName() + " Success", Toast.LENGTH_LONG).show();
                    listUser.clear();
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("Fail", "Adding Friend Error ", task.getException());
                    Toast.makeText(view.getContext(), "Adding Friend Error!!", Toast.LENGTH_LONG).show();
                }
                progressBar.setProgress(100, true);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void searchFriend(String query, RecyclerView.Adapter getAdapter, List<UserDetail> getListUser, View v, Activity activity) {
        final RecyclerView.Adapter adapter = getAdapter;
        final List<UserDetail> listUser = getListUser;
        final View view = v;
        final ProgressBar progressBar = activity.findViewById(R.id.toolbarprogresshome);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(60, true);
        db.collection("UserDetail").whereEqualTo("email", query).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressBar.setProgress(100, true);
                        if(task.isSuccessful()) {
                            listUser.clear();
                            if(!task.getResult().isEmpty()) {
                                for (final QueryDocumentSnapshot document : task.getResult()) {
                                    // -------------------------------------------------------------
                                    Log.d("Success", document.getId() + " => " + document.getData());
                                    // -------------------------------------------------------------
                                    // Check friend is duplicated.
                                    final String uid = document.getData().get("uid").toString();
                                    db.collection("UserDetail").
                                            document(auth.getCurrentUser().getUid()).
                                            get().
                                            addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    DocumentSnapshot currentUserDocument = task.getResult();
                                                    UserDetail currentUser = currentUserDocument.toObject(UserDetail.class);
                                                    if(!currentUserDocument.getData().isEmpty()) {
                                                        for (UserChatRoom element:
                                                                currentUser.getChatRoom()) {
                                                            if(element.getDocumentName().equals(uid + auth.getCurrentUser().getUid())) {
                                                                duplicatedFriend = true;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    if (!duplicatedFriend) {
                                                        String email = document.getData().get("email").toString();
                                                        String displayName = document.getData().get("displayName").toString();
                                                        String photoUrl =  document.getData().get("photoURL").toString();
                                                        listUser.add(new UserDetail(uid, email, displayName, photoUrl));
                                                        adapter.notifyDataSetChanged();
                                                    } else {
                                                        Toast.makeText(view.getContext(), "Can not find the results!!! ", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });

                                }
                            } else {
                                listUser.clear();
                                adapter.notifyDataSetChanged();
                                Log.d("Success", "Can not find the results", task.getException());
                                Toast.makeText(view.getContext(), "Can not find the results!!! ", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.d("Fail", "Error getting document: ", task.getException());
                            Toast.makeText(view.getContext(), "Error getting result try again later!!! ", Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    public void getChatRoom(final RecyclerView.Adapter adapter, final List<UserChatRoom> userChatRooms, final View view) {
        DocumentReference docRef = db.collection("UserDetail").document(auth.getCurrentUser().getUid());
        docRef.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                // Check for error
                if (e != null) {
                    Log.w("Fail", "Listen failed.", e);
                    Toast.makeText(view.getContext(), "Error in getting chat room please try again later", Toast.LENGTH_LONG).show();
                    return;
                }
                // Check for change at where local or server.
                String source = documentSnapshot != null && documentSnapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";

                // Check for data is not null and display the new list to the screen.
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d("Get Data", source + " data: " + documentSnapshot.getData());
                    UserDetail currentUser = documentSnapshot.toObject(UserDetail.class);
                    if(!documentSnapshot.getData().isEmpty()) {
                        userChatRooms.clear();
                        userChatRooms.addAll(currentUser.getChatRoom());
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d("Get Data", source + " data: null");
                }
            }
        });
    }

    public void getRoomMessage(String documentName, final List<RoomMessage> messages, final RecyclerView.Adapter adapter, final Activity activity) {
        DocumentReference docRef = db.collection("ChatRoom").document(documentName);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                // Check for error
                if (e != null) {
                    Log.w("Fail", "Listen failed.", e);
                    Toast.makeText(activity, "Error in getting chat room please try again later", Toast.LENGTH_LONG).show();
                    return ;
                }

                // Check for change at where local or server.
                String source = documentSnapshot != null && documentSnapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";

                // Check for data is not null and display the new list to the screen.
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    ChatRoom chatRoom = documentSnapshot.toObject(ChatRoom.class);
                    MessageListActivity.setChatRoom(chatRoom);
                    Log.d("Get Data", source + " data: " + documentSnapshot.getData());
                    //check data and the first load
                    if(!documentSnapshot.getData().isEmpty()) {
                        if (isFirstLoadMessage()) {
                            // set title of the chat room.
                            TextView txtTitle = activity.findViewById(R.id.txt_chatTitle);
                            ImageView imgAvatarTitle = activity.findViewById(R.id.img_profileChatTitle);
                            String currentUser = auth.getCurrentUser().getUid();
                            for (UserDetail element:
                                    chatRoom.getMembers()) {
                                if (!element.getUid().equals(currentUser)) {
                                    txtTitle.setText(element.getDisplayName());
                                    Glide.with(activity).load(Uri.parse(element.getPhotoURL())).into(imgAvatarTitle);
                                }
                            }
                            messages.addAll(chatRoom.getRoomMessages());
                            adapter.notifyDataSetChanged();
                            setFirstLoadMessage(false);
                        } else {
                            messages.add(chatRoom.getRoomMessages().get(chatRoom.getRoomMessages().size() - 1));
                            adapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    Log.d("Get Data", source + " data: null");
                }
            }
        });
    }

    public void sendMessage(String documentName, String message, final List<RoomMessage> messages) {
        String id;
        if(messages.size() == 0) {
            id = "1";
        } else {
            id = Integer.parseInt(messages.get(messages.size() - 1).getId()) + 1 + "";
        }
        RoomMessage roomMessage = new RoomMessage(id, auth.getCurrentUser().getUid(), message, new Timestamp(new Date()));
        DocumentReference userDetailRef = db.collection("ChatRoom").document(documentName);
        userDetailRef.update("roomMessages", FieldValue.arrayUnion(roomMessage),
                "lastMessage", message,
                "timeStamp", new Timestamp(new Date()));
        DocumentReference docRef = db.collection("UserDetail").document(auth.getCurrentUser().getUid());
    }

    public boolean isFirstLoadMessage() {
        return firstLoadMessage;
    }

    public void setFirstLoadMessage(boolean firstLoadMessage) {
        this.firstLoadMessage = firstLoadMessage;
    }

    public void signUp(User user) {
        UserDetail userDetail = new UserDetail(
                user.getUid(),
                user.getEmail(),
                user.getName(),
                user.getPhotoUrl().toString(),
                new ArrayList<UserChatRoom>());
        db.collection("UserDetail").document(user.getUid()).set(userDetail);
    }

    public void updateProfile(String name, Uri photoUri, String userId) {
        db.collection("UserDetail").document(userId).update("displayName", name, "photoURL", photoUri.toString());
    }

    public void registerGoogleAccountToDatabase(final User currentUser) {
        db.collection("UserDetail").
                whereEqualTo("uid", currentUser.getUid()).
                get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().isEmpty()) {
                    signUp(currentUser);
                }
            }
        });

    }
}
