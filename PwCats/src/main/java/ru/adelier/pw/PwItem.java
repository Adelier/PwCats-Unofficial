package ru.adelier.pw;

import java.util.OptionalInt;

/**
 * Created by Adelier on 08.06.2014.
 */
public class PwItem {
    public PwItem(int id, String name, String imageLink, int count, OptionalInt priceLo, OptionalInt priceHi) {
        this.id = id;
        this.name = name;
        this.imageLink = imageLink;
        this.count = count;
        this.priceLo = priceLo;
        this.priceHi = priceHi;
    }

    private int id;
    private String name;
    private String imageLink;
    private int count;
    private OptionalInt priceLo;
    private OptionalInt priceHi;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public OptionalInt getPriceLo() {
        return priceLo;
    }

    public OptionalInt getPriceHi() {
        return priceHi;
    }

    public String getImageLink() {
        return imageLink;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PwItem pwItem = (PwItem) o;

        if (count != pwItem.count) return false;
        if (id != pwItem.id) return false;
        if (!name.equals(pwItem.name)) return false;
        if (!priceHi.equals(pwItem.priceHi)) return false;
        if (!priceLo.equals(pwItem.priceLo)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + count;
        result = 31 * result + priceLo.hashCode();
        result = 31 * result + priceHi.hashCode();
        return result;
    }
}
