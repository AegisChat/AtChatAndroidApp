package application.DatabaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import application.Message.RecievedMessage;
import application.Message.SentMessage;
import application.Message.TextMessage;


/**
 * Created by Mendel on 2018-02-07.
 */

public class TextMessageDatabaseHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "messages.db";
    public static final String TABLE_NAME = "messages_table";
    public static final String MESSAGE_ID = "messageID";
    public static final String SENDER = "sender";
    public static final String RECIEVER = "receiver";
    public static final String TIME_STAMP = "timeStamp";
    public static final String MESSAGE = "message";

    private UUID userID;

    public TextMessageDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (messageID text primary key unique, sender text, receiver text, timeStamp real, message text) ");
        userID = UUID.randomUUID(); //THIS IS WRONG, ITS JUST TEMPORARY
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertMessageEntry(String messageID, String sender, String receiver, String timeStamp, String message){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MESSAGE_ID, messageID);
        contentValues.put(SENDER, sender);
        contentValues.put(RECIEVER, receiver);
        contentValues.put(TIME_STAMP, timeStamp);
        contentValues.put(MESSAGE, message);

        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insterMessageEntry(TextMessage message){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MESSAGE_ID, message.getId().toString());
        contentValues.put(SENDER, message.getSender().toString());
        contentValues.put(RECIEVER, message.getRecipient().toString());
        contentValues.put(TIME_STAMP, message.getTime());
        contentValues.put(MESSAGE, message.getContext().toString());

        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }



    //this will get all data from the database
    public Cursor getAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " +TABLE_NAME,null);
        return res;
    }

    public List<TextMessage> getMessagesForUniqueConversation(UUID id){
        List<TextMessage> messages = new ArrayList<TextMessage>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(" SELECT * FROM " +TABLE_NAME +" WHERE "+SENDER+" = "+id.toString() +"OR "+RECIEVER+ " = "+ id.toString() + "  ORDER BY " + TIME_STAMP + "DESC",null);
        while(res.moveToNext()){
            if(res.getString(1).equals(userID.toString())){
                SentMessage sentMessage = new SentMessage();
                sentMessage.setStringToUUID(res.getString(0));
                sentMessage.setSender(UUID.fromString(res.getString(1)));
                sentMessage.setRecipient(UUID.fromString(res.getString(2)));
                sentMessage.setTime(res.getLong(3));
                sentMessage.setContext(res.getString(4));

                messages.add(sentMessage);
            }
            else {
                RecievedMessage recievedMessage = new RecievedMessage();
                recievedMessage.setStringToUUID(res.getString(0));
                recievedMessage.setSender(UUID.fromString(res.getString(1)));
                recievedMessage.setRecipient(UUID.fromString(res.getString(2)));
                recievedMessage.setTime(res.getLong(3));
                recievedMessage.setContext(res.getString(4));

                messages.add(recievedMessage);
            }
        }
        return null;
    }


}