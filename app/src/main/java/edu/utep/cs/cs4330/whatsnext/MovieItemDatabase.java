package edu.utep.cs.cs4330.whatsnext;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MovieItemDatabase extends SQLiteOpenHelper {

//--ATTRIBUTES--------------------------------------------------------------------------------------

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "todoDB";
    private static final String TODO_TABLE = "items";

    private static MovieFinder mf = new MovieFinder();

    private static final String KEY_ID = "_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_ACTORS = "actors";
    private static final String KEY_REVIEWS = "reviews";
    private static final String KEY_RATING = "rating";
    private static final String KEY_WATCHED = "watched";
    private static final String KEY_SERVICES = "services";

//--CONSTRUCTOR-------------------------------------------------------------------------------------

    public MovieItemDatabase(Context context){
        super (context, DB_NAME, null, DB_VERSION);
    }

//--ONCREATE----------------------------------------------------------------------------------------

    @Override
    public void onCreate(SQLiteDatabase db) {
        String table = "CREATE TABLE " + TODO_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_TITLE + " TEXT, "
                + KEY_IMAGE + " TEXT, "
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_ACTORS + " TEXT,"
                + KEY_REVIEWS + " TEXT,"
                + KEY_RATING + " DOUBLE,"
                + KEY_WATCHED + " INTEGER,"
                + KEY_SERVICES + " TEXT" + ")";
        db.execSQL(table);
    }

//--DATABASE-METHODS--------------------------------------------------------------------------------

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        onCreate(database);
    }

    public void addItem(MovieItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, item.getTitle());
        values.put(KEY_IMAGE, item.getImage());
        values.put(KEY_DESCRIPTION, item.getDescription());
        values.put(KEY_ACTORS, mf.convertArrayToString(item.getActors()));
        values.put(KEY_REVIEWS, mf.convertArrayToString(item.getReviews()));
        values.put(KEY_RATING, item.getRating());
        values.put(KEY_WATCHED, item.getWatched() ? 1 : 0);
        values.put(KEY_SERVICES, mf.convertArrayToString(item.getServices()));
        long id = db.insert(TODO_TABLE, null, values);
        item.setId((int) id);
        db.close();
    }

    public void update(MovieItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, item.getTitle());
        values.put(KEY_IMAGE, item.getImage());
        values.put(KEY_DESCRIPTION, item.getDescription());
        values.put(KEY_ACTORS, mf.convertArrayToString(item.getActors()));
        values.put(KEY_REVIEWS, mf.convertArrayToString(item.getReviews()));
        values.put(KEY_RATING, item.getRating());
        values.put(KEY_WATCHED, item.getWatched() ? 1 : 0);
        values.put(KEY_SERVICES, mf.convertArrayToString(item.getServices()));
        db.update(TODO_TABLE, values, KEY_ID + " = ?", new String[]{String.valueOf(item.getId())});
        db.close();
    }

    public void delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TODO_TABLE, KEY_ID + " = ?", new String[] { Integer.toString(id) } );
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TODO_TABLE, null, new String[]{});
        db.close();
    }

    public List<MovieItem> allItems() {
        List<MovieItem> movieList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TODO_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String image = cursor.getString(2);
                String description = cursor.getString(3);
                String[] actors = mf.convertStringToArray(cursor.getString(4));
                String[] reviews = mf.convertStringToArray(cursor.getString(5));
                Double rating = cursor.getDouble(6);
                boolean watched = cursor.getInt(7) == 1;
                String[] services = mf.convertStringToArray(cursor.getString(8));
                MovieItem task = new MovieItem(id, title, image, description, actors, reviews, rating, watched, services);
                movieList.add(task);
            } while (cursor.moveToNext());
        }
        return movieList;
    }
}