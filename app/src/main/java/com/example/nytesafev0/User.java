package com.example.nytesafev0;

public class User {
    public String name, email; // Properties of the user stored in the Firebase DB

    public User() { } // Firebase needs an empty constructor for user for some reason

    public User(String name, String email) { // Constructor to initialise the user properties
        this.name = name;
        this.email = email;
    }
}
