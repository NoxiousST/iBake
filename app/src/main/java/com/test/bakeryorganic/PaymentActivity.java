package com.test.bakeryorganic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;


public class PaymentActivity extends AppCompatActivity implements OnItemChange {

    private RecyclerView recyclerView;
    private ArrayList<RecyclerPayment> recyclerDataArrayList;
    TextView vsubtotal, vongkir, vtotal;
    PaymentViewAdapter adapter;
    String st;
    ArrayList<String> myListNama, myListHarga;
    ArrayList<Integer> myListGambar;
    GridLayoutManager layoutManager;
    ExtendedFloatingActionButton checkout;
    NestedScrollView touch;
    NumberFormat format = NumberFormat.getCurrencyInstance();
    Intent dIntent = new Intent("delete"), cIntent = new Intent("clear");;

    int x1, x2, x3, x4, x5, x6, y;
    int[] arr, icount;
    int ong = 20000, t = 0, zero = 0, tx, ty;
    int sub, hrg, sldHarga, x;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Pembayaran");
        vsubtotal = findViewById(R.id.vsubtotal);
        vongkir = findViewById(R.id.vongkir);
        vtotal = findViewById(R.id.vtotal);
        recyclerView = findViewById(R.id.idCourseRV);
        checkout = findViewById(R.id.checkout);
        touch = findViewById(R.id.relative);
        myListNama = getIntent().getStringArrayListExtra("mylistnama");
        myListHarga = getIntent().getStringArrayListExtra("mylistharga");
        myListGambar = getIntent().getIntegerArrayListExtra("mylistgambar");


        format.setMaximumFractionDigits(0);
        format.setCurrency(Currency.getInstance("IDR"));

        st = getIntent().getStringExtra("subt");
        vsubtotal.setText(st);
        vongkir.setText(format.format(ong));
        t = Integer.parseInt(st.replaceAll("[^0-9]", "")) + ong;
        vtotal.setText(format.format(t));

        x1 = getIntent().getIntExtra("x1", 0);
        x2 = getIntent().getIntExtra("x2", 0);
        x3 = getIntent().getIntExtra("x3", 0);
        x4 = getIntent().getIntExtra("x4", 0);
        x5 = getIntent().getIntExtra("x5", 0);
        x6 = getIntent().getIntExtra("x6", 0);

        int n = myListNama.size();
        arr = new int[n];

        for (int i = 0; i < n; i++) {
            if (myListNama.contains(getResources().getString(R.string.itemNama1)))
                arr[myListNama.indexOf(getResources().getString(R.string.itemNama1))] = x1;
            if (myListNama.contains(getResources().getString(R.string.itemNama2)))
                arr[myListNama.indexOf(getResources().getString(R.string.itemNama2))] = x2;
            if (myListNama.contains(getResources().getString(R.string.itemNama3)))
                arr[myListNama.indexOf(getResources().getString(R.string.itemNama3))] = x3;
            if (myListNama.contains(getResources().getString(R.string.itemNama4)))
                arr[myListNama.indexOf(getResources().getString(R.string.itemNama4))] = x4;
            if (myListNama.contains(getResources().getString(R.string.itemNama5)))
                arr[myListNama.indexOf(getResources().getString(R.string.itemNama5))] = x5;
            if (myListNama.contains(getResources().getString(R.string.itemNama6)))
                arr[myListNama.indexOf(getResources().getString(R.string.itemNama6))] = x6;
        }


        recyclerDataArrayList = new ArrayList<>();
        for (int i = 0; i < myListNama.size(); i++) {
            recyclerDataArrayList.add(new RecyclerPayment(myListNama.get(i), myListHarga.get(i), myListGambar.get(i), arr[i]));
        }

        adapter = new PaymentViewAdapter(recyclerDataArrayList, this, this);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        checkout.setEnabled(true);
        checkout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                startRevealActivity(v, event.getX()+tx, event.getY()+ty);
            }
            new Handler().postDelayed(() -> {
                recyclerDataArrayList.clear();
                LocalBroadcastManager.getInstance(this).sendBroadcast(cIntent);
                finish();
            },700);

            return true;
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getLayoutPosition();
                RecyclerPayment deletedItem = recyclerDataArrayList.get(position);

                x = Integer.parseInt(deletedItem.getHarga());
                y = deletedItem.getItemCount();

                sub = Integer.parseInt(vsubtotal.getText().toString().replaceAll("[^0-9]", ""));
                sub -= x*y;
                vsubtotal.setText(format.format(sub));
                sub += 20000;
                vtotal.setText(format.format(sub));

                onDelete(position);

            }

        }).attachToRecyclerView(recyclerView);


    }



    private Point getPointOfView(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        return new Point(location[0], location[1]);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Point point = getPointOfView(checkout);
        tx = point.x;
        ty = point.y;
    }

    @Override
    public void onDelete(int pos) {
        recyclerDataArrayList.remove(pos);
        adapter.notifyItemRemoved(pos);
        adapter.notifyItemRangeChanged(pos, recyclerDataArrayList.size());

        if (recyclerDataArrayList.isEmpty()) {

            format.setMaximumFractionDigits(0);
            format.setCurrency(Currency.getInstance("IDR"));

            vongkir.setText(format.format(zero));
            vtotal.setText(format.format(zero));
            recyclerDataArrayList.clear();
            checkout.setEnabled(false);
            LocalBroadcastManager.getInstance(this).sendBroadcast(cIntent);
        } else {
            dIntent.putExtra("pos", pos);
            LocalBroadcastManager.getInstance(this).sendBroadcast(dIntent);
        }
    }

    @Override
    public void onIncDecClick(int pos, int count, String nama, String harga, String status, int gambar) {

        String scount = String.valueOf(count);
        sldHarga = Integer.parseInt(harga.replaceAll("[^0-9]", ""));

        hrg = Integer.parseInt(harga);
        sub = Integer.parseInt(vsubtotal.getText().toString().replaceAll("[^0-9]", ""));

        if (status.equals("plus")) {sub += hrg;}
        else if (status.equals("minus")) {sub -= hrg;}

        format.setMaximumFractionDigits(0);
        format.setCurrency(Currency.getInstance("IDR"));

        vsubtotal.setText(format.format(sub));
        sub += 20000;
        vtotal.setText(format.format(sub));


        if (count == 0) onDelete(pos);
        else {
            recyclerDataArrayList.set(pos, new RecyclerPayment(nama, harga, gambar, count));
            adapter.notifyItemChanged(pos);
            Intent intent = new Intent("increment");
            intent.putExtra("count", scount);
            intent.putExtra("nama", nama);
            intent.putExtra("harga", harga);
            intent.putExtra("status", status);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }






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
                Intent i = new Intent(PaymentActivity.this, UpdateActivity.class);
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

    private void startRevealActivity(View v, float x, float y) {
        //calculates the center of the View v you are passing

        int xc = (int) x;
        int yc = (int) y;


        //create an intent, that launches the second activity and pass the x and y coordinates
        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_X, xc);
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_Y, yc);

        //just start the activity as an shared transition, but set the options bundle to null
        ActivityCompat.startActivity(this, intent, null);

        //to prevent strange behaviours override the pending transitions
        overridePendingTransition(0, 0);
    }



}