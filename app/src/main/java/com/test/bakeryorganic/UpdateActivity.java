package com.test.bakeryorganic;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


import java.util.Locale;

public class UpdateActivity extends AppCompatActivity {

    TextInputLayout updUser, updCurPass, updNewPass1, updNewPass2;
    TextInputEditText updUserText;
    ExtendedFloatingActionButton updBtn, rstBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Update Profile");
        actionBar.setDisplayHomeAsUpEnabled(true);
        updUser = findViewById(R.id.updUser);
        updBtn = findViewById(R.id.updBtn);

        updCurPass = findViewById(R.id.updCurPass);
        updNewPass1 = findViewById(R.id.updNewPass1);
        updNewPass2 = findViewById(R.id.updNewPass2);
        rstBtn = findViewById(R.id.rstBtn);

        updUserText = findViewById(R.id.updUserText);

        updUserText.setText(Preferences.getLoggedInUser(getBaseContext()));

        updBtn.setOnClickListener(view -> {
            updUser.setError(null);
            View fokus = null;
            boolean cancel = false;

            String user = String.valueOf(updUser.getEditText().getText());

            if (TextUtils.isEmpty(user)){
                updUser.setError("This field is required");
                fokus = updUser;
                cancel = true;
            }else if(cekUser(user)){
                updUser.setError("This Username is already exist");
                fokus = updUser;
                cancel = true;
            }
            if (cancel){
                fokus.requestFocus();
            }else{
                Preferences.setLoggedInUser(getBaseContext(),user);
                Preferences.updateRegisteredUser(getBaseContext(),user);
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

            String cur_password = String.valueOf(updCurPass.getEditText().getText());
            String new_password = String.valueOf(updNewPass1.getEditText().getText());
            String new_repassword = String.valueOf(updNewPass2.getEditText().getText());

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
            }else if (!cur_password.equals(Preferences.getLoggedInPass(getBaseContext()))) {
                updCurPass.setError("Incorrect current password");
                fokus = updCurPass;
                cancel = true;
            }

            if (cancel){
                fokus.requestFocus();
            }else{
                Preferences.setLoggedInPass(getBaseContext(),new_password);
                Preferences.updateRegisteredPass(getBaseContext(),new_password);
                Snackbar.make(view, "Password berhasil diupdate", Snackbar.LENGTH_LONG)
                        .setAction("OK", v ->{})
                        .show();

            }

        });

    }

    private boolean cekPassword(String password, String repassword){
        return password.equals(repassword);
    }

    private boolean cekUser(String user){
        return user.equals(Preferences.getRegisteredUser(getBaseContext()));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            //noinspection RestrictedApi
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
                Toast.makeText(this, "Call Center is Selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:0123456789"));
                startActivity(intent);
                return true;
            case R.id.msg:
                Toast.makeText(this, "SMS Center is Selected", Toast.LENGTH_SHORT).show();
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.setData(Uri.parse("smsto:0123456789"));
                smsIntent.putExtra("sms_body", "");
                startActivity(smsIntent);
                return true;
            case R.id.map:
                Toast.makeText(this, "Lokasi/Map is Selected", Toast.LENGTH_SHORT).show();
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", -7.4007514414175715, 110.68323302612266);
                Intent phoneIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(phoneIntent);
                return true;
            case R.id.upd:
                Toast.makeText(this, Preferences.getLoggedInUser(getBaseContext()) + " " + Preferences.getRegisteredPass(getBaseContext()), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout:
                Preferences.clearLoggedInUser(getBaseContext());
                startActivity(new Intent(getBaseContext(),LoginActivity.class));
                finish();
                return true;

        }
        return (super.onOptionsItemSelected(item));
    }
}