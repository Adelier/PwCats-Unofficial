package ru.adelier.pw;

import java.util.List;

/**
 * Created by Adelier on 03.07.2014.
 */
public class PwItemCatDetailed extends PwItemCat {

    private int refineLevel;
    private List<String> bonuses;

    public PwItemCatDetailed(int id, String name, String desc, int count, String nickname, String catTitle,
                             int[] coord, Location location, Integer priceLo, Integer priceHi,
                             int refineLevel, List<String> bonuses) {
        super(id, name, desc, count, nickname, catTitle, coord, location, priceLo, priceHi);
        this.bonuses = bonuses;
        this.refineLevel = refineLevel;
    }

    public int getRefineLevel() {
        return refineLevel;
    }

    public void setRefineLevel(int refineLevel) {
        this.refineLevel = refineLevel;
    }

    public void setBonuses(List<String> bonuses) {
        this.bonuses = bonuses;
    }

    public List<String> getBonuses() {
        return bonuses;
    }

    @Override
    public String toString() {
        return "PwItemCatDetailed{" +
                "refineLevel=" + refineLevel +
                ", bonuses=" + bonuses +
                "} " + super.toString();
    }
}
