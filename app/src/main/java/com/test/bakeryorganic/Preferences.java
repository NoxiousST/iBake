package com.test.bakeryorganic;

public class Preferences {

    String username;
    String password;

    public Preferences(String myusername, String mypassword) {
        username = myusername;
        password = mypassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String myusername) {
        username = myusername;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String mypassword) {
        password = mypassword;
    }

}