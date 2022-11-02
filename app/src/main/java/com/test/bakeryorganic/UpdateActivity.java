package com.test.bakeryorganic;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class UpdateActivity extends AppCompatActivity {

    TextInputLayout updUser, updCurPass, updNewPass1, updNewPass2, dltPass;
    TextInputEditText updUserText;
    ExtendedFloatingActionButton updBtn, rstBtn, dltBtn;
    TinyDB tinydb;
    ArrayList<Preferences> pr = new ArrayList<>();
    int position;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Update Profile");
        actionBar.setDisplayHomeAsUpEnabled(true);
        updUser = findViewById(R.id.updUser);
        updBtn = findViewById(R.id.updBtn);

        updCurPass = findViewById(R.id.updCurPass);
        updNewPass1 = findViewById(R.id.updNewPass1);
        updNewPass2 = findViewById(R.id.updNewPass2);
        rstBtn = findViewById(R.id.rstBtn);

        dltPass = findViewById(R.id.dltPass);
        dltBtn = findViewById(R.id.dltBtn);

        updUserText = findViewById(R.id.updUserText);
        tinydb = new TinyDB(this);

        pr = tinydb.getListPreferences("data");
        position = tinydb.getInt("login");

        updUserText.setText(pr.get(position).getUsername());

        updBtn.setOnClickListener(view -> {
            updUser.setError(null);
            View fokus = null;
            boolean cancel = false;

            String user = String.valueOf(Objects.requireNonNull(updUser.getEditText()).getText());

            if (TextUtils.isEmpty(user)){
                updUser.setError("This field is required");
                fokus = updUser;
                cancel = true;
            }
            else if(cekUser(user)){
                updUser.setError("Username must be different");
                fokus = updUser;
                cancel = true;
            }
            if (cancel){
                fokus.requestFocus();
            }else{
                pr.get(position).setUsername(user);
                Snackbar.make(view, "Username berhasil diupdate", Snackbar.LENGTH_LONG)
                        .setAction("OK", v ->{})
                        .show();
            }
        });

        rstBtn.setOnClickListener(view -> {
            updCurPass.setError(null);
            updNewPass1.setError(null);
            updNewPass2.setError(null);
            View fokus = null;
            boolean cancel = false;

            String cur_password = String.valueOf(Objects.requireNonNull(updCurPass.getEditText()).getText());
            String new_password = String.valueOf(Objects.requireNonNull(updNewPass1.getEditText()).getText());
            String new_repassword = String.valueOf(Objects.requireNonNull(updNewPass2.getEditText()).getText());

            if (TextUtils.isEmpty(cur_password)){
                updCurPass.setError("This field is required");
                fokus = updCurPass;
                cancel = true;
            }else if (!cekPassword(new_password,new_repassword)){
                updNewPass1.setError("This password is incorrect");
                updNewPass1.setError("This password is incorrect");
                fokus = updNewPass1;
                cancel = true;
            }else if (cur_password.equals(new_password)) {
                updCurPass.setError("New Password should not be the same as old password");
                fokus = updCurPass;
                cancel = true;
            }else if (!cekCurrentPassword(cur_password)) {
                updCurPass.setError("Password is incorrect");
                fokus = updCurPass;
                cancel = true;
            }

            if (cancel){
                fokus.requestFocus();
            }else{
                pr.get(position).setPassword(new_password);
                Snackbar.make(view, "Password berhasil diupdate", Snackbar.LENGTH_LONG)
                        .setAction("OK", v ->{})
                        .show();
            }
        });

        dltBtn.setOnClickListener(view -> {
            dltPass.setError(null);
            View fokus = null;
            boolean cancel = false;

            String dlt_pass = String.valueOf(Objects.requireNonNull(dltPass.getEditText()).getText());

            if(!cekCurrentPassword(dlt_pass)){
                dltPass.setError("Password is incorrect");
                fokus = dltPass;
                cancel = true;
            }

            if (TextUtils.isEmpty(dlt_pass)){
                dltPass.setError("This field is required");
                fokus = dltPass;
                cancel = true;
            }

            if (cancel) fokus.requestFocus();
            else {
                pr.remove(position);
                tinydb.putListPreferences("data", pr);
                tinydb.putInt("login", -1);
                startActivity(new Intent(getBaseContext(),LoginActivity.class));
                Snackbar.make(view, "Akun berhasil dihapus", Snackbar.LENGTH_LONG)
                        .setAction("OK", v ->{})
                        .show();
            }
        });
    }

    private boolean cekPassword(String password, String repassword){
        return password.equals(repassword);
    }

    private boolean cekUser(String user){
        return pr.get(position).getUsername().equals(user);
    }

    private boolean cekCurrentPassword(String password){
        return pr.get(position).getPassword().equals(password);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.call:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:0123456789"));
                startActivity(intent);
                return true;
            case R.id.msg:
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.setData(Uri.parse("smsto:0123456789"));
                smsIntent.putExtra("sms_body", "");
                startActivity(smsIntent);
                return true;
            case R.id.map:
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", -7.4007514414175715, 110.68323302612266);
                Intent phoneIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(phoneIntent);
                return true;
            case R.id.upd:
                return true;
            case R.id.logout:
                tinydb.putInt("login", -1);
                startActivity(new Intent(getBaseContext(),LoginActivity.class));
                finish();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }
}