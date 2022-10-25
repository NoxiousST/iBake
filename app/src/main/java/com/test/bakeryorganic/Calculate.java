package com.test.bakeryorganic;

import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Currency;

public class Calculate {

    NumberFormat format;
    float hargaFl;
    String hargaStr;

    public String setHarga(String str) {
        hargaFl = Float.parseFloat(str);
        format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(0);
        format.setCurrency(Currency.getInstance("IDR"));
        hargaStr = format.format(hargaFl);
        return hargaStr;
    }

    public String getHarga(String str) {
        hargaStr = str.replaceAll("[^0-9]", "");
        hargaFl = Float.parseFloat(hargaStr);
        return hargaStr;
    }

}
