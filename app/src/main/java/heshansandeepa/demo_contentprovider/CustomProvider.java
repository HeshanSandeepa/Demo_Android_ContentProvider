package heshansandeepa.demo_contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.HashMap;

/**
 * Created by Heshan on 12/16/2015.
 */
public class CustomProvider extends ContentProvider {

    static final String PROVIDER_NAME = "heshansandeepa.demo_contentprovider.CustomProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/cte";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String id = "id";
    static final String name = "name";
    static final int uriCode = 1;
    static final UriMatcher uriMatcher;
    private DataBaseHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private static HashMap<String, String> values;


    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "cte", uriCode);
        uriMatcher.addURI(PROVIDER_NAME, "cte/*", uriCode);
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DataBaseHelper(context);
        sqLiteDatabase = dbHelper.getWritableDatabase();
        if (sqLiteDatabase != null) {
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(DataBaseHelper.TABLE_NAME);

            switch (uriMatcher.match(uri)) {
                case uriCode:
                    qb.setProjectionMap(values);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
            if (sortOrder == null || sortOrder == "") {
                sortOrder = name;
            }
            Cursor c = qb.query(sqLiteDatabase, projection, selection, selectionArgs, null,
                    null, sortOrder);
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case uriCode:
                return "vnd.android.cursor.dir/cte";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = sqLiteDatabase.insert(DataBaseHelper.TABLE_NAME, "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case uriCode:
                count = sqLiteDatabase.delete(DataBaseHelper.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case uriCode:
                count = sqLiteDatabase.update(DataBaseHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
