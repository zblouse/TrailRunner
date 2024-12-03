package com.example.trailrunner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLiteOpenHelper for the TrailDatabase
 */
public class TrailDatabaseHelper extends SQLiteOpenHelper {

    //Sets up keys for use in the sql queries
    private static final String DATABASE_NAME = "trail_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "trails";
    private static final String ID_KEY = "id";
    private static final String TRAIL_NAME_KEY = "trail_name";
    private static final String TRAIL_DISTANCE_KEY = "trail_distance";
    private static final String TRAIL_DISTANCE_UNIT_KEY = "trail_distance_unit";
    private static final String UID_KEY = "uid";
    private static final String USER_TRAIL_DISTANCE_KEY = "user_trail_distance";
    private static final String TRAIL_START_LATITUDE_KEY = "trail_start_latitude";
    private static final String TRAIL_START_LONGITUDE_KEY = "trail_start_longitude";

    public TrailDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * When the TrailDatabaseHelper is created this method is called, it execs an SQL query to create the trail table
     * @param database The database.
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + "(" +
                ID_KEY + " INTEGER PRIMARY KEY," +
                TRAIL_NAME_KEY + " TEXT," +
                TRAIL_DISTANCE_KEY + " REAL," +
                TRAIL_DISTANCE_UNIT_KEY + " TEXT," +
                UID_KEY + " REAL," +
                USER_TRAIL_DISTANCE_KEY + " REAL," +
                TRAIL_START_LATITUDE_KEY + " REAL," +
                TRAIL_START_LONGITUDE_KEY + " REAL" +
                ")";
        database.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * Adds a trail to the database
     * @param trail
     */
    public void addTrailToDatabase(Trail trail){
        SQLiteDatabase database = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TRAIL_NAME_KEY,trail.getTrailName());
        contentValues.put(TRAIL_DISTANCE_KEY, trail.getTrailDistance());
        contentValues.put(TRAIL_DISTANCE_UNIT_KEY, trail.getTrailDistanceUnit());
        contentValues.put(UID_KEY, trail.getUid());
        contentValues.put(USER_TRAIL_DISTANCE_KEY, trail.getUserTrailDistance());
        contentValues.put(TRAIL_START_LATITUDE_KEY, trail.getTrailStartLatitude());
        contentValues.put(TRAIL_START_LONGITUDE_KEY, trail.getTrailStartLongitude());
        database.insert(TABLE_NAME, null, contentValues);
        database.close();
    }

    /**
     * Gets all trails from the database for the user with the supplied uid
     * @param uid
     * @return
     */
    public List<Trail> getAllTrailsForUser(String uid){
        SQLiteDatabase database = getReadableDatabase();

        Cursor trailCursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + UID_KEY + "='" + uid + "'", null);
        ArrayList<Trail> trailList = new ArrayList<>();
        if(trailCursor.moveToFirst()) {
            do {
                Trail trail = new Trail(trailCursor.getInt(0),trailCursor.getString(1),
                        trailCursor.getDouble(2), trailCursor.getString(3), trailCursor.getString(4),
                        trailCursor.getDouble(5), trailCursor.getDouble(6), trailCursor.getDouble(7));
                trailList.add(trail);
            } while(trailCursor.moveToNext());
        }
        trailCursor.close();
        database.close();
        return trailList;
    }

    /**
     * Deletes all trails for the user with the provided uid
     * @param uid
     */
    public void deleteAllTrailsForUser(String uid){
        SQLiteDatabase database = getReadableDatabase();
        database.delete(TABLE_NAME,UID_KEY+"=?",new String[]{String.valueOf(uid)});
        database.close();
    }

    /**
     * Gets a trail with the specific id
     * @param id
     * @return
     */
    public Trail getTrailById(String id){
        SQLiteDatabase database = getReadableDatabase();

        Cursor trailCursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ID_KEY + "='" + id + "'", null);
        Trail trail = null;
        if(trailCursor.moveToFirst()) {
            trail = new Trail(trailCursor.getInt(0),trailCursor.getString(1),
                    trailCursor.getDouble(2), trailCursor.getString(3), trailCursor.getString(4),
                    trailCursor.getDouble(5), trailCursor.getDouble(6), trailCursor.getDouble(7));
        }
        trailCursor.close();
        database.close();
        return trail;
    }

    /**
     * Updates a trail that is saved in the database
     * @param trail
     */
    public void updateTrail(Trail trail){
        SQLiteDatabase database = getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_KEY,trail.getId());
        contentValues.put(TRAIL_NAME_KEY,trail.getTrailName());
        contentValues.put(TRAIL_DISTANCE_KEY, trail.getTrailDistance());
        contentValues.put(TRAIL_DISTANCE_UNIT_KEY, trail.getTrailDistanceUnit());
        contentValues.put(UID_KEY, trail.getUid());
        contentValues.put(USER_TRAIL_DISTANCE_KEY, trail.getUserTrailDistance());
        contentValues.put(TRAIL_START_LATITUDE_KEY, trail.getTrailStartLatitude());
        contentValues.put(TRAIL_START_LONGITUDE_KEY, trail.getTrailStartLongitude());
        database.update(TABLE_NAME, contentValues,ID_KEY+"=?",new String[]{String.valueOf(trail.getId())});
        database.close();
    }

    /**
     * Deletes a trail from the database
     * @param trail
     */
    public void deleteTrail(Trail trail){
        SQLiteDatabase database = getReadableDatabase();
        database.delete(TABLE_NAME,ID_KEY+"=?",new String[]{String.valueOf(trail.getId())});
        database.close();
    }

    /**
     * Fetches all trails from the database
     * @return
     */
    public List<Trail> getAllTrails(){
        SQLiteDatabase database = getReadableDatabase();

        Cursor trailCursor = database.rawQuery("SELECT * FROM " + TABLE_NAME , null);
        ArrayList<Trail> trailList = new ArrayList<>();
        if(trailCursor.moveToFirst()) {
            do {
                Trail trail = new Trail(trailCursor.getInt(0),trailCursor.getString(1),
                        trailCursor.getDouble(2), trailCursor.getString(3), trailCursor.getString(4),
                        trailCursor.getDouble(5), trailCursor.getDouble(6), trailCursor.getDouble(7));
                trailList.add(trail);
            } while(trailCursor.moveToNext());
        }
        trailCursor.close();
        database.close();
        return trailList;
    }
}
