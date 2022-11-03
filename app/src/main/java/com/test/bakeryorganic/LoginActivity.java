package com.test.bakeryorganic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout mViewUser, mViewPassword;
    TextInputEditText text1, text2;
    TinyDB tinydb;
    ArrayList<Preferences> pr = new ArrayList<>();
    MaterialCardView error;
    Snackbar snackbar;
    View snackbarLayout;
    LinearLayout.LayoutParams lp;
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
        error = findViewById(R.id.error);

        tinydb = new TinyDB(this);

        text1.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) {}
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewUser.setError(null);
                error.setVisibility(View.GONE);
                mViewUser.setStartIconTintList(ColorStateList.valueOf(Color.rgb(255, 255, 255)));
            }
        });
        text2.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) {}
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewPassword.setError(null);
                error.setVisibility(View.GONE);
                mViewPassword.setStartIconTintList(ColorStateList.valueOf(Color.rgb(255, 255, 255)));
            }
        });

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (tinydb.getBoolean("deleted")) {
            snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Akun berhasil dihapus", Snackbar.LENGTH_LONG);
            snackbarLayout = snackbar.getView();
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(32, Resources.getSystem().getDisplayMetrics().heightPixels - 64, 32, 0);

            snackbarLayout.setLayoutParams(lp);
            snackbar.show();
            tinydb.putBoolean("deleted", false);
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
            mViewUser.setStartIconTintList(ColorStateList.valueOf(Color.rgb(255, 69, 69)));
            fokus = mViewUser;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mViewPassword.setError("This field is required");
            mViewPassword.setStartIconTintList(ColorStateList.valueOf(Color.rgb(255, 69, 69)));
            fokus = mViewPassword;
            cancel = true;
        } else if (!cekUser(user, password)) {
            mViewUser.setError("this");
            mViewPassword.setError("this");
            mViewUser.setStartIconTintList(ColorStateList.valueOf(Color.rgb(255, 69, 69)));
            mViewPassword.setStartIconTintList(ColorStateList.valueOf(Color.rgb(255, 69, 69)));
            if (mViewUser.getChildCount() == 2) {mViewUser.getChildAt(1).setVisibility(View.GONE);}
            if (mViewPassword.getChildCount() == 2) {mViewPassword.getChildAt(1).setVisibility(View.GONE);}
            error.setVisibility(View.VISIBLE);
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

    public void deleted(View view) {
        if (tinydb.getBoolean("deleted")) {
            Snackbar.make(view, "Akun berhasil dihapus", Snackbar.LENGTH_LONG).show();
            tinydb.putBoolean("deleted", false);
        }
    }

    private final BroadcastReceiver pMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            text1.setText(intent.getStringExtra("user"));
            text2.setText(intent.getStringExtra("pass"));
        }
    };
}