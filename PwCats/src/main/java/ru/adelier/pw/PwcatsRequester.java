package ru.adelier.pw;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class PwcatsRequester {

    // same in values/servers.xml
    public enum Server {vega, orion, sirius, mira, terazet, altair,
        gelios, pegas, antares, kassiopeya, lira, andromeda, omega, persey};

	private static String formatPwcatsCatUrl(String server, int itemId){
        final String pwcatsUrlFormat = "http://pwcats.info/cats/%s/item/%d";
		return String.format(pwcatsUrlFormat, server, itemId);
	}
    private static String formatPwcatsAucUrl(String server, int itemId){
        final String pwcatsUrlFormat = "http://pwcats.info/cats/%s/auc/%d";
        return String.format(pwcatsUrlFormat, server, itemId);
    }
    private static String formatItemStarsUrl(String server, int starCount){
        final String pwcatsUrlFormat = "http://pwcats.info/cats/%s/star/%d";
        return String.format(pwcatsUrlFormat, server, starCount);
    }
    private static String formatRefineUrl(String server){
        final String pwcatsUrlFormat = "http://pwcats.info/cats/%d/level_item";
        return String.format(pwcatsUrlFormat, server);
    }
    private static String formatProfitUrl(String server){
        final String pwcatsUrlFormat = "http://pwcats.info/cats/%s/pro";
        return String.format(pwcatsUrlFormat, server);
    }

    public static List<PwItemCat> itemsCat(Server server, int id) {
        List<PwItemCat> items = new ArrayList<PwItemCat>();
        Document doc = openDocument(formatPwcatsCatUrl(server.name(), id));
        if (doc == null)
            return items;

        Elements table = doc.body().getElementById("tabs").getElementsByTag("tr");
        for (int i = 1; i < table.size(); i++) {
            Elements columns = table.get(i).getElementsByTag("td");
            // names
            String nickname = columns.get(0).text();
            String catTitle = columns.get(1).text();

            // coord & location
            String[] sCoord = columns.get(2).text().split(" ");
            int x = Integer.parseInt(sCoord[0]);
            int y = Integer.parseInt(sCoord[1]);
            int[] coord = new int[]{x, y};
            PwItemCat.Location location =
                    PwItemCat.Location.valueOf(columns.get(3).text());

            // name & count
            String[] nameCount = columns.get(4).text().split("[()]");
            String name = nameCount[0].trim();
            int count = Integer.parseInt(nameCount[1]);
            String imageLink = columns.get(4).child(0).toString();

            // prices
            String sPriceLo = columns.get(6).text();
            String sPriceHi = columns.get(5).text(); // 6 is low, 5 is high
            Integer priceLo = myParseInt(sPriceLo);
            Integer priceHi = myParseInt(sPriceHi);

            PwItemCat item = new PwItemCat(id, name, imageLink, count, nickname, catTitle, coord, location, priceLo, priceHi);
            items.add(item);
        }
        return items;
    }
    private static Document openDocument(String url) {
        Document doc;
        try {
            doc = Jsoup.parse(new URL(url), 10000);
        } catch (MalformedURLException e) {
            // normally shouldn't be here
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // normally shouldn't be here
            e.printStackTrace();
            return null;
        }
        return doc;
    }
    public static List<PwItemAuc> itemsAuc(Server server, int id) {
        List<PwItemAuc> items = new ArrayList<PwItemAuc>();
        Document doc = openDocument(formatPwcatsAucUrl(server.name(), id));
        if (doc == null)
            return items;

        Elements table = doc.body().getElementById("tabs").getElementsByTag("tr");
        for (int i = 1; i < table.size(); i++) {
            Elements columns = table.get(i).getElementsByTag("td");
            // lot
            int lot_id = Integer.parseInt(columns.get(0).text());

            // name & count
            String[] nameCount = columns.get(1).text().split("[()]");
            String name = nameCount[0].trim();
            int count = Integer.parseInt(nameCount[1]);

            String imageLink = columns.get(1).child(0).toString();
//            System.out.printf("%s, %d, %s\n", name, count, imageLink);
//          >>Недописанная небесная глава, 34, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 30, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 41, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 59, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 41, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 20, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 4, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 17, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 18, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 14, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 19, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 58, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 55, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 98, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 70, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 1, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 3, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 2, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 5, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 2, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 13, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 20, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 20, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 2, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 20, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 20, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 6, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 1, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />

            // prices
            String sPriceLo = columns.get(2).text();
            String sPriceHi = columns.get(3).text();
            Integer priceLo = myParseInt(sPriceLo);
            Integer priceHi = myParseInt(sPriceHi);

            // upToTime
            String upToTime = columns.get(5).text();

            PwItemAuc item = new PwItemAuc(id, name, imageLink, count, priceLo, priceHi, lot_id, upToTime);
            items.add(item);
        }
        return items;
    }

	private static Integer myParseInt(String s) {
		s = s.trim().replace(" ", "");
		if (s == null || s.equals(""))
			return null;
		Integer res = null;
		try {
			res = Integer.parseInt(s);
            return res;
		} catch (NumberFormatException e) {
			return null;
		}
	}

    public static List<PwItemCatDetailed> itemsStars(Server server, int starsCount) {
        if (starsCount < 1 || starsCount > 3)
            throw new IllegalArgumentException("starsCount should be in {1,2,3}");
        List<PwItemCatDetailed> items = new ArrayList<PwItemCatDetailed>(70);
        Document doc = openDocument(formatItemStarsUrl(server.name(), starsCount));
        if (doc == null)
            return items;

        Elements table = doc.body().getElementsByClass("flexigrid").get(0).getElementsByTag("tr");
        for (int i = 1; i < table.size(); i++) {
            Elements columns = table.get(i).getElementsByTag("td");
            // name & desc
            String name = columns.get(0).text().trim();
            String[] splittedUrl = columns.get(0).child(0).child(0).attr("href").split("/");
            int id = Integer.parseInt( splittedUrl[splittedUrl.length - 1] );
            String desc = columns.get(0).child(0).child(0).child(0).attr("title");
            //desc = desc.substring(1, desc.length() - 1);

            // prices
            String sPrice = columns.get(1).text().replace(" ", "");
            Integer priceHi = myParseInt(sPrice);
            Integer priceLo = null;

            String nickname = columns.get(2).text();
            String catTitle = columns.get(3).text();
            String[] sCoord = columns.get(4).text().split(" ");
            int [] coord = {Integer.parseInt(sCoord[0]), Integer.parseInt(sCoord[1])};
            PwItemCat.Location location = PwItemCat.Location.valueOf( columns.get(5).text() );

            String[] sRefineLevel = desc.split(name)[1].split("<")[0].trim().split("\\+");
            int refineLevel;
            if (sRefineLevel.length == 1)
                refineLevel = 0;
            else // there was +nn
                refineLevel = Integer.parseInt( sRefineLevel[1] );
            List<String> bonuses = null; // TODO get em

            int count = 1;

            PwItemCatDetailed item = new PwItemCatDetailed(id, name, desc, count, nickname, catTitle, coord, location, priceLo, priceHi,
                    refineLevel, bonuses);
            items.add(item);
        }
        return items;
    }

    public static void main(String[] args) {
        List<PwItemCatDetailed> items = itemsStars(Server.vega, 1);
        for (PwItem item : items)
            System.out.println(item.getDesc());
    }
}
