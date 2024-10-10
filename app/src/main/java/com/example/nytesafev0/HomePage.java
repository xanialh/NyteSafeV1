package com.example.nytesafev0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class HomePage extends AppCompatActivity implements View.OnClickListener {
    private Button startMapButton, sosButton;
    private CheckBox accel;
    private EditText phone;
    private static final int permissionToSendSMS = 0;

    private String userID; // Variable for the user's ID
    private FirebaseUser user; // Variable to hold user data
    private DatabaseReference dbRef; // Variable to reference the database

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

        findViewById(R.id.imageMenu).setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        NavigationView navigationView = findViewById(R.id.navigationView);
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(navigationView, navController);

        final TextView textViewTitle = findViewById(R.id.textViewTitle);

        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> textViewTitle.setText(navDestination.getLabel()));

        phone = findViewById(R.id.editTextPhone);

        startMapButton = findViewById(R.id.mapStart);
        startMapButton.setOnClickListener(this);

        sosButton = findViewById(R.id.SOSbutton);
        sosButton.setOnClickListener(this);
        sosButton.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick (View v){
                sendSMSMessage();
                return true;
            }
        });

        accel = findViewById(R.id.checkBoxAccel);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();

        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                if (accel.isChecked() && count > 2) {
                    sendSMSMessage();
                }
            }
        });
    }

    @Override
    public void onClick(View clickedElement) { // When something in this page is clicked
        switch (clickedElement.getId()) { // Get the ID of the clicked element
            case R.id.mapStart:
                startActivity(new Intent(this, MapsActivity.class));
                break;

            case R.id.SOSbutton:
                Toast.makeText(getApplicationContext(), "Hold to send out SOS alert", Toast.LENGTH_LONG).show();
                break;
        }
    }

    protected void sendSMSMessage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS))
            {
                //Toast.makeText(getApplicationContext(), "Already has perms", Toast.LENGTH_LONG).show(); ?????????????????????????????????????????????????
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, permissionToSendSMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case permissionToSendSMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone.getText().toString().trim(), null, "NyteSafe: The person from this number needs your help ASAP.", null, null);
                    Toast.makeText(getApplicationContext(), "Sending message...", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Failed to send the message, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public String getUser() {
        final String[] finalMsg = {"The person from this number"};

        user = FirebaseAuth.getInstance().getCurrentUser(); // Get the current user
        userID = user.getUid(); // Get the user's ID
        dbRef = FirebaseDatabase.getInstance("https://nytesafev0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users"); // Get the reference of the Users table in the database

        dbRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class); // Get the user data from the DB

                if (userProfile != null) { // If the user exists
                    finalMsg[0] = userProfile.name; // Get the name of the user
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return finalMsg[0];
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }
}