package ru.adelier.pw;

/**
 * Created by Adelier on 08.06.2014.
 */
public class PwItem {
    public PwItem(int id, String name, String desc, int count, Integer priceLo, Integer priceHi) {
        this.id = id;
        this.itemName = name;
        this.desc = desc;
        this.count = count;
        this.priceLo = priceLo;
        this.priceHi = priceHi;
    }

    private int id;
    private String itemName;
    private String desc;
    private int count;
    private Integer priceLo;
    private Integer priceHi;

    public int getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public int getCount() {
        return count;
    }

    public Integer getPriceLo() {
        return priceLo;
    }

    public Integer getPriceHi() {
        return priceHi;
    }

    public String getDesc() {
        return desc;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PwItem pwItem = (PwItem) o;

        if (count != pwItem.count) return false;
        if (id != pwItem.id) return false;
        if (!itemName.equals(pwItem.itemName)) return false;
        if (!priceHi.equals(pwItem.priceHi)) return false;
        if (!priceLo.equals(pwItem.priceLo)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + itemName.hashCode();
        result = 31 * result + count;
        result = 31 * result + priceLo.hashCode();
        result = 31 * result + priceHi.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PwItem{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", desc='" + desc + '\'' +
                ", count=" + count +
                ", priceLo=" + priceLo +
                ", priceHi=" + priceHi +
                '}';
    }
}
