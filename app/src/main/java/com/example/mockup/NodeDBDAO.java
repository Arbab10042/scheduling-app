package com.example.mockup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

public class NodeDBDAO implements INodeDAO {

    private Context context;

    public NodeDBDAO(Context ctx){
        context = ctx;
    }

    @Override
    public void insertNode(Node node) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO NodesList(title,time,current_episode,total_episodes,status,bookmarked) " +
                "VALUES('"+node.getTitle()+"','"+node.getTime()+"',"+node.getCurrent_episode()+","+node.getTotal_episodes()+"," +
                ""+node.getStatus()+","+node.getBookmarked()+")");
    }

    @Override
    public void saveNode(Node node) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE NodesList SET title = '"+node.getTitle()+"',time = '"+node.getTime()+"',current_episode = "+node.getCurrent_episode()+"" +
                ",total_episodes = "+node.getTotal_episodes()+",status = "+node.getStatus()+",bookmarked = "+node.getBookmarked()+" " +
                "WHERE node_id = " + node.getNode_id());
    }

    @Override
    public void deleteNode(Node node) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM NodesList WHERE node_id = " + node.getNode_id());
    }

    @SuppressLint("LongLogTag")
    @Override
    public int getMaxID() {
        DBHelper dbHelper=new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "SELECT node_id FROM NodesList ORDER BY node_id DESC LIMIT 1";
        int id = 0;
        Cursor cursor = db.rawQuery(query,null);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                id = cursor.getInt(0);
            }
        }catch (Exception e) {
            Log.d("Error Message in NodeDBDAO", e.getMessage());
        }
        return id;
    }

    @Override
    public ArrayList<Hashtable<String, String>> getTodayNodes() {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        String dayToday = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());

        String query = "SELECT * FROM NodesList WHERE time LIKE '%"+dayToday+"%'";
        Cursor cursor = db.rawQuery(query,null);

        ArrayList<Hashtable<String,String>> objects = new ArrayList<Hashtable<String, String>>();
        while(cursor.moveToNext()){
            Hashtable<String,String> obj = new Hashtable<String, String>();
            String [] columns = cursor.getColumnNames();
            for(String col : columns){
                obj.put(col,cursor.getString(cursor.getColumnIndex(col)));
            }
            objects.add(obj);
        }
        return objects;
    }

    @Override
    public ArrayList<Hashtable<String, String>> getBookmarkedNodes() {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM NodesList WHERE bookmarked = 1";
        Cursor cursor = db.rawQuery(query,null);

        ArrayList<Hashtable<String,String>> objects = new ArrayList<Hashtable<String, String>>();
        while(cursor.moveToNext()){
            Hashtable<String,String> obj = new Hashtable<String, String>();
            String [] columns = cursor.getColumnNames();
            for(String col : columns){
                obj.put(col,cursor.getString(cursor.getColumnIndex(col)));
            }
            objects.add(obj);
        }
        return objects;
    }

    @Override
    public ArrayList<Hashtable<String, String>> getAllNodes() {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM NodesList";
        Cursor cursor = db.rawQuery(query,null);

        ArrayList<Hashtable<String,String>> objects = new ArrayList<Hashtable<String, String>>();
        while(cursor.moveToNext()){
            Hashtable<String,String> obj = new Hashtable<String, String>();
            String [] columns = cursor.getColumnNames();
            for(String col : columns){
                obj.put(col,cursor.getString(cursor.getColumnIndex(col)));
            }
            objects.add(obj);
        }
        return objects;
    }

    @Override
    public void insertXMLData(String xmlData) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        int limit = 100;

        Log.d("NodeParseLog","Starting...");

        try{
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(xmlData));
            int event = parser.getEventType();
            String title = "", time = "";
            Integer current = 0, total = 0;
            String text = "";

            while(event != XmlPullParser.END_DOCUMENT && limit > 0){

                if(event == XmlPullParser.TEXT){
                    text = parser.getText();
                }

                if(event == XmlPullParser.END_TAG && parser.getName().equals("title") ){
                    title = text;
                }
                if(event == XmlPullParser.END_TAG && parser.getName().equals("time") ){
                    time = text;
                }
                if(event == XmlPullParser.END_TAG && parser.getName().equals("current") ){
                    current = Integer.parseInt(text);
                }
                if(event == XmlPullParser.END_TAG && parser.getName().equals("total") ){
                    total = Integer.parseInt(text);
                }

                if(event == XmlPullParser.END_TAG && parser.getName().equals("node") ){

                    String query = "SELECT * FROM NodesList WHERE title = '"+title+"'";
                    Cursor cursor = db.rawQuery(query,null);
                    if(cursor.getCount() == 0){
                        //avoid duplicates
                        insertNode(new Node(0,title,time,current,total,3,0));
                        Log.d("NodeParseLog","Inserted: " + title);
                    }else{
                        Log.d("NodeParseLog","Duplicate: " + title);
                    }

                    limit = limit - 1;
                }

                event = parser.next();
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }

        Log.d("NodeParseLog","Ending.");
    }

    public void clearDatabase(){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.execSQL("DELETE FROM NodesList");
    }

}
