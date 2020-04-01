package com.lethdz.onlinechatdemo.auth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.lethdz.onlinechatdemo.FirebaseSingleton;
import com.lethdz.onlinechatdemo.HomeActivity;
import com.lethdz.onlinechatdemo.ProfileActivity;
import com.lethdz.onlinechatdemo.R;
import com.lethdz.onlinechatdemo.modal.User;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // TODO: Declare an instance of FirebaseSingleton
    private FirebaseSingleton instance;

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        // TODO: Initialize Firebase Auth
        instance = FirebaseSingleton.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.login_field, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signIn(view);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    /**
     * signIn Function:
     * @param view
     * Login the user to the system. The records are stored in Firebase Cloud.
     * Create a new signIn method which takes in an email address and password,
     * validates them, and then signs a user in with the signInWithEmailAndPassword method.
     */
    private static EditText txtEmail;
    private static EditText txtPassword;
    private static Button btnLogin;
    private static ProgressBar progressBar;

    public void signIn(View view) {
        txtEmail = view.findViewById(R.id.txt_Email);
        txtPassword = view.findViewById(R.id.txt_Password);
        btnLogin = view.findViewById(R.id.btn_Login);
        progressBar = getActivity().findViewById(R.id.toolbarprogress);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (txtEmail.getText().toString().equals("") || txtPassword.getText().toString().equals("")) {
                        Toast.makeText(getContext(), "Please Enter email or password.", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                    txtEmail.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    txtPassword.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    progressBar.setProgress(0);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(60, true);
                    instance.getmAuth().signInWithEmailAndPassword(
                            txtEmail.getText().toString(),
                            txtPassword.getText().toString())
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        Log.d("Tag", "signInWithEmail:success");
                                        User user = instance.getCurrentUserInformation();
                                        progressBar.setProgress(100, true);
                                        Toast.makeText(getContext(), "Authentication success.", Toast.LENGTH_SHORT)
                                                .show();
                                        updateUI(user);
                                    } else {
                                        Log.w("waring", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT)
                                                .show();
                                        progressBar.setProgress(100, true);
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                    }
                }
            });

    }

    private void updateUI(User currentUser) {
        if(currentUser != null) {
            Intent intent = new Intent(getContext(), HomeActivity.class);
            this.startActivity(intent);
            getActivity().finish();
        }
    }
}
