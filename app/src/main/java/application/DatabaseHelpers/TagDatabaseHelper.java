package application.DatabaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.ResultSet;
import java.util.ArrayList;

import application.Tag.Tag;
import application.Users.LoggedInUserContainer;

/**
 * Created by mendel on 2018-02-10.
 */

public class TagDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "tags.db";
    public static final String TABLE_NAME = "tags_table";
    public static final String TAG_ID = "tagID";
    public static final String CATAGORY = "catagory";
    public static final String TAG_CONTENT = "tagContent";
    public static final String SELECTED = "selected";


    public TagDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table "+TABLE_NAME+" (tagID integer primary key autoincrement unique , catagory text, tagContent text unique, selected text)");
        Log.i("DBHelpler", "onCreate()");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists "+TABLE_NAME);
        onCreate(db);
//        initTagEntry();
    }

    public void dumpTable(){
        getWritableDatabase().execSQL("drop table if exists "+TABLE_NAME);
        onCreate(getWritableDatabase());
//        initTagEntry();
    }

    public boolean isSelected(Tag tag){
        return true;
    }

    private void initTagEntry(){
        insertTagEntry(new Tag("Movies"));
        insertTagEntry(new Tag("Food"));
        insertTagEntry(new Tag("Technology"));
        insertTagEntry(new Tag("Video Games"));
        insertTagEntry(new Tag("Computers"));
        insertTagEntry(new Tag("Studying"));
        insertTagEntry(new Tag("Cars"));
        insertTagEntry(new Tag("Basketball"));
        insertTagEntry(new Tag("Baseball"));
        insertTagEntry(new Tag("Hockey"));

        insertTagEntry(new Tag("Pizza"));
        insertTagEntry(new Tag("Lasagna"));
        insertTagEntry(new Tag("Sushi"));
        insertTagEntry(new Tag("Burger"));
        insertTagEntry(new Tag("Cake"));
        insertTagEntry(new Tag("Nutella"));
        insertTagEntry(new Tag("Cheese"));
        insertTagEntry(new Tag("Fruit"));
        insertTagEntry(new Tag("Chicken"));
        insertTagEntry(new Tag("Salad"));
        insertTagEntry(new Tag("Cereal"));
        insertTagEntry(new Tag("Shrimp"));
        insertTagEntry(new Tag("Potato"));
        insertTagEntry(new Tag("Smoothie"));

        insertTagEntry(new Tag("Canada"));
        insertTagEntry(new Tag("USA"));
        insertTagEntry(new Tag("Mexico"));
        insertTagEntry(new Tag("Germany"));
        insertTagEntry(new Tag("India"));
        insertTagEntry(new Tag("Japan"));
        insertTagEntry(new Tag("China"));
        insertTagEntry(new Tag("Brazil"));
        insertTagEntry(new Tag("Iran"));
        insertTagEntry(new Tag("Australia"));

        insertTagEntry(new Tag("Math"));
        insertTagEntry(new Tag("Calculus"));
        insertTagEntry(new Tag("Science"));
        insertTagEntry(new Tag("English"));
        insertTagEntry(new Tag("Biology"));
        insertTagEntry(new Tag("Computer Science"));
        insertTagEntry(new Tag("Engineering"));
        insertTagEntry(new Tag("Java"));
        insertTagEntry(new Tag("Python"));
    }


    public boolean insertTagEntry(Tag tag) {
        boolean inserted = false;
        if(!tagExists(tag)) {
            String catagory = null;
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(CATAGORY, catagory);
            contentValues.put(TAG_CONTENT, tag.getTag());
            contentValues.put(SELECTED, this.isSelected(tag));

            long result = db.insert(TABLE_NAME, null, contentValues);
            if (result == -1)
                inserted = false;
            else
                inserted = true;
        }
        return inserted;
    }

    public boolean tagExists(Tag tag){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " +TABLE_NAME + " WHERE " + TAG_CONTENT + " = '" + tag.getTag() + "'" + "COLLATE NOCASE";
        Cursor res = db.rawQuery(query, null);
        boolean tagExists = false;
        try{
            if(res.moveToFirst()){
                tagExists = res.getInt(0) > 0;
            }
        } finally {
            res.close();
        }
        res.close();
        return tagExists;
    }

    public ArrayList<Tag> getAllTags(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Tag> tagList = new ArrayList<Tag>();

        for(Tag t : LoggedInUserContainer.getInstance().getUser().getTags()){
            insertTagEntry(t);
        }
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        while(res.moveToNext()){
//            Log.i("DBHelper", "Tag "+res.getString(2));
            tagList.add(new Tag(res.getString(2)));
        }
        if(tagList.isEmpty()){
            initTagEntry();
            Log.i("DBHelper", "is empty");
            getAllTags();
        }
        res.close();
        return tagList;
    }
    public void deleteTag(Tag tag){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("delete from "+TABLE_NAME+" where "+TAG_CONTENT+" = "+tag.getTag(),null);
        res.close();
    }

    public void changeSelection(Tag tag, Boolean selected){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("update "+TABLE_NAME+" set "+SELECTED+" = "+selected+" where "+TAG_CONTENT+" = '" + tag.getTag() + "'" + "COLLATE NOCASE",null);
        res.close();
    }


//    private class AddToDatabase implements Runnable{
//
//        private Tag newTag;
//
//        public AddToDatabase(Tag tag){
//            newTag = tag;
//        }
//
//        @Override
//        public void run() {
//            insertTagEntry(newTag);
//        }
//    }

}