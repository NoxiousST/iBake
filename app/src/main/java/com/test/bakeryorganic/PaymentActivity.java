package com.test.bakeryorganic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.ContextCompat;
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
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity implements OnItemChange {

    private static final Locale locale = new Locale("id", "ID");
    private ArrayList<RecyclerPayment> recyclerPaymentArrayList;

    RecyclerView recyclerView;
    TextView total, ongkir, fullTotal, vkembali;
    PaymentViewAdapter adapter;
    ArrayList<String> myListNama;
    ArrayList<Integer> myListHarga, myListGambar, myListCount;
    GridLayoutManager layoutManager;
    ExtendedFloatingActionButton checkout;
    NestedScrollView touch;
    EditText saldo;
    NumberFormat format = NumberFormat.getCurrencyInstance(locale);
    Intent clr = new Intent("clear");
    Intent dlt = new Intent("delete");
    Intent inc = new Intent("change");
    Intent pass = new Intent("pass");
    Intent callIntent, smsIntent, mapIntent, updIntent;
    RecyclerPayment deletedItem;
    String swipeNama;
    BigDecimal value;
    TinyDB tinydb;

    int jumlahOngkir = 20000, zero = 0;
    int sub = 0, kembali = 0, tx, ty, getTotal, swipePosition, swipeGambar;
    int[] location;
    private static final int MAX_CLICK_DURATION = 500;
    private long startClickTime;
    boolean isChecked;
    double typeSaldo = 0, setKembali = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Pembayaran");
        total = findViewById(R.id.vsubtotal);
        ongkir = findViewById(R.id.vongkir);
        fullTotal = findViewById(R.id.vtotal);
        recyclerView = findViewById(R.id.idCourseRV);
        checkout = findViewById(R.id.checkout);
        touch = findViewById(R.id.relative);
        saldo = findViewById(R.id.vsaldo);
        vkembali = findViewById(R.id.vkembali);
        tinydb = new TinyDB(this);

        myListNama = getIntent().getStringArrayListExtra("mylistnama");
        myListHarga = getIntent().getIntegerArrayListExtra("mylistharga");
        myListGambar = getIntent().getIntegerArrayListExtra("mylistgambar");
        myListCount = getIntent().getIntegerArrayListExtra("mylistcount");

        format.setMaximumFractionDigits(0);

        getTotal = getIntent().getIntExtra("totalInt", 0);
        total.setText(format.format(getTotal));
        ongkir.setText(format.format(jumlahOngkir));
        fullTotal.setText(format.format((long) getTotal + jumlahOngkir));

        recyclerPaymentArrayList = new ArrayList<>();
        for (int i = 0; i < myListNama.size(); i++) {
            recyclerPaymentArrayList.add(new RecyclerPayment(myListNama.get(i), myListHarga.get(i), myListGambar.get(i), myListCount.get(i)));
        }

        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PaymentViewAdapter(recyclerPaymentArrayList, this, this);
        recyclerView.setAdapter(adapter);

        checkout.setEnabled(false);
        checkout.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startClickTime = Calendar.getInstance().getTimeInMillis();
                    break;
                case MotionEvent.ACTION_UP:
                    long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                    if (clickDuration < MAX_CLICK_DURATION) {
                        startRevealActivity(event.getX() + tx, event.getY() + ty);
                        for (Integer i : myListHarga)
                            kembali += i;
                        tinydb.putDouble("saldokembali", typeSaldo - (kembali + jumlahOngkir));
                        tinydb.putBoolean("ischeckout", true);
                        kembali = 0;
                        new Handler().postDelayed(() -> {
                            recyclerPaymentArrayList.clear();
                            LocalBroadcastManager.getInstance(this).sendBroadcast(clr);
                            finish();
                        }, 700);

                        break;
                    }
            }
            return true;
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                swipePosition = viewHolder.getLayoutPosition();
                deletedItem = recyclerPaymentArrayList.get(swipePosition);
                swipeNama = deletedItem.getTitle();
                swipeGambar = deletedItem.getImgid();

                onIncDecClick(swipePosition, swipeNama, 0, swipeGambar, 0);
            }
        }).attachToRecyclerView(recyclerView);

        saldo.addTextChangedListener(new MoneyTextWatcher(saldo) {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!recyclerPaymentArrayList.isEmpty()) {
                    value = MoneyTextWatcher.parseCurrencyValue(saldo.getText().toString());
                    typeSaldo = value.doubleValue();
                    saldoKembali();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        tinydb.putDouble("typesaldo", typeSaldo);

        pass.putStringArrayListExtra("passnama", myListNama);
        pass.putIntegerArrayListExtra("passharga", myListHarga);
        pass.putIntegerArrayListExtra("passgambar", myListGambar);
        pass.putIntegerArrayListExtra("passcount", myListCount);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pass);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isChecked = tinydb.getBoolean("ischeckout");

        if (isChecked) {setKembali = tinydb.getDouble("saldokembali");isChecked = false;}
        else {setKembali = tinydb.getDouble("typesaldo");}

        saldo.setText(format.format(setKembali));
    }

    private Point getPointOfView(View view) {
        location = new int[2];
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
        saldoKembali();
        myListNama.remove(pos);
        myListHarga.remove(pos);
        myListGambar.remove(pos);
        myListCount.remove(pos);
        recyclerPaymentArrayList.remove(pos);
        adapter.notifyItemRemoved(pos);
        adapter.notifyItemRangeChanged(pos, recyclerPaymentArrayList.size());
        dlt.putExtra("pos", pos);

        if (recyclerPaymentArrayList.isEmpty()) {
            format.setMaximumFractionDigits(0);

            total.setText(format.format(zero));
            ongkir.setText(format.format(zero));
            fullTotal.setText(format.format(zero));
            vkembali.setText("-");
            checkout.setEnabled(false);
            dlt.putExtra("isempty", true);
        } else dlt.putExtra("isempty", false);
        LocalBroadcastManager.getInstance(this).sendBroadcast(dlt);
    }

    @Override
    public void onIncDecClick(int pos, String nama, int harga, int gambar, int count) {
        myListHarga.set(pos, harga);
        myListCount.set(pos, count);
        sub = 0;
        for (Integer i : myListHarga) sub += i;

        getTotal = 0;
        total.setText(format.format(sub));
        fullTotal.setText(format.format((long) sub + jumlahOngkir));

        if (count > 0) {
            inc.putExtra("pos", pos);
            inc.putExtra("harga", harga);
            inc.putExtra("count", count);
            LocalBroadcastManager.getInstance(this).sendBroadcast(inc);
            recyclerPaymentArrayList.set(pos, new RecyclerPayment(nama, harga, gambar, count));
            adapter.notifyItemChanged(pos);
        }
        saldoKembali();
        if (count <= 0) {onDelete(pos);}
    }

    public void saldoKembali() {
        kembali = 0;
        for (Integer i : myListHarga) kembali += i;

        if (typeSaldo >= kembali + jumlahOngkir) {
            vkembali.setText(format.format(typeSaldo - (kembali + jumlahOngkir)));
            checkout.setEnabled(true);
        }
        else {
            vkembali.setText("-");
            checkout.setEnabled(false);
        }
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.call:
                callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:0123456789"));
                startActivity(callIntent);
                return true;
            case R.id.msg:
                smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.setData(Uri.parse("smsto:0123456789"));
                smsIntent.putExtra("sms_body", "");
                startActivity(smsIntent);
                return true;
            case R.id.map:
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", -7.4007514414175715, 110.68323302612266);
                mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(mapIntent);
                return true;
            case R.id.upd:
                updIntent = new Intent(PaymentActivity.this, UpdateActivity.class);
                startActivity(updIntent);
                return true;
            case R.id.logout:
                tinydb.putInt("login", -1);
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                finish();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    private void startRevealActivity(float x, float y) {
        int xc = (int) x;
        int yc = (int) y;

        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_X, xc);
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_Y, yc);

        ContextCompat.startActivity(this, intent, null);

        overridePendingTransition(0, 0);
    }
}
