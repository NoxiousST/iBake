package com.test.bakeryorganic;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.daasuu.ei.Ease;
import com.daasuu.ei.EasingInterpolator;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private final ArrayList<RecyclerData> courseDataArrayList;
    private final Context mcontext;
    private OnItemClick mCallback;

    int hargaTemp = 0, numberFlt;
    int gambar;
    String numberStr, nama, harga, total;

    MaterialAlertDialogBuilder materialAlertDialogBuilder;
    View customAlertDialogView;
    TextView namaDlg, hargaDlg, description;
    ImageView gambarDlg;
    Animation anim;
    NumberFormat format;



    public RecyclerViewAdapter(ArrayList<RecyclerData> recyclerDataArrayList, Context mcontext, OnItemClick listener) {
        this.courseDataArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
        this.mCallback = listener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        RecyclerData recyclerData = courseDataArrayList.get(position);
        holder.namaTV.setText(recyclerData.getTitle());
        holder.hargaTV.setText(recyclerData.getHarga());
        holder.gambarIV.setImageResource(recyclerData.getImgid());

        holder.gambarIV.setOnClickListener(v -> {
            anim = AnimationUtils.loadAnimation(mcontext, R.anim.item_click);
            anim.setInterpolator(new EasingInterpolator(Ease.QUINT_IN_OUT));
            holder.cardView.startAnimation(anim);

            numberStr = holder.hargaTV.getText().toString().replaceAll("[^0-9]", "");
            numberFlt = Integer.parseInt(numberStr);
            hargaTemp += numberFlt;

            format = NumberFormat.getCurrencyInstance();
            format.setMaximumFractionDigits(0);
            format.setCurrency(Currency.getInstance("IDR"));

            total = format.format(hargaTemp);
            nama = holder.namaTV.getText().toString();
            harga = format.format(numberFlt);
            gambar = recyclerData.getImgid();
            mCallback.onClickImage(total, nama, harga, gambar);

        });

        holder.texts.setOnClickListener(view -> {
            materialAlertDialogBuilder = new MaterialAlertDialogBuilder(mcontext, R.style.MaterialAlertDialog_Rounded);
            customAlertDialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.order_details_custom_dialog, null, false);
            namaDlg = customAlertDialogView.findViewById(R.id.namaDialog);
            hargaDlg = customAlertDialogView.findViewById(R.id.hargaDialog);
            gambarDlg = customAlertDialogView.findViewById(R.id.gambarDialog);
            description = customAlertDialogView.findViewById(R.id.description);

            namaDlg.setText(holder.namaTV.getText().toString());
            hargaDlg.setText(holder.hargaTV.getText().toString());
            gambarDlg.setImageResource(recyclerData.getImgid());
            description.setText(R.string.desc+position+1);

            materialAlertDialogBuilder.setView(customAlertDialogView)
                    .setPositiveButton("OK", (dialogInterface, i) -> {})

                    .show();
        });

    }

    @Override
    public int getItemCount() {
        return courseDataArrayList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private final TextView namaTV, hargaTV;
        private final ImageView gambarIV;
        private final CardView cardView;
        private final LinearLayout texts;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            namaTV = itemView.findViewById(R.id.nama);
            hargaTV = itemView.findViewById(R.id.harga);
            gambarIV = itemView.findViewById(R.id.gambar);
            texts = itemView.findViewById(R.id.texts);
            cardView = itemView.findViewById(R.id.cardView);

        }
    }

}
