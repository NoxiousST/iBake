package com.test.bakeryorganic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.daasuu.ei.Ease;
import com.daasuu.ei.EasingInterpolator;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private static final Locale locale = new Locale("id", "ID");
    private final ArrayList<RecyclerData> courseDataArrayList;
    private final Context mcontext;
    private long startClickTime;
    private static final int MAX_CLICK_DURATION = 500;
    int harga;
    int gambar, count;
    View customAlertDialogView;
    String nama;
    TextView namaDlg, hargaDlg, description;
    ImageView gambarDlg;
    Animation maskin, maskout;
    OnItemClick mCallback;
    MaterialAlertDialogBuilder materialAlertDialogBuilder;
    NumberFormat format = NumberFormat.getCurrencyInstance(locale);
    ArrayList<Integer> pos = new ArrayList<>();
    boolean isPos;


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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        format.setMaximumFractionDigits(0);

        RecyclerData recyclerData = courseDataArrayList.get(position);
        holder.namaTV.setText(recyclerData.getTitle());
        holder.hargaTV.setText(format.format(recyclerData.getHarga()));
        holder.gambarIV.setImageResource(recyclerData.getImgid());


        holder.gambarIV.setOnTouchListener((v, event) -> {
            maskin = AnimationUtils.loadAnimation(mcontext, R.anim.maskin);
            maskout = AnimationUtils.loadAnimation(mcontext, R.anim.maskout);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    holder.cardView.setScaleX((float) 0.95);
                    holder.cardView.setScaleY((float) 0.95);
                    startClickTime = Calendar.getInstance().getTimeInMillis();
                    break;

                case MotionEvent.ACTION_UP:
                    long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;

                    isPos = !pos.contains(position);

                    holder.cardView.setScaleX(1);
                    holder.cardView.setScaleY(1);

                    if (clickDuration < MAX_CLICK_DURATION && isPos) {

                        maskin.setInterpolator(new EasingInterpolator(Ease.QUINT_OUT));
                        holder.relative.setVisibility(View.VISIBLE);
                        holder.relative.startAnimation(maskin);
                        pos.add(position);

                        nama = recyclerData.getTitle();
                        harga = recyclerData.getHarga();
                        gambar = recyclerData.getImgid();
                        count = recyclerData.getCount() + 1;
                        mCallback.onClickImage(nama, harga * count, gambar, count);

                        new Handler().postDelayed(() -> {
                            pos.remove(0);
                        }, 1700);

                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    holder.cardView.setScaleX(1);
                    holder.cardView.setScaleY(1);
                    break;
            }

            maskin.setAnimationListener(new Animation.AnimationListener() {
                @Override public void onAnimationStart(Animation animation) {}
                @Override public void onAnimationRepeat(Animation animation) {}
                @Override public void onAnimationEnd(Animation animation) {
                    maskout.setInterpolator(new EasingInterpolator(Ease.QUINT_IN));
                    holder.relative.startAnimation(maskout);
                }
            });
            return true;
        });

        holder.texts.setOnClickListener(view -> {
            materialAlertDialogBuilder = new MaterialAlertDialogBuilder(mcontext, R.style.MaterialAlertDialog_Rounded);
            customAlertDialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.order_details_custom_dialog, null, false);
            namaDlg = customAlertDialogView.findViewById(R.id.namaDialog);
            hargaDlg = customAlertDialogView.findViewById(R.id.hargaDialog);
            gambarDlg = customAlertDialogView.findViewById(R.id.gambarDialog);
            description = customAlertDialogView.findViewById(R.id.description);

            namaDlg.setText(recyclerData.getTitle());
            hargaDlg.setText(format.format(recyclerData.getHarga()));
            gambarDlg.setImageResource(recyclerData.getImgid());
            description.setText(R.string.desc + position + 1);

            materialAlertDialogBuilder.setView(customAlertDialogView)
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                    })
                    .show();
        });
    }

    @Override
    public int getItemCount() {return courseDataArrayList.size();}

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private final TextView namaTV, hargaTV;
        private final ImageView gambarIV;
        private final MaterialCardView cardView;
        private final LinearLayout texts;
        private final RelativeLayout relative;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            namaTV = itemView.findViewById(R.id.nama);
            hargaTV = itemView.findViewById(R.id.harga);
            gambarIV = itemView.findViewById(R.id.gambar);
            texts = itemView.findViewById(R.id.texts);
            cardView = itemView.findViewById(R.id.cardView);
            relative = itemView.findViewById(R.id.relative);
        }
    }
}
