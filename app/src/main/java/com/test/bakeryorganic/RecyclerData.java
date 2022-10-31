package com.test.bakeryorganic;

public class RecyclerData {

    private final String title;
    private final int hrg, imgid, count;

    public String getTitle() {
        return title;
    }

    public int getHarga() {return hrg;}

    public int getImgid() {
        return imgid;
    }

    public int getCount() {
        return count;
    }

    public RecyclerData(String title, int hrg, int imgid, int count) {
        this.title = title;
        this.hrg = hrg;
        this.imgid = imgid;
        this.count = count;
    }
}