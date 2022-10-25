package com.test.bakeryorganic;


import java.text.NumberFormat;
import java.util.Currency;

public class RecyclerData {

    private String title, hrg;
    private int imgid;

    public String getTitle() {
        return title;
    }

    public String getHarga() {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(0);
        format.setCurrency(Currency.getInstance("IDR"));
        hrg = format.format(Float.parseFloat(hrg));
        return hrg;
    }

    public int getImgid() {
        return imgid;
    }

    public RecyclerData(String title, String hrg, int imgid) {
        this.title = title;
        this.hrg = hrg;
        this.imgid = imgid;
    }
}