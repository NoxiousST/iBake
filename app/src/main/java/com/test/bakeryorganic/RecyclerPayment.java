package com.test.bakeryorganic;

public class RecyclerPayment {

    private final String title;
    private final int hrg, imgid, itemCount;

    public String getTitle() {
        return title;
    }

    public int getHarga() {return hrg;}

    public int getImgid() {
        return imgid;
    }

    public int getItemCount() {
        return itemCount;
    }

    public RecyclerPayment(String title, int hrg, int imgid, int itemCount) {
        this.title = title;
        this.hrg = hrg;
        this.imgid = imgid;
        this.itemCount = itemCount;
    }
}