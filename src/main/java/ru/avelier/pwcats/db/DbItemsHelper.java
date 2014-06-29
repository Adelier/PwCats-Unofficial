package ru.avelier.pwcats.db;

import android.content.Context;
import android.database.sqlite.*;
import android.util.Log;
import ru.avelier.pwcats.db.DbItemsContract.*;

import java.io.*;
import java.sql.SQLException;

/**
 * Created by Adelier on 28.06.2014.
 */
public class DbItemsHelper extends SQLiteOpenHelper {
    public final Context context;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PwcatsItems.db";
    private static String DB_PATH = null;

//    private static final String TEXT_TYPE = " VARVHAR(100)";
//    private static final String COMMA_SEP = ",";
//    private static final String SQL_CREATE_ENTRIES =
//            "CREATE TABLE " + ItemsEntry.TABLE_NAME + " (" +
//                    ItemsEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
//                    ItemsEntry.COL_NAME + TEXT_TYPE +
//                    " )";
//    private static final String SQL_DELETE_ENTRIES =
//            "DROP TABLE IF EXISTS " + ItemsEntry.TABLE_NAME;

    public SQLiteDatabase database;

    public DbItemsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        String packageName = context.getPackageName();
        DB_PATH = String.format("//data//data//%s//databases//", packageName);

        openDataBase();
    }

    //Создаст базу, если она не создана
    public void createDataBase() {
        try{
            Log.i("db", "cpying db");
            copyDataBase();
        } catch (IOException e) {
            Log.e(this.getClass().toString(), "Copying error");
            throw new Error("Error copying database!");
        }
//        boolean dbExist = checkDataBase();
//        if (!dbExist) {
//            this.getReadableDatabase();
//            try {
//                copyDataBase();
//            } catch (IOException e) {
//                Log.e(this.getClass().toString(), "Copying error");
//                throw new Error("Error copying database!");
//            }
//        } else {
//            Log.i(this.getClass().toString(), "Database already exists");
//        }
    }
    //Проверка существования базы данных
    private boolean checkDataBase() {
        SQLiteDatabase checkDb = null;
        try {
            String path = DB_PATH + DATABASE_NAME;
            checkDb = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "Error while checking db. All ok, db not exists. Starting to copy it from assets");
        }
        //Андроид не любит утечки ресурсов, все должно закрываться
        if (checkDb != null) {
            checkDb.close();
        }
        return checkDb != null;
    }
    //Метод копирования базы
    private void copyDataBase() throws IOException {
        // Открываем поток для чтения из уже созданной нами БД
        //источник в assets
        InputStream externalDbStream = new BufferedInputStream(context.getAssets().open(DATABASE_NAME));

        // Путь к уже созданной пустой базе в андроиде
        String outFileName = DB_PATH + DATABASE_NAME;

        // Теперь создадим поток для записи в эту БД побайтно
        OutputStream localDbStream = new BufferedOutputStream(new FileOutputStream(outFileName));

        // Собственно, копирование
        byte[] buffer = new byte[4*1024];
        int bytesRead;
        while ((bytesRead = externalDbStream.read(buffer)) > 0) {
            localDbStream.write(buffer, 0, bytesRead);
        }
        // Мы будем хорошими мальчиками (девочками) и закроем потоки
        localDbStream.close();
        externalDbStream.close();

    }

    public SQLiteDatabase openDataBase() {
        String path = DB_PATH + DATABASE_NAME;
        if (database == null) {
            createDataBase();
            database = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READWRITE);
        }
        return database;
    }


    public void onCreate(SQLiteDatabase db) {
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
