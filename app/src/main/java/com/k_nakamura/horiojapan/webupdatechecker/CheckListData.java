package com.k_nakamura.horiojapan.webupdatechecker;

import android.content.Context;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by user on 2016/08/18.
 */
public class CheckListData
        implements Serializable
{
    protected int id;
    protected String title;
    protected String url;
    protected String lastupdate;
    protected String lastHtml;
    protected String ignoreWords;
    protected boolean isUpdate;
    protected boolean isNotification;

    public CheckListData(int id, String title, String url, String lastupdate, String lastHtml, String ignoreWords, boolean isUpdate, boolean isNotification)
    {
        this.id = id;
        this.title = title;
        this.url = url;
        this.lastupdate = lastupdate;
        this.lastHtml = lastHtml;
        this.ignoreWords = ignoreWords;

        this.isUpdate = isUpdate;
        this.isNotification = isNotification;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastHtml() {
        return lastHtml;
    }

    public void setLastHtml(String lastHtml) {
        this.lastHtml = lastHtml;
    }

    public String getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(String lastupdate) {
        this.lastupdate = lastupdate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIgnoreWords() {
        return ignoreWords;
    }

    public void setIgnoreWords(String ignoreWords) {
        this.ignoreWords = ignoreWords;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(boolean update) {
        isUpdate = update;
    }
    public String getIsUpdateText()
    {
        if(isUpdate)
            return "Updated!";
        else
            return "Not Updated";
    }

    public boolean isNotification() {
        return isNotification;
    }

    public void setIsNotification(boolean notification) {
        isNotification = notification;
    }

    public void updateDB(Context context)
    {
        CheckListDBAdapter clDBAdapter = new CheckListDBAdapter(context);
        clDBAdapter.open();
        clDBAdapter.update(this);
        clDBAdapter.close();
    }
}
