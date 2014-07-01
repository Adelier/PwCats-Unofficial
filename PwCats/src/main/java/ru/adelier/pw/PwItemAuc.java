package ru.adelier.pw;

/**
 * Created by Adelier on 08.06.2014.
 */
public class PwItemAuc extends PwItem {

    private int lot_id;
    private String upToTime;

    public PwItemAuc(int id, String name, String imageLink, int count, Integer priceLo, Integer priceHi, int lot_id, String upToTime) {
        super(id, name, imageLink, count, priceLo, priceHi);

        this.lot_id = lot_id;
        this.upToTime = upToTime;
    }

    public String getUpToTime() {
        return upToTime;
    }

    public void setUpToTime(String upToTime) {
        this.upToTime = upToTime;
    }

    public int getLot_id() {
        return lot_id;
    }

    public void setLot_id(int lot_id) {
        this.lot_id = lot_id;
    }
}
