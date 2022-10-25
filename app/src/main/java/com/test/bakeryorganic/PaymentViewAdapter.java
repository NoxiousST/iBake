package com.test.bakeryorganic;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;

public class PaymentViewAdapter extends RecyclerView.Adapter<PaymentViewAdapter.ViewHolder> {

    private final ArrayList<RecyclerPayment> courseDataArrayList;
    private final Context mcontext;
    private OnItemChange mCallback;
    int incCount, decCount, stNumberInt;
    int incNumberFlt, decNumberFlt, incHarga, decHarga;
    String status, incTotal, decTotal, incNumberStr, decNumberStr, stNumberStr, s;
    NumberFormat format = NumberFormat.getCurrencyInstance();


    public PaymentViewAdapter(ArrayList<RecyclerPayment> recyclerDataArrayList, Context mcontext, OnItemChange listener) {
        this.courseDataArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
        this.mCallback = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RecyclerPayment recyclerData = courseDataArrayList.get(position);

        holder.namaPay.setText(recyclerData.getTitle());
        holder.hargaPay.setText(recyclerData.getHarga());
        holder.gambarPay.setImageResource(recyclerData.getImgid());
        holder.itemCount.setText(String.valueOf(recyclerData.getItemCount()));

        stNumberStr = holder.hargaPay.getText().toString().replaceAll("[^0-9]", "");
        stNumberInt = Integer.parseInt(stNumberStr) * recyclerData.getItemCount();
        format.setMaximumFractionDigits(0);
        format.setCurrency(Currency.getInstance("IDR"));
        s = format.format(stNumberInt);
        holder.hargaPay.setText(s);

        holder.incr.setOnClickListener(view -> {
            incNumberStr = holder.hargaPay.getText().toString().replaceAll("[^0-9]", "");
            incCount = Integer.parseInt(holder.itemCount.getText().toString());
            status = "plus";

            incCount++;
            incNumberFlt = Integer.parseInt(incNumberStr);
            incHarga = incNumberFlt / (incCount-1);

            format.setMaximumFractionDigits(0);
            format.setCurrency(Currency.getInstance("IDR"));
            incTotal = format.format(incNumberFlt + incHarga);

            holder.hargaPay.setText(incTotal);
            holder.itemCount.setText(String.valueOf(incCount));
            mCallback.onIncDecClick(position, incCount, holder.namaPay.getText().toString(), String.valueOf(incHarga), status, recyclerData.getImgid());
        });

        holder.decr.setOnClickListener(view -> {
            decNumberStr = holder.hargaPay.getText().toString().replaceAll("[^0-9]", "");
            decCount = Integer.parseInt(holder.itemCount.getText().toString());
            status = "minus";

            decCount--;
            decNumberFlt = Integer.parseInt(decNumberStr);
            decHarga = decNumberFlt / (decCount+1);

            format.setMaximumFractionDigits(0);
            format.setCurrency(Currency.getInstance("IDR"));
            decTotal = format.format(decNumberFlt - decHarga);

            holder.hargaPay.setText(decTotal);
            holder.itemCount.setText(String.valueOf(decCount));

            mCallback.onIncDecClick(position, decCount, holder.namaPay.getText().toString(), String.valueOf(decHarga), status, recyclerData.getImgid());

        });

    }

    @Override
    public int getItemCount() {
        return courseDataArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView namaPay, hargaPay, itemCount;
        private final ImageView gambarPay;
        private final ImageButton incr, decr;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            namaPay = itemView.findViewById(R.id.namaPay);
            hargaPay = itemView.findViewById(R.id.hargaPay);
            gambarPay = itemView.findViewById(R.id.gambarPay);
            incr = itemView.findViewById(R.id.incr);
            decr = itemView.findViewById(R.id.decr);
            itemCount = itemView.findViewById(R.id.itemCount);
        }
    }


}
