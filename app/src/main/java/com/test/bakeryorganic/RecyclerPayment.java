package com.test.bakeryorganic;

import java.text.NumberFormat;
import java.util.Currency;

public class RecyclerPayment {

    private String title, hrg;
    private int imgid, itemCount;

    public String getTitle() {
        return title;
    }

    public String getHarga() {
        return hrg;
    }

    public int getImgid() {
        return imgid;
    }

    public int getItemCount() {
        return itemCount;
    }

    public RecyclerPayment(String title, String hrg, int imgid, int itemCount) {
        this.title = title;
        this.hrg = hrg;
        this.imgid = imgid;
        this.itemCount = itemCount;
    }
}