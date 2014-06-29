package ru.adelier.pw;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Adelier on 12.06.2014.
 */
public class LocalDB {
    static {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
//            Class.forName("SQLite.JDBCDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String formatReqItemUri(int id) {
        return String.format("http://www.pwdatabase.com/ru/items/%d", id);
    }

    private static String requestItemName(int id) {
        try {
            Document doc = Jsoup.parse(new URL(formatReqItemUri(id)), 5000);
            if (doc.body().getElementsByClass("message").isEmpty())
                return doc.body().getElementsByClass("itemHeader").get(0).text();
        } catch (MalformedURLException e) {
            // nothing. URI is correct
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Connection con;

    private static Connection createConnection() {
        Connection res = null;
        try {
            res = DriverManager.getConnection("jdbc:derby:myDB;create=true");
            System.out.println("Connection to jdbc:derby:myDB established");
            try {
                res.createStatement().execute("DROP TABLE Items");
            } catch (SQLException e) {
                // ok
            }
            res.createStatement().execute(
                    "CREATE TABLE Items (Id INTEGER NOT NULL, Name VARCHAR (100) NOT NULL)");
            System.out.println("Table Items dropped and created");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return res;
        }
    }

    private static void closeDB() {
        try {
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            con.close();
            System.out.println("Connection to jdbc:derby:myDB closed");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void exec(String update_query) throws SQLException {
        Statement stmt = con.createStatement();
        stmt.executeUpdate(update_query);
        stmt.close();
    }

    private static void add_idname(int id, String name) {
        try {
            String insertQuery = String.format("INSERT INTO Items VALUES (%d, '%s')", id, name);
            exec(insertQuery);
            System.out.println(insertQuery);
        } catch (SQLSyntaxErrorException e) {
            System.out.println(String.format("some buggy name? ok, next (%s)", e.getMessage()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
      * select * from Items where lower(name) like '%идеальный%';
      */
    public static Map<Integer, String> searchSubstrName(String subname) {
        con = createConnection();

        Map<Integer, String> res = new TreeMap<Integer, String>();
        try {
            String q = String.format("SELECT (id, name) FROM items WHERE lower(name) LIKE '%s';", "%" + subname.toLowerCase() + "%");
            Statement s = con.createStatement();
            ResultSet resultSet = s.executeQuery(q);
            while (!resultSet.isLast()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                res.put(id, name);
                if (resultSet.next() == false)
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeDB();
        return res;
    }

    /**
     * main() that goes to the pwdatabase.com/ru, finds all id-name, and stores to the DB
     */
    public static void main(String[] args) {
        int k = 0; // continuous id without name
        int i = 1; // id
        while (k < 10000) { // max gap between
            String name = requestItemName(i);
            if (name != null) {
                add_idname(i, name);
                k = 0;
            }
            i++;
            k++;
        }
    }
}
