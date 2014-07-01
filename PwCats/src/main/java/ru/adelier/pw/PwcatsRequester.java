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

public class PwcatsRequester {

    // same in values/servers.xml
    public enum Server {vega, orion, sirius, mira, terazet, altair,
        gelios, pegas, antares, kassiopeya, lira, andromeda, omega, persey};

	private static String formatPwcatsUrl(String server, int itemId){
        final String pwcatsUrlFormat = "http://pwcats.info/cats/%s/item/%d";
		return String.format(pwcatsUrlFormat, server, itemId);
	}

    public static List<PwItemCat> itemsCat(Server server, int id) {
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
            String[] sCoord = cloumns.get(2).text().split(" ");
            int x = Integer.parseInt(sCoord[0]);
            int y = Integer.parseInt(sCoord[1]);
            int[] coord = new int[]{x, y};
            PwItemCat.Location location =
                    PwItemCat.Location.valueOf(cloumns.get(3).text());

            // name & count
            String[] nameCount = cloumns.get(4).text().split("[()]");
            String name = nameCount[0].trim();
            int count = Integer.parseInt(nameCount[1]);
            String imageLink = cloumns.get(4).child(0).toString();
//            System.out.printf("%s, %d, %s\n", name, count, imageLink);
//          >>Недописанная небесная глава, 34, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 30, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 41, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 59, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 41, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 20, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 4, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 17, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 18, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 14, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 19, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 58, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 55, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 98, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 70, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 1, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 3, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 2, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 5, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 2, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 13, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 20, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 20, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 2, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 20, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 20, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 6, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />Недописанная небесная глава, 1, <img src="http://www.pwdatabase.com/images/icons/generalm/20746.gif" width="30" height="30" border="0" title="&lt;span style='color:#; white-space:nowrap; font-size:15px;'&gt;Недописанная небесная глава&lt;/span&gt;&lt;br /&gt;&lt;span style=color:#ffcb4a&gt;Используйте 20 страниц чтобы создать свиток умений в печи Города слез неба.&lt;br /&gt;Также необходимы средние чернила.&lt;/span&gt;" class="TipsyTip2" />


                    // prices
            String sPriceLo = cloumns.get(6).text();
            String sPriceHi = cloumns.get(5).text(); // 6 is low, 5 is high
            Integer priceLo = myParseInt(sPriceLo);
            Integer priceHi = myParseInt(sPriceHi);

            PwItemCat item = new PwItemCat(id, name, imageLink, count, nickname, catTitle, coord, location, priceLo, priceHi);
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
}
