package ru.adelier.pw;

import javafx.util.Pair;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Created by Adelier on 08.06.2014.
 */
public class PwItemCat extends PwItem {
    public enum Location {ГДЗ, ГДВ, ГО, ГП, ГМ, ГЦ, ГИ, КБ};

    public PwItemCat(int id, String name, String imageLink, int count, String nickname, String catTitle, int[] coord, Location location, Integer priceLo, Integer priceHi) {
        super(id, name, imageLink, count, priceLo, priceHi);

        this.nickname = nickname;
        this.catTitle = catTitle;
        this.coord = coord;
        this.location = location;
    }

    private String nickname;
    private String catTitle;
    private int[] coord;
    private Location location;

    public String getNickname() {
        return nickname;
    }

    public String getCatTitle() {
        return catTitle;
    }

    public int[] getCoord() {
        return coord;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PwItemCat pwItemCat = (PwItemCat) o;

        if (!catTitle.equals(pwItemCat.catTitle)) return false;
        if (!coord.equals(pwItemCat.coord)) return false;
        if (location != pwItemCat.location) return false;
        if (!nickname.equals(pwItemCat.nickname)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + nickname.hashCode();
        result = 31 * result + catTitle.hashCode();
        result = 31 * result + coord.hashCode();
        result = 31 * result + location.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PwItemCat{" +
                "nickname='" + nickname + '\'' +
                ", catTitle='" + catTitle + '\'' +
                ", coord=" + coord[0] + " " + coord[1] +
                ", location=" + location +
                "} " + super.toString();
    }
}
