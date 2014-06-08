package ru.adelier.pw;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class PwcatsRequester implements Runnable {

    public enum Server {vega, orion, sirius, mira, terazet, altair,
        gelios, pegas, antaresm, kassiopeya, lira, andromeda, omega, persey};

	private int id;
	public int getId() {
		return id;
	}

	private float prob;
	private String server;
	
	private Float result = null;

	final private static String pwcatsUrlFormat = "http://pwcats.info/cats/%s/item/%d";
	public PwcatsRequester(int id, float prob, String server) {
		this.id = id;
		this.prob = prob;
		this.server = server;
	}

	private static String formatPwcatsUrl(String server, int itemId){
		return String.format(pwcatsUrlFormat, server, itemId);
	}
	
	@Override
	public void run() {
		Float price;
		try {
			price = requestItemPrice();
			result = price == null ? null : price * prob;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public List<PwItemCat> itemsCat(Server server, int id) {
        List<PwItemCat> items = new ArrayList<PwItemCat>();
        Document doc;
        try {
            doc = Jsoup.parse(new URL(formatPwcatsUrl(server.name(), id)), 10000);
        } catch (MalformedURLException e) {
            // normally shouldn't be here
            e.printStackTrace();
            return items;
        } catch (IOException e) {
            // normally shouldn't be here
            e.printStackTrace();
            return items;
        }

        Elements table = doc.body().getElementById("tabs").getElementsByTag("tr");
        for (int i = 1; i < table.size(); i++) {
            Elements cloumns = table.get(i).getElementsByTag("td");
            // names
            String nickname = cloumns.get(0).text();
            String catTitle = cloumns.get(1).text();

            // coord & location
            String[] sCoord = cloumns.get(3).text().split(" ");
            int x = Integer.parseInt(sCoord[0]);
            int y = Integer.parseInt(sCoord[0]);
            Pair<Integer, Integer> coord = new Pair<Integer, Integer>(x, y);
            PwItemCat.Location location =
                    PwItemCat.Location.valueOf(cloumns.get(3).text());

            // name & count
            String nameCount = cloumns.get(4).text();
            System.out.println(nameCount);

            // prices
            String sPriceLo = cloumns.get(5).text();
            String sPriceHi = cloumns.get(6).text();
            OptionalInt priceLo = myParseInt(sPriceLo);
            OptionalInt priceHi = myParseInt(sPriceHi);

            PwItemCat item = new PwItemCat(id, nameCount, 1, nickname, catTitle, coord, location, priceLo, priceHi);
        }
        return items;
    }
	
	private Float requestItemPrice() throws IOException, MalformedURLException {
//		Logger.getGlobal().fine("enter callable " + this);
		Document doc = Jsoup.parse(new URL(formatPwcatsUrl(server, id)), 10000);

		Integer hiPrice = Integer.MAX_VALUE;
		Integer loPrice = 0;
		
		Elements table = doc.body().getElementById("tabs").getElementsByTag("tr");
		for (int i = 1; i < table.size(); i++) {
			Elements cloumns = table.get(i).getElementsByTag("td");
			String sPriceLo = cloumns.get(5).text();
			String sPriceHi = cloumns.get(6).text();
            OptionalInt iPriceLo = myParseInt(sPriceLo);
            OptionalInt iPriceHi = myParseInt(sPriceHi);

			if (iPriceLo.isPresent())
				hiPrice = Math.min(hiPrice, iPriceLo.getAsInt());
			if (iPriceHi.isPresent())
				loPrice = Math.max(loPrice, iPriceHi.getAsInt());
		}

		Logger.getGlobal().fine("exit  callable " + this);
		if (hiPrice < 1.2 * loPrice)
			return (hiPrice + loPrice) / 2F;
		else if (loPrice > 0)
			return (float) loPrice;
		else if (hiPrice != Integer.MAX_VALUE)
			return (float) hiPrice;
		else {
			return null;
		}
	}

	private OptionalInt myParseInt(String s) {
		s = s.trim().replace(" ", "");
		if (s == null || s.equals(""))
			return OptionalInt.empty();
		Integer res = null;
		try {
			res = Integer.parseInt(s);
            return OptionalInt.of(res);
		} catch (NumberFormatException e) {
			return OptionalInt.empty();
		}
	}

	@Override
	public String toString() {
		return "PwcatsRequester [id=" + id + ", prob=" + prob + ", server="
				+ server + "]";
	}

	public Float getResult() {
		return result;
	}
}
