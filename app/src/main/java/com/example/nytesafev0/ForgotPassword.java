package com.example.nytesafev0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    EditText editTextResetPasswordEmail; // Variable for e-mail text box
    Button buttonResetPassword; // Variable for reset password button

    private FirebaseAuth authenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editTextResetPasswordEmail = (EditText) findViewById(R.id.editTextResetPasswordEmail); // Set variable for text box

        buttonResetPassword = (Button) findViewById(R.id.buttonResetPassword); // Set variable for button

        authenticator = FirebaseAuth.getInstance(); // Create an instance of firebase authenticator

        buttonResetPassword.setOnClickListener(new View.OnClickListener() { // Add a listener for when the reset password button is clicked
            @Override
            public void onClick(View view) { // When the reset password button is clicked
                resetPassword(); // Start the password reset process
            }
        });
    }

    private void resetPassword() {
        String email = editTextResetPasswordEmail.getText().toString().trim(); // Get the string of the e-mail

        if (email.isEmpty()) { // If the e-mail is empty
            editTextResetPasswordEmail.setError("E-Mail is required."); // Show an error message
            editTextResetPasswordEmail.requestFocus();
            return; // End the method early
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // If the e-mail is in an invalid format
            editTextResetPasswordEmail.setError("Please enter a valid email."); // Show an error message
            editTextResetPasswordEmail.requestFocus();
            return; // End the method early
        }

        // If the code reached here start attempting

        authenticator.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() { // Send password reset e-mail and add a listener for whether it completed successfully or not
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) { // If sending the reset password email was successful
                    Toast.makeText(ForgotPassword.this, "Check your email to reset your password. Make sure to check your spam folder.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(ForgotPassword.this, "Something went wrong.", Toast.LENGTH_LONG).show(); // Absolute disaster
                }
            }
        });
    }
}