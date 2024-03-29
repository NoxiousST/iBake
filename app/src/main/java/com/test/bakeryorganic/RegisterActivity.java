package com.test.bakeryorganic;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mViewUser, mViewPassword, mViewRepassword;
    TextInputEditText text1, text2, text3;
    TinyDB tinydb;
    ArrayList<Preferences> pr = new ArrayList<>();
    Intent reg = new Intent("reg");
    String user, password, repassword;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mViewUser = findViewById(R.id.et_emailSignup);
        mViewPassword = findViewById(R.id.et_passwordSignup);
        mViewRepassword = findViewById(R.id.et_passwordSignup2);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);

        tinydb = new TinyDB(this);

        text3.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                razia();
                return true;
            }
            return false;
        });
        findViewById(R.id.button_signupSignup).setOnClickListener(v -> razia());
        findViewById(R.id.button_signupSignin).setOnClickListener(v -> finish());


        text1.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) {}
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewUser.setError(null);
                mViewUser.setStartIconTintList(ColorStateList.valueOf(Color.rgb(255, 255, 255)));
            }
        });
        text2.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) {}
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewPassword.setError(null);
                mViewPassword.setStartIconTintList(ColorStateList.valueOf(Color.rgb(255, 255, 255)));
            }
        });
        text3.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) {}
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewRepassword.setError(null);
                mViewRepassword.setStartIconTintList(ColorStateList.valueOf(Color.rgb(255, 255, 255)));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        pr = tinydb.getListPreferences("data");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void razia() {
        mViewUser.setError(null);
        mViewPassword.setError(null);
        mViewRepassword.setError(null);
        View fokus = null;
        boolean cancel = false;

        repassword = String.valueOf(Objects.requireNonNull(mViewRepassword.getEditText()).getText());
        user = String.valueOf(Objects.requireNonNull(mViewUser.getEditText()).getText());
        password = String.valueOf(Objects.requireNonNull(mViewPassword.getEditText()).getText());
        if (TextUtils.isEmpty(user)) {
            mViewUser.setError("This field is required");
            mViewUser.setStartIconTintList(ColorStateList.valueOf(Color.rgb(255, 69, 69)));
            fokus = mViewUser;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mViewPassword.setError("This field is required");
            mViewPassword.setStartIconTintList(ColorStateList.valueOf(Color.rgb(255, 69, 69)));
            fokus = mViewPassword;
            cancel = true;
        } else if (!cekPassword(password, repassword)) {
            mViewRepassword.setError("This password is incorrect");
            mViewRepassword.setStartIconTintList(ColorStateList.valueOf(Color.rgb(255, 69, 69)));
            fokus = mViewRepassword;
            cancel = true;
        } else if (containsName(pr, user)) {
            mViewUser.setError("This Username is already exist");
            mViewUser.setStartIconTintList(ColorStateList.valueOf(Color.rgb(255, 69, 69)));
            fokus = mViewUser;
            cancel = true;
        }

        if (cancel) {
            fokus.requestFocus();
        } else {
            pr.add(new Preferences(user, password));
            tinydb.putListPreferences("data", pr);
            reg.putExtra("user", user);
            reg.putExtra("pass", password);
            LocalBroadcastManager.getInstance(this).sendBroadcast(reg);
            finish();

        }
    }

    private boolean cekPassword(String password, String repassword) {
        return password.equals(repassword);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean containsName(final ArrayList<Preferences> list, final String name) {
        return list.stream().anyMatch(o -> name.equals(o.getUsername()));

    }
}