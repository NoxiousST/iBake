package com.test.bakeryorganic;

public interface OnItemChange {
    void onIncDecClick (int pos, String nama, int harga, int gambar, int count);

    void onDelete (int pos);
}
