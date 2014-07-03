package ru.adelier.pw;

/**
 * Created by Adelier on 08.06.2014.
 */
public class PwItemAuc extends PwItem {

    protected int lot_id;
    protected String upToTime;

    public PwItemAuc(int id, String name, String imageLink, int count, Integer priceLo, Integer priceHi, int lot_id, String upToTime) {
        super(id, name, imageLink, count, priceLo, priceHi);

        if (this.getPriceHi() == 0)
            this.priceHi = null;
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
