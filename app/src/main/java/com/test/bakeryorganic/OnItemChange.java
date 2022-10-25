package com.test.bakeryorganic;

public interface OnItemChange {
    void onIncDecClick (int pos, int count, String nama, String harga, String status, int gambar);

    void onDelete (int pos);
}
