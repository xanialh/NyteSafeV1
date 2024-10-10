package com.example.nytesafev0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextSignUpName, editTextSignUpEmail, editTextSignUpPassword, editTextSignUpConfirmPassword; // Variables for all text boxes
    private Button buttonBackToLogin, buttonSignUp; // Variables for the log in and sign up buttons

    private FirebaseAuth authenticator; // Variable for a Firebase authenticator

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextSignUpName = (EditText) findViewById(R.id.editTextSignUpName); // Set variables for the text boxes
        editTextSignUpEmail = (EditText) findViewById(R.id.editTextSignUpEmail);
        editTextSignUpPassword = (EditText) findViewById(R.id.editTextSignUpPassword);
        editTextSignUpConfirmPassword = (EditText) findViewById(R.id.editTextSignUpConfirmPassword);

        buttonBackToLogin = (Button) findViewById(R.id.buttonBackToLogin); // Set variables for the buttons
        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);

        buttonSignUp.setOnClickListener(this); // Add a listeners for clicking the buttons
        buttonBackToLogin.setOnClickListener(this);

        authenticator = FirebaseAuth.getInstance(); // Create an instance of a firebase authenticator
    }

    @Override
    public void onClick(View clickedElement) { // When something in this page is clicked
        switch (clickedElement.getId()) { // Get the ID of the clicked element
            case R.id.buttonSignUp:
                userSignUp(); // Start user sign up process
                break;

            case R.id.buttonBackToLogin:
                startActivity(new Intent(this, Login.class));
                break;
        }
    }

    private void userSignUp() {
        String name = editTextSignUpName.getText().toString().trim(); // Get the name in the name text box
        String email = editTextSignUpEmail.getText().toString().trim(); // Get the e-mail in the e-mail text box
        String password = editTextSignUpPassword.getText().toString().trim(); // Get the password in the password text box
        String confirmPassword = editTextSignUpConfirmPassword.getText().toString().trim(); // Get the confirmed password in the confirm password text box

        if (email.isEmpty()) { // If the e-mail is missing
            editTextSignUpEmail.setError("E-Mail is required."); // Set an error
            editTextSignUpEmail.requestFocus();
            return; // End the method early
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // If the e-mail is in a non e-mail format
            editTextSignUpEmail.setError("Please enter a valid e-mail."); // Set an error
            editTextSignUpEmail.requestFocus();
            return; // End the method early
        }

        if (password.isEmpty()) { // If the password is missing
            editTextSignUpPassword.setError("Password is required."); // Set an error
            editTextSignUpPassword.requestFocus();
            return; // End the method early
        }
        else if (password.length() < 8) { // If the password is too short (later add a regular expression to check for other things e.g. different characters)
            editTextSignUpPassword.setError("Minimum password length is 8 characters."); // Set an error
            editTextSignUpPassword.requestFocus();
            return; // End the method early
        }
        else if (!password.equals(confirmPassword)) { // If the password and confirmed passwords don't match
            editTextSignUpConfirmPassword.setError("Passwords do not match."); // Set an error
            editTextSignUpConfirmPassword.requestFocus();
            return; // End the method early
        }

        if (name.isEmpty()) { // If the name is missing
            editTextSignUpName.setError("Name is required."); // Set an error
            editTextSignUpName.requestFocus();
            return; // End the method early
        }

        // If the code reaches here, attempt to register the details

        authenticator.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() { // Start registering with the given e-mail and password
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) { // If the user is new
                    User user = new User(name, email); // Create a user object with the given details

                    FirebaseDatabase.getInstance("https://nytesafev0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() { // Connect to the database and attempt to add the user
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) { // If the database was successfully updated
                                Toast.makeText(SignUp.this, "Successfully registered your account!", Toast.LENGTH_LONG).show(); // Show a message
                                startActivity(new Intent(SignUp.this, Login.class)); // Go to the login page
                            }
                            else { // If the database failed to update for whatever reason
                                authenticator.getCurrentUser().delete(); // Delete the newly made user from Firebase Authentication since the database failed to update with it
                                Toast.makeText(SignUp.this, "Failed to register your account. Please try again later.", Toast.LENGTH_LONG).show(); // Show an error message
                            }
                        }
                    });
                }
                else { // If the e-mail address is already taken
                    Toast.makeText(SignUp.this, "This e-mail address is already being used.", Toast.LENGTH_LONG).show(); // Show an error message
                }
            }
        });
    }
}