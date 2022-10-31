package com.test.bakeryorganic;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout mViewUser, mViewPassword;
    TextInputEditText text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mViewUser=findViewById(R.id.et_emailSignin);
        mViewPassword =findViewById(R.id.et_passwordSignin);
        text2 =findViewById(R.id.text2);

        text2.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                razia();
                return true;
            }
            return false;
        });

        findViewById(R.id.button_signinSignin).setOnClickListener(v -> razia());
        findViewById(R.id.button_signupSignin).setOnClickListener(v -> startActivity(new Intent(getBaseContext(),RegisterActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Preferences.getLoggedInStatus(getBaseContext())){
            startActivity(new Intent(getBaseContext(),HomeActivity.class));
            finish();
        }
    }

    private void razia(){
        mViewUser.setError(null);
        mViewPassword.setError(null);
        View fokus = null;
        boolean cancel = false;

        String user = String.valueOf(Objects.requireNonNull(mViewUser.getEditText()).getText());
        String password = String.valueOf(Objects.requireNonNull(mViewPassword.getEditText()).getText());

        if (TextUtils.isEmpty(user)){
            mViewUser.setError("This field is required");
            fokus = mViewUser;
            cancel = true;
        }else if(!cekUser(user)){
            mViewUser.setError("This Username is not found");
            fokus = mViewUser;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)){
            mViewPassword.setError("This field is required");
            fokus = mViewPassword;
            cancel = true;
        }else if (!cekPassword(password)){
            mViewPassword.setError("This password is incorrect");
            fokus = mViewPassword;
            cancel = true;
        }

        if (cancel) fokus.requestFocus();
        else masuk();
    }

    private void masuk(){
        try {
            Preferences.setLoggedInUser(getBaseContext(),Preferences.getRegisteredUser(getBaseContext()));
            Preferences.setLoggedInPass(getBaseContext(),Preferences.getRegisteredPass(getBaseContext()));
            Preferences.setLoggedInStatus(getBaseContext(),true);
            startActivity(new Intent(getBaseContext(),HomeActivity.class));finish();
        } catch (Exception e) {
            Preferences.clearLoggedInUser(getBaseContext());
        }
    }

    private boolean cekPassword(String password){
        return password.equals(Preferences.getRegisteredPass(getBaseContext()));
    }

    private boolean cekUser(String user){
        return user.equals(Preferences.getRegisteredUser(getBaseContext()));
    }
}