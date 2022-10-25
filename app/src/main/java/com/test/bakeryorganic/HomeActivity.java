package com.test.bakeryorganic;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.widget.LinearLayout.LayoutParams;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements OnItemClick {

    private RecyclerView recyclerView;
    private ArrayList<RecyclerData> recyclerDataArrayList;
    TextView total;
    public Intent i;
    ArrayList<String> myListNama = new ArrayList<>();
    ArrayList<String> myListHarga = new ArrayList<>();
    ArrayList<Integer> myListGambar = new ArrayList<>();
    int x1 = 1;
    int x2 = 1;
    int x3 = 1;
    int x4 = 1;
    int x5 = 1;
    int x6 = 1;
    int h = 0, tots = 0, x = 0, tot = 0;
    String status = "", strHarga;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Dashboard");
        recyclerView = findViewById(R.id.idCourseRV);
        total = findViewById(R.id.total);

        i = new Intent(HomeActivity.this, PaymentActivity.class);

        recyclerDataArrayList = new ArrayList<>();
        recyclerDataArrayList.add(new RecyclerData(getResources().getString(R.string.itemNama1), "20000", R.drawable.cinnamonbread));
        recyclerDataArrayList.add(new RecyclerData(getResources().getString(R.string.itemNama2), "25000", R.drawable.baguettebread));
        recyclerDataArrayList.add(new RecyclerData(getResources().getString(R.string.itemNama3), "30000", R.drawable.multiseedbread));
        recyclerDataArrayList.add(new RecyclerData(getResources().getString(R.string.itemNama4), "35000", R.drawable.focacciabread));
        recyclerDataArrayList.add(new RecyclerData(getResources().getString(R.string.itemNama5), "33500", R.drawable.walnutraisinbread));
        recyclerDataArrayList.add(new RecyclerData(getResources().getString(R.string.itemNama6), "45000", R.drawable.wholemealbread));

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(recyclerDataArrayList, this, this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        LocalBroadcastManager.getInstance(this).registerReceiver(iMessageReceiver, new IntentFilter("increment"));
        LocalBroadcastManager.getInstance(this).registerReceiver(dMessageReceiver, new IntentFilter("delete"));
        LocalBroadcastManager.getInstance(this).registerReceiver(cMessageReceiver, new IntentFilter("clear"));

        recyclerView.setOnClickListener(view -> {

        });

        if (total.getText().toString().equals(getResources().getString(R.string.tambah)))
            total.setEnabled(false);

        total.setOnClickListener(view -> {

            startActivity(i);
        });

    }

    private final BroadcastReceiver cMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            myListNama.clear();
            myListHarga.clear();
            myListGambar.clear();
            x1 = 1;
            x2 = 1;
            x3 = 1;
            x4 = 1;
            x5 = 1;
            x6 = 1;
            total.setText(R.string.tambah);
            total.setEnabled(false);
        }
    };
    private final BroadcastReceiver dMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int pos = intent.getIntExtra("pos", 0);
            myListNama.remove(pos);
            myListHarga.remove(pos);
            myListGambar.remove(pos);
            if (pos == 0) x1 = 1;
            else if (pos == 1) x2 = 1;
            else if (pos == 2) x3 = 1;
            else if (pos == 3) x4 = 1;
            else if (pos == 4) x5 = 1;
            else if (pos == 5) x6 = 1;
        }
    };

    private final BroadcastReceiver iMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String count = intent.getStringExtra("count");
            String nama = intent.getStringExtra("nama");
            String harga = intent.getStringExtra("harga");
            status = intent.getStringExtra("status");
            int icount = Integer.parseInt(count);

            int hrg = Integer.parseInt(harga);
            if (!total.getText().toString().equals(getResources().getString(R.string.tambah))) {
                tot = Integer.parseInt(total.getText().toString().replaceAll("[^0-9]", ""));
            }

            NumberFormat format = NumberFormat.getCurrencyInstance();
            format.setMaximumFractionDigits(0);
            format.setCurrency(Currency.getInstance("IDR"));

            if (status.equals("plus")) {
                tots = tot + hrg / (icount - 1);
                total.setText(format.format(tots));
            } else if (status.equals("minus")) {
                tots = tot - hrg / (icount + 1);
                if (tots == 0) {
                    total.setText(R.string.tambah);
                    total.setEnabled(false);
                } else {
                    total.setText(format.format(tots));
                }
            }
            i.putExtra("subt", format.format(tots));

            if (nama.equals(getResources().getString(R.string.itemNama1)) && icount > 0) {
                x1 = icount;
            } else if (nama.equals(getResources().getString(R.string.itemNama2)) && icount > 0) {
                x2 = icount;
            } else if (nama.equals(getResources().getString(R.string.itemNama3)) && icount > 0) {
                x3 = icount;
            } else if (nama.equals(getResources().getString(R.string.itemNama4)) && icount > 0) {
                x4 = icount;
            } else if (nama.equals(getResources().getString(R.string.itemNama5)) && icount > 0) {
                x5 = icount;
            } else if (nama.equals(getResources().getString(R.string.itemNama6)) && icount > 0) {
                x6 = icount;
            }
            i.putExtra("x1", x1);
            i.putExtra("x2", x2);
            i.putExtra("x3", x3);
            i.putExtra("x4", x4);
            i.putExtra("x5", x5);
            i.putExtra("x6", x6);
        }
    };


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
                Intent i = new Intent(HomeActivity.this, UpdateActivity.class);
                startActivity(i);
                return true;
            case R.id.logout:
                Preferences.clearLoggedInUser(getBaseContext());
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                finish();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onClickImage(String sum, String nama, String harga, int gambar) {
        total.setEnabled(true);


        strHarga = harga.replaceAll("[^0-9]", "");
        h = Integer.parseInt(strHarga);

        if (total.getText().toString().equals(getResources().getString(R.string.tambah))) x = h + 0;
        else {
            tot = Integer.parseInt(total.getText().toString().replaceAll("[^0-9]", ""));
            x = h + tot;
        }

        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(0);
        format.setCurrency(Currency.getInstance("IDR"));

        total.setText(format.format(x));

        if (!myListNama.contains(nama)) {
            myListNama.add(nama);
            myListHarga.add(strHarga);
            myListGambar.add(gambar);

        } else {
            if (nama.equals(getResources().getString(R.string.itemNama1))) {
                x1++;
            } else if (nama.equals(getResources().getString(R.string.itemNama2))) {
                x2++;
            } else if (nama.equals(getResources().getString(R.string.itemNama3))) {
                x3++;
            } else if (nama.equals(getResources().getString(R.string.itemNama4))) {
                x4++;
            } else if (nama.equals(getResources().getString(R.string.itemNama5))) {
                x5++;
            } else if (nama.equals(getResources().getString(R.string.itemNama6))) {
                x6++;
            }
        }

        i.putExtra("mylistnama", myListNama);
        i.putExtra("mylistharga", myListHarga);
        i.putExtra("mylistgambar", myListGambar);
        if (x1 > 0) i.putExtra("x1", x1);
        if (x2 > 0) i.putExtra("x2", x2);
        if (x3 > 0) i.putExtra("x3", x3);
        if (x4 > 0) i.putExtra("x4", x4);
        if (x5 > 0) i.putExtra("x5", x5);
        if (x6 > 0) i.putExtra("x6", x6);
        i.putExtra("subt", format.format(x));


    }


}