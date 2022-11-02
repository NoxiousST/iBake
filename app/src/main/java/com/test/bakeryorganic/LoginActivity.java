package com.test.bakeryorganic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout mViewUser, mViewPassword;
    TextInputEditText text1, text2;
    TinyDB tinydb;
    ArrayList<Preferences> pr = new ArrayList<>();
    Preferences prx;
    int position;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mViewUser = findViewById(R.id.et_emailSignin);
        mViewPassword = findViewById(R.id.et_passwordSignin);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);

        tinydb = new TinyDB(this);


        text2.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                razia();
                return true;
            }
            return false;
        });

        findViewById(R.id.button_signinSignin).setOnClickListener(v -> razia());
        findViewById(R.id.button_signupSignin).setOnClickListener(v -> startActivity(new Intent(getBaseContext(), RegisterActivity.class)));

        LocalBroadcastManager.getInstance(this).registerReceiver(pMessageReceiver, new IntentFilter("reg"));
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (getId() >= 0) {
            startActivity(new Intent(getBaseContext(), HomeActivity.class));
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void razia() {
        position = 0;
        pr = tinydb.getListPreferences("data");
        mViewUser.setError(null);
        mViewPassword.setError(null);
        View fokus = null;
        boolean cancel = false;

        String user = String.valueOf(Objects.requireNonNull(mViewUser.getEditText()).getText());
        String password = String.valueOf(Objects.requireNonNull(mViewPassword.getEditText()).getText());

        if (TextUtils.isEmpty(user)) {
            mViewUser.setError("This field is required");
            fokus = mViewUser;
            cancel = true;
        } else if (!cekUser(user, password)) {
            mViewUser.setError("Username or password is incorrect");
            fokus = mViewUser;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mViewPassword.setError("This field is required");
            fokus = mViewPassword;
            cancel = true;
        }

        if (cancel) fokus.requestFocus();
        else masuk();
    }

    private void masuk() {
        try {
            tinydb.putInt("login", position);
            startActivity(new Intent(getBaseContext(), HomeActivity.class));
            finish();
        } catch (Exception e) {
            tinydb.putInt("login", -1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean cekUser(String name, String password) {
        for (position = 0; position < pr.size(); position++)
            if (name.equals(pr.get(position).getUsername()) && password.equals(pr.get(position).getPassword()))
                return true;
        return false;
    }

    public Integer getId() {
        return tinydb.getInt("login");
    }

    private final BroadcastReceiver pMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            text1.setText(intent.getStringExtra("user"));
            text2.setText(intent.getStringExtra("pass"));
        }
    };
}