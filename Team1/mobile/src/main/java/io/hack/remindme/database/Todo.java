package io.hack.remindme.database;

import io.hack.remindme.PollService;

/**
 * Created by iQube_2 on 10/2/2015.
 */
public class Todo {

    int id;
    String content;
    String wdate;
   public boolean present=false;
    public Todo()
    {

    }
    public Todo(int id,String content,String wdate)
    {
        this.id=id;
        this.content=content;
        this.wdate=wdate;
    }

    public Todo (String content,String wdate)
    {
        this.content=content;
        this.wdate=wdate;
    }

    public void setId(int id)
    {
        this.id=id;

    }

    public void setContent(String content)
    {
        this.content=content;
    }

    public void setWdate(String wdate)
    {
        this.wdate=wdate;
    }


    public int getId()
    {
        return id;

    }
    public String getContent()
    {
        return content;

    }
    public String getWdate()
    {
        return wdate;
    }



}
