package ru.adelier.pw;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PwDBChestQuestRequester {

	private List<Integer> unknownPricesOn = new ArrayList<Integer>(5);
	private float price;
	private int unknownPrice;

	public void getTotalChestPrice(String server, int chestId,
			int unknownPrice) throws MalformedURLException, IOException, InterruptedException {
		this.unknownPrice = unknownPrice;
		
		Document pwdatChest = Jsoup.parse(
				new URL(formatPwdatabaseUrl(chestId)), 5000);

		// System.out.println(pwdatChest.toString());

		Elements ps = pwdatChest.body().getElementsByTag("p");
		int i = 0;
		int size = ps.size();
		for (i = 0; i < size; i++) {
            System.out.println(ps.get(i).text());
			if (ps.get(i).text().contains("Количество"))
				break;
		}

		Element element = ps.get(i);
		String sCount = element.text().replaceAll("[^0-9]", "");
		int iCount = Integer.parseInt(sCount);
		Map<Integer, Float> idsAndChance = new TreeMap<Integer, Float>();

		for (int j = 0;; j++) {
			element = element.nextElementSibling();
			if (element == null)
				break;

			try {
				int elementId = Integer.parseInt(element.child(0).attr("href")
						.replaceAll("[^0-9]", ""));

				String textLine = element.text();
				// System.out.println(textLine);
				Scanner scan = new Scanner(textLine);
				scan.useDelimiter("[ ()%]");
				String sItemPack = scan.findInLine(" [0-9]{1,} ");
				String sItemProb = scan.findInLine("[.0-9]{1,}%");
				scan.close();

				int iItemPack = sItemPack == null ? 1 : Integer
						.parseInt(sItemPack.trim());
				float iItemProb = Float.parseFloat(sItemProb.replace("%", ""));

				idsAndChance.put(elementId, iItemPack * iItemProb / 100f);
			} catch (Exception e) {
				// �������� ������ � �������
				// e.printStackTrace();
				break;
			}
		}
		// System.out.println(idsAndChance.toString());

		List<PwcatsRequester> requesters = new ArrayList<PwcatsRequester>(idsAndChance.size());
		List<Thread> threads = new ArrayList<Thread>(idsAndChance.size());
		ExecutorService execServ = Executors.newCachedThreadPool();
		
		
		for (Entry<Integer, Float> entry : idsAndChance.entrySet()) {
			PwcatsRequester pwcatsRequester = new PwcatsRequester(/*entry.getKey(), entry.getValue(), server*/);
			requesters.add(pwcatsRequester);
			
			Thread pwcatsRequesterThread = new Thread(/*pwcatsRequester*/);
			threads.add(pwcatsRequesterThread);
			
			pwcatsRequesterThread.start();
		}
		
		for (Thread thread : threads) {
			thread.join();
		}
		float totalPrice = 0;
		for (PwcatsRequester pwcatsRequester : requesters) {
//			if (pwcatsRequester.getResult() == null) {
//				unknownPricesOn.add(pwcatsRequester.getId());
//			} else {
//				totalPrice += pwcatsRequester.getResult();
//			}
		}

		price = totalPrice;
	}

	final private static String pwdatabaseUrlFormat = "http://www.pwdatabase.com/ru/quest/%d";

	private static String formatPwdatabaseUrl(int itemId) {
		return String.format(pwdatabaseUrlFormat, itemId);
	}

	public static String formatPwPrice(int price) {
		String normal = "" + price;
		StringBuffer spaced = new StringBuffer(normal);
		int length = spaced.length();
		for (int i = 0; i < (length - 1) / 3; i++)
			spaced.insert(length - 3 * (i + 1), ' ');
		return spaced.toString();
	}

	public static String requestItemName(int id) {
		final String url = "http://www.pwdatabase.com/ru/items/%d";
		try {
			Document doc = Jsoup.parse(new URL(String.format(url, id)), 5000);
			return doc.body().getElementsByClass("itemHeader").get(0).text();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "error";
	}
	public static String requestQuestName(int id) {
		final String url = "http://www.pwdatabase.com/ru/quest/%d";
		try {
			Document doc = Jsoup.parse(new URL(String.format(url, id)), 5000);
			return doc.body().getElementsByTag("th").get(0).text();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "error";
	}

	public static void main(String[] args) throws MalformedURLException,
			IOException {
//		System.out.println(new PwDBChestQuestRequester("", 12).formatPwPrice(100));
//		System.out.println(getTotalChestPrice("vega", 28842, 0));
//		requestName(28842);
	}

	public float getPrice() {
		return price;
	}

	public List<String> getUnknownPricesItems() {
		List<String> res = new ArrayList<String>(5);
		for (int id : unknownPricesOn) {
			res.add(requestItemName(id));
		}
		return res;
	}

	public int getUnknownPrice() {
		return unknownPrice;
	}

	private void setUnknownPrice(int unknownPrice) {
		this.unknownPrice = unknownPrice;
	}
}
