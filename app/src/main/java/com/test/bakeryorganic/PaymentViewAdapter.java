package com.test.bakeryorganic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class PaymentViewAdapter extends RecyclerView.Adapter<PaymentViewAdapter.ViewHolder> {

    private static final Locale locale = new Locale("id", "ID");
    private final ArrayList<RecyclerPayment> itemDataArrayList;
    private final OnItemChange mCallback;
    final Context mcontext;
    int incHarga, decHarga, harga;
    int count;
    NumberFormat format = NumberFormat.getCurrencyInstance(locale);
    boolean first = true;
    ArrayList<Integer> pos = new ArrayList<>();

    public PaymentViewAdapter(ArrayList<RecyclerPayment> recyclerDataArrayList, Context mcontext, OnItemChange listener) {
        this.itemDataArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
        this.mCallback = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_recycler, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        format.setMaximumFractionDigits(0);

        RecyclerPayment recyclerData = itemDataArrayList.get(position);
        holder.namaPay.setText(recyclerData.getTitle());
        holder.hargaPay.setText(format.format(recyclerData.getHarga()));
        holder.gambarPay.setImageResource(recyclerData.getImgid());
        holder.itemCount.setText(String.valueOf(recyclerData.getItemCount()));

        holder.incr.setOnClickListener(view -> {
            harga = recyclerData.getHarga();
            count = recyclerData.getItemCount();

            incHarga = harga + (harga / count);
            count++;

            mCallback.onIncDecClick(position, recyclerData.getTitle(), incHarga, recyclerData.getImgid(), count);
        });

        holder.decr.setOnClickListener(view -> {
            harga = recyclerData.getHarga();
            count = recyclerData.getItemCount();

            decHarga = harga - (harga / count);
            count--;

            mCallback.onIncDecClick(position, recyclerData.getTitle(), decHarga, recyclerData.getImgid(), count);
        });

    }

    @Override
    public int getItemCount() {
        return itemDataArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView namaPay, hargaPay, itemCount;
        private final ImageView gambarPay;
        private final ImageButton incr, decr;
        private final MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            namaPay = itemView.findViewById(R.id.namaPay);
            hargaPay = itemView.findViewById(R.id.hargaPay);
            gambarPay = itemView.findViewById(R.id.gambarPay);
            incr = itemView.findViewById(R.id.incr);
            decr = itemView.findViewById(R.id.decr);
            itemCount = itemView.findViewById(R.id.itemCount);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }


}
