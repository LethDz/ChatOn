package com.lethdz.onlinechatdemo.home;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lethdz.onlinechatdemo.R;
import com.lethdz.onlinechatdemo.modal.UserDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FindFriendFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FindFriendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindFriendFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // Initiate firestore database
    private FirebaseFirestore db;
    // Initiate user list
    private List<UserDetail> listUser = new ArrayList<>();
    //Initiate RecylerView
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    public FindFriendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindFriendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindFriendFragment newInstance(String param1, String param2) {
        FindFriendFragment fragment = new FindFriendFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_friend, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView(view);
        setupSearch(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setupSearch(View view) {
        db = FirebaseFirestore.getInstance();
        SearchView search = view.findViewById(R.id.s_searchFriend);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                db.collection("UserDetail").whereEqualTo("displayName", query).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    listUser.clear();
                                    if(!task.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("Success", document.getId() + " => " + document.getData());
                                            String uid = document.getData().get("uid").toString();
                                            String email = document.getData().get("email").toString();
                                            String displayName = document.getData().get("displayName").toString();
                                            Uri photoUrl = document.getData().get("photoUri") == null ? null : (Uri) document.getData().get("photoUri");
                                            listUser.add(new UserDetail(uid, email, displayName, photoUrl));
                                            adapter.notifyDataSetChanged();
                                        }
                                    } else {
                                        listUser.clear();
                                        adapter.notifyDataSetChanged();
                                        Log.d("Success", "Can not find the results", task.getException());
                                        Toast.makeText(getContext(), "Can not find the results!!! ", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Log.d("Fail", "Error getting document: ", task.getException());
                                    Toast.makeText(getContext(), "Error getting result try again later!!! ", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.rv_Friend);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FindFriendRecylerViewAdapter(listUser);
        recyclerView.setAdapter(adapter);
    }
}
