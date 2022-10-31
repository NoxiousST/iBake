package com.test.bakeryorganic;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements OnItemClick {

    private static final Locale locale = new Locale("id", "ID");
    ArrayList<RecyclerData> recyclerDataArrayList;
    RecyclerView recyclerView;
    TextView total;
    Intent intent, callIntent, smsIntent, mapIntent, updIntent;
    ArrayList<String> myListNama = new ArrayList<>();
    ArrayList<Integer> myListHarga = new ArrayList<>();
    ArrayList<Integer> myListGambar = new ArrayList<>();
    ArrayList<Integer> myListCount = new ArrayList<>();
    RecyclerViewAdapter adapter;
    ActionBar actionBar;
    String getMap;
    TinyDB tinydb;
    int getTotalHarga, getCount, getPosition;
    int totalInt = 0;
    boolean broadcasted = false, isClear, dataPassed;

    NumberFormat format = NumberFormat.getCurrencyInstance(locale);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Dashboard");

        recyclerView = findViewById(R.id.itemRV);
        total = findViewById(R.id.total);
        tinydb = new TinyDB(this);
        intent = new Intent(HomeActivity.this, PaymentActivity.class);

        recyclerDataArrayList = new ArrayList<>();
        recyclerDataArrayList.add(new RecyclerData(getResources().getString(R.string.itemNama1), 20000, R.drawable.cinnamonbread, 0));
        recyclerDataArrayList.add(new RecyclerData(getResources().getString(R.string.itemNama2), 25000, R.drawable.baguettebread, 0));
        recyclerDataArrayList.add(new RecyclerData(getResources().getString(R.string.itemNama3), 30000, R.drawable.multiseedbread, 0));
        recyclerDataArrayList.add(new RecyclerData(getResources().getString(R.string.itemNama4), 35000, R.drawable.focacciabread, 0));
        recyclerDataArrayList.add(new RecyclerData(getResources().getString(R.string.itemNama5), 33500, R.drawable.walnutraisinbread, 0));
        recyclerDataArrayList.add(new RecyclerData(getResources().getString(R.string.itemNama6), 45000, R.drawable.wholemealbread, 0));

        adapter = new RecyclerViewAdapter(recyclerDataArrayList, this, this);
        recyclerView.setAdapter(adapter);

        LocalBroadcastManager.getInstance(this).registerReceiver(iMessageReceiver, new IntentFilter("change"));
        LocalBroadcastManager.getInstance(this).registerReceiver(dMessageReceiver, new IntentFilter("delete"));
        LocalBroadcastManager.getInstance(this).registerReceiver(cMessageReceiver, new IntentFilter("clear"));
        LocalBroadcastManager.getInstance(this).registerReceiver(pMessageReceiver, new IntentFilter("pass"));
        total.setOnClickListener(view -> startActivity(intent));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (totalInt == 0)
            tinydb.putBoolean("isclear", true);
        tinydb.putListString("mylistnama", myListNama);
        tinydb.putListInt("mylistharga", myListHarga);
        tinydb.putListInt("mylistgambar", myListGambar);
        tinydb.putListInt("mylistcount", myListCount);
        broadcasted = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        totalInt = 0;
        broadcasted = tinydb.getBoolean("broadcast");
        dataPassed = tinydb.getBoolean("getpass");
        isClear = tinydb.getBoolean("isclear");

        if (isClear) {
            setClear();
            isClear = false;
        }
        else if (!broadcasted) {
            myListNama = tinydb.getListString("mylistnama");
            myListHarga = tinydb.getListInt("mylistharga");
            myListGambar = tinydb.getListInt("mylistgambar");
            myListCount = tinydb.getListInt("mylistcount");
            retrieveTotal();
        } else if (dataPassed) {
            myListNama = tinydb.getListString("getpassnama");
            myListHarga = tinydb.getListInt("getpassharga");
            myListGambar = tinydb.getListInt("getpassgambar");
            myListCount = tinydb.getListInt("getpasscount");
            dataPassed = false;
            retrieveTotal();
        }
    }

    private final BroadcastReceiver pMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dataPassed = true;
            tinydb.putListString("getpassnama", intent.getStringArrayListExtra("passnama"));
            tinydb.putListInt("getpassharga", intent.getIntegerArrayListExtra("passharga"));
            tinydb.putListInt("getpassgambar", intent.getIntegerArrayListExtra("passgambar"));
            tinydb.putListInt("getpasscount", intent.getIntegerArrayListExtra("passcount"));
            tinydb.putBoolean("getpass", dataPassed);
        }
    };

    private final BroadcastReceiver cMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            tinydb.putBoolean("isclear", true);
            setClear();
        }
    };

    private final BroadcastReceiver dMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            broadcasted = true;
            tinydb.putBoolean("broadcast", true);
            totalInt = 0;

            getPosition = intent.getIntExtra("pos", 0);
            isClear = intent.getBooleanExtra("isempty", false);
            myListNama.remove(getPosition);
            myListHarga.remove(getPosition);
            myListGambar.remove(getPosition);
            myListCount.remove(getPosition);

            for (Integer i : myListHarga)
                totalInt += i;
            format.setMaximumFractionDigits(0);
            total.setText(format.format(totalInt));
        }
    };

    private final BroadcastReceiver iMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            broadcasted = true;
            tinydb.putBoolean("broadcast", true);
            totalInt = 0;

            getPosition = intent.getIntExtra("pos", 0);
            getTotalHarga = intent.getIntExtra("harga", 0);
            getCount = intent.getIntExtra("count", 0);

            if (myListCount.get(getPosition) > getCount) {totalInt += getTotalHarga/getCount;}
            else if (myListCount.get(getPosition) < getCount) {totalInt -= getTotalHarga/getCount;}
            myListHarga.set(getPosition, getTotalHarga);
            myListCount.set(getPosition, getCount);

            total.setText(format.format(totalInt));
        }
    };

    @Override
    public void onClickImage(String nama, int harga, int gambar, int count) {
        total.setEnabled(true);
        totalInt = 0;

        if (!myListNama.contains(nama)) {
            myListNama.add(nama);
            myListHarga.add(harga);
            myListGambar.add(gambar);
            myListCount.add(1);
        } else {
            myListCount.set(myListNama.indexOf(nama), myListCount.get(myListNama.indexOf(nama))+1);
            myListHarga.set(myListNama.indexOf(nama), harga * myListCount.get(myListNama.indexOf(nama)));
        }

        broadcasted = false;
        tinydb.putBoolean("broadcast", false);
        tinydb.putBoolean("isclear", false);
        retrieveTotal();
    }

    public void retrieveTotal() {
        for (Integer i : myListHarga)
            totalInt += i;

        if (!isClear) {
            format.setMaximumFractionDigits(0);
            total.setText(format.format(totalInt));
        }
        isClear = false;
        intent.putExtra("mylistnama", myListNama);
        intent.putExtra("mylistharga", myListHarga);
        intent.putExtra("mylistgambar", myListGambar);
        intent.putExtra("mylistcount", myListCount);
        intent.putExtra("totalInt", totalInt);
    }

    public void setClear() {
        isClear = true;
        tinydb.putBoolean("broadcast", true);
        broadcasted = true;
        myListNama.clear();
        myListHarga.clear();
        myListGambar.clear();
        myListCount.clear();

        total.setText(R.string.tambah);
        total.setEnabled(false);
    }

    @Override
    @SuppressLint("RestrictedApi")
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.call:
                callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:0123456789"));
                startActivity(callIntent);
                return true;
            case R.id.msg:
                smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.setData(Uri.parse("smsto:0123456789"));
                smsIntent.putExtra("sms_body", "Hello There");
                startActivity(smsIntent);
                return true;
            case R.id.map:
                getMap = String.format(Locale.ENGLISH, "geo:%f,%f", -7.4007514414175715, 110.68323302612266);
                mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getMap));
                startActivity(mapIntent);
                return true;
            case R.id.upd:
                updIntent = new Intent(HomeActivity.this, UpdateActivity.class);
                startActivity(updIntent);
                return true;
            case R.id.logout:
                Preferences.clearLoggedInUser(getBaseContext());
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                finish();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }



}