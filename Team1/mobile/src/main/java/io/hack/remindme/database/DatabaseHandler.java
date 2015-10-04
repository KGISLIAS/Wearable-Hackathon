package io.hack.remindme.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iQube_2 on 10/2/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {



    public static String DATABASE_NAME="remindme";
    public static int DATABASE_VERSION=1;

    //table names

    public static String TABLE_TODOS="todos";

    public static String TODOS_ID="id";
    public static String TODOS_CONTENT="content";
    public static String TODOS_DATE="wdate";








    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
String CreateTodos="CREATE TABLE IF NOT EXISTS "+TABLE_TODOS+" ("+TODOS_ID+" INTEGER PRIMARY KEY,"+TODOS_CONTENT+" TEXT,"+TODOS_DATE+" TEXT)";
        db.execSQL(CreateTodos);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + TABLE_TODOS);

    }


    public void createTodo(Todo todo)
    {
        ContentValues cv=new ContentValues();

        cv.put(TODOS_CONTENT,todo.getContent());
        cv.put(TODOS_DATE,todo.getWdate());
        SQLiteDatabase db=getWritableDatabase();
        db.insert(TABLE_TODOS, null, cv);
    }

    public Todo getTodoByID(int id)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor= db.query(TABLE_TODOS,new String[]{TODOS_ID,TODOS_CONTENT,TODOS_DATE} , TODOS_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if(cursor!=null)
            cursor.moveToFirst();
        Todo todo= new Todo(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2));
        db.close();
        return todo;
    }

    public List getAllTodos()
    {
        List<Todo> todoList = new ArrayList<Todo>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TODOS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Todo todo = new Todo();
                todo.setId(Integer.parseInt(cursor.getString(0)));
                todo.setContent(cursor.getString(1));
                todo.setWdate(cursor.getString(2));


                // Adding event to list
                todoList.add(todo);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        // return contact list
        return todoList;

    }

    public int getTodoCountt()
    {
        String countQuery = "SELECT  * FROM " + TABLE_TODOS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        db.close();
        cursor.close();

        // return count
        return cursor.getCount();

    }

    public void deleteTodo(int i)

    {

        SQLiteDatabase db=getWritableDatabase();
        db.delete(TABLE_TODOS,TODOS_ID+"=?",new String[]{String.valueOf(i)});
        db.close();
    }
}


