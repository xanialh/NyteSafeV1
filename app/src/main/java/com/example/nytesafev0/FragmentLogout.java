package com.example.nytesafev0;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentLogout#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentLogout extends Fragment {
    private String userID; // Variable for the user's ID
    private Button buttonLogout; // Variable for the logout button

    private FirebaseUser user; // Variable to hold user data
    private DatabaseReference dbRef; // Variable to reference the database

    public FragmentLogout() {
        // Required empty public constructor
    }

    public static FragmentLogout newInstance() {
        FragmentLogout fragment = new FragmentLogout();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        buttonLogout = (Button) getView().findViewById(R.id.buttonLogout); // Set variable for the logout button

        buttonLogout.setOnClickListener(new View.OnClickListener() { // When the logout button is pressed
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut(); // Logout the user
                startActivity(new Intent(getActivity(), Login.class)); // Switch to login page
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser(); // Get the current user
        userID = user.getUid(); // Get the user's ID
        dbRef = FirebaseDatabase.getInstance("https://nytesafev0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users"); // Get the reference of the Users table in the database

        TextView textViewLoggedInAs = (TextView) getView().findViewById(R.id.textViewLoggedInAs); // Set variable for the logged in as label

        dbRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class); // Get the user data from the DB

                if (userProfile != null) { // If the user exists
                    String name = userProfile.name; // Get the name of the user
                    String email = userProfile.email; // Get the email of the user
                    textViewLoggedInAs.setText("Logged in as: " + name + ", " + email); // Set the logged-in-as details
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_LONG).show(); // Failed to read from the DB for whatever reason
            }
        });

    }
}